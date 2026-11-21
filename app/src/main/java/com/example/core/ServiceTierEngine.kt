package com.example.core

import com.example.data.AppointmentBooking
import com.example.data.ConsultationType
import com.example.data.HealthRecord
import com.example.data.HealthRecordType
import com.example.data.ServiceTier
import com.example.data.SymptomCategory
import com.example.data.SymptomEntry
import com.example.data.UserProfile

enum class ConsultationAccessMode {
    NONE,
    ONLINE_OR_OFFLINE,
    ONLINE_AND_OFFLINE,
}

data class TierEntitlements(
    val tier: ServiceTier,
    val hasPeriodTracker: Boolean,
    val weeklyFreeAiTokens: Int?,
    val hasUnlimitedAiTokens: Boolean,
    val hasPersonalisedPcosPmsCare: Boolean,
    val hasCycleAnalysis: Boolean,
    val consultationAccess: ConsultationAccessMode,
    val hasEPrescription: Boolean,
    val hasLifestyleGuide: Boolean,
    val hasAlwaysOnPrivacy: Boolean = true,
) {
    val canBookConsultation: Boolean
        get() = consultationAccess != ConsultationAccessMode.NONE

    fun canUseConsultationType(type: ConsultationType, preferredMode: ConsultationType): Boolean =
        when (consultationAccess) {
            ConsultationAccessMode.NONE -> false
            ConsultationAccessMode.ONLINE_OR_OFFLINE -> type == preferredMode
            ConsultationAccessMode.ONLINE_AND_OFFLINE -> true
        }
}

data class WeeklyTokenStatus(
    val tokensRemaining: Int,
    val resetEpochDay: Long,
    val needsFirestoreSync: Boolean,
)

data class TierFeatureSummary(
    val tier: ServiceTier,
    val enTagline: String,
    val bnTagline: String,
    val enBullets: List<String>,
    val bnBullets: List<String>,
)

data class LifestyleHabitGuidance(
    val id: String,
    val focusTag: String,
    val enTitle: String,
    val bnTitle: String,
    val enAction: String,
    val bnAction: String,
    val enWhyItHelps: String,
    val bnWhyItHelps: String,
)

object ServiceTierEngine {
    const val FREE_WEEKLY_AI_TOKENS = 10
    const val TOKEN_RESET_INTERVAL_DAYS = 7L

    fun entitlementsFor(tier: ServiceTier): TierEntitlements = when (tier) {
        ServiceTier.FREE -> TierEntitlements(
            tier = ServiceTier.FREE,
            hasPeriodTracker = true,
            weeklyFreeAiTokens = FREE_WEEKLY_AI_TOKENS,
            hasUnlimitedAiTokens = false,
            hasPersonalisedPcosPmsCare = false,
            hasCycleAnalysis = false,
            consultationAccess = ConsultationAccessMode.NONE,
            hasEPrescription = false,
            hasLifestyleGuide = false,
            hasAlwaysOnPrivacy = true,
        )

        ServiceTier.TIER_1 -> TierEntitlements(
            tier = ServiceTier.TIER_1,
            hasPeriodTracker = true,
            weeklyFreeAiTokens = null,
            hasUnlimitedAiTokens = true,
            hasPersonalisedPcosPmsCare = true,
            hasCycleAnalysis = true,
            consultationAccess = ConsultationAccessMode.NONE,
            hasEPrescription = false,
            hasLifestyleGuide = false,
            hasAlwaysOnPrivacy = true,
        )

        ServiceTier.TIER_2 -> TierEntitlements(
            tier = ServiceTier.TIER_2,
            hasPeriodTracker = true,
            weeklyFreeAiTokens = null,
            hasUnlimitedAiTokens = true,
            hasPersonalisedPcosPmsCare = true,
            hasCycleAnalysis = true,
            consultationAccess = ConsultationAccessMode.ONLINE_OR_OFFLINE,
            hasEPrescription = false,
            hasLifestyleGuide = false,
            hasAlwaysOnPrivacy = true,
        )

        ServiceTier.TIER_3 -> TierEntitlements(
            tier = ServiceTier.TIER_3,
            hasPeriodTracker = true,
            weeklyFreeAiTokens = null,
            hasUnlimitedAiTokens = true,
            hasPersonalisedPcosPmsCare = true,
            hasCycleAnalysis = true,
            consultationAccess = ConsultationAccessMode.ONLINE_AND_OFFLINE,
            hasEPrescription = true,
            hasLifestyleGuide = true,
            hasAlwaysOnPrivacy = true,
        )
    }

