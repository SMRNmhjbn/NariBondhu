package com.example.ui.profile

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.core.AppStrings
import com.example.core.rememberTtsSpeaker
import com.example.data.AppLanguage
import com.example.data.ConsentLog
import com.example.data.ConsentType
import com.example.data.UserProfile
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyGreenContainer
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.WarmCard

@Composable
fun ProfileAndPrivacyScreen(
    profile: UserProfile?,
    language: AppLanguage,
    discreetMode: Boolean,
    pinEnabled: Boolean,
    consentLogs: List<ConsentLog>,
    onLanguageChange: (AppLanguage) -> Unit,
    onToggleDiscreetMode: (Boolean) -> Unit,
    onSetPin: (String?) -> Unit,
    onUpdateConsent: (ConsentType, Boolean) -> Unit,
    onExportJson: () -> String,
    onDeleteAllDataAndSignOut: () -> Unit,
    onSignOut: () -> Unit,
) {
    val isBn = language == AppLanguage.BN
    val copy = AppStrings.of(language)
    val context = LocalContext.current
    val speaker = rememberTtsSpeaker()

    var showPinDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showConsentHistory by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Profile Header & Language
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Lavender,
                    contentColor = InkPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = profile?.displayName?.ifBlank { "Friend" } ?: "Friend",
                                style = MaterialTheme.typography.headlineSmall,
                                color = DeepPlumDark,
                            )
                            Text(
                                text = "${profile?.city ?: "Dhaka"} • ${if (profile?.ageConfirmed18Plus == true) "18+ Verified" else "Adult Profile"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary,
                            )
                        }
                        TextButton(
                            onClick = {
                                val summaryText = if (isBn) {
                                    "নারীবন্ধু প্রাইভেসি ও সম্মতি কেন্দ্র। আপনার তথ্য কেবল আপনার অনুমতিতে সংরক্ষিত থাকে এবং কখনো বিক্রি বা বিজ্ঞাপনে ব্যবহার হয় না।"
                                } else {
                                    "NariBondhu Privacy and Consent Hub. Your health data is encrypted, never sold, and every AI or doctor sharing option is under your control."
                                }
                                if (speaker.isSpeaking) speaker.stop() else speaker.speak(summaryText, language)
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = DeepPlumDark),
                        ) {
                            Icon(if (speaker.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp, contentDescription = copy.listenBtn)
                            Spacer(Modifier.width(4.dp))
                            Text(if (speaker.isSpeaking) copy.stopListenBtn else copy.listenBtn)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppLanguage.entries.forEach { lang ->
                            FilterChip(
                                selected = language == lang,
                                onClick = { onLanguageChange(lang) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = WarmCard,
                                    labelColor = InkPrimary,
                                    selectedContainerColor = DeepPlum,
                                    selectedLabelColor = Color.White,
                                ),
                                label = { Text(lang.nativeLabel) },
                                modifier = Modifier.testTag("profile_lang_${lang.code}"),
                            )
                        }
                    }
                }
            }
        }

        // Privacy & Consent Center (PRD Sections 4, 15, 16)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            if (isBn) "প্রাইভেসি ও সম্মতি নিয়ন্ত্রণ কেন্দ্র" else "Privacy & Consent Controls",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    ConsentControlRow(
                        title = copy.consentAiTitle,
                        subtitle = if (isBn) "যেকোনো সময় চালু বা বন্ধ করতে পারবেন" else "Toggle educational AI assistance anytime",
                        checked = profile?.aiConsentGranted ?: false,
                        onCheckedChange = { onUpdateConsent(ConsentType.AI_DATA_PROCESSING, it) },
                        tag = "privacy_ai_switch",
                    )

                    ConsentControlRow(
                        title = copy.consentReminderTitle,
                        subtitle = if (isBn) "গোপনীয় ও নিরপেক্ষ ভাষায় রিমাইন্ডার" else "Neutral wording without sensitive labels",
                        checked = profile?.remindersEnabled ?: false,
                        onCheckedChange = { onUpdateConsent(ConsentType.REMINDERS, it) },
                        tag = "privacy_reminders_switch",
                    )

                    ConsentControlRow(
                        title = copy.consentStorageTitle,
                        subtitle = if (isBn) "আপনার একাউন্টের আওতায় এনক্রিপ্টেড সংরক্ষণ" else "User-isolated private cloud storage",
                        checked = profile?.cloudSyncConsentGranted ?: true,
                        onCheckedChange = { onUpdateConsent(ConsentType.PRIVACY_POLICY, it) },
                        tag = "privacy_storage_switch",
                    )

                    ConsentControlRow(
                        title = if (isBn) "গোপনীয় মোড (Discreet Veil)" else "Discreet Privacy Mode",
                        subtitle = if (isBn) "হোম স্ক্রিনে সংবেদনশীল শিরোনাম আড়াল রাখে" else "Masks sensitive menstrual & symptom terms on dashboard",
                        checked = discreetMode,
                        onCheckedChange = onToggleDiscreetMode,
                        tag = "privacy_discreet_switch",
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Rounded.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column {
                                Text(
                                    if (isBn) "৪-সংখ্যার প্রাইভেসি পিন লক" else "4-Digit Discreet PIN Lock",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Text(
                                    if (pinEnabled) (if (isBn) "চালু আছে" else "Enabled on app open")
                                    else (if (isBn) "বন্ধ আছে" else "Not set"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (pinEnabled) SafetyGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { showPinDialog = true },
                            modifier = Modifier.testTag("manage_pin_btn"),
                        ) {
                            Text(if (pinEnabled) (if (isBn) "পরিবর্তন" else "Manage") else (if (isBn) "সেট করুন" else "Set PIN"))
                        }
                    }
                }
            }
        }

        // PRD Section 15: 6-Point AI Data Protection Transparency Notice
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.VisibilityOff, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            if (isBn) "AI তথ্য সুরক্ষা ও স্বচ্ছতা বিবরণী" else "AI Data Protection Transparency",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    val points = if (isBn) {
                        listOf(
                            "১. কী পাঠানো হয়: কেবল আপনার টাইপ করা প্রশ্ন এবং (আপনি নিজে চালু করলে) নামবিহীন সারাংশ।",
                            "২. কেন: সহজ ভাষায় শিক্ষামূলক তথ্য দিতে।",
                            "৩. সুরক্ষা: Firebase App Check ও HTTPS এনক্রিপশন ব্যবহার করা হয়।",
                            "৪. সংরক্ষণ: আমাদের অ্যাপ আপনার AI প্রশ্ন স্থায়ীভাবে সংরক্ষণ করে না।",
                            "৫. তৃতীয় পক্ষ: কখনো বিজ্ঞাপন বা ডেটা ব্রোকারের কাছে বিক্রি হয় না।",
                            "৬. সম্মতি: যেকোনো সময় AI সম্মতি বাতিল করা যায়।"
                        )
                    } else {
                        listOf(
                            "1. What is sent: Only your question and (only if toggled on) a de-identified summary.",
                            "2. Why: Plain-language health education and care navigation.",
                            "3. Protection: Verified app integrity (App Check) and encrypted transport.",
                            "4. Retention: Chat sessions are transient and never sold.",
                            "5. Third parties: Zero ad networks, zero data brokers.",
                            "6. Consent basis: Strictly opt-in; revocable anytime."
                        )
                    }
                    points.forEach { p ->
                        Text(p, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Free Essential Safety vs Optional Premium Care Plan (PRD Section 17.2)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MutedRoseContainer,
                    contentColor = InkPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = DeepPlumDark)
                        Text(
                            if (isBn) "নারীবন্ধু ফ্রি ও প্রিমিয়াম স্বচ্ছতা" else "Always-Free Safety + Optional Care Plan",
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepPlumDark,
                        )
                    }
                    Text(
                        if (isBn) {
                            "চক্র ও লক্ষণ ট্র্যাকিং, স্বাস্থ্য পাঠাগার, জরুরি নিরাপত্তা সতর্কতা এবং ডাক্তার ডিরেক্টরি সবার জন্য চিরকাল ফ্রি। ভবিষ্যতে ঐচ্ছিক প্রিমিয়াম (৳৯৯/মাস) অতিরিক্ত রিপোর্ট সুবিধা দিতে পারে।"
                        } else {
                            "Cycle & symptom tracking, Health Library, Red/Amber safety escalation, and BondhuHelp directory access always remain FREE (never paywalled). Optional future Premium (৳99/month) supports extended insights."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = InkPrimary,
                    )
                }
            }
        }

        // Consent Audit Trail + Data Export + Account Deletion + Sign Out
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            if (isBn) "সম্মতির ইতিহাস (${consentLogs.size})" else "Consent Audit History (${consentLogs.size})",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        TextButton(onClick = { showConsentHistory = !showConsentHistory }) {
                            Text(if (showConsentHistory) (if (isBn) "লুকান" else "Hide") else (if (isBn) "দেখুন" else "View"))
                        }
                    }

                    if (showConsentHistory) {
                        if (consentLogs.isEmpty()) {
                            Text("No consent events logged yet.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            consentLogs.take(10).forEach { log ->
                                Surface(
                                    color = if (log.granted) SafetyGreenContainer else Lavender,
                                    contentColor = InkPrimary,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = "${ConsentType.fromName(log.consentType).enLabel}: ${if (log.granted) "GRANTED" else "WITHDRAWN"} — ${log.summary}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = InkPrimary,
                                        modifier = Modifier.padding(10.dp),
                                    )
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val json = onExportJson()
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_SUBJECT, "NariBondhu Personal Health Data Export")
                                putExtra(Intent.EXTRA_TEXT, json)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, copy.exportDataBtn))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("export_data_btn"),
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(copy.exportDataBtn)
                    }

                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("sign_out_btn"),
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(copy.signOutBtn)
                    }

                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SafetyRed, contentColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("delete_account_btn"),
                    ) {
                        Icon(Icons.Rounded.DeleteForever, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(copy.deleteAccountBtn)
                    }
                }
            }
        }

        // Intended Use & Bangladesh Regulatory Transparency (PRD Section 16.3, 16.4)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Lavender,
                    contentColor = InkPrimary,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Policy, contentDescription = null, tint = DeepPlumDark)
                        Text(
                            if (isBn) "ব্যবহারের উদ্দেশ্য ও বাংলাদেশ নীতিমালা" else "Intended Use & Bangladesh Compliance",
                            style = MaterialTheme.typography.titleSmall,
                            color = DeepPlumDark,
                        )
                    }
                    Text(
                        if (isBn) {
                            "নারীবন্ধু একটি স্বাস্থ্য-সচেতনতা, ব্যক্তিগত রেকর্ড ও যাচাইকৃত ডাক্তার সংযোগ প্ল্যাটফর্ম (১৮+ প্রাপ্তবয়স্কদের জন্য)। এটি কোনো মেডিকেল ডিভাইস নয়, রোগ নির্ণয় বা গর্ভনিরোধক নিশ্চয়তা দেয় না। সকল টেলিমেডিসিন পরামর্শ BMDC টেলিমেডিসিন গাইডলাইন (জুলাই ২০২০) অনুসরণ করে নিবন্ধিত চিকিৎসকদের দ্বারা পরিচালিত হয়।"
                        } else {
                            "NariBondhu is a women's health tracking, education, and care-navigation platform for adults (18+). It is NOT a medical device, does not diagnose or prescribe, and is not for contraception or emergency triage. All clinical consultations on BondhuHelp are conducted by BMDC-registered doctors in alignment with Bangladesh BMDC Telemedicine Guidelines (July 2020) and DGDA boundaries."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = InkPrimary,
                    )
                }
            }
        }
    }

    if (showPinDialog) {
        var pinInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text(if (isBn) "৪-সংখ্যার গোপন পিন" else "4-Digit Discreet PIN") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        if (isBn) "অ্যাপ খোলার সময় আপনার ব্যক্তিগত তথ্য সুরক্ষিত রাখতে ৪ সংখ্যার পিন দিন।"
                        else "Protect your personal screen with a 4-digit PIN when opening the app.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = { raw -> pinInput = raw.filter { it.isDigit() }.take(4) },
                        label = { Text(if (isBn) "৪ সংখ্যার পিন" else "Enter 4 digits") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetPin(pinInput)
                        showPinDialog = false
                    },
                    enabled = pinInput.length == 4,
                ) {
                    Text(copy.saveBtn)
                }
            },
            dismissButton = {
                Row {
                    if (pinEnabled) {
                        TextButton(onClick = {
                            onSetPin(null)
                            showPinDialog = false
                        }) {
                            Text(if (isBn) "পিন বন্ধ করুন" else "Remove PIN", color = SafetyRed)
                        }
                    }
                    TextButton(onClick = { showPinDialog = false }) { Text(copy.cancelBtn) }
                }
            },
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(if (isBn) "সকল স্বাস্থ্য তথ্য মুছে ফেলবেন?" else "Delete All Health Data?") },
            text = {
                Text(
                    if (isBn) "আপনার মাসিক ইতিহাস, লক্ষণ, রেকর্ড, বুকিং ও প্রোফাইল স্থায়ীভাবে মুছে ফেলা হবে।"
                    else "This permanently deletes your cycle entries, symptoms, wellness logs, health records, appointments, and profile."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAllDataAndSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyRed),
                ) {
                    Text(if (isBn) "স্থায়ীভাবে মুছুন" else "Delete Forever")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(copy.cancelBtn) }
            },
        )
    }
}

@Composable
private fun ConsentControlRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag),
        )
    }
}
