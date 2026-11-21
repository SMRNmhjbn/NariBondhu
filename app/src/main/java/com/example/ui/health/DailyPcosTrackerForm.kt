package com.example.ui.health

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryAlert
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.FaceRetouchingNatural
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Flare
import androidx.compose.material.icons.rounded.Healing
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.EpochDateUtil
import com.example.data.SymptomCategory
import com.example.data.SymptomEntry
import com.example.data.SymptomSeverity
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SafetyAmberContainer
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyGreenContainer
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.SafetyRedContainer

enum class PcosMarkerPreset(
    val id: String,
    val enTitle: String,
    val bnTitle: String,
    val enHint: String,
    val bnHint: String,
    val icon: ImageVector,
) {
    HAIR_GROWTH(
        id = "hair_growth",
        enTitle = "Hair growth (Hirsutism)",
        bnTitle = "অতিরিক্ত লোম বৃদ্ধি",
        enHint = "Chin, upper lip, jawline, chest, or abdomen",
        bnHint = "থুতনি, ঠোঁটের উপরে, বুক বা শরীরে অতিরিক্ত লোম",
        icon = Icons.Rounded.FaceRetouchingNatural,
    ),
    ACNE(
        id = "acne",
        enTitle = "Acne / skin flare",
        bnTitle = "ব্রণ বা ত্বকের সমস্যা",
        enHint = "Jawline, chin, cheeks, back, or oily skin",
        bnHint = "থুতনি, গাল, কপাল বা পিঠে ব্রণ ও তৈলাক্ত ভাব",
        icon = Icons.Rounded.Flare,
    ),
    FATIGUE(
        id = "fatigue",
        enTitle = "Fatigue / low energy",
        bnTitle = "অতিরিক্ত ক্লান্তি ও দুর্বলতা",
        enHint = "Persistent tiredness or daytime energy dip",
        bnHint = "সারা দিন ক্লান্ত, ঝিমুনি বা দুর্বল অনুভব",
        icon = Icons.Rounded.BatteryAlert,
    ),
    HAIR_THINNING(
        id = "hair_thinning",
        enTitle = "Hair thinning / hair fall",
        bnTitle = "চুল পড়া বা পাতলা হওয়া",
        enHint = "Noticeable shedding or widening hair part",
        bnHint = "মাথার চুল বেশি পড়া বা সিঁথি পাতলা হয়ে যাওয়া",
        icon = Icons.Rounded.Face,
    ),
    WEIGHT_BLOATING(
        id = "weight_bloating",
        enTitle = "Weight shift / bloating",
        bnTitle = "ওজন পরিবর্তন বা পেট ফাঁপা",
        enHint = "Abdominal fullness or sudden weight fluctuation",
        bnHint = "পেট ভার লাগা বা হঠাৎ ওজন বেড়ে যাওয়া",
        icon = Icons.Rounded.MonitorWeight,
    ),
    IRREGULAR_CYCLE(
        id = "irregular_cycle",
        enTitle = "Irregular / delayed cycle",
        bnTitle = "অনিয়মিত বা দেরিতে মাসিক",
        enHint = "Late period, skipped cycle, or unexpected spotting",
        bnHint = "মাসিক দেরিতে হওয়া, বাদ পড়া বা অনিয়মিত হওয়া",
        icon = Icons.Rounded.EventBusy,
    ),
    CRAVINGS(
        id = "cravings",
        enTitle = "Sugar cravings / crash",
        bnTitle = "মিষ্টি খাওয়ার প্রবণতা",
        enHint = "Strong carb/sweet cravings or post-meal slump",
        bnHint = "হঠাৎ মিষ্টি বা শর্করা খাওয়ার তীব্র ইচ্ছা",
        icon = Icons.Rounded.LocalCafe,
    ),
    PELVIC_DISCOMFORT(
        id = "pelvic_discomfort",
        enTitle = "Pelvic discomfort",
        bnTitle = "তলপেটে ব্যথা বা অস্বস্তি",
        enHint = "Lower belly ache, pressure, or cramping",
        bnHint = "তলপেটে চিনচিনে ব্যথা বা চাপ অনুভব",
        icon = Icons.Rounded.FavoriteBorder,
    ),
}