    fun resolveWeeklyTokenStatus(
        profile: UserProfile?,
        todayEpochDay: Long = EpochDateUtil.todayEpochDay(),
    ): WeeklyTokenStatus {
        val currentReset = profile?.weeklyTokenResetEpochDay ?: 0L
        val storedRemaining = (profile?.weeklyAiTokensRemaining ?: FREE_WEEKLY_AI_TOKENS.toLong())
            .toInt()
            .coerceIn(0, FREE_WEEKLY_AI_TOKENS)

        return if (profile != null && (currentReset <= 0L || (todayEpochDay - currentReset) >= TOKEN_RESET_INTERVAL_DAYS)) {
            WeeklyTokenStatus(
                tokensRemaining = FREE_WEEKLY_AI_TOKENS,
                resetEpochDay = todayEpochDay,
                needsFirestoreSync = true,
            )
        } else {
            WeeklyTokenStatus(
                tokensRemaining = storedRemaining,
                resetEpochDay = if (currentReset > 0L) currentReset else todayEpochDay,
                needsFirestoreSync = false,
            )
        }
    }

    val tierCatalog: List<TierFeatureSummary> = listOf(
        TierFeatureSummary(
            tier = ServiceTier.FREE,
            enTagline = "Essential period tracking & weekly AI starter access",
            bnTagline = "প্রয়োজনীয় মাসিক ট্র্যাকার ও সাপ্তাহিক ফ্রি AI সুবিধা",
            enBullets = listOf(
                "Period tracker (start/end calendar & flow logs)",
                "10 free AI chatbot tokens every week",
                "Clinician-reviewed Health Library & Emergency 999 safety alerts",
                "Full Privacy Controls included (Discreet Mode, PIN Lock, Data Export)",
            ),
            bnBullets = listOf(
                "মাসিক ট্র্যাকার (শুরু-শেষ ক্যালেন্ডার ও ফ্লো লগ)",
                "প্রতি সপ্তাহে ১০টি ফ্রি AI চ্যাটবট টোকেন",
                "ডাক্তার-পর্যালোচিত স্বাস্থ্য পাঠাগার ও জরুরি ৯৯৯ সতর্কতা",
                "সম্পূর্ণ প্রাইভেসি সুরক্ষা অন্তর্ভুক্ত (গোপনীয় মোড, পিন লক, ডেটা এক্সপোর্ট)",
            ),
        ),
        TierFeatureSummary(
            tier = ServiceTier.TIER_1,
            enTagline = "Unlimited AI guidance + deeper cycle & PCOS/PMS insights",
            bnTagline = "আনলিমিটেড AI সহায়ক + গভীর চক্র বিশ্লেষণ ও PCOS/PMS যত্ন",
            enBullets = listOf(
                "Everything in Free Tier",
                "Unlimited tokens in NariBondhu AI chatbot",
                "Personalised PCOS & PMS care (daily marker tracker, patterns & visit prep)",
                "Cycle Analysis (min-max range, variability, confidence & predicted window)",
                "Full Privacy Controls always included",
            ),
            bnBullets = listOf(
                "ফ্রি প্ল্যানের সকল সুবিধা",
                "নারীবন্ধু AI চ্যাটবটে আনলিমিটেড টোকেন",
                "ব্যক্তিগত PCOS ও PMS যত্ন (দৈনিক ফর্ম, ধারা বিশ্লেষণ ও ভিজিট প্রস্তুতি)",
                "চক্র বিশ্লেষণ (সময়সীমা, পরিবর্তনশীলতা, নিশ্চয়তা ও সম্ভাব্য তারিখ)",
                "সকল প্রাইভেসি নিয়ন্ত্রণ সর্বদা অন্তর্ভুক্ত",
            ),
        ),
        TierFeatureSummary(
            tier = ServiceTier.TIER_2,
            enTagline = "Direct care with a verified gynaecologist (Online OR In-Person)",
            bnTagline = "যাচাইকৃত স্ত্রীরোগ বিশেষজ্ঞের পরামর্শ (অনলাইন অথবা সরাসরি চেম্বার)",
            enBullets = listOf(
                "Everything in Tier 1 (Unlimited AI + PCOS/PMS + Cycle Analysis)",
                "Book Online Video/Audio OR Offline In-Person consultation on BondhuHelp",
                "Consent-controlled sharing of health records with your selected doctor",
                "Full Privacy Controls always included",
            ),
            bnBullets = listOf(
                "টিয়ার ১-এর সকল সুবিধা (আনলিমিটেড AI + PCOS/PMS + চক্র বিশ্লেষণ)",
                "বন্ধুহেল্প-এ অনলাইন অথবা সরাসরি চেম্বার কনসালটেশন বুকিং",
                "কেবল আপনার সম্মতিতে ডাক্তারের সাথে স্বাস্থ্য রেকর্ড শেয়ার",
                "সকল প্রাইভেসি নিয়ন্ত্রণ সর্বদা অন্তর্ভুক্ত",
            ),
        ),
        TierFeatureSummary(
            tier = ServiceTier.TIER_3,
            enTagline = "All-in-one Online + Offline care, E-Prescription & Lifestyle Guide",
            bnTagline = "অনলাইন ও চেম্বার উভয় পরামর্শ, ই-প্রেসক্রিপশন এবং জীবনযাত্রা গাইড",
            enBullets = listOf(
                "Everything in Tier 2",
                "BOTH Online AND Offline doctor consultations unlocked together",
                "Doctor E-Prescription vault (structured Rx notes, dosage & follow-up export)",
                "Personalised Lifestyle Guide (Bangladesh-friendly nutrition, sleep & movement plan)",
                "Full Privacy Controls always included",
            ),
            bnBullets = listOf(
                "টিয়ার ২-এর সকল সুবিধা",
                "অনলাইন এবং সরাসরি চেম্বার—উভয় ধরনের কনসালটেশন একসাথে",
                "ডাক্তার ই-প্রেসক্রিপশন ভল্ট (ওষুধের নিয়ম, ফলো-আপ ও শেয়ার সুবিধা)",
                "ব্যক্তিগত জীবনযাত্রা গাইড (দেশীয় খাবার, ঘুম, ব্যায়াম ও মানসিক যত্ন পরিকল্পনা)",
                "সকল প্রাইভেসি নিয়ন্ত্রণ সর্বদা অন্তর্ভুক্ত",
            ),
        ),
    )

