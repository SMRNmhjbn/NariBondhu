package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

enum class AppLanguage(val code: String, val label: String, val nativeLabel: String) {
    EN("EN", "English", "English"),
    BN("BN", "Bangla", "বাংলা");

    companion object {
        fun fromCode(raw: String?): AppLanguage =
            entries.firstOrNull { it.code == raw } ?: EN
    }
}

enum class FlowIntensity(val enLabel: String, val bnLabel: String) {
    SPOTTING("Spotting", "হালকা দাগ (স্পটিং)"),
    LIGHT("Light", "হালকা"),
    MEDIUM("Medium", "মাঝারি"),
    HEAVY("Heavy", "ভারী");

    companion object {
        fun fromName(raw: String?): FlowIntensity =
            entries.firstOrNull { it.name == raw } ?: MEDIUM
    }
}

enum class SymptomCategory(val enLabel: String, val bnLabel: String) {
    MENSTRUAL("Menstrual", "মাসিক"),
    PCOS("PCOS", "পিসিওএস"),
    PMS("PMS", "পিএমএস"),
    INTIMATE("Intimate Health", "গোপন ও প্রজনন স্বাস্থ্য"),
    GENERAL("General", "সাধারণ");

    companion object {
        fun fromName(raw: String?): SymptomCategory =
            entries.firstOrNull { it.name == raw } ?: GENERAL
    }
}

enum class SymptomSeverity(val enLabel: String, val bnLabel: String) {
    MILD("Mild", "হালকা"),
    MODERATE("Moderate", "মাঝারি"),
    SEVERE("Severe", "তীব্র");

    companion object {
        fun fromName(raw: String?): SymptomSeverity =
            entries.firstOrNull { it.name == raw } ?: MILD
    }
}

enum class WellnessMood(val enLabel: String, val bnLabel: String) {
    CALM("Calm", "শান্ত"),
    HAPPY("Happy", "খুশি"),
    LOW("Low", "মন খারাপ"),
    ANXIOUS("Anxious", "উদ্বিগ্ন"),
    IRRITABLE("Irritable", "খিটখিটে"),
    TIRED("Tired", "ক্লান্ত");

    companion object {
        fun fromName(raw: String?): WellnessMood =
            entries.firstOrNull { it.name == raw } ?: CALM
    }
}

enum class HealthRecordType(val enLabel: String, val bnLabel: String) {
    DIAGNOSIS("Diagnosis", "রোগ নির্ণয়"),
    MEDICATION("Medication", "ওষুধ"),
    ALLERGY("Allergy", "অ্যালার্জি"),
    LAB_REPORT("Lab Report", "ল্যাব রিপোর্ট"),
    ULTRASOUND("Ultrasound", "আল্ট্রাসাউন্ড"),
    CONSULTATION("Previous Consultation", "পূর্বের পরামর্শ"),
    NOTE("Personal Note", "ব্যক্তিগত নোট");

    companion object {
        fun fromName(raw: String?): HealthRecordType =
            entries.firstOrNull { it.name == raw } ?: NOTE
    }
}

enum class ConsultationType(val enLabel: String, val bnLabel: String) {
    ONLINE("Online (Video/Audio)", "অনলাইন (ভিডিও/অডিও)"),
    IN_PERSON("In-Person Clinic", "সরাসরি চেম্বার");

    companion object {
        fun fromName(raw: String?): ConsultationType =
            entries.firstOrNull { it.name == raw } ?: ONLINE
    }
}

enum class BookingStatus(val enLabel: String, val bnLabel: String) {
    CONFIRMED("Confirmed", "নিশ্চিত"),
    RESCHEDULE_REQUESTED("Reschedule Requested", "সময় পরিবর্তনের অনুরোধ"),
    CANCELLED("Cancelled", "বাতিল"),
    COMPLETED("Completed", "সম্পন্ন");

    companion object {
        fun fromName(raw: String?): BookingStatus =
            entries.firstOrNull { it.name == raw } ?: CONFIRMED
    }
}

enum class ConsentType(val enLabel: String, val bnLabel: String) {
    PRIVACY_POLICY("Privacy & Health Storage", "প্রাইভেসি ও স্বাস্থ্য তথ্য সংরক্ষণ"),
    AI_DATA_PROCESSING("NariBondhu AI Assistant", "নারীবন্ধু AI সহায়ক"),
    DOCTOR_RECORD_SHARING("Doctor Record Sharing", "ডাক্তারের সাথে তথ্য শেয়ার"),
    REMINDERS("Discreet Reminders", "গোপনীয় রিমাইন্ডার"),
    DATA_EXPORT("Personal Data Export", "ব্যক্তিগত তথ্য এক্সপোর্ট");