enum class PcosPatternFrequency(val enLabel: String, val bnLabel: String) {
    NEW_TODAY("New today", "আজ নতুন"),
    ONGOING_WEEK("Ongoing this week", "এই সপ্তাহে চলছে"),
    RECURRING("Recurring", "বারবার দেখা দিচ্ছে"),
}

@Stable
class DailyPcosFormState(
    initialEpochDay: Long = EpochDateUtil.todayEpochDay(),
) {
    var selectedEpochDay by mutableLongStateOf(initialEpochDay)
        private set

    var selectedSeverities by mutableStateOf<Map<PcosMarkerPreset, SymptomSeverity>>(emptyMap())
        private set

    var customMarkerText by mutableStateOf("")
        private set

    var customMarkerSeverity by mutableStateOf(SymptomSeverity.MILD)
        private set

    var patternFrequency by mutableStateOf(PcosPatternFrequency.ONGOING_WEEK)
        private set

    var notes by mutableStateOf("")
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val selectedCount: Int
        get() = selectedSeverities.size + if (customMarkerText.isNotBlank()) 1 else 0

    fun shiftDate(deltaDays: Long) {
        selectedEpochDay = (selectedEpochDay + deltaDays).coerceIn(0L, 100000L)
        errorMessage = null
    }

    fun selectToday(today: Long = EpochDateUtil.todayEpochDay()) {
        selectedEpochDay = today
        errorMessage = null
    }

    fun selectYesterday(today: Long = EpochDateUtil.todayEpochDay()) {
        selectedEpochDay = (today - 1).coerceAtLeast(0L)
        errorMessage = null
    }

    fun toggleMarker(marker: PcosMarkerPreset) {
        val next = selectedSeverities.toMutableMap()
        if (next.containsKey(marker)) {
            next.remove(marker)
        } else {
            next[marker] = SymptomSeverity.MILD
        }
        selectedSeverities = next
        errorMessage = null
    }

    fun setMarkerSeverity(marker: PcosMarkerPreset, severity: SymptomSeverity) {
        val next = selectedSeverities.toMutableMap()
        next[marker] = severity
        selectedSeverities = next
        errorMessage = null
    }

    fun updateCustomMarkerText(value: String) {
        customMarkerText = value.take(60)
        errorMessage = null
    }

    fun updateCustomMarkerSeverity(severity: SymptomSeverity) {
        customMarkerSeverity = severity
    }

    fun updateFrequency(freq: PcosPatternFrequency) {
        patternFrequency = freq
    }

    fun updateNotes(value: String) {
        notes = value.take(500)
    }

    fun updateSubmitting(loading: Boolean) {
        isSubmitting = loading
    }

    fun setError(message: String?) {
        errorMessage = message
    }

    fun buildSymptomEntries(): List<SymptomEntry> {
        val trimmedNotes = notes.trim().take(500)
        val entries = selectedSeverities.entries.map { (marker, severity) ->
            SymptomEntry(
                dateEpochDay = selectedEpochDay,
                category = SymptomCategory.PCOS.name,
                symptomType = marker.enTitle.take(60),
                severity = severity.name,
                durationText = "Daily PCOS check-in",
                frequencyText = patternFrequency.enLabel,
                notes = trimmedNotes,
            )
        }.toMutableList()

        val custom = customMarkerText.trim().take(60)
        if (custom.isNotEmpty()) {
            entries.add(
                SymptomEntry(
                    dateEpochDay = selectedEpochDay,
                    category = SymptomCategory.PCOS.name,
                    symptomType = custom,
                    severity = customMarkerSeverity.name,
                    durationText = "Daily PCOS check-in",
                    frequencyText = patternFrequency.enLabel,
                    notes = trimmedNotes,
                )
            )
        }
        return entries
    }

    fun resetAfterSave() {
        selectedSeverities = emptyMap()
        customMarkerText = ""
        customMarkerSeverity = SymptomSeverity.MILD
        notes = ""
        errorMessage = null
        isSubmitting = false
    }
}