    fun personalisedLifestyleGuide(
        healthInterests: List<String>,
        symptoms: List<SymptomEntry>,
    ): List<LifestyleHabitGuidance> {
        val hasPcosSignals = healthInterests.any { it.contains("PCOS", ignoreCase = true) } ||
            symptoms.any { it.category == SymptomCategory.PCOS.name }
        val hasPmsSignals = healthInterests.any { it.contains("PMS", ignoreCase = true) } ||
            symptoms.any { it.category == SymptomCategory.PMS.name }

        val core = mutableListOf(
            LifestyleHabitGuidance(
                id = "nutrition_gi",
                focusTag = if (hasPcosSignals) "PCOS Insulin & Hormone Balance" else "Balanced Cycle Nutrition",
                enTitle = "Low-GI Deshi Plate Method",
                bnTitle = "স্বাস্থ্যকর ও সুষম দেশীয় খাবার",
                enAction = "Pair brown/red rice or 1–2 whole-wheat ruti with dal, leafy shak, eggs/fish, and 1 tbsp soaked chia or flaxseed daily.",
                bnAction = "লাল চালের ভাত বা ১-২টি লাল আটার রুটির সাথে ডাল, শাকসবজি, ডিম/মাছ এবং প্রতিদিন ১ চামচ চিয়া বা তিসি বীজ রাখুন।",
                enWhyItHelps = "Slow-release carbohydrates help steady blood sugar spikes linked to fatigue, acne, and hormonal fluctuations.",
                bnWhyItHelps = "রক্তের শর্করা স্থিতিশীল রাখতে সাহায্য করে, যা ক্লান্তি, ব্রণ ও হরমোনের ওঠানামা কমাতে সহায়ক।",
            ),
            LifestyleHabitGuidance(
                id = "movement_routine",
                focusTag = if (hasPcosSignals) "Metabolic & Cycle Rhythm" else "Cramp & Mood Relief",
                enTitle = "30-Minute Gentle Movement & Strength",
                bnTitle = "প্রতিদিন ৩০ মিনিট হালকা হাঁটা ও স্ট্রেচিং",
                enAction = "Brisk walk for 20–30 minutes after your largest meal or do 15 minutes of pelvic floor & lower-back stretching.",
                bnAction = "দুপুর বা রাতের খাবারের পর ২০-৩০ মিনিট হাঁটুন অথবা ১৫ মিনিট কোমর ও পেলভিক স্ট্রেচিং করুন।",
                enWhyItHelps = "Improves insulin sensitivity, reduces prostaglandin cramp intensity, and supports steadier energy.",
                bnWhyItHelps = "শরীরের ইনসুলিন সংবেদনশীলতা বাড়ায়, পিরিয়ডের ব্যথা কমায় এবং কর্মশক্তি ঠিক রাখে।",
            ),
            LifestyleHabitGuidance(
                id = "sleep_circadian",
                focusTag = if (hasPmsSignals) "PMS Mood & Cortisol Reset" else "Restorative Hormone Recovery",
                enTitle = "Consistent 7–8 Hour Sleep Anchor",
                bnTitle = "প্রতিদিন ৭-৮ ঘণ্টা নিয়মিত ঘুম",
                enAction = "Dim phone screens 30 minutes before bed, sip warm ginger-tulsi or plain warm water, and keep wake times within ±30 mins.",
                bnAction = "ঘুমানোর ৩০ মিনিট আগে ফোন সরিয়ে রাখুন এবং প্রতিদিন প্রায় একই সময়ে ঘুমাতে ও উঠতে চেষ্টা করুন।",
                enWhyItHelps = "Sleep regularity lowers evening cortisol surges that can worsen pre-menstrual anxiety and irregular ovulation.",
                bnWhyItHelps = "নিয়মিত ঘুম মানসিক চাপের হরমোন কমায় এবং মাসিকের আগে খিটখিটে ভাব বা অনিয়ম সামলাতে সাহায্য করে।",
            ),
            LifestyleHabitGuidance(
                id = "hydration_iron",
                focusTag = "Hydration & Iron Support",
                enTitle = "8–10 Glasses Hydration + Iron Boost",
                bnTitle = "পর্যাপ্ত পানি ও আয়রনসমৃদ্ধ খাবার",
                enAction = "Aim for 8+ glasses of water daily; add lemon (Vitamin C) over spinach, kochu shak, or lentils during heavy-flow days.",
                bnAction = "দিনে ৮ গ্লাস বা তার বেশি পানি পান করুন; ভারী ফ্লো-এর দিনগুলোতে শাক বা ডালের সাথে লেবু রাখুন।",
                enWhyItHelps = "Vitamin C boosts plant iron absorption to prevent post-period dizziness and afternoon fatigue.",
                bnWhyItHelps = "ভিটামিন-সি খাবারের আয়রন শোষণে সাহায্য করে, ফলে মাসিকের পরে মাথা ঘোরা বা দুর্বলতা কমে।",
            ),
        )
        return core
    }