    companion object {
        fun fromName(raw: String?): ConsentType =
            entries.firstOrNull { it.name == raw } ?: PRIVACY_POLICY
    }
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val preferredLanguage: String = "EN",
    val city: String = "Dhaka",
    val ageConfirmed18Plus: Boolean = false,
    val healthInterests: List<String> = emptyList(),
    val discreetMode: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val remindersEnabled: Boolean = false,
    val aiConsentGranted: Boolean = false,
    val cloudSyncConsentGranted: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
) {
    fun toCreateMap(): Map<String, Any> {
        val safeName = displayName.trim().take(80).ifEmpty { "User" }
        val safeCity = city.trim().take(50)
        return mapOf(
            "userId" to userId,
            "displayName" to safeName,
            "preferredLanguage" to if (preferredLanguage == "BN") "BN" else "EN",
            "city" to safeCity,
            "ageConfirmed18Plus" to ageConfirmed18Plus,
            "healthInterests" to healthInterests.take(10),
            "discreetMode" to discreetMode,
            "onboardingCompleted" to onboardingCompleted,
            "remindersEnabled" to remindersEnabled,
            "aiConsentGranted" to aiConsentGranted,
            "cloudSyncConsentGranted" to cloudSyncConsentGranted,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
        )
    }
}

@Keep
@IgnoreExtraProperties
data class MenstrualPeriod(
    @DocumentId val id: String = "",
    val userId: String = "",
    val startDateEpochDay: Long = 0L,
    val endDateEpochDay: Long? = null,
    val flowIntensity: String = FlowIntensity.MEDIUM.name,
    val spottingOnly: Boolean = false,
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
) {
    fun toCreateMap(): Map<String, Any> {
        val raw = mutableMapOf<String, Any?>(
            "userId" to userId,
            "startDateEpochDay" to startDateEpochDay.coerceIn(0L, 100000L),
            "endDateEpochDay" to endDateEpochDay?.coerceIn(startDateEpochDay, 100000L),
            "flowIntensity" to FlowIntensity.fromName(flowIntensity).name,
            "spottingOnly" to spottingOnly,
            "notes" to notes.trim().take(500),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        @Suppress("UNCHECKED_CAST")
        return raw.filterValues { it != null } as Map<String, Any>
    }
}

@Keep
@IgnoreExtraProperties
data class SymptomEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val dateEpochDay: Long = 0L,
    val category: String = SymptomCategory.GENERAL.name,
    val symptomType: String = "",
    val severity: String = SymptomSeverity.MILD.name,
    val durationText: String = "",
    val frequencyText: String = "",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "dateEpochDay" to dateEpochDay.coerceIn(0L, 100000L),
        "category" to SymptomCategory.fromName(category).name,
        "symptomType" to symptomType.trim().take(60).ifEmpty { "General discomfort" },
        "severity" to SymptomSeverity.fromName(severity).name,
        "durationText" to durationText.trim().take(60),
        "frequencyText" to frequencyText.trim().take(60),
        "notes" to notes.trim().take(500),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
    )
}

@Keep
@IgnoreExtraProperties
data class WellnessLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val dateEpochDay: Long = 0L,
    val sleepHours: Double = 7.0,
    val mood: String = WellnessMood.CALM.name,
    val stressLevel: Long = 2L,
    val energyLevel: Long = 3L,
    val activityMinutes: Long = 20L,
    val hydrationGlasses: Long = 6L,
    val nutritionNotes: String = "",
    val wellnessGoal: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "dateEpochDay" to dateEpochDay.coerceIn(0L, 100000L),
        "sleepHours" to sleepHours.coerceIn(0.0, 24.0),
        "mood" to WellnessMood.fromName(mood).name,
        "stressLevel" to stressLevel.coerceIn(1L, 5L),
        "energyLevel" to energyLevel.coerceIn(1L, 5L),
        "activityMinutes" to activityMinutes.coerceIn(0L, 600L),
        "hydrationGlasses" to hydrationGlasses.coerceIn(0L, 30L),
        "nutritionNotes" to nutritionNotes.trim().take(300),
        "wellnessGoal" to wellnessGoal.trim().take(200),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
    )
}