@Composable
fun rememberDailyPcosFormState(): DailyPcosFormState {
    val today = remember { EpochDateUtil.todayEpochDay() }
    return remember { DailyPcosFormState(initialEpochDay = today) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyPcosTrackerCard(
    state: DailyPcosFormState,
    existingPcosEntries: List<SymptomEntry>,
    isBn: Boolean,
    onSubmit: (List<SymptomEntry>) -> Unit,
) {
    val todayEpochDay = remember { EpochDateUtil.todayEpochDay() }
    val loggedForSelectedDate = remember(existingPcosEntries, state.selectedEpochDay) {
        existingPcosEntries.filter { it.dateEpochDay == state.selectedEpochDay }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp))
            .testTag("daily_pcos_form_card"),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBn) "দৈনিক PCOS লক্ষণ ফর্ম" else "Daily PCOS Marker Check-In",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = if (isBn) {
                            "অতিরিক্ত লোম, ব্রণ, ক্লান্তি ও অন্যান্য লক্ষণ প্রতিদিন আলাদা মাত্রাসহ লিখে রাখুন।"
                        } else {
                            "Select today's PCOS markers—like hair growth, acne, or fatigue—set severity, and save securely."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (state.selectedCount > 0) {
                    Surface(
                        color = Lavender,
                        contentColor = DeepPlumDark,
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text(
                            text = if (isBn) "${state.selectedCount}টি নির্বাচিত" else "${state.selectedCount} selected",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepPlumDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                }
            }

            // Check-In Date Picker Strip
            Surface(
                color = Lavender,
                contentColor = InkPrimary,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = if (isBn) "নথিভুক্তির তারিখ" else "Check-In Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = DeepPlumDark,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = EpochDateUtil.formatEpochDay(state.selectedEpochDay, isBn),
                                style = MaterialTheme.typography.titleSmall,
                                color = InkPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        if (loggedForSelectedDate.isNotEmpty()) {
                            Surface(
                                color = SafetyGreenContainer,
                                contentColor = SafetyGreen,
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text(
                                    text = if (isBn) "${loggedForSelectedDate.size}টি সংরক্ষিত" else "${loggedForSelectedDate.size} logged",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SafetyGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        OutlinedButton(
                            onClick = { state.shiftDate(-1) },
                            border = BorderStroke(1.dp, DeepPlum),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPlumDark),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp),
                        ) {
                            Text("-1d", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = { state.selectYesterday(todayEpochDay) },
                            border = BorderStroke(1.dp, DeepPlum),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (state.selectedEpochDay == todayEpochDay - 1) DeepPlum else Color.Transparent,
                                contentColor = if (state.selectedEpochDay == todayEpochDay - 1) Color.White else DeepPlumDark,
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp),
                        ) {
                            Text(if (isBn) "গতকাল" else "Yesterday", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = { state.selectToday(todayEpochDay) },
                            border = BorderStroke(1.dp, DeepPlum),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (state.selectedEpochDay == todayEpochDay) DeepPlum else Color.Transparent,
                                contentColor = if (state.selectedEpochDay == todayEpochDay) Color.White else DeepPlumDark,
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp),
                        ) {
                            Icon(Icons.Rounded.Today, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (isBn) "আজ" else "Today", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = { state.shiftDate(1) },
                            enabled = state.selectedEpochDay < todayEpochDay,
                            border = BorderStroke(1.dp, DeepPlum.copy(alpha = if (state.selectedEpochDay < todayEpochDay) 1f else 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPlumDark),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(36.dp),
                        ) {
                            Text("+1d", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Common PCOS Marker Multi-Select List
            Text(
                text = if (isBn) "১. আজ কোন কোন PCOS লক্ষণ লক্ষ্য করছেন?" else "1. Select PCOS Markers Present Today",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PcosMarkerPreset.entries.forEach { marker ->
                    val severity = state.selectedSeverities[marker]
                    val isSelected = severity != null
                    PcosMarkerOptionRow(
                        marker = marker,
                        isSelected = isSelected,
                        severity = severity ?: SymptomSeverity.MILD,
                        isBn = isBn,
                        onToggle = { state.toggleMarker(marker) },
                        onSelectSeverity = { sev -> state.setMarkerSeverity(marker, sev) },
                    )
                }
            }

            // Custom PCOS Marker Input
            OutlinedTextField(
                value = state.customMarkerText,
                onValueChange = { state.updateCustomMarkerText(it) },
                label = {
                    Text(
                        if (isBn) "অন্যান্য PCOS লক্ষণ (ঐচ্ছিক, যেমন: ত্বকে কালো দাগ)"
                        else "Other PCOS marker (optional, e.g., dark skin patches)"
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pcos_custom_marker_input"),
            )

            AnimatedVisibility(visible = state.customMarkerText.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isBn) "তীব্রতা:" else "Severity:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SymptomSeverity.entries.forEach { sev ->
                        SeverityChip(
                            severity = sev,
                            selected = state.customMarkerSeverity == sev,
                            isBn = isBn,
                            onClick = { state.updateCustomMarkerSeverity(sev) },
                        )
                    }
                }
            }

            // Frequency / Pattern Selection
            Text(
                text = if (isBn) "২. লক্ষণের ধরন / ধারা" else "2. Pattern / Frequency",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                PcosPatternFrequency.entries.forEach { freq ->
                    FilterChip(
                        selected = state.patternFrequency == freq,
                        onClick = { state.updateFrequency(freq) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepPlum,
                            selectedLabelColor = Color.White,
                        ),
                        label = { Text(if (isBn) freq.bnLabel else freq.enLabel) },
                    )
                }
            }

            // Daily Context Notes
            OutlinedTextField(
                value = state.notes,
                onValueChange = { state.updateNotes(it) },
                label = {
                    Text(
                        if (isBn) "দৈনিক নোট (ঘুম, চাপ, খাদ্যাভ্যাস বা ডাক্তারকে বলার কথা)"
                        else "Daily notes (triggers, sleep, stress, or notes for your doctor)"
                    )
                },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pcos_daily_notes_input"),
            )

            if (state.errorMessage != null) {
                Surface(
                    color = SafetyRedContainer,
                    contentColor = InkPrimary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SafetyRed, RoundedCornerShape(12.dp)),
                ) {
                    Text(
                        text = state.errorMessage.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = SafetyRed,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }

            // Submit to Firestore Button
            Button(
                onClick = {
                    val entries = state.buildSymptomEntries()
                    if (entries.isEmpty()) {
                        state.setError(
                            if (isBn) "অনুগ্রহ করে অন্তত একটি PCOS লক্ষণ নির্বাচন করুন।"
                            else "Please select at least one PCOS marker before saving."
                        )
                    } else {
                        onSubmit(entries)
                    }
                },
                enabled = state.selectedCount > 0 && !state.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepPlum,
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_pcos_checkin_button"),
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                    Spacer(Modifier.width(8.dp))
                } else {
                    Icon(Icons.Rounded.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = if (isBn) {
                        if (state.selectedCount > 0) "আজকের PCOS তথ্য সংরক্ষণ করুন (${state.selectedCount}টি)"
                        else "অন্তত একটি লক্ষণ বেছে নিন"
                    } else {
                        if (state.selectedCount > 0) "Save Daily PCOS Check-In (${state.selectedCount})"
                        else "Select Marker(s) to Save"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun PcosMarkerOptionRow(
    marker: PcosMarkerPreset,
    isSelected: Boolean,
    severity: SymptomSeverity,
    isBn: Boolean,
    onToggle: () -> Unit,
    onSelectSeverity: (SymptomSeverity) -> Unit,
) {
    val containerColor = if (isSelected) Lavender else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (isSelected) DeepPlum else MaterialTheme.colorScheme.outlineVariant
    val titleColor = if (isSelected) DeepPlumDark else MaterialTheme.colorScheme.onSurface
    val hintColor = if (isSelected) InkSecondary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = containerColor,
        contentColor = titleColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle)
            .testTag("pcos_marker_${marker.id}"),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) DeepPlum else MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = marker.icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBn) marker.bnTitle else marker.enTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = titleColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (isBn) marker.bnHint else marker.enHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = hintColor,
                    )
                }
                Icon(
                    imageVector = if (isSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                    contentDescription = if (isSelected) "Selected" else "Not selected",
                    tint = if (isSelected) DeepPlum else MaterialTheme.colorScheme.outline,
                )
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(150)) + expandVertically(tween(180)),
                exit = fadeOut(tween(120)) + shrinkVertically(tween(150)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isBn) "মাত্রা:" else "Severity:",
                        style = MaterialTheme.typography.labelMedium,
                        color = DeepPlumDark,
                        fontWeight = FontWeight.SemiBold,
                    )
                    SymptomSeverity.entries.forEach { sev ->
                        SeverityChip(
                            severity = sev,
                            selected = severity == sev,
                            isBn = isBn,
                            onClick = { onSelectSeverity(sev) },
                            tag = "pcos_sev_${marker.id}_${sev.name.lowercase()}",
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SeverityChip(
    severity: SymptomSeverity,
    selected: Boolean,
    isBn: Boolean,
    onClick: () -> Unit,
    tag: String? = null,
) {
    val selectedBg = when (severity) {
        SymptomSeverity.MILD -> SafetyGreen
        SymptomSeverity.MODERATE -> SafetyAmber
        SymptomSeverity.SEVERE -> SafetyRed
    }
    FilterChip(
        selected = selected,
        onClick = onClick,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = InkPrimary,
            selectedContainerColor = selectedBg,
            selectedLabelColor = Color.White,
        ),
        label = {
            Text(
                text = if (isBn) severity.bnLabel else severity.enLabel,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        modifier = if (tag != null) Modifier.testTag(tag) else Modifier,
    )
}

@Composable
fun DailyPcosHistoryCard(
    pcosEntries: List<SymptomEntry>,
    isBn: Boolean,
    onDeleteSymptom: (String) -> Unit,
) {
    val grouped = remember(pcosEntries) {
        pcosEntries.groupBy { it.dateEpochDay }.entries.sortedByDescending { it.key }.take(7)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(Icons.Rounded.Healing, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = if (isBn) "সাম্প্রতিক দৈনিক PCOS রেকর্ড" else "Recent Daily PCOS Marker Logs",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            if (grouped.isEmpty()) {
                Text(
                    text = if (isBn) {
                        "উপরের দৈনিক ফর্ম থেকে অতিরিক্ত লোম, ব্রণ বা ক্লান্তি নথিভুক্ত করলে এখানে তারিখের ক্রমে দেখা যাবে।"
                    } else {
                        "Your daily hair growth, acne, fatigue, and other PCOS check-ins saved to Firestore will appear here by date."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                grouped.forEach { (epochDay, dayEntries) ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = EpochDateUtil.formatEpochDay(epochDay, isBn),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            dayEntries.forEach { entry ->
                                val sev = SymptomSeverity.fromName(entry.severity)
                                val pillBg = when (sev) {
                                    SymptomSeverity.MILD -> SafetyGreenContainer
                                    SymptomSeverity.MODERATE -> SafetyAmberContainer
                                    SymptomSeverity.SEVERE -> SafetyRedContainer
                                }
                                val pillFg = when (sev) {
                                    SymptomSeverity.MILD -> SafetyGreen
                                    SymptomSeverity.MODERATE -> SafetyAmber
                                    SymptomSeverity.SEVERE -> SafetyRed
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            Text(
                                                text = entry.symptomType,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                            )
                                            Surface(
                                                color = pillBg,
                                                contentColor = pillFg,
                                                shape = RoundedCornerShape(6.dp),
                                            ) {
                                                Text(
                                                    text = if (isBn) sev.bnLabel else sev.enLabel,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = pillFg,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                )
                                            }
                                        }
                                        if (entry.notes.isNotBlank()) {
                                            Text(
                                                text = entry.notes,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                    if (entry.id.isNotBlank()) {
                                        IconButton(onClick = { onDeleteSymptom(entry.id) }) {
                                            Icon(
                                                Icons.Rounded.DeleteOutline,
                                                contentDescription = if (isBn) "মুছুন" else "Delete marker",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
