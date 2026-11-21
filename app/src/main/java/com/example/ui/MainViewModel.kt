package com.example.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.ClinicalRulesEngine
import com.example.core.CycleCalculationEngine
import com.example.core.CycleStatistics
import com.example.core.EpochDateUtil
import com.example.core.ServiceTierEngine
import com.example.core.TierEntitlements
import com.example.data.AppLanguage
import com.example.data.AppointmentBooking
import com.example.data.BookingStatus
import com.example.data.ChatMessage
import com.example.data.ConsentLog
import com.example.data.ConsentType
import com.example.data.ConsultationType
import com.example.data.DoctorProfile
import com.example.data.ExtractedReportPreview
import com.example.data.HealthRecord
import com.example.data.HealthRecordType
import com.example.data.MenstrualPeriod
import com.example.data.NariBondhuAiRepository
import com.example.data.NariBondhuRepository
import com.example.data.SafetyTier
import com.example.data.ServiceTier
import com.example.data.SymptomEntry
import com.example.data.UserProfile
import com.example.data.WellnessLog
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface AiStreamState {
    data object Idle : AiStreamState
    data object Loading : AiStreamState
    data class Streaming(val text: String) : AiStreamState
    data class Error(val message: String) : AiStreamState
}

sealed interface ExtractionState {
    data object Idle : ExtractionState
    data object Loading : ExtractionState
    data class PreviewReady(val preview: ExtractedReportPreview) : ExtractionState
    data class Error(val message: String) : ExtractionState
}

sealed interface SummaryPrepState {
    data object Idle : SummaryPrepState
    data object Loading : SummaryPrepState
    data class Ready(val summary: String, val questions: List<String>) : SummaryPrepState
    data class Error(val message: String) : SummaryPrepState
}

