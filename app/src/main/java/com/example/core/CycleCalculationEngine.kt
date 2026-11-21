package com.example.core

import com.example.data.MenstrualPeriod
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Pure API-24-safe UTC epoch-day utilities (never relies on java.time API 26+).
 */
object EpochDateUtil {
    private const val MILLIS_PER_DAY = 86_400_000L

    fun todayEpochDay(): Long {
        val cal = Calendar.getInstance()
        return calendarToEpochDay(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun calendarToEpochDay(year: Int, monthZeroBased: Int, dayOfMonth: Int): Long {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(year, monthZeroBased, dayOfMonth, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return utc.timeInMillis / MILLIS_PER_DAY
    }

    fun epochDayToYearMonthDay(epochDay: Long): Triple<Int, Int, Int> {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = epochDay * MILLIS_PER_DAY
        }
        return Triple(
            utc.get(Calendar.YEAR),
            utc.get(Calendar.MONTH),
            utc.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun formatEpochDay(epochDay: Long, isBangla: Boolean = false): String {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = epochDay * MILLIS_PER_DAY
        }
        val locale = if (isBangla) Locale.forLanguageTag("bn-BD") else Locale.ENGLISH
        val fmt = SimpleDateFormat("dd MMM yyyy", locale).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return fmt.format(utc.time)
    }

    fun formatShortDate(epochDay: Long): String {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = epochDay * MILLIS_PER_DAY
        }
        val fmt = SimpleDateFormat("dd MMM", Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return fmt.format(utc.time)
    }
}

enum class PredictionConfidence(val enLabel: String, val bnLabel: String) {
    INSUFFICIENT("Insufficient Data", "পর্যাপ্ত তথ্য নেই"),
    LOW("Low Confidence (Variable)", "কম নিশ্চয়তা (চক্র পরিবর্তনশীল)"),
    MODERATE("Moderate Confidence", "মাঝারি নিশ্চয়তা"),
    HIGH("Consistent Pattern", "তুলনামূলক নিয়মিত ধারা");
}

data class CycleStatistics(
    val recordedPeriodCount: Int,
    val individualCycleLengths: List<Int>,
    val averageLengthDays: Double?,
    val medianLengthDays: Double?,
    val minLengthDays: Int?,
    val maxLengthDays: Int?,
    val variabilityRangeDays: Int?,
    val standardDeviationDays: Double?,
    val lastPeriodStartEpochDay: Long?,
    val estimatedWindowStartEpochDay: Long?,
    val estimatedWindowEndEpochDay: Long?,
    val confidence: PredictionConfidence,
    val uncertaintyNoteEn: String,
    val uncertaintyNoteBn: String,
)

/**
 * Deterministic Cycle Calculation Engine (PRD Section 4 & Section 14.5).
 * - Cycle length = First day of next menstrual period - First day of previous menstrual period.
 * - Excludes spotting-only records from cycle-start calculations.
 * - Never assumes every cycle is 28 days.
 * - Always displays future estimates as a range and communicates uncertainty.
 * - Never claims calendar calculations establish ovulation or fertility.
 */
object CycleCalculationEngine {
    fun calculate(periods: List<MenstrualPeriod>): CycleStatistics {
        // Only full menstrual periods (exclude spotting-only entries per PRD Section 4)
        val sortedStarts = periods
            .filter { !it.spottingOnly }
            .map { it.startDateEpochDay }
            .distinct()
            .sorted()

        if (sortedStarts.isEmpty()) {
            return CycleStatistics(
                recordedPeriodCount = 0,
                individualCycleLengths = emptyList(),
                averageLengthDays = null,
                medianLengthDays = null,
                minLengthDays = null,
                maxLengthDays = null,
                variabilityRangeDays = null,
                standardDeviationDays = null,
                lastPeriodStartEpochDay = null,
                estimatedWindowStartEpochDay = null,
                estimatedWindowEndEpochDay = null,
                confidence = PredictionConfidence.INSUFFICIENT,
                uncertaintyNoteEn = "Record at least two period start dates so we can calculate your personal cycle pattern. We never assume a 28-day cycle.",
                uncertaintyNoteBn = "আপনার ব্যক্তিগত চক্রের ধারা বুঝতে অন্তত দুটি মাসিকের শুরুর তারিখ লিখুন। আমরা কখনোই অনুমান করে ২৮ দিন ধরে নিই না।"
            )
        }

        if (sortedStarts.size == 1) {
            return CycleStatistics(
                recordedPeriodCount = 1,
                individualCycleLengths = emptyList(),
                averageLengthDays = null,
                medianLengthDays = null,
                minLengthDays = null,
                maxLengthDays = null,
                variabilityRangeDays = null,
                standardDeviationDays = null,
                lastPeriodStartEpochDay = sortedStarts.last(),
                estimatedWindowStartEpochDay = null,
                estimatedWindowEndEpochDay = null,
                confidence = PredictionConfidence.INSUFFICIENT,
                uncertaintyNoteEn = "1 period recorded. Once you record your next period, your first personal cycle length will be calculated. Calendar estimates do not determine ovulation or fertility.",
                uncertaintyNoteBn = "১টি মাসিক নথিভুক্ত হয়েছে। পরবর্তী মাসিক শুরু হলে প্রথম চক্রের দৈর্ঘ্য হিসাব করা হবে। ক্যালেন্ডার হিসাব ডিম্বস্ফোটন বা গর্ভধারণ-ক্ষমতা নির্ধারণ করে না।"
            )
        }

        val cycleLengths = mutableListOf<Int>()
        for (i in 1 until sortedStarts.size) {
            val diff = (sortedStarts[i] - sortedStarts[i - 1]).toInt()
            // Ignore duplicate/same-week entries (< 10 days) and extreme multi-year gaps (> 365 days) from single diff distortion if needed,
            // but preserve realistic irregular/PCOS cycles (15 to 180 days)
            if (diff in 10..180) {
                cycleLengths.add(diff)
            }
        }

        if (cycleLengths.isEmpty()) {
            return CycleStatistics(
                recordedPeriodCount = sortedStarts.size,
                individualCycleLengths = emptyList(),
                averageLengthDays = null,
                medianLengthDays = null,
                minLengthDays = null,
                maxLengthDays = null,
                variabilityRangeDays = null,
                standardDeviationDays = null,
                lastPeriodStartEpochDay = sortedStarts.last(),
                estimatedWindowStartEpochDay = null,
                estimatedWindowEndEpochDay = null,
                confidence = PredictionConfidence.INSUFFICIENT,
                uncertaintyNoteEn = "Recorded dates are either too close or more than 180 days apart to form a cycle estimate. Please consult a doctor on BondhuHelp if you have prolonged missed periods.",
                uncertaintyNoteBn = "নথিভুক্ত তারিখগুলো খুব কাছাকাছি বা ১৮০ দিনের বেশি ব্যবধানে রয়েছে। দীর্ঘদিন মাসিক বন্ধ থাকলে বন্ধুহেল্প-এর মাধ্যমে ডাক্তারের পরামর্শ নিন।"
            )
        }

        val avg = cycleLengths.average()
        val sortedLengths = cycleLengths.sorted()
        val median = if (sortedLengths.size % 2 == 0) {
            (sortedLengths[sortedLengths.size / 2 - 1] + sortedLengths[sortedLengths.size / 2]) / 2.0
        } else {
            sortedLengths[sortedLengths.size / 2].toDouble()
        }
        val minLen = sortedLengths.first()
        val maxLen = sortedLengths.last()
        val range = maxLen - minLen

        val variance = cycleLengths.map { (it - avg) * (it - avg) }.average()
        val sd = sqrt(variance)

        val confidence = when {
            cycleLengths.size < 2 -> PredictionConfidence.LOW
            range > 10 || sd > 5.0 -> PredictionConfidence.LOW
            cycleLengths.size >= 3 && range <= 5 -> PredictionConfidence.HIGH
            else -> PredictionConfidence.MODERATE
        }

        // Display estimated next-period window as a wider range when variable/limited
        val marginDays = when (confidence) {
            PredictionConfidence.INSUFFICIENT -> 7
            PredictionConfidence.LOW -> maxOf(5, (range / 2).coerceAtMost(14))
            PredictionConfidence.MODERATE -> 3
            PredictionConfidence.HIGH -> 2
        }

        val centerDays = median.roundToInt()
        val lastStart = sortedStarts.last()
        val windowStart = lastStart + (centerDays - marginDays).coerceAtLeast(15)
        val windowEnd = lastStart + (centerDays + marginDays)

        val noteEn = when (confidence) {
            PredictionConfidence.LOW ->
                "Your recorded cycles vary (${minLen}–${maxLen} days). This range is an estimate only—not a medical certainty—and does not measure ovulation or fertility."
            PredictionConfidence.MODERATE ->
                "Estimate based on ${cycleLengths.size} recorded cycles (median ${median.roundToInt()} days). Cycles naturally shift; calendar tracking does not determine fertility."
            PredictionConfidence.HIGH ->
                "Based on ${cycleLengths.size} consistent recorded cycles (${minLen}–${maxLen} days). Still an estimate only—not a fertility or ovulation indicator."
            PredictionConfidence.INSUFFICIENT ->
                "More cycle history is needed for a reliable window."
        }

        val noteBn = when (confidence) {
            PredictionConfidence.LOW ->
                "আপনার পূর্বের চক্রগুলোতে পরিবর্তন দেখা গেছে (${minLen}–${maxLen} দিন)। এটি কেবল একটি সম্ভাব্য পরিসর, নিশ্চিত পূর্বাভাস বা উর্বরতার নির্দেশক নয়।"
            PredictionConfidence.MODERATE ->
                "${cycleLengths.size}টি নথিভুক্ত চক্রের ভিত্তিতে সম্ভাব্য সময় (মধ্যক ${median.roundToInt()} দিন)। ক্যালেন্ডার হিসাব ডিম্বস্ফোটন নির্ধারণ করে না।"
            PredictionConfidence.HIGH ->
                "${cycleLengths.size}টি নিয়মিত চক্রের ভিত্তিতে (${minLen}–${maxLen} দিন)। তবুও এটি কেবল একটি আনুমানিক সময়, উর্বরতার প্রমাণ নয়।"
            PredictionConfidence.INSUFFICIENT ->
                "নির্ভরযোগ্য সময়ের জন্য আরও চক্রের তথ্য প্রয়োজন।"
        }

        return CycleStatistics(
            recordedPeriodCount = sortedStarts.size,
            individualCycleLengths = cycleLengths,
            averageLengthDays = avg,
            medianLengthDays = median,
            minLengthDays = minLen,
            maxLengthDays = maxLen,
            variabilityRangeDays = range,
            standardDeviationDays = sd,
            lastPeriodStartEpochDay = lastStart,
            estimatedWindowStartEpochDay = windowStart,
            estimatedWindowEndEpochDay = windowEnd,
            confidence = confidence,
            uncertaintyNoteEn = noteEn,
            uncertaintyNoteBn = noteBn,
        )
    }
}
