package com.example.core

import com.example.data.SafetyAssessment
import com.example.data.SafetyTier
import com.example.data.SymptomEntry
import com.example.data.SymptomSeverity

/**
 * Deterministic Clinical Rules Engine (PRD Section 11 & Section 14.6).
 * Takes strict precedence over conversational AI when escalation triggers fire.
 * Three pathways:
 * - RED: Urgent Medical Attention (emergency services, never substitute appointment booking)
 * - AMBER: Professional Consultation Recommended (refer to BondhuHelp, avoid diagnosing)
 * - GREEN: General Guidance (approved educational content + source + limitations)
 */
object ClinicalRulesEngine {

    private val redPatterns = listOf(
        "faint", "fainting", "passed out", "unconscious", "collapse",
        "dizzy", "dizziness", "lightheaded",
        "very heavy bleeding", "soaking", "flooding", "bleeding through pad", "clots every hour",
        "severe pain", "unbearable pain", "sharp abdominal", "severe pelvic", "excruciating",
        "pregnant and bleeding", "pregnancy bleeding", "ectopic", "positive test and pain",
        "fever and pelvic", "high fever", "chest pain", "shortness of breath",
        // Bangla emergency cues
        "অজ্ঞান", "মাথা ঘোরা", "অতিরিক্ত রক্তপাত", "তীব্র ব্যথা", "অসহ্য ব্যথা", "গর্ভাবস্থায় রক্তপাত", "শ্বাসকষ্ট"
    )

    private val amberPatterns = listOf(
        "repeated unusual bleeding", "bleeding between periods", "spotting for weeks",
        "persistent pelvic", "pain for weeks", "chronic pain",
        "unusual discharge", "foul smell", "fishy", "green discharge", "yellow discharge",
        "itching", "burning", "vulvar pain", "soreness", "recurring irritation",
        "missed period", "no period for", "irregular period", "irregular cycle", "3 months", "months without",
        "fertility", "cannot conceive", "trying to conceive", "pregnant?", "am i pregnant",
        "diagnose", "do i have pcos", "prescribe", "medicine", "medication", "dosage", "tablet", "pill dose",
        "ultrasound result", "lab report", "blood test result", "hormone level",
        // Bangla amber cues
        "অনিয়মিত মাসিক", "মাসিক বন্ধ", "সাদা স্রাব", "চুলকানি", "জ্বালাপোড়া", "দুর্গন্ধ",
        "ডাক্তার", "ওষুধ", "চিকিৎসা", "রিপোর্ট", "পিসিওএস আছে কিনা", "সন্তান"
    )

    fun evaluateMessage(
        userMessage: String,
        recentSymptoms: List<SymptomEntry> = emptyList(),
    ): SafetyAssessment {
        val text = userMessage.lowercase().trim()

        // 1. Check RED urgent medical cues first (strict precedence)
        val hasRedKeyword = redPatterns.any { text.contains(it) } ||
            (text.contains("heavy bleeding") && (text.contains("weak") || text.contains("dizzy") || text.contains("faint"))) ||
            (text.contains("pregnan") && (text.contains("bleed") || text.contains("severe")))

        if (hasRedKeyword) {
            return SafetyAssessment(
                tier = SafetyTier.RED,
                reasonEn = "Your message describes potentially urgent symptoms (such as severe pain, fainting/dizziness with heavy bleeding, or pregnancy-related bleeding). Please seek urgent in-person medical care or call Bangladesh National Emergency Service (999) immediately.",
                reasonBn = "আপনার বর্ণনায় জরুরি চিকিৎসার প্রয়োজন হতে পারে এমন লক্ষণ রয়েছে (যেমন তীব্র ব্যথা, অতিরিক্ত রক্তপাতের সাথে মাথা ঘোরা/অজ্ঞান হওয়া বা গর্ভাবস্থায় রক্তপাত)। অনুগ্রহ করে দেরি না করে নিকটস্থ হাসপাতালের জরুরি বিভাগে যান অথবা জাতীয় জরুরি সেবা ৯৯৯-এ কল করুন।",
                recommendBondhuHelp = false,
                requireEmergency = true,
            )
        }

        // 2. Check AMBER clinical consultation triggers or prohibited diagnosis/prescription asks
        val hasAmberKeyword = amberPatterns.any { text.contains(it) }
        val hasRecentSevereSymptoms = recentSymptoms.take(5).any {
            it.severity == SymptomSeverity.SEVERE.name &&
                (text.contains("symptom") || text.contains("pain") || text.contains("bleed") || text.contains("summary") || text.contains("লক্ষণ"))
        }

        if (hasAmberKeyword || hasRecentSevereSymptoms) {
            return SafetyAssessment(
                tier = SafetyTier.AMBER,
                reasonEn = "This concern involves persistent, uncertain, or clinical questions that require assessment by a qualified gynaecologist. NariBondhu AI cannot diagnose conditions, interpret lab results, or recommend medications.",
                reasonBn = "এই বিষয়টি নিশ্চিত হতে বা সঠিক পরামর্শ পেতে একজন যোগ্য স্ত্রীরোগ বিশেষজ্ঞের সরাসরি মূল্যায়ন প্রয়োজন। নারীবন্ধু AI রোগ নির্ণয়, রিপোর্ট ব্যাখ্যা বা ওষুধের পরামর্শ দিতে পারে না।",
                recommendBondhuHelp = true,
                requireEmergency = false,
            )
        }

        // 3. Default GREEN educational pathway
        return SafetyAssessment(
            tier = SafetyTier.GREEN,
            reasonEn = "General educational guidance based on clinically reviewed women's health resources.",
            reasonBn = "চিকিৎসক-পর্যালোচিত নারী স্বাস্থ্য তথ্যের ভিত্তিতে সাধারণ শিক্ষামূলক নির্দেশনা।",
            recommendBondhuHelp = false,
            requireEmergency = false,
        )
    }

    /**
     * Evaluate recent user-logged symptoms proactively on the dashboard / symptom journal
     * so essential safety guidance is always accessible (PRD Section 17.2).
     */
    fun evaluateRecentLogs(symptoms: List<SymptomEntry>): SafetyTier {
        val recent = symptoms.take(10)
        val severeCount = recent.count { it.severity == SymptomSeverity.SEVERE.name }
        val hasUrgentText = recent.any { entry ->
            val combined = "${entry.symptomType} ${entry.notes}".lowercase()
            redPatterns.any { combined.contains(it) }
        }
        return when {
            hasUrgentText -> SafetyTier.RED
            severeCount >= 2 -> SafetyTier.AMBER
            else -> SafetyTier.GREEN
        }
    }
}