class MainViewModel(
    private val repository: NariBondhuRepository,
    private val aiRepository: NariBondhuAiRepository,
    private val currentUserId: String,
) : ViewModel() {

    private companion object {
        const val TAG = "NariBondhuVM"
        const val STOP_TIMEOUT = 5000L
    }

    private fun <T> observeWrapped(
        flow: kotlinx.coroutines.flow.Flow<T>,
        fallbackError: String,
    ): StateFlow<UiState<T>> = flow
        .map<T, UiState<T>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val root = firestoreError ?: error
            Log.w(TAG, fallbackError, root)
            emit(UiState.Error(root.message ?: fallbackError))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            initialValue = UiState.Loading
        )

    val profileState: StateFlow<UiState<UserProfile?>> =
        observeWrapped(repository.observeProfile(currentUserId), "Failed to load profile")

    val periodsState: StateFlow<UiState<List<MenstrualPeriod>>> =
        observeWrapped(repository.observePeriods(currentUserId), "Failed to load menstrual history")

    val symptomsState: StateFlow<UiState<List<SymptomEntry>>> =
        observeWrapped(repository.observeSymptoms(currentUserId), "Failed to load symptoms")

    val wellnessState: StateFlow<UiState<List<WellnessLog>>> =
        observeWrapped(repository.observeWellnessLogs(currentUserId), "Failed to load wellness logs")

    val recordsState: StateFlow<UiState<List<HealthRecord>>> =
        observeWrapped(repository.observeHealthRecords(currentUserId), "Failed to load health records")

    val bookingsState: StateFlow<UiState<List<AppointmentBooking>>> =
        observeWrapped(repository.observeBookings(currentUserId), "Failed to load appointments")

    val consentLogsState: StateFlow<UiState<List<ConsentLog>>> =
        observeWrapped(repository.observeConsentLogs(currentUserId), "Failed to load consent logs")

    val cycleStats: StateFlow<CycleStatistics> = periodsState
        .map { state ->
            val periods = (state as? UiState.Success)?.data.orEmpty()
            CycleCalculationEngine.calculate(periods)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            initialValue = CycleCalculationEngine.calculate(emptyList())
        )

    val recentSymptomSafetyTier: StateFlow<SafetyTier> = symptomsState
        .map { state ->
            val symptoms = (state as? UiState.Success)?.data.orEmpty()
            ClinicalRulesEngine.evaluateRecentLogs(symptoms)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            initialValue = SafetyTier.GREEN
        )

    val activeServiceTier: StateFlow<ServiceTier> = profileState
        .map { state ->
            val raw = (state as? UiState.Success)?.data?.serviceTier
            ServiceTier.fromCode(raw)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            initialValue = ServiceTier.FREE
        )

    val tierEntitlements: StateFlow<TierEntitlements> = activeServiceTier
        .map { ServiceTierEngine.entitlementsFor(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            initialValue = ServiceTierEngine.entitlementsFor(ServiceTier.FREE)
        )

    val weeklyTokensRemaining: StateFlow<Int> = profileState
        .map { state ->
            val profile = (state as? UiState.Success)?.data
            ServiceTierEngine.resolveWeeklyTokenStatus(profile).tokensRemaining
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            initialValue = ServiceTierEngine.FREE_WEEKLY_AI_TOKENS
        )

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiStreamState = MutableStateFlow<AiStreamState>(AiStreamState.Idle)
    val aiStreamState: StateFlow<AiStreamState> = _aiStreamState.asStateFlow()

    private val _extractionState = MutableStateFlow<ExtractionState>(ExtractionState.Idle)
    val extractionState: StateFlow<ExtractionState> = _extractionState.asStateFlow()

    private val _summaryPrepState = MutableStateFlow<SummaryPrepState>(SummaryPrepState.Idle)
    val summaryPrepState: StateFlow<SummaryPrepState> = _summaryPrepState.asStateFlow()

    private var aiJob: Job? = null
    private var lastAutoResetEpochDay: Long? = null

    init {
        viewModelScope.launch {
            profileState.collect { state ->
                val profile = (state as? UiState.Success)?.data ?: return@collect
                if (!profile.onboardingCompleted) return@collect
                val tokenStatus = ServiceTierEngine.resolveWeeklyTokenStatus(profile)
                if (tokenStatus.needsFirestoreSync && lastAutoResetEpochDay != tokenStatus.resetEpochDay) {
                    lastAutoResetEpochDay = tokenStatus.resetEpochDay
                    repository.syncWeeklyTokens(
                        remaining = tokenStatus.tokensRemaining.toLong(),
                        resetEpochDay = tokenStatus.resetEpochDay,
                    )
                }
            }
        }
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun completeOnboarding(
        displayName: String,
        language: AppLanguage,
        city: String,
        ageConfirmed18Plus: Boolean,
        healthInterests: List<String>,
        aiConsentGranted: Boolean,
        remindersEnabled: Boolean,
        cloudSyncConsentGranted: Boolean,
    ) {
        viewModelScope.launch {
            val profile = UserProfile(
                userId = currentUserId,
                displayName = displayName.ifBlank { "Friend" },
                preferredLanguage = language.code,
                city = city.ifBlank { "Dhaka" },
                ageConfirmed18Plus = ageConfirmed18Plus,
                healthInterests = healthInterests,
                discreetMode = false,
                onboardingCompleted = true,
                remindersEnabled = remindersEnabled,
                aiConsentGranted = aiConsentGranted,
                cloudSyncConsentGranted = cloudSyncConsentGranted,
                serviceTier = ServiceTier.FREE.code,
                weeklyAiTokensRemaining = ServiceTierEngine.FREE_WEEKLY_AI_TOKENS.toLong(),
                weeklyTokenResetEpochDay = EpochDateUtil.todayEpochDay(),
                preferredConsultMode = ConsultationType.ONLINE.name,
            )
            val res = repository.saveInitialProfile(profile)
            if (res.isSuccess) {
                repository.logConsent(
                    ConsentType.PRIVACY_POLICY,
                    cloudSyncConsentGranted,
                    if (cloudSyncConsentGranted) "Encrypted private health storage consented" else "Minimal storage preference selected"
                )
                repository.logConsent(
                    ConsentType.AI_DATA_PROCESSING,
                    aiConsentGranted,
                    if (aiConsentGranted) "Opted in to NariBondhu AI educational queries" else "Declined AI processing during onboarding"
                )
                repository.logConsent(
                    ConsentType.REMINDERS,
                    remindersEnabled,
                    if (remindersEnabled) "Enabled discreet reminders" else "Reminders disabled"
                )
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to save onboarding."
            }
        }
    }

    fun selectServiceTier(tier: ServiceTier) {
        viewModelScope.launch {
            val res = repository.updateServiceTier(tier)
            _statusBanner.value = if (res.isSuccess) {
                "${tier.enName} (${tier.enPriceLabel}) activated. Privacy remains fully enabled."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Could not switch care plan."
            }
        }
    }

    fun setPreferredConsultMode(mode: ConsultationType) {
        viewModelScope.launch {
            val res = repository.updatePreferredConsultMode(mode)
            _statusBanner.value = if (res.isSuccess) {
                "Consultation preference updated to ${mode.enLabel}."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Could not update consultation format."
            }
        }
    }

    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            val res = repository.updateProfileFields(mapOf("preferredLanguage" to language.code))
            if (res.isFailure) {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
            }
        }
    }

    fun toggleDiscreetMode(enabled: Boolean) {
        viewModelScope.launch {
            val res = repository.updateProfileFields(mapOf("discreetMode" to enabled))
            if (res.isFailure) {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
            }
        }
    }

    fun updateConsent(type: ConsentType, granted: Boolean) {
        viewModelScope.launch {
            val fieldMap = when (type) {
                ConsentType.AI_DATA_PROCESSING -> mapOf("aiConsentGranted" to granted)
                ConsentType.REMINDERS -> mapOf("remindersEnabled" to granted)
                ConsentType.PRIVACY_POLICY -> mapOf("cloudSyncConsentGranted" to granted)
                else -> emptyMap()
            }
            if (fieldMap.isNotEmpty()) {
                val updateRes = repository.updateProfileFields(fieldMap)
                if (updateRes.isFailure) {
                    _statusBanner.value = updateRes.exceptionOrNull()?.localizedMessage ?: "Could not update consent."
                    return@launch
                }
            }
            repository.logConsent(
                consentType = type,
                granted = granted,
                summary = if (granted) "Consent granted by user" else "Consent withdrawn by user"
            )
            _statusBanner.value = if (granted) "Consent updated." else "Consent withdrawn."
        }
    }

    fun savePeriod(period: MenstrualPeriod) {
        viewModelScope.launch {
            val res = repository.savePeriod(period)
            _statusBanner.value = if (res.isSuccess) {
                "Cycle entry saved."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to save cycle entry."
            }
        }
    }

    fun deletePeriod(periodId: String) {
        viewModelScope.launch {
            val res = repository.deletePeriod(periodId)
            if (res.isFailure) _statusBanner.value = res.exceptionOrNull()?.localizedMessage
        }
    }

    fun addSymptom(entry: SymptomEntry) {
        viewModelScope.launch {
            val res = repository.addSymptom(entry)
            _statusBanner.value = if (res.isSuccess) {
                "Symptom logged."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to save symptom."
            }
        }
    }

    fun addPcosDailyCheckIn(
        entries: List<SymptomEntry>,
        onResult: (Result<Int>) -> Unit = {},
    ) {
        viewModelScope.launch {
            val res = repository.addSymptomsBatch(entries)
            _statusBanner.value = if (res.isSuccess) {
                val count = res.getOrDefault(entries.size)
                if (count == 1) "Saved 1 PCOS symptom marker." else "Saved $count PCOS symptom markers."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to save PCOS check-in."
            }
            onResult(res)
        }
    }

    fun deleteSymptom(symptomId: String) {
        viewModelScope.launch {
            val res = repository.deleteSymptom(symptomId)
            if (res.isFailure) _statusBanner.value = res.exceptionOrNull()?.localizedMessage
        }
    }

    fun addWellnessLog(log: WellnessLog) {
        viewModelScope.launch {
            val res = repository.addWellnessLog(log)
            _statusBanner.value = if (res.isSuccess) {
                "Wellness check-in saved."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to save wellness log."
            }
        }
    }

    fun addHealthRecord(record: HealthRecord) {
        viewModelScope.launch {
            val res = repository.addHealthRecord(record)
            if (res.isSuccess) {
                _extractionState.value = ExtractionState.Idle
                _statusBanner.value = "Health record saved securely."
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to save record."
            }
        }
    }

    fun deleteHealthRecord(recordId: String) {
        viewModelScope.launch {
            val res = repository.deleteHealthRecord(recordId)
            if (res.isFailure) _statusBanner.value = res.exceptionOrNull()?.localizedMessage
        }
    }

    fun extractReportFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _extractionState.value = ExtractionState.Loading
            try {
                val preview = aiRepository.extractHealthReportPreview(bitmap)
                _extractionState.value = ExtractionState.PreviewReady(preview)
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Report extraction timed out", e)
                _extractionState.value = ExtractionState.Error("Document scan timed out. Please enter details manually.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Report extraction failed", e)
                _extractionState.value = ExtractionState.Error(e.localizedMessage ?: "Could not scan document.")
            }
        }
    }

    fun resetExtractionState() {
        _extractionState.value = ExtractionState.Idle
    }

    fun bookAppointment(
        doctor: DoctorProfile,
        consultationType: String,
        slotLabel: String,
        shareHealthSummary: Boolean,
        selectedRecordIds: List<String>,
        notesForDoctor: String,
    ) {
        viewModelScope.launch {
            val profile = (profileState.value as? UiState.Success)?.data
            val tier = ServiceTier.fromCode(profile?.serviceTier)
            val entitlements = ServiceTierEngine.entitlementsFor(tier)
            val consultEnum = ConsultationType.fromName(consultationType)
            val preferredEnum = ConsultationType.fromName(profile?.preferredConsultMode)
            if (!entitlements.canUseConsultationType(consultEnum, preferredEnum)) {
                _statusBanner.value = if (!entitlements.canBookConsultation) {
                    "Upgrade to Tier 2 (BDT 450/mo) or Tier 3 (BDT 900/mo) to book consultations."
                } else {
                    "Tier 2 supports ${preferredEnum.enLabel}. Switch mode or upgrade to Tier 3 for both."
                }
                return@launch
            }
            val fee = if (consultationType == "ONLINE") doctor.onlineFeeBdt.toLong() else doctor.inPersonFeeBdt.toLong()
            val booking = AppointmentBooking(
                userId = currentUserId,
                doctorId = doctor.doctorId,
                doctorName = doctor.name,
                specialty = doctor.specialty,
                clinicName = doctor.clinicName,
                city = doctor.city,
                consultationType = consultationType,
                slotLabel = slotLabel,
                feeBdt = fee,
                status = BookingStatus.CONFIRMED.name,
                sharedSummaryGranted = shareHealthSummary,
                sharedRecordCount = if (shareHealthSummary) selectedRecordIds.size.toLong() else 0L,
                notesForDoctor = notesForDoctor,
            )
            val res = repository.createBooking(booking)
            if (res.isSuccess) {
                if (shareHealthSummary) {
                    val allRecords = (recordsState.value as? UiState.Success)?.data.orEmpty()
                    selectedRecordIds.forEach { recId ->
                        val rec = allRecords.firstOrNull { it.id == recId }
                        if (rec != null && !rec.sharedWithDoctorIds.contains(doctor.doctorId)) {
                            repository.updateHealthRecordSharing(
                                recId,
                                rec.sharedWithDoctorIds + doctor.doctorId
                            )
                        }
                    }
                    repository.logConsent(
                        ConsentType.DOCTOR_RECORD_SHARING,
                        true,
                        "Explicitly shared ${selectedRecordIds.size} health record(s) with ${doctor.name}"
                    )
                }
                _statusBanner.value = "Appointment confirmed with ${doctor.name} ($slotLabel)."
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage ?: "Booking failed."
            }
        }
    }

    fun saveClinicianEPrescription(
        booking: AppointmentBooking,
        issuedDateText: String,
        clinicalImpression: String,
        regimenNotes: String,
        followUpInstructions: String,
    ) {
        viewModelScope.launch {
            if (!tierEntitlements.value.hasEPrescription) {
                _statusBanner.value = "E-Prescription access is included in Tier 3 (BDT 900/month)."
                return@launch
            }
            val record = ServiceTierEngine.buildClinicianEPrescriptionRecord(
                userId = currentUserId,
                booking = booking,
                issuedDateText = issuedDateText,
                clinicalImpression = clinicalImpression,
                regimenNotes = regimenNotes,
                followUpInstructions = followUpInstructions,
            )
            val res = repository.addHealthRecord(record)
            _statusBanner.value = if (res.isSuccess) {
                "Verified E-Prescription saved to your Tier 3 Health Records."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to save e-prescription."
            }
        }
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus, slotLabel: String) {
        viewModelScope.launch {
            val res = repository.updateBookingStatus(bookingId, status, slotLabel)
            _statusBanner.value = if (res.isSuccess) {
                "Appointment updated (${status.enLabel})."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Could not update appointment."
            }
        }
    }

    fun sendAiMessage(
        userText: String,
        language: AppLanguage,
        includeDeidentifiedSummary: Boolean,
    ) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        val recentSymptoms = (symptomsState.value as? UiState.Success)?.data.orEmpty()
        val assessment = ClinicalRulesEngine.evaluateMessage(trimmed, recentSymptoms)

        val currentProfile = (profileState.value as? UiState.Success)?.data
        val entitlements = ServiceTierEngine.entitlementsFor(ServiceTier.fromCode(currentProfile?.serviceTier))
        val tokenStatus = ServiceTierEngine.resolveWeeklyTokenStatus(currentProfile)

        // Enforce weekly 10-token limit on Free Tier (emergency RED triage stays free & immediate!)
        if (assessment.tier != SafetyTier.RED && !entitlements.hasUnlimitedAiTokens && tokenStatus.tokensRemaining <= 0) {
            _statusBanner.value = if (language == AppLanguage.BN) {
                "এই সপ্তাহের ১০টি ফ্রি AI টোকেন শেষ হয়েছে। আনলিমিটেড AI-এর জন্য টিয়ার ১ (৳২০০/মাস) চালু করুন।"
            } else {
                "All 10 free weekly AI tokens are used. Upgrade to Tier 1 (BDT 200/mo) for unlimited AI."
            }
            return
        }

        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            isUser = true,
            text = trimmed,
            safetyTier = assessment.tier,
        )
        _chatMessages.value = _chatMessages.value + userMsg

        // RED Urgent Medical Escalation: deterministic immediate guidance takes precedence; never paywalled!
        if (assessment.tier == SafetyTier.RED) {
            val urgentBody = if (language == AppLanguage.BN) {
                "${assessment.reasonBn}\n\n• বাংলাদেশে জরুরি নম্বরে কল করুন: ৯৯৯\n• নিকটস্থ হাসপাতালের জরুরি বিভাগে সরাসরি যান।\n• জরুরি অবস্থায় সাধারণ অ্যাপয়েন্টমেন্ট বা চ্যাটের জন্য অপেক্ষা করবেন না।"
            } else {
                "${assessment.reasonEn}\n\n• Call Bangladesh National Emergency: 999\n• Go directly to the nearest hospital emergency department.\n• Do not wait for a routine appointment booking or lengthy chat."
            }
            val urgentMsg = ChatMessage(
                id = "ai_${System.currentTimeMillis()}",
                isUser = false,
                text = urgentBody,
                safetyTier = SafetyTier.RED,
                sourcesCited = listOf("NariBondhu Clinical Safety Protocol"),
                showBondhuHelpCta = false,
            )
            _chatMessages.value = _chatMessages.value + urgentMsg
            _aiStreamState.value = AiStreamState.Idle
            return
        }

        val optionalSummary = if (includeDeidentifiedSummary) {
            buildDeidentifiedSummaryText()
        } else null

        aiJob?.cancel()
        aiJob = viewModelScope.launch {
            _aiStreamState.value = AiStreamState.Loading
            var lastText = ""
            try {
                aiRepository.streamEducationalResponse(
                    userMessage = trimmed,
                    language = language,
                    safetyAssessment = assessment,
                    optionalContextSummary = optionalSummary,
                ).collect { chunk ->
                    lastText = chunk
                    _aiStreamState.value = AiStreamState.Streaming(chunk)
                }

                val finalText = if (assessment.tier == SafetyTier.AMBER) {
                    val notice = if (language == AppLanguage.BN) assessment.reasonBn else assessment.reasonEn
                    "$notice\n\n$lastText"
                } else {
                    lastText
                }

                val aiMsg = ChatMessage(
                    id = "ai_${System.currentTimeMillis()}",
                    isUser = false,
                    text = finalText,
                    safetyTier = assessment.tier,
                    sourcesCited = listOf("NariBondhu Approved Library", "WHO / RCOG / NICE"),
                    showBondhuHelpCta = assessment.recommendBondhuHelp,
                )
                _chatMessages.value = _chatMessages.value + aiMsg
                _aiStreamState.value = AiStreamState.Idle

                if (!entitlements.hasUnlimitedAiTokens) {
                    val updatedTokens = (tokenStatus.tokensRemaining - 1).coerceAtLeast(0).toLong()
                    repository.syncWeeklyTokens(
                        remaining = updatedTokens,
                        resetEpochDay = tokenStatus.resetEpochDay,
                    )
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI stream timed out", e)
                _aiStreamState.value = AiStreamState.Error("NariBondhu AI timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI stream failed", e)
                _aiStreamState.value = AiStreamState.Error(e.localizedMessage ?: "Failed to get response from NariBondhu AI.")
            }
        }
    }

    fun generatePreConsultationSummary(language: AppLanguage) {
        viewModelScope.launch {
            _summaryPrepState.value = SummaryPrepState.Loading
            try {
                val facts = buildDeidentifiedSummaryText()
                val (summary, questions) = aiRepository.generateConsultationSummary(facts, language)
                _summaryPrepState.value = SummaryPrepState.Ready(summary, questions)
            } catch (e: TimeoutCancellationException) {
                _summaryPrepState.value = SummaryPrepState.Error("Summary preparation timed out.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Consultation summary failed", e)
                _summaryPrepState.value = SummaryPrepState.Error(e.localizedMessage ?: "Could not prepare summary.")
            }
        }
    }

    fun buildDeidentifiedSummaryText(): String {
        val stats = cycleStats.value
        val periods = (periodsState.value as? UiState.Success)?.data.orEmpty().take(6)
        val symptoms = (symptomsState.value as? UiState.Success)?.data.orEmpty().take(12)
        val records = (recordsState.value as? UiState.Success)?.data.orEmpty().take(10)
        return buildString {
            appendLine("Recorded periods: ${stats.recordedPeriodCount}")
            if (stats.individualCycleLengths.isNotEmpty()) {
                appendLine("Cycle lengths (days): ${stats.individualCycleLengths.joinToString(", ")}")
                appendLine("Min/Max: ${stats.minLengthDays}-${stats.maxLengthDays} days, Median: ${stats.medianLengthDays}")
            }
            if (periods.isNotEmpty()) {
                appendLine("Recent bleedings:")
                periods.forEach { p ->
                    appendLine("- Start ${EpochDateUtil.formatShortDate(p.startDateEpochDay)}, Flow: ${p.flowIntensity}, SpottingOnly: ${p.spottingOnly}")
                }
            }
            if (symptoms.isNotEmpty()) {
                appendLine("Recent symptoms:")
                symptoms.forEach { s ->
                    appendLine("- ${EpochDateUtil.formatShortDate(s.dateEpochDay)} [${s.category}] ${s.symptomType} (${s.severity})")
                }
            }
            if (records.isNotEmpty()) {
                appendLine("Personal records (meds/allergies/notes):")
                records.forEach { r ->
                    appendLine("- [${r.recordType}] ${r.title} (${r.dateText}): ${r.details.take(160)}")
                }
            }
        }
    }

    fun exportAllDataJson(): String {
        val profile = (profileState.value as? UiState.Success)?.data
        val periods = (periodsState.value as? UiState.Success)?.data.orEmpty()
        val symptoms = (symptomsState.value as? UiState.Success)?.data.orEmpty()
        val wellness = (wellnessState.value as? UiState.Success)?.data.orEmpty()
        val records = (recordsState.value as? UiState.Success)?.data.orEmpty()
        val bookings = (bookingsState.value as? UiState.Success)?.data.orEmpty()
        viewModelScope.launch {
            repository.logConsent(ConsentType.DATA_EXPORT, true, "User exported personal health records")
        }
        return repository.buildExportJson(profile, periods, symptoms, wellness, records, bookings)
    }

    fun deleteAllDataAndSignOut(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val res = repository.deleteAllUserData()
            if (res.isSuccess) {
                onDeleted()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to delete account data."
            }
        }
    }
}
