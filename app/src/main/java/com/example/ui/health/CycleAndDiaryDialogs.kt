package com.example.ui.health

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.core.EpochDateUtil
import com.example.data.AppLanguage
import com.example.data.ExtractedReportPreview
import com.example.data.FlowIntensity
import com.example.data.HealthRecord
import com.example.data.HealthRecordType
import com.example.data.MenstrualPeriod
import com.example.data.SymptomCategory
import com.example.data.SymptomEntry
import com.example.data.SymptomSeverity
import com.example.data.WellnessLog
import com.example.data.WellnessMood
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.Lavender
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogPeriodDialog(
    initialPeriod: MenstrualPeriod?,
    defaultStartEpochDay: Long,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (MenstrualPeriod) -> Unit,
    onDelete: ((String) -> Unit)? = null,
) {
    val isBn = language == AppLanguage.BN
    var startDay by remember { mutableLongStateOf(initialPeriod?.startDateEpochDay ?: defaultStartEpochDay) }
    var hasEndDate by remember { mutableStateOf(initialPeriod?.endDateEpochDay != null) }
    var endDay by remember { mutableLongStateOf(initialPeriod?.endDateEpochDay ?: (defaultStartEpochDay + 4)) }
    var flow by remember { mutableStateOf(FlowIntensity.fromName(initialPeriod?.flowIntensity)) }
    var spottingOnly by remember { mutableStateOf(initialPeriod?.spottingOnly ?: false) }
    var notes by remember { mutableStateOf(initialPeriod?.notes.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initialPeriod == null) {
                    if (isBn) "মাসিক বা স্পটিং লিখুন" else "Log Period or Spotting"
                } else {
                    if (isBn) "তথ্য সম্পাদনা করুন" else "Edit Cycle Entry"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = if (isBn) "শুরুর তারিখ: ${EpochDateUtil.formatEpochDay(startDay, true)}"
                    else "Start Date: ${EpochDateUtil.formatEpochDay(startDay, false)}",
                    style = MaterialTheme.typography.titleSmall,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { startDay -= 1; if (endDay < startDay) endDay = startDay }) {
                        Text(if (isBn) "-১ দিন" else "-1 day")
                    }
                    OutlinedButton(onClick = { startDay += 1; if (endDay < startDay) endDay = startDay }) {
                        Text(if (isBn) "+১ দিন" else "+1 day")
                    }
                    OutlinedButton(onClick = { startDay = EpochDateUtil.todayEpochDay(); if (endDay < startDay) endDay = startDay }) {
                        Text(if (isBn) "আজ" else "Today")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (isBn) "এটি কেবল স্পটিং (পূর্ণ মাসিক নয়)" else "Spotting Only (Separate from Period)",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            if (isBn) "স্পটিং আলাদা রাখলে চক্রের হিসাব নির্ভুল থাকে" else "Keeps cycle length calculation accurate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = spottingOnly,
                        onCheckedChange = {
                            spottingOnly = it
                            if (it) flow = FlowIntensity.SPOTTING
                        },
                    )
                }

                Text(
                    if (isBn) "রক্তপাতের মাত্রা" else "Flow Intensity",
                    style = MaterialTheme.typography.titleSmall,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    FlowIntensity.entries.forEach { item ->
                        FilterChip(
                            selected = flow == item,
                            onClick = {
                                flow = item
                                spottingOnly = (item == FlowIntensity.SPOTTING)
                            },
                            label = { Text(if (isBn) item.bnLabel else item.enLabel) },
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasEndDate, onCheckedChange = { hasEndDate = it })
                    Text(if (isBn) "শেষের তারিখ যোগ করুন" else "Add End Date", style = MaterialTheme.typography.bodyMedium)
                }
                if (hasEndDate) {
                    Text(
                        if (isBn) "শেষের তারিখ: ${EpochDateUtil.formatEpochDay(endDay, true)}"
                        else "End Date: ${EpochDateUtil.formatEpochDay(endDay, false)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { if (endDay > startDay) endDay -= 1 }) { Text("-1 day") }
                        OutlinedButton(onClick = { endDay += 1 }) { Text("+1 day") }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it.take(500) },
                    label = { Text(if (isBn) "ব্যক্তিগত নোট (ঐচ্ছিক)" else "Personal Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        MenstrualPeriod(
                            id = initialPeriod?.id.orEmpty(),
                            userId = initialPeriod?.userId.orEmpty(),
                            startDateEpochDay = startDay,
                            endDateEpochDay = if (hasEndDate) endDay.coerceAtLeast(startDay) else null,
                            flowIntensity = flow.name,
                            spottingOnly = spottingOnly,
                            notes = notes,
                        )
                    )
                },
                modifier = Modifier.testTag("save_period_button"),
            ) {
                Text(if (isBn) "সংরক্ষণ" else "Save")
            }
        },
        dismissButton = {
            Row {
                if (initialPeriod != null && initialPeriod.id.isNotBlank() && onDelete != null) {
                    TextButton(onClick = { onDelete(initialPeriod.id) }) {
                        Text(if (isBn) "মুছুন" else "Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogSymptomDialog(
    defaultCategory: SymptomCategory,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (SymptomEntry) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    var category by remember { mutableStateOf(defaultCategory) }
    var symptomType by remember { mutableStateOf("Cramps") }
    var severity by remember { mutableStateOf(SymptomSeverity.MILD) }
    var durationText by remember { mutableStateOf("Today") }
    var frequencyText by remember { mutableStateOf("Once") }
    var notes by remember { mutableStateOf("") }

    val quickSymptoms = when (category) {
        SymptomCategory.INTIMATE -> listOf(
            "Discharge change", "Vaginal itching", "Irritation / Burning", "Odour concern", "Vulvar discomfort"
        )
        SymptomCategory.PCOS -> listOf(
            "Irregular / Delayed cycle", "Acne flare", "Facial / Body hair", "Fatigue", "Weight shift", "Pelvic discomfort"
        )
        SymptomCategory.PMS -> listOf(
            "Mood changes", "Fatigue", "Breast tenderness", "Bloating", "Cramps", "Sleep changes"
        )
        else -> listOf(
            "Cramps", "Pelvic discomfort", "Headaches", "Fatigue", "Bloating", "Breast tenderness", "Mood changes", "Sleep disturbances", "Bleeding changes"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBn) "লক্ষণ ও অভিজ্ঞতা লিখুন" else "Log Symptom or Observation") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(if (isBn) "বিভাগ" else "Category", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SymptomCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                symptomType = when (cat) {
                                    SymptomCategory.INTIMATE -> "Discharge change"
                                    SymptomCategory.PCOS -> "Fatigue"
                                    SymptomCategory.PMS -> "Mood changes"
                                    else -> "Cramps"
                                }
                            },
                            label = { Text(if (isBn) cat.bnLabel else cat.enLabel) },
                        )
                    }
                }

                Text(if (isBn) "দ্রুত নির্বাচন করুন" else "Quick Select Symptom", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickSymptoms.forEach { sym ->
                        FilterChip(
                            selected = symptomType == sym,
                            onClick = { symptomType = sym },
                            label = { Text(sym) },
                        )
                    }
                }

                OutlinedTextField(
                    value = symptomType,
                    onValueChange = { symptomType = it.take(60) },
                    label = { Text(if (isBn) "লক্ষণের নাম" else "Symptom Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(if (isBn) "তীব্রতা" else "Severity", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SymptomSeverity.entries.forEach { sev ->
                        FilterChip(
                            selected = severity == sev,
                            onClick = { severity = sev },
                            label = { Text(if (isBn) sev.bnLabel else sev.enLabel) },
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it.take(60) },
                        label = { Text(if (isBn) "সময়কাল" else "Duration") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = frequencyText,
                        onValueChange = { frequencyText = it.take(60) },
                        label = { Text(if (isBn) "কতবার" else "Frequency") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it.take(500) },
                    label = { Text(if (isBn) "ব্যক্তিগত নোট (ঐচ্ছিক)" else "Optional Personal Notes") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        SymptomEntry(
                            dateEpochDay = EpochDateUtil.todayEpochDay(),
                            category = category.name,
                            symptomType = symptomType.ifBlank { "General symptom" },
                            severity = severity.name,
                            durationText = durationText,
                            frequencyText = frequencyText,
                            notes = notes,
                        )
                    )
                },
                modifier = Modifier.testTag("save_symptom_button"),
            ) {
                Text(if (isBn) "সংরক্ষণ" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBn) "বাতিল" else "Cancel")
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogWellnessDialog(
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (WellnessLog) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    var sleepHours by remember { mutableFloatStateOf(7.0f) }
    var mood by remember { mutableStateOf(WellnessMood.CALM) }
    var stress by remember { mutableIntStateOf(2) }
    var energy by remember { mutableIntStateOf(3) }
    var activityMins by remember { mutableIntStateOf(25) }
    var hydration by remember { mutableIntStateOf(6) }
    var nutritionNotes by remember { mutableStateOf("") }
    var wellnessGoal by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isBn) "আজকের সুস্থতার নোট" else "Daily Wellness Check-In") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    if (isBn) "ঘুম: ${(sleepHours * 2).roundToInt() / 2.0} ঘণ্টা" else "Sleep: ${(sleepHours * 2).roundToInt() / 2.0} hours",
                    style = MaterialTheme.typography.titleSmall,
                )
                Slider(value = sleepHours, onValueChange = { sleepHours = it }, valueRange = 0f..14f)

                Text(if (isBn) "মন ও মেজাজ" else "Mood", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    WellnessMood.entries.forEach { m ->
                        FilterChip(
                            selected = mood == m,
                            onClick = { mood = m },
                            label = { Text(if (isBn) m.bnLabel else m.enLabel) },
                        )
                    }
                }

                Text(if (isBn) "মানসিক চাপ (১–৫): $stress" else "Stress Level (1–5): $stress", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { lvl ->
                        FilterChip(selected = stress == lvl, onClick = { stress = lvl }, label = { Text("$lvl") })
                    }
                }

                Text(if (isBn) "শক্তির মাত্রা (১–৫): $energy" else "Energy Level (1–5): $energy", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { lvl ->
                        FilterChip(selected = energy == lvl, onClick = { energy = lvl }, label = { Text("$lvl") })
                    }
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isBn) "পানি পান: $hydration গ্লাস" else "Hydration: $hydration glasses", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = { if (hydration > 0) hydration -= 1 }) { Text("-") }
                        OutlinedButton(onClick = { if (hydration < 20) hydration += 1 }) { Text("+") }
                    }
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isBn) "হাঁটা/ব্যায়াম: $activityMins মি." else "Activity: $activityMins min", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(onClick = { activityMins = (activityMins - 10).coerceAtLeast(0) }) { Text("-10") }
                        OutlinedButton(onClick = { activityMins = (activityMins + 10).coerceAtMost(300) }) { Text("+10") }
                    }
                }

                OutlinedTextField(
                    value = nutritionNotes,
                    onValueChange = { nutritionNotes = it.take(300) },
                    label = { Text(if (isBn) "পুষ্টি ও খাবারের নোট" else "Nutrition Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = wellnessGoal,
                    onValueChange = { wellnessGoal = it.take(200) },
                    label = { Text(if (isBn) "ব্যক্তিগত লক্ষ্য" else "Personal Wellness Goal") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    WellnessLog(
                        dateEpochDay = EpochDateUtil.todayEpochDay(),
                        sleepHours = ((sleepHours * 2).roundToInt() / 2.0),
                        mood = mood.name,
                        stressLevel = stress.toLong(),
                        energyLevel = energy.toLong(),
                        activityMinutes = activityMins.toLong(),
                        hydrationGlasses = hydration.toLong(),
                        nutritionNotes = nutritionNotes,
                        wellnessGoal = wellnessGoal,
                    )
                )
            }) {
                Text(if (isBn) "সংরক্ষণ" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(if (isBn) "বাতিল" else "Cancel") }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddOrVerifyHealthRecordDialog(
    preview: ExtractedReportPreview?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (HealthRecord) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    var recordType by remember(preview) { mutableStateOf(preview?.recordType ?: HealthRecordType.NOTE) }
    var title by remember(preview) { mutableStateOf(preview?.title.orEmpty()) }
    var dateText by remember(preview) { mutableStateOf(preview?.dateText ?: "") }
    var details by remember(preview) { mutableStateOf(preview?.details.orEmpty()) }
    var verified by remember { mutableStateOf(preview == null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (preview != null) {
                    if (isBn) "স্ক্যানকৃত তথ্য যাচাই করুন" else "Verify Scanned Record Details"
                } else {
                    if (isBn) "স্বাস্থ্য রেকর্ড যোগ করুন" else "Add Personal Health Record"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (preview != null) {
                    Surface(color = Lavender, contentColor = InkPrimary, shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = if (isBn) "অনুগ্রহ করে সংরক্ষণের আগে প্রতিটি সংখ্যা ও তথ্য মিলিয়ে দেখুন। AI রিপোর্ট দেখে কোনো রোগ নির্ণয় করে না।"
                            else "Please verify the extracted headings and values before saving. AI never diagnoses from lab or ultrasound reports.",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkPrimary,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                }

                Text(if (isBn) "রেকর্ডের ধরন" else "Record Type", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    HealthRecordType.entries.forEach { t ->
                        FilterChip(
                            selected = recordType == t,
                            onClick = { recordType = t },
                            label = { Text(if (isBn) t.bnLabel else t.enLabel) },
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(120) },
                    label = { Text(if (isBn) "শিরোনাম (যেমন: থাইরয়েড রিপোর্ট)" else "Title (e.g., Thyroid Panel / Medication)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it.take(40) },
                    label = { Text(if (isBn) "তারিখ" else "Date (e.g., 2026-11-15)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it.take(1500) },
                    label = { Text(if (isBn) "বিস্তারিত তথ্য / মাত্রা / নোট" else "Details / Values / Notes") },
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (preview != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = verified, onCheckedChange = { verified = it })
                        Text(
                            if (isBn) "আমি নিশ্চিত করছি যে তথ্যটি সঠিক" else "I verified these extracted details are accurate",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        HealthRecord(
                            recordType = recordType.name,
                            title = title.ifBlank { recordType.enLabel },
                            dateText = dateText,
                            details = details,
                            verifiedByUser = verified,
                        )
                    )
                },
                enabled = title.isNotBlank() && verified,
            ) {
                Text(if (isBn) "সংরক্ষণ" else "Save Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(if (isBn) "বাতিল" else "Cancel") }
        },
    )
}