@Keep
@IgnoreExtraProperties
data class HealthRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val recordType: String = HealthRecordType.NOTE.name,
    val title: String = "",
    val dateText: String = "",
    val details: String = "",
    val verifiedByUser: Boolean = true,
    val sharedWithDoctorIds: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "recordType" to HealthRecordType.fromName(recordType).name,
        "title" to title.trim().take(120).ifEmpty { "Health Record" },
        "dateText" to dateText.trim().take(40),
        "details" to details.trim().take(1500),
        "verifiedByUser" to verifiedByUser,
        "sharedWithDoctorIds" to sharedWithDoctorIds.take(20),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
    )
}

@Keep
@IgnoreExtraProperties
data class AppointmentBooking(
    @DocumentId val id: String = "",
    val userId: String = "",
    val doctorId: String = "",
    val doctorName: String = "",
    val specialty: String = "",
    val clinicName: String = "",
    val city: String = "",
    val consultationType: String = ConsultationType.ONLINE.name,
    val slotLabel: String = "",
    val feeBdt: Long = 0L,
    val status: String = BookingStatus.CONFIRMED.name,
    val sharedSummaryGranted: Boolean = false,
    val sharedRecordCount: Long = 0L,
    val notesForDoctor: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null,
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "doctorId" to doctorId.trim().take(64),
        "doctorName" to doctorName.trim().take(100),
        "specialty" to specialty.trim().take(80),
        "clinicName" to clinicName.trim().take(120),
        "city" to city.trim().take(50),
        "consultationType" to ConsultationType.fromName(consultationType).name,
        "slotLabel" to slotLabel.trim().take(80),
        "feeBdt" to feeBdt.coerceIn(0L, 50000L),
        "status" to BookingStatus.CONFIRMED.name,
        "sharedSummaryGranted" to sharedSummaryGranted,
        "sharedRecordCount" to sharedRecordCount.coerceIn(0L, 100L),
        "notesForDoctor" to notesForDoctor.trim().take(500),
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp(),
    )
}

@Keep
@IgnoreExtraProperties
data class ConsentLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val consentType: String = ConsentType.PRIVACY_POLICY.name,
    val granted: Boolean = false,
    val summary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
) {
    fun toCreateMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "consentType" to ConsentType.fromName(consentType).name,
        "granted" to granted,
        "summary" to summary.trim().take(200).ifEmpty { "Consent updated" },
        "createdAt" to FieldValue.serverTimestamp(),
    )
}

// --- Static Domain Models for Governance, Education, and BondhuHelp ---

enum class SafetyTier {
    GREEN,
    AMBER,
    RED,
}

data class SafetyAssessment(
    val tier: SafetyTier,
    val reasonEn: String,
    val reasonBn: String,
    val recommendBondhuHelp: Boolean,
    val requireEmergency: Boolean,
)

data class EducationalArticle(
    val articleId: String,
    val category: String,
    val topicEn: String,
    val topicBn: String,
    val summaryEn: String,
    val summaryBn: String,
    val bodyEn: String,
    val bodyBn: String,
    val intendedAudience: String,
    val contentOwner: String,
    val clinicalReviewer: String,
    val supportingReferences: List<String>,
    val publicationDate: String,
    val reviewDate: String,
    val version: String,
    val limitationsEn: String,
    val limitationsBn: String,
    val escalationInstructionsEn: String,
    val escalationInstructionsBn: String,
)

data class DoctorProfile(
    val doctorId: String,
    val name: String,
    val nameBn: String,
    val qualifications: String,
    val bmdcNumber: String,
    val specialty: String,
    val expertiseTags: List<String>,
    val experienceYears: Int,
    val clinicName: String,
    val area: String,
    val city: String,
    val clinicDirections: String,
    val languages: List<String>,
    val offersOnline: Boolean,
    val offersInPerson: Boolean,
    val onlineFeeBdt: Int,
    val inPersonFeeBdt: Int,
    val availableSlots: List<String>,
    val verifiedIdentity: Boolean = true,
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val safetyTier: SafetyTier = SafetyTier.GREEN,
    val sourcesCited: List<String> = emptyList(),
    val showBondhuHelpCta: Boolean = false,
)

data class ExtractedReportPreview(
    val title: String,
    val recordType: HealthRecordType,
    val dateText: String,
    val details: String,
)