    fun buildClinicianEPrescriptionRecord(
        userId: String,
        booking: AppointmentBooking,
        issuedDateText: String,
        clinicalImpression: String,
        regimenNotes: String,
        followUpInstructions: String,
    ): HealthRecord {
        val details = buildString {
            appendLine("Issued after BMDC Consultation (${booking.consultationType} • ${booking.slotLabel})")
            appendLine("Clinician: ${booking.doctorName} (${booking.clinicName}, ${booking.city})")
            if (clinicalImpression.isNotBlank()) {
                appendLine("Clinical Impression: ${clinicalImpression.trim()}")
            }
            if (regimenNotes.isNotBlank()) {
                appendLine("Prescribed Plan & Dosage: ${regimenNotes.trim()}")
            }
            if (followUpInstructions.isNotBlank()) {
                appendLine("Follow-up & Red Flags: ${followUpInstructions.trim()}")
            }
            append("Note: Always follow your BMDC-registered doctor's verified prescription instructions.")
        }
        return HealthRecord(
            userId = userId,
            recordType = HealthRecordType.E_PRESCRIPTION.name,
            title = "E-Rx • ${booking.doctorName}",
            dateText = issuedDateText,
            details = details.take(1450),
            verifiedByUser = true,
            sharedWithDoctorIds = listOf(booking.doctorId).filter { it.isNotBlank() },
        )
    }
}
