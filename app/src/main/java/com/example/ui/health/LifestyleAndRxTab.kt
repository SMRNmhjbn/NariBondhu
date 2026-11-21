package com.example.ui.health

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.EpochDateUtil
import com.example.core.ServiceTierEngine
import com.example.core.TierEntitlements
import com.example.data.AppLanguage
import com.example.data.AppointmentBooking
import com.example.data.BookingStatus
import com.example.data.HealthRecord
import com.example.data.HealthRecordType
import com.example.data.ServiceTier
import com.example.data.SymptomEntry
import com.example.ui.profile.TierLockedFeatureCard
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyGreenContainer

@Composable
fun LifestyleAndRxTab(
    language: AppLanguage,
    entitlements: TierEntitlements,
    healthInterests: List<String>,
    symptoms: List<SymptomEntry>,
    healthRecords: List<HealthRecord>,
    bookings: List<AppointmentBooking>,
    onOpenPlans: () -> Unit,
    onOpenWellnessDialog: () -> Unit,
    onOpenBondhuHelp: () -> Unit,
    onSaveClinicianEPrescription: (
        booking: AppointmentBooking,
        issuedDateText: String,
        clinicalImpression: String,
        regimenNotes: String,
        followUpInstructions: String,
    ) -> Unit,
    onDeleteRecord: (String) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    val context = LocalContext.current
    val ePrescriptions = remember(healthRecords) {
        healthRecords.filter { it.recordType == HealthRecordType.E_PRESCRIPTION.name }
    }
    val guideItems = remember(healthInterests, symptoms) {
        ServiceTierEngine.personalisedLifestyleGuide(healthInterests, symptoms)
    }
    var showCreateRxDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Surface(
                color = Lavender,
                contentColor = InkPrimary,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isBn) "ই-প্রেসক্রিপশন ও জীবনযাত্রা গাইড" else "E-Prescription & Lifestyle Guide",
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepPlumDark,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (isBn) {
                            "টিয়ার ৩ (৳৯০০/মাস) প্ল্যানে অনলাইন ও সরাসরি উভয় ডাক্তার পরামর্শ, যাচাইকৃত ই-প্রেসক্রিপশন এবং সম্পূর্ণ জীবনযাত্রা গাইড অন্তর্ভুক্ত।"
                        } else {
                            "Tier 3 (BDT 900/month) unlocks both Online & Offline doctor visits, verified clinician E-Prescriptions, and a tailored Lifestyle Guide — with full privacy always included."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = InkPrimary,
                    )
                }
            }
        }

        // Section 1: Verified Doctor E-Prescriptions
        item {
            Text(
                text = if (isBn) "যাচাইকৃত ডাক্তার ই-প্রেসক্রিপশন" else "Doctor E-Prescriptions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }

        if (!entitlements.hasEPrescription) {
            item {
                TierLockedFeatureCard(
                    requiredTier = ServiceTier.TIER_3,
                    title = if (isBn) "ডাক্তারের ই-প্রেসক্রিপশন ভল্ট আনলক করুন" else "Unlock Verified Doctor E-Prescriptions",
                    description = if (isBn) {
                        "টিয়ার ৩ প্ল্যানে অনলাইন ও সরাসরি চেম্বার ভিজিটের পর BMDC-নিবন্ধিত ডাক্তারের প্রেসক্রিপশন ডিজিটালভাবে সংরক্ষণ ও শেয়ার করতে পারবেন।"
                    } else {
                        "Store, review, and share structured digital prescriptions from your BMDC-verified BondhuHelp consultations in Tier 3."
                    },
                    highlights = if (isBn) {
                        listOf(
                            "অনলাইন এবং সরাসরি উভয় কনসালটেশন সুবিধা",
                            "ওষুধের নিয়ম, ডোজ ও ফলো-আপ তারিখসহ ই-প্রেসক্রিপশন",
                            "গোপনীয় ও এনক্রিপ্টেড প্রাইভেসি সকল প্ল্যানেই অন্তর্ভুক্ত",
                        )
                    } else {
                        listOf(
                            "BOTH Online and Offline (In-Person) doctor consultations",
                            "Structured dosage, follow-up & safety-net instructions",
                            "One-tap prescription export; full privacy included",
                        )
                    },
                    language = language,
                    onOpenPlans = onOpenPlans,
                    testTag = "locked_eprescription_card",
                )
            }
        } else {
            item {
                Surface(
                    color = SafetyGreenContainer,
                    contentColor = InkPrimary,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Rounded.Medication, contentDescription = null, tint = SafetyGreen)
                            Text(
                                text = if (isBn) "টিয়ার ৩ ই-প্রেসক্রিপশন সক্রিয়" else "Tier 3 E-Prescription Vault Active",
                                style = MaterialTheme.typography.titleSmall,
                                color = SafetyGreen,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Text(
                            text = if (isBn) {
                                "সব ই-প্রেসক্রিপশন কেবল BMDC-নিবন্ধিত ডাক্তারের পরামর্শের পর যোগ করা হয়। নারীবন্ধু AI কখনো ওষুধ লিখে দেয় না।"
                            } else {
                                "E-Prescriptions come strictly from verified BMDC clinicians after a BondhuHelp consultation — NariBondhu AI never prescribes medication."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = InkPrimary,
                        )
                        val activeConsultations = bookings.filter { it.status != BookingStatus.CANCELLED.name }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (activeConsultations.isNotEmpty()) {
                                Button(
                                    onClick = { showCreateRxDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepPlum, contentColor = Color.White),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("record_eprescription_btn"),
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = null)
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (isBn) "প্রেসক্রিপশন যোগ করুন" else "Log Consultation Rx")
                                }
                            } else {
                                OutlinedButton(
                                    onClick = onOpenBondhuHelp,
                                    border = BorderStroke(1.dp, DeepPlum),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPlumDark),
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Icon(Icons.Rounded.LocalHospital, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (isBn) "প্রথমে ডাক্তার বুক করুন" else "Book Consultation for Rx")
                                }
                            }
                        }
                    }
                }
            }

            if (ePrescriptions.isEmpty()) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = if (isBn) {
                                "এখনো কোনো ই-প্রেসক্রিপশন সংরক্ষিত নেই। বন্ধুহেল্প পরামর্শের পর আপনার ডাক্তারের ই-প্রেসক্রিপশন এখানে দেখা যাবে।"
                            } else {
                                "No E-Prescriptions recorded yet. After booking a BondhuHelp consultation, tap 'Log Consultation Rx' to store your doctor's verified prescription."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            } else {
                items(ePrescriptions, key = { it.id.ifBlank { "${it.title}_${it.dateText}" } }) { rx ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(rx.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                    if (rx.dateText.isNotBlank()) {
                                        Text(rx.dateText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = { onDeleteRecord(rx.id) }) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete e-prescription")
                                }
                            }
                            Text(rx.details, style = MaterialTheme.typography.bodySmall)
                            OutlinedButton(
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "NariBondhu E-Prescription: ${rx.title}")
                                        putExtra(Intent.EXTRA_TEXT, "${rx.title}\nDate: ${rx.dateText}\n\n${rx.details}")
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share E-Prescription"))
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Rounded.Share, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(if (isBn) "ই-প্রেসক্রিপশন শেয়ার / সেভ করুন" else "Share / Save E-Prescription")
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Personalised Lifestyle Guide
        item {
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isBn) "আপনার ব্যক্তিগত জীবনযাত্রা গাইড" else "Personalised Lifestyle Guide",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }

        if (!entitlements.hasLifestyleGuide) {
            item {
                TierLockedFeatureCard(
                    requiredTier = ServiceTier.TIER_3,
                    title = if (isBn) "সম্পূর্ণ জীবনযাত্রা ও পুষ্টি গাইড আনলক করুন" else "Unlock Your Personalised Lifestyle Guide",
                    description = if (isBn) {
                        "পিসিওএস, পিএমএস ও মাসিক চক্রের ধারা অনুযায়ী বাংলাদেশের সহজলভ্য খাবার, ঘুম, ব্যায়াম ও আয়রন-হাইড্রেশন পরিকল্পনা পান।"
                    } else {
                        "Get a practical Bangladesh-friendly daily plan covering low-GI nutrition, gentle movement, sleep rhythm, and hydration tailored to your cycle & PCOS/PMS logs."
                    },
                    highlights = if (isBn) {
                        listOf(
                            "দেশীয় লো-জিআই খাবার ও আয়রন প্লেট পদ্ধতি",
                            "৩০ মিনিট হাঁটা, ঘুমের রুটিন ও মানসিক চাপ নিয়ন্ত্রণ",
                            "টিয়ার ৩ (৳৯০০/মাস)-এ ই-প্রেসক্রিপশন ও উভয় কনসালটেশন সহ",
                        )
                    } else {
                        listOf(
                            "Low-GI Deshi plate & iron-rich meal pairing",
                            "30-minute movement, 7–8h sleep anchor & stress tips",
                            "Included in Tier 3 (BDT 900/mo) alongside E-Prescription & Online + Offline visits",
                        )
                    },
                    language = language,
                    onOpenPlans = onOpenPlans,
                    testTag = "locked_lifestyle_guide_card",
                )
            }
        } else {
            items(guideItems, key = { it.id }) { habit ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MutedRoseContainer,
                        contentColor = InkPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = DeepPlum,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = habit.focusTag,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                        Text(
                            text = if (isBn) habit.bnTitle else habit.enTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = DeepPlumDark,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = SafetyGreen,
                                modifier = Modifier.padding(top = 2.dp).size(16.dp),
                            )
                            Text(
                                text = if (isBn) habit.bnAction else habit.enAction,
                                style = MaterialTheme.typography.bodySmall,
                                color = InkPrimary,
                            )
                        }
                        Text(
                            text = if (isBn) habit.bnWhyItHelps else habit.enWhyItHelps,
                            style = MaterialTheme.typography.labelMedium,
                            color = InkSecondary,
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = onOpenWellnessDialog,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Icon(Icons.Rounded.SelfImprovement, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (isBn) "আজকের জীবনযাত্রা ও ঘুম নোট করুন" else "Track Today's Lifestyle & Sleep")
                }
            }
        }
    }

    if (showCreateRxDialog) {
        val activeBookings = bookings.filter { it.status != BookingStatus.CANCELLED.name }
        var pickedBooking by remember(activeBookings) { mutableStateOf(activeBookings.first()) }
        var impression by remember { mutableStateOf("Cycle irregularity & PCOS symptom follow-up") }
        var regimen by remember { mutableStateOf("1. Myo-Inositol + Folic Acid sachet once daily after dinner x 30 days.\n2. Low-GI meal plan & 30 min brisk walk daily.") }
        var followUp by remember { mutableStateOf("Review in 4 weeks with updated cycle calendar. Seek urgent care if heavy bleeding > 7 days or severe pain.") }

        AlertDialog(
            onDismissRequest = { showCreateRxDialog = false },
            title = { Text(if (isBn) "ডাক্তার ই-প্রেসক্রিপশন সংরক্ষণ" else "Save Verified Doctor E-Prescription") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isBn) "আপনার কনসালটেশন বেছে নিন:" else "Select Verified Consultation:",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    activeBookings.take(3).forEach { b ->
                        FilterChip(
                            selected = pickedBooking.id == b.id,
                            onClick = { pickedBooking = b },
                            label = { Text("${b.doctorName} (${b.consultationType})") },
                        )
                    }
                    OutlinedTextField(
                        value = impression,
                        onValueChange = { impression = it.take(180) },
                        label = { Text(if (isBn) "ক্লিনিক্যাল সারসংক্ষেপ" else "Clinical Impression") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = regimen,
                        onValueChange = { regimen = it.take(450) },
                        label = { Text(if (isBn) "ওষুধ ও ডোজ নির্দেশনা" else "Doctor Regimen & Dosage") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = followUp,
                        onValueChange = { followUp = it.take(250) },
                        label = { Text(if (isBn) "ফলো-আপ নির্দেশনা" else "Follow-up & Safety Advice") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveClinicianEPrescription(
                            pickedBooking,
                            EpochDateUtil.formatShortDate(EpochDateUtil.todayEpochDay()),
                            impression,
                            regimen,
                            followUp,
                        )
                        showCreateRxDialog = false
                    },
                    enabled = regimen.isNotBlank(),
                ) {
                    Text(if (isBn) "সংরক্ষণ করুন" else "Save E-Rx")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateRxDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            },
        )
    }
}
