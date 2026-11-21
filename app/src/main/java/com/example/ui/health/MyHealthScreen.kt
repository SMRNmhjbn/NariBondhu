package com.example.ui.health

import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.AppStrings
import com.example.core.CycleStatistics
import com.example.core.EpochDateUtil
import com.example.core.TierEntitlements
import com.example.core.rememberTtsSpeaker
import com.example.data.AppLanguage
import com.example.data.AppointmentBooking
import com.example.data.ClinicalContentCatalog
import com.example.data.EducationalArticle
import com.example.data.FlowIntensity
import com.example.data.HealthRecord
import com.example.data.HealthRecordType
import com.example.data.MenstrualPeriod
import com.example.data.ServiceTier
import com.example.data.SymptomCategory
import com.example.data.SymptomEntry
import com.example.data.SymptomSeverity
import com.example.data.WellnessLog
import com.example.data.WellnessMood
import com.example.ui.ExtractionState
import com.example.ui.SummaryPrepState
import com.example.ui.home.HealthSubTab
import com.example.ui.profile.TierLockedFeatureCard
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SafetyAmberContainer
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyGreenContainer
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MyHealthScreen(
    selectedTab: HealthSubTab,
    onSelectTab: (HealthSubTab) -> Unit,
    language: AppLanguage,
    discreetMode: Boolean = false,
    entitlements: TierEntitlements,
    healthInterests: List<String>,
    periods: List<MenstrualPeriod>,
    cycleStats: CycleStatistics,
    symptoms: List<SymptomEntry>,
    wellnessLogs: List<WellnessLog>,
    healthRecords: List<HealthRecord>,
    bookings: List<AppointmentBooking>,
    extractionState: ExtractionState,
    summaryPrepState: SummaryPrepState,
    aiConsentGranted: Boolean,
    onOpenPlans: () -> Unit,
    onSavePeriod: (MenstrualPeriod) -> Unit,
    onOpenPeriodDialog: (MenstrualPeriod?, Long) -> Unit,
    onDeletePeriod: (String) -> Unit,
    onOpenSymptomDialog: (SymptomCategory) -> Unit,
    onSavePcosCheckIn: (List<SymptomEntry>, (Result<Int>) -> Unit) -> Unit,
    onDeleteSymptom: (String) -> Unit,
    onOpenWellnessDialog: () -> Unit,
    onOpenRecordDialog: () -> Unit,
    onDeleteRecord: (String) -> Unit,
    onScanReportBitmap: (android.graphics.Bitmap) -> Unit,
    onSaveScannedRecord: (HealthRecord) -> Unit,
    onResetExtraction: () -> Unit,
    onGenerateAppointmentSummary: () -> Unit,
    onBuildPlainSummaryText: () -> String,
    onSaveClinicianEPrescription: (AppointmentBooking, String, String, String, String) -> Unit,
    onOpenBondhuHelp: () -> Unit,
    onAskAiTopic: (String) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    var openArticle by remember { mutableStateOf<EducationalArticle?>(null) }
    var showManualRecordDialog by remember { mutableStateOf(false) }

    if (openArticle != null) {
        BackHandler { openArticle = null }
        ArticleDetailScreen(
            article = openArticle!!,
            language = language,
            onBack = { openArticle = null },
            onAskAi = {
                val topic = if (isBn) openArticle!!.topicBn else openArticle!!.topicEn
                openArticle = null
                onAskAiTopic(topic)
            },
            onOpenBondhuHelp = {
                openArticle = null
                onOpenBondhuHelp()
            },
        )
        return
    }

    if (showManualRecordDialog || extractionState is ExtractionState.PreviewReady) {
        val preview = (extractionState as? ExtractionState.PreviewReady)?.preview
        AddOrVerifyHealthRecordDialog(
            preview = preview,
            language = language,
            onDismiss = {
                showManualRecordDialog = false
                onResetExtraction()
            },
            onSave = { rec ->
                showManualRecordDialog = false
                onSaveScannedRecord(rec)
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal sub-navigation chips inside My Health
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val tabItems = listOf(
                HealthSubTab.CYCLE to (if (isBn) "চক্র ও লক্ষণ" else "Cycle & Symptoms"),
                HealthSubTab.PCOS_PMS to (if (isBn) "পিসিওএস ও পিএমএস" else "PCOS & PMS"),
                HealthSubTab.WELLNESS to (if (isBn) "সুস্থতা" else "Wellness"),
                HealthSubTab.LIFESTYLE_RX to (if (isBn) "জীবনযাত্রা ও Rx" else "Lifestyle & Rx"),
                HealthSubTab.LIBRARY to (if (isBn) "স্বাস্থ্য পাঠাগার" else "Health Library"),
                HealthSubTab.RECORDS to (if (isBn) "স্বাস্থ্য রেকর্ড" else "Health Records"),
            )
            tabItems.forEach { (tab, label) ->
                FilterChip(
                    selected = selectedTab == tab,
                    onClick = { onSelectTab(tab) },
                    label = { Text(label) },
                    modifier = Modifier.testTag("subtab_${tab.name.lowercase()}"),
                )
            }
        }

        when (selectedTab) {
            HealthSubTab.CYCLE -> CycleAndSymptomTab(
                language = language,
                discreetMode = discreetMode,
                hasCycleAnalysis = entitlements.hasCycleAnalysis,
                periods = periods,
                cycleStats = cycleStats,
                symptoms = symptoms,
                onOpenPlans = onOpenPlans,
                onSavePeriod = onSavePeriod,
                onDeletePeriod = onDeletePeriod,
                onOpenSymptomDialog = onOpenSymptomDialog,
                onDeleteSymptom = onDeleteSymptom,
            )
            HealthSubTab.PCOS_PMS -> PcosAndPmsTab(
                language = language,
                hasPersonalisedPcosPmsCare = entitlements.hasPersonalisedPcosPmsCare,
                cycleStats = cycleStats,
                symptoms = symptoms,
                summaryPrepState = summaryPrepState,
                aiConsentGranted = aiConsentGranted,
                onOpenPlans = onOpenPlans,
                onSavePcosCheckIn = onSavePcosCheckIn,
                onDeleteSymptom = onDeleteSymptom,
                onLogPcosOrPmsSymptom = { cat -> onOpenSymptomDialog(cat) },
                onGenerateSummary = onGenerateAppointmentSummary,
                onOpenBondhuHelp = onOpenBondhuHelp,
                onOpenArticle = { openArticle = it },
            )
            HealthSubTab.WELLNESS -> WellnessTab(
                language = language,
                logs = wellnessLogs,
                onAddLog = onOpenWellnessDialog,
            )
            HealthSubTab.LIFESTYLE_RX -> LifestyleAndRxTab(
                language = language,
                entitlements = entitlements,
                healthInterests = healthInterests,
                symptoms = symptoms,
                healthRecords = healthRecords,
                bookings = bookings,
                onOpenPlans = onOpenPlans,
                onOpenWellnessDialog = onOpenWellnessDialog,
                onOpenBondhuHelp = onOpenBondhuHelp,
                onSaveClinicianEPrescription = onSaveClinicianEPrescription,
                onDeleteRecord = onDeleteRecord,
            )
            HealthSubTab.LIBRARY -> LibraryTab(
                language = language,
                onOpenArticle = { openArticle = it },
            )
            HealthSubTab.RECORDS -> RecordsTab(
                language = language,
                records = healthRecords,
                extractionState = extractionState,
                aiConsentGranted = aiConsentGranted,
                onAddManual = { showManualRecordDialog = true },
                onScanBitmap = onScanReportBitmap,
                onDeleteRecord = onDeleteRecord,
                onBuildPlainSummary = onBuildPlainSummaryText,
            )
        }
    }
}

@Composable
private fun CycleAndSymptomTab(
    language: AppLanguage,
    discreetMode: Boolean,
    hasCycleAnalysis: Boolean,
    periods: List<MenstrualPeriod>,
    cycleStats: CycleStatistics,
    symptoms: List<SymptomEntry>,
    onOpenPlans: () -> Unit,
    onSavePeriod: (MenstrualPeriod) -> Unit,
    onDeletePeriod: (String) -> Unit,
    onOpenSymptomDialog: (SymptomCategory) -> Unit,
    onDeleteSymptom: (String) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    val calendarState = rememberCycleCalendarState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedCategoryFilter by remember { mutableStateOf<SymptomCategory?>(null) }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Interactive Compose State-Managed Cycle Calendar Card
        item {
            InteractiveCycleCalendarCard(
                state = calendarState,
                periods = periods,
                cycleStats = cycleStats,
                isBn = isBn,
                discreetMode = discreetMode,
                onDayClick = { epochDay ->
                    calendarState.onDayClick(epochDay, periods)
                },
            )
        }

        // Inline Start/End Date & Flow Composer Sheet (animated on selection or edit)
        item {
            CycleEntryComposerCard(
                state = calendarState,
                isBn = isBn,
                discreetMode = discreetMode,
                onSave = onSavePeriod,
                onDelete = onDeletePeriod,
                onCancel = { calendarState.cancelSelection() },
            )
        }

        // Action Buttons
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        calendarState.startNewLogMode()
                        coroutineScope.launch { listState.animateScrollToItem(0) }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        when {
                            discreetMode && isBn -> "তারিখ লিখুন"
                            discreetMode -> "Log Dates"
                            isBn -> "মাসিক/স্পটিং"
                            else -> "Log Period"
                        }
                    )
                }
                OutlinedButton(
                    onClick = { onOpenSymptomDialog(SymptomCategory.MENSTRUAL) },
                    modifier = Modifier.weight(1f).height(50.dp),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (isBn) "লক্ষণ লিখুন" else "Log Symptom")
                }
            }
        }

        // Cycle Statistics & Analysis Card (Tier 1+)
        item {
            if (hasCycleAnalysis) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (isBn) "চক্রের পরিসংখ্যান ও অনিশ্চয়তা তথ্য" else "Cycle Statistics & Prediction Uncertainty",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        if (cycleStats.individualCycleLengths.isNotEmpty()) {
                            Text(
                                text = if (isBn) {
                                    "পূর্বের চক্রসমূহ (দিন): ${cycleStats.individualCycleLengths.joinToString(", ")}"
                                } else {
                                    "Individual cycle lengths: ${cycleStats.individualCycleLengths.joinToString(", ")} days"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                StatPill(if (isBn) "গড়" else "Average", "${cycleStats.averageLengthDays?.roundToInt()}d")
                                StatPill(if (isBn) "মধ্যক" else "Median", "${cycleStats.medianLengthDays?.roundToInt()}d")
                                StatPill(if (isBn) "সীমা" else "Min–Max", "${cycleStats.minLengthDays}–${cycleStats.maxLengthDays}d")
                                StatPill(if (isBn) "পার্থক্য" else "Variability", "±${cycleStats.variabilityRangeDays}d")
                            }
                        }
                        Surface(color = Lavender, contentColor = InkPrimary, shape = RoundedCornerShape(12.dp)) {
                            Text(
                                text = if (isBn) cycleStats.uncertaintyNoteBn else cycleStats.uncertaintyNoteEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = InkPrimary,
                                modifier = Modifier.padding(10.dp),
                            )
                        }
                    }
                }
            } else {
                TierLockedFeatureCard(
                    requiredTier = ServiceTier.TIER_1,
                    title = if (isBn) "চক্র বিশ্লেষণ ও পূর্বাভাস আনলক করুন" else "Unlock Full Cycle Analysis",
                    description = if (isBn) {
                        "ফ্রি প্ল্যানে মাসিক ক্যালেন্ডার ও তারিখ সংরক্ষণ সবসময় চালু আছে। গড় চক্রের দৈর্ঘ্য, পরিবর্তনশীলতা এবং পরবর্তী সম্ভাব্য তারিখ দেখতে টিয়ার ১ (৳২০০/মাস) বেছে নিন।"
                    } else {
                        "Your Period Calendar & logging are always free. Upgrade to Tier 1 (BDT 200/month) for full cycle variability stats, median calculations, and next-window predictions."
                    },
                    highlights = if (isBn) {
                        listOf(
                            "সর্বনিম্ন–সর্বোচ্চ চক্রের পরিসর ও পরিবর্তনশীলতা",
                            "নারীবন্ধু AI চ্যাটবটে আনলিমিটেড টোকেন",
                            "ব্যক্তিগত PCOS ও PMS যত্ন অন্তর্ভুক্ত",
                        )
                    } else {
                        listOf(
                            "Median length, min-max range & variability trends",
                            "Unlimited NariBondhu AI chatbot tokens",
                            "Personalised PCOS & PMS care included",
                        )
                    },
                    language = language,
                    onOpenPlans = onOpenPlans,
                    testTag = "locked_cycle_analysis_card",
                )
            }
        }

        // Recorded Periods History (Editable)
        item {
            Text(
                if (isBn) "নথিভুক্ত মাসিক ও স্পটিং" else "Recorded Periods & Spotting",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        if (periods.isEmpty()) {
            item {
                EmptyTipCard(
                    if (isBn) "ক্যালেন্ডারের যেকোনো তারিখে ট্যাপ করে বা 'মাসিক/স্পটিং' বাটনে চাপ দিয়ে আপনার প্রথম তথ্য লিখুন।"
                    else "Tap any date on the calendar or tap Log Period to start tracking your cycle history."
                )
            }
        } else {
            items(periods, key = { it.id.ifBlank { "${it.startDateEpochDay}_${it.flowIntensity}" } }) { period ->
                val flowObj = FlowIntensity.fromName(period.flowIntensity)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val startText = EpochDateUtil.formatEpochDay(period.startDateEpochDay, isBn)
                            val endText = if (period.endDateEpochDay != null) {
                                val days = (period.endDateEpochDay - period.startDateEpochDay + 1).toInt().coerceAtLeast(1)
                                " → ${EpochDateUtil.formatEpochDay(period.endDateEpochDay, isBn)} ($days${if (isBn) " দিন" else "d"})"
                            } else {
                                if (isBn) " • চলমান" else " • Ongoing"
                            }
                            Text("$startText$endText", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = (if (isBn) flowObj.bnLabel else flowObj.enLabel) +
                                    if (period.notes.isNotBlank()) " • ${period.notes}" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(
                            onClick = {
                                calendarState.editPeriod(period)
                                coroutineScope.launch { listState.animateScrollToItem(0) }
                            }
                        ) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit entry")
                        }
                        IconButton(onClick = { onDeletePeriod(period.id) }) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete entry")
                        }
                    }
                }
            }
        }

        // Symptom & Private Intimate Diary
        item {
            Spacer(Modifier.height(4.dp))
            Text(
                if (isBn) "লক্ষণ ও গোপন স্বাস্থ্য ডায়েরি" else "Symptom & Private Intimate Diary",
                style = MaterialTheme.typography.titleMedium,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text(if (isBn) "সব" else "All") },
                )
                SymptomCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(if (isBn) cat.bnLabel else cat.enLabel) },
                    )
                }
            }
        }
        val filteredSymptoms = symptoms.filter {
            selectedCategoryFilter == null || it.category == selectedCategoryFilter?.name
        }
        if (filteredSymptoms.isEmpty()) {
            item {
                EmptyTipCard(
                    if (isBn) "এখনো কোনো লক্ষণ লেখা হয়নি। ব্যথা, ক্লান্তি, মেজাজ বা গোপন স্বাস্থ্য লক্ষণ নিরাপদে নোট করুন।"
                    else "No symptoms recorded in this category yet. Log cramps, mood, fatigue, or intimate observations privately."
                )
            }
        } else {
            items(filteredSymptoms, key = { it.id.ifBlank { "${it.dateEpochDay}_${it.symptomType}" } }) { sym ->
                val catObj = SymptomCategory.fromName(sym.category)
                val sevObj = SymptomSeverity.fromName(sym.severity)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${sym.symptomType} (${if (isBn) sevObj.bnLabel else sevObj.enLabel})",
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                "${EpochDateUtil.formatEpochDay(sym.dateEpochDay, isBn)} • ${if (isBn) catObj.bnLabel else catObj.enLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            if (sym.notes.isNotBlank()) {
                                Text(sym.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = { onDeleteSymptom(sym.id) }) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete symptom")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PcosAndPmsTab(
    language: AppLanguage,
    hasPersonalisedPcosPmsCare: Boolean,
    cycleStats: CycleStatistics,
    symptoms: List<SymptomEntry>,
    summaryPrepState: SummaryPrepState,
    aiConsentGranted: Boolean,
    onOpenPlans: () -> Unit,
    onSavePcosCheckIn: (List<SymptomEntry>, (Result<Int>) -> Unit) -> Unit,
    onDeleteSymptom: (String) -> Unit,
    onLogPcosOrPmsSymptom: (SymptomCategory) -> Unit,
    onGenerateSummary: () -> Unit,
    onOpenBondhuHelp: () -> Unit,
    onOpenArticle: (EducationalArticle) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    val pcosFormState = rememberDailyPcosFormState()
    val pcosOnlySymptoms = symptoms.filter { it.category == SymptomCategory.PCOS.name }
    val pcosPmsSymptoms = symptoms.filter {
        it.category == SymptomCategory.PCOS.name || it.category == SymptomCategory.PMS.name
    }
    val topRecurring = pcosPmsSymptoms.groupBy { it.symptomType }
        .entries.sortedByDescending { it.value.size }
        .take(4)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Surface(color = Lavender, contentColor = InkPrimary, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        if (isBn) "পিসিওএস এবং পিএমএস বিশেষ সহায়তা" else "PCOS & PMS Specialised Support",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepPlumDark,
                    )
                    Text(
                        if (isBn) "নারীবন্ধু কখনোই নিজে থেকে পিসিওএস নির্ণয় করে না বা ওষুধের পরামর্শ দেয় না, বরং আপনার লক্ষণের ধারা গুছিয়ে রাখতে এবং ডাক্তার দেখানোর প্রস্তুতিতে সাহায্য করে।"
                        else "NariBondhu never independently diagnoses PCOS or recommends prescriptions. It helps you organize symptom patterns and prepare confidently for your doctor.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkPrimary,
                    )
                }
            }
        }

        if (!hasPersonalisedPcosPmsCare) {
            item {
                TierLockedFeatureCard(
                    requiredTier = ServiceTier.TIER_1,
                    title = if (isBn) "ব্যক্তিগত PCOS ও PMS যত্ন আনলক করুন" else "Unlock Personalised PCOS & PMS Care",
                    description = if (isBn) {
                        "টিয়ার ১ (৳২০০/মাস) প্ল্যানে দৈনিক PCOS মার্কার ফর্ম (অতিরিক্ত লোম, ব্রণ, ক্লান্তি), পুনরাবৃত্ত লক্ষণ বিশ্লেষণ, ডাক্তার ভিজিট প্রস্তুতি এবং আনলিমিটেড AI টোকেন পাবেন।"
                    } else {
                        "Upgrade to Tier 1 (BDT 200/month) for your Daily PCOS Marker Check-In (hair growth, acne, fatigue), Recurring Pattern Recognition, Doctor Appointment Prep, and Unlimited AI Tokens."
                    },
                    highlights = if (isBn) {
                        listOf(
                            "দৈনিক PCOS লক্ষণ ফর্ম ও প্রবণতা",
                            "পুনরাবৃত্ত প্যাটার্ন ও চক্র বিশ্লেষণ",
                            "ডাক্তার দেখানোর প্রস্তুতি সারাংশ + আনলিমিটেড AI",
                        )
                    } else {
                        listOf(
                            "Daily PCOS Marker Form (Hair Growth, Acne, Fatigue) synced to Firestore",
                            "Recurring symptom pattern analysis & Cycle Analysis",
                            "Doctor Appointment Preparation & Unlimited AI Tokens",
                        )
                    },
                    language = language,
                    onOpenPlans = onOpenPlans,
                    testTag = "locked_pcos_pms_card",
                )
            }
        } else {
            // Dedicated Daily PCOS Marker Tracking Form (Hair growth, Acne, Fatigue, etc. -> Firestore)
            item {
                DailyPcosTrackerCard(
                    state = pcosFormState,
                    existingPcosEntries = pcosOnlySymptoms,
                    isBn = isBn,
                    onSubmit = { entries ->
                        pcosFormState.updateSubmitting(true)
                        onSavePcosCheckIn(entries) { result ->
                            pcosFormState.updateSubmitting(false)
                            if (result.isSuccess) {
                                pcosFormState.resetAfterSave()
                            } else {
                                pcosFormState.setError(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: if (isBn) "তথ্য সংরক্ষণ করা যায়নি, আবার চেষ্টা করুন।"
                                        else "Could not save check-in. Please try again."
                                )
                            }
                        }
                    },
                )
            }

            // Daily PCOS Marker Log History
            item {
                DailyPcosHistoryCard(
                    pcosEntries = pcosOnlySymptoms,
                    isBn = isBn,
                    onDeleteSymptom = onDeleteSymptom,
                )
            }

            item {
                OutlinedButton(
                    onClick = { onLogPcosOrPmsSymptom(SymptomCategory.PMS) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (isBn) "অতিরিক্ত PMS লক্ষণ লিখুন" else "Log Quick PMS Symptom")
                }
            }

            // Pattern Recognition Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (isBn) "আপনার পুনরাবৃত্ত লক্ষণের ধারা" else "Recurring Pattern Recognition",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (topRecurring.isEmpty()) {
                            Text(
                                if (isBn) "PCOS বা PMS লক্ষণ নথিভুক্ত করলে এখানে পুনরাবৃত্ত ধারা দেখা যাবে।"
                                else "Log PCOS or PMS entries over time to see recurring patterns here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            topRecurring.forEach { (symName, entries) ->
                                Text("• $symName — ${entries.size}x recorded", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        if (cycleStats.variabilityRangeDays != null) {
                            Text(
                                text = if (isBn) "চক্রের পার্থক্য: ±${cycleStats.variabilityRangeDays} দিন"
                                else "Recorded cycle variability: ±${cycleStats.variabilityRangeDays} days",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

            // Doctor Appointment Preparation Tool (PRD Section 5.1 & Automation Module H)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            if (isBn) "ডাক্তার দেখানোর প্রস্তুতি (Appointment Prep)" else "Doctor Appointment Preparation",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            if (isBn) "আপনার রেকর্ড করা মাসিক ও লক্ষণগুলো ক্রমানুসারে সাজিয়ে ডাক্তারকে দেখানোর সহজ প্রশ্ন তৈরি করুন।"
                            else "Organise your recorded cycles and symptoms into a doctor-ready chronological summary and helpful questions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (aiConsentGranted) {
                            Button(
                                onClick = onGenerateSummary,
                                enabled = summaryPrepState !is SummaryPrepState.Loading,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                if (summaryPrepState is SummaryPrepState.Loading) {
                                    CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text(if (isBn) "সারাংশ ও প্রশ্ন তৈরি করুন" else "Prepare Chronological Summary")
                            }
                        } else {
                            Text(
                                if (isBn) "প্রস্তুতি সারাংশ তৈরির জন্য প্রোফাইল থেকে NariBondhu AI সম্মতি চালু করুন।"
                                else "Enable NariBondhu AI consent in Profile to format an AI summary, or consult a doctor directly via BondhuHelp.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        when (summaryPrepState) {
                            is SummaryPrepState.Ready -> {
                                Surface(color = SafetyGreenContainer, contentColor = InkPrimary, shape = RoundedCornerShape(12.dp)) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(summaryPrepState.summary, style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                                        if (summaryPrepState.questions.isNotEmpty()) {
                                            Text(
                                                if (isBn) "ডাক্তারকে জিজ্ঞাসা করার প্রশ্ন:" else "Suggested Questions for Your Doctor:",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = SafetyGreen,
                                            )
                                            summaryPrepState.questions.forEach { q ->
                                                Text("• $q", style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                                            }
                                        }
                                    }
                                }
                            }
                            is SummaryPrepState.Error -> {
                                Text(summaryPrepState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            else -> Unit
                        }

                        OutlinedButton(onClick = onOpenBondhuHelp, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Rounded.LocalHospital, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (isBn) "BondhuHelp-এ বিশেষজ্ঞ খুঁজুন" else "Find a PCOS / Gynae Specialist on BondhuHelp")
                        }
                    }
                }
            }
        }

        // Relevant PCOS & PMS Articles (Always readable as educational library content)
        val pcosPmsArticles = ClinicalContentCatalog.articles.filter { it.category == "PCOS" || it.category == "PMS" }
        items(pcosPmsArticles, key = { it.articleId }) { article ->
            ArticlePreviewCard(article = article, isBn = isBn, onClick = { onOpenArticle(article) })
        }
    }
}

@Composable
private fun WellnessTab(
    language: AppLanguage,
    logs: List<WellnessLog>,
    onAddLog: () -> Unit,
) {
    val isBn = language == AppLanguage.BN
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Surface(color = Lavender, contentColor = InkPrimary, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isBn) "ব্যক্তিগত সুস্থতা ও জীবনযাত্রা" else "Personal Wellness Awareness",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepPlumDark,
                    )
                    Text(
                        if (isBn) "ঘুম, মেজাজ, মানসিক চাপ, শক্তি, হাঁটাচলা, পুষ্টি ও পানি পানের অভ্যাস পর্যবেক্ষণ করুন।"
                        else "Track sleep, mood, stress, energy, physical activity, nutrition, and hydration to support daily awareness.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkPrimary,
                    )
                    Button(
                        onClick = onAddLog,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepPlum, contentColor = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (isBn) "আজকের সুস্থতার নোট যোগ করুন" else "Add Daily Wellness Check-In")
                    }
                }
            }
        }

        if (logs.isEmpty()) {
            item {
                EmptyTipCard(
                    if (isBn) "এখনো কোনো সুস্থতার নোট নেই। আজকের ঘুম ও পানি পানের হিসাব দিয়ে শুরু করুন।"
                    else "No wellness logs yet. Tap above to record today's sleep, mood, energy, and hydration."
                )
            }
        } else {
            items(logs, key = { it.id.ifBlank { "${it.dateEpochDay}_${it.mood}" } }) { log ->
                val moodObj = WellnessMood.fromName(log.mood)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "${EpochDateUtil.formatEpochDay(log.dateEpochDay, isBn)} • ${if (isBn) moodObj.bnLabel else moodObj.enLabel}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            "Sleep: ${log.sleepHours}h • Water: ${log.hydrationGlasses} glasses • Activity: ${log.activityMinutes}m",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            "Stress: ${log.stressLevel}/5 • Energy: ${log.energyLevel}/5",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (log.nutritionNotes.isNotBlank()) {
                            Text(log.nutritionNotes, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryTab(
    language: AppLanguage,
    onOpenArticle: (EducationalArticle) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = remember(query, selectedCategory) {
        ClinicalContentCatalog.articles.filter { art ->
            val catMatch = selectedCategory == "All" || art.category.equals(selectedCategory, ignoreCase = true)
            val qMatch = query.isBlank() ||
                art.topicEn.contains(query, ignoreCase = true) ||
                art.topicBn.contains(query, ignoreCase = true) ||
                art.summaryEn.contains(query, ignoreCase = true) ||
                art.summaryBn.contains(query, ignoreCase = true)
            catMatch && qMatch
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(if (isBn) "বিষয় খুঁজুন (যেমন: PCOS, PMS, স্রাব)" else "Search topics (e.g., PCOS, PMS, discharge)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ClinicalContentCatalog.categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filtered, key = { it.articleId }) { article ->
                ArticlePreviewCard(article = article, isBn = isBn, onClick = { onOpenArticle(article) })
            }
        }
    }
}

@Composable
private fun ArticlePreviewCard(
    article: EducationalArticle,
    isBn: Boolean,
    onClick: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Surface(color = Lavender, contentColor = DeepPlumDark, shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = "${article.category} • v${article.version}",
                    style = MaterialTheme.typography.labelMedium,
                    color = DeepPlumDark,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }
            Text(if (isBn) article.topicBn else article.topicEn, style = MaterialTheme.typography.titleMedium)
            Text(
                if (isBn) article.summaryBn else article.summaryEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Reviewed by ${article.clinicalReviewer}",
                style = MaterialTheme.typography.labelMedium,
                color = SafetyGreen,
            )
        }
    }
}

@Composable
private fun ArticleDetailScreen(
    article: EducationalArticle,
    language: AppLanguage,
    onBack: () -> Unit,
    onAskAi: () -> Unit,
    onOpenBondhuHelp: () -> Unit,
) {
    val isBn = language == AppLanguage.BN
    val copy = AppStrings.of(language)
    val speaker = rememberTtsSpeaker()
    val title = if (isBn) article.topicBn else article.topicEn
    val body = if (isBn) article.bodyBn else article.bodyEn

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = copy.backBtn)
                Spacer(Modifier.width(4.dp))
                Text(copy.backBtn)
            }
            OutlinedButton(onClick = {
                if (speaker.isSpeaking) speaker.stop()
                else speaker.speak("$title. $body", language)
            }) {
                Icon(
                    if (speaker.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = copy.listenBtn
                )
                Spacer(Modifier.width(6.dp))
                Text(if (speaker.isSpeaking) copy.stopListenBtn else copy.listenBtn)
            }
        }

        Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        Text(body, style = MaterialTheme.typography.bodyLarge)

        Surface(color = SafetyAmberContainer, contentColor = InkPrimary, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    if (isBn) "সতর্কতা ও সীমাবদ্ধতা" else "Limitations & Escalation",
                    style = MaterialTheme.typography.titleSmall,
                    color = SafetyAmber,
                )
                Text(if (isBn) article.limitationsBn else article.limitationsEn, style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                Text(if (isBn) article.escalationInstructionsBn else article.escalationInstructionsEn, style = MaterialTheme.typography.bodySmall, color = InkPrimary, fontWeight = FontWeight.Medium)
            }
        }

        // PRD Section 16.2 Mandatory Medical Content Governance Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Lavender,
                contentColor = InkPrimary,
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = DeepPlumDark)
                    Text(if (isBn) "ক্লিনিক্যাল গভর্ন্যান্স ও তথ্যসূত্র" else "Clinical Governance & References", style = MaterialTheme.typography.titleSmall, color = DeepPlumDark)
                }
                Text("ID: ${article.articleId} • Version: ${article.version}", style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                Text("Audience: ${article.intendedAudience}", style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                Text("Owner: ${article.contentOwner}", style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                Text("Reviewer: ${article.clinicalReviewer}", style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                Text("Published: ${article.publicationDate} • Reviewed: ${article.reviewDate}", style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                Text("References: ${article.supportingReferences.joinToString("; ")}", style = MaterialTheme.typography.bodySmall, color = InkPrimary)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onAskAi, modifier = Modifier.weight(1f)) {
                Text(if (isBn) "AI-কে জিজ্ঞাসা করুন" else "Ask NariBondhu AI")
            }
            Button(onClick = onOpenBondhuHelp, modifier = Modifier.weight(1f)) {
                Text(if (isBn) "ডাক্তার দেখান" else "Find Doctor")
            }
        }
    }
}

@Composable
private fun RecordsTab(
    language: AppLanguage,
    records: List<HealthRecord>,
    extractionState: ExtractionState,
    aiConsentGranted: Boolean,
    onAddManual: () -> Unit,
    onScanBitmap: (android.graphics.Bitmap) -> Unit,
    onDeleteRecord: (String) -> Unit,
    onBuildPlainSummary: () -> String,
) {
    val isBn = language == AppLanguage.BN
    val context = LocalContext.current

    // Zero-Permission Android Photo Picker (Play Policy Compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                onScanBitmap(bitmap)
            }
        }
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Surface(color = Lavender, contentColor = InkPrimary, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isBn) "ব্যক্তিগত স্বাস্থ্য রেকর্ড ও রিপোর্ট" else "Personal Health Records Space",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepPlumDark,
                    )
                    Text(
                        if (isBn) "রোগ নির্ণয়, ওষুধ, অ্যালার্জি, ল্যাব ও আল্ট্রাসাউন্ড রিপোর্ট নিরাপদে রাখুন। কোনো ডাক্তারের সাথে শেয়ার করতে চাইলে কেবল আপনার সম্মতিতে শেয়ার হবে।"
                        else "Store diagnoses, medications, allergies, lab results, ultrasound reports, and notes. Records are never shared without your explicit permission.",
                        style = MaterialTheme.typography.bodySmall,
                        color = InkPrimary,
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAddManual,
                            colors = ButtonDefaults.buttonColors(containerColor = DeepPlum, contentColor = Color.White),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text(if (isBn) "হাতে লিখুন" else "Add Entry")
                        }
                        if (aiConsentGranted) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPlumDark),
                                border = BorderStroke(1.dp, DeepPlum),
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(Icons.Rounded.DocumentScanner, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text(if (isBn) "রিপোর্ট স্ক্যান" else "Scan Photo")
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val summaryText = onBuildPlainSummary()
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "NariBondhu Personal Health Summary\n\n$summaryText")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Health Summary"))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPlumDark),
                        border = BorderStroke(1.dp, DeepPlum),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Rounded.Share, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (isBn) "ডাক্তারের জন্য সারাংশ শেয়ার/সেভ করুন" else "Export / Share Health Summary")
                    }
                }
            }
        }

        if (extractionState is ExtractionState.Loading) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(if (isBn) "রিপোর্ট থেকে তথ্য পড়া হচ্ছে..." else "Extracting document details for your verification...")
                }
            }
        } else if (extractionState is ExtractionState.Error) {
            item {
                Text(extractionState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }

        if (records.isEmpty()) {
            item {
                EmptyTipCard(
                    if (isBn) "এখনো কোনো স্বাস্থ্য রেকর্ড নেই। ওষুধ, অ্যালার্জি বা পরীক্ষার রিপোর্ট এখানে যোগ করুন।"
                    else "No personal health records saved yet. Add medications, allergies, lab reports, or notes."
                )
            }
        } else {
            items(records, key = { it.id.ifBlank { "${it.title}_${it.dateText}" } }) { rec ->
                val typeObj = HealthRecordType.fromName(rec.recordType)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Surface(color = Lavender, contentColor = DeepPlumDark, shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    if (isBn) typeObj.bnLabel else typeObj.enLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = DeepPlumDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                            Text(rec.title, style = MaterialTheme.typography.titleSmall)
                            if (rec.dateText.isNotBlank()) {
                                Text(rec.dateText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (rec.details.isNotBlank()) {
                                Text(rec.details, style = MaterialTheme.typography.bodySmall)
                            }
                            if (rec.sharedWithDoctorIds.isNotEmpty()) {
                                Text(
                                    if (isBn) "ডাক্তারের সাথে শেয়ারকৃত: ${rec.sharedWithDoctorIds.size} জন"
                                    else "Shared with ${rec.sharedWithDoctorIds.size} consented doctor(s)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SafetyGreen,
                                )
                            }
                        }
                        IconButton(onClick = { onDeleteRecord(rec.id) }) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete record")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTipCard(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
