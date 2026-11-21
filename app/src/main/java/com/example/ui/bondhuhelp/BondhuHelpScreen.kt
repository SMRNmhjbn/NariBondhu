package com.example.ui.bondhuhelp

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AppLanguage
import com.example.data.AppointmentBooking
import com.example.data.BondhuHelpCatalog
import com.example.data.BookingStatus
import com.example.data.ConsultationType
import com.example.data.DoctorProfile
import com.example.data.HealthRecord
import com.example.ui.SummaryPrepState
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyGreenContainer
import com.example.ui.theme.SafetyRed

@Composable
fun BondhuHelpScreen(
    language: AppLanguage,
    userCity: String,
    healthRecords: List<HealthRecord>,
    bookings: List<AppointmentBooking>,
    summaryPrepState: SummaryPrepState,
    aiConsentGranted: Boolean,
    onGenerateSummary: () -> Unit,
    onBookAppointment: (
        doctor: DoctorProfile,
        consultationType: String,
        slotLabel: String,
        shareHealthSummary: Boolean,
        selectedRecordIds: List<String>,
        notesForDoctor: String,
    ) -> Unit,
    onUpdateBookingStatus: (bookingId: String, status: BookingStatus, slotLabel: String) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    var subTab by remember { mutableIntStateOf(0) } // 0 = Find Doctor, 1 = My Appointments
    var selectedDoctor by remember { mutableStateOf<DoctorProfile?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCity by remember(userCity) { mutableStateOf("All Cities") }
    var selectedExpertise by remember { mutableStateOf("All Expertise") }
    var onlineOnly by remember { mutableStateOf(false) }

    selectedDoctor?.let { doc ->
        DoctorBookingDetailScreen(
            doctor = doc,
            language = language,
            healthRecords = healthRecords,
            summaryPrepState = summaryPrepState,
            aiConsentGranted = aiConsentGranted,
            onGenerateSummary = onGenerateSummary,
            onBack = { selectedDoctor = null },
            onConfirmBooking = { consultType, slot, shareSummary, recIds, notes ->
                onBookAppointment(doc, consultType, slot, shareSummary, recIds, notes)
                selectedDoctor = null
                subTab = 1
            },
        )
        return
    }

    val filteredDoctors = remember(searchQuery, selectedCity, selectedExpertise, onlineOnly) {
        BondhuHelpCatalog.doctors.filter { doc ->
            val matchesCity = selectedCity == "All Cities" || doc.city.equals(selectedCity, ignoreCase = true)
            val matchesExp = selectedExpertise == "All Expertise" || doc.expertiseTags.contains(selectedExpertise)
            val matchesOnline = !onlineOnly || doc.offersOnline
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                doc.name.lowercase().contains(q) ||
                doc.area.lowercase().contains(q) ||
                doc.clinicName.lowercase().contains(q) ||
                doc.specialty.lowercase().contains(q)
            matchesCity && matchesExp && matchesOnline && matchesQuery
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                label = { Text(if (isBn) "ডাক্তার খুঁজুন" else "Find a Gynaecologist") },
                modifier = Modifier.testTag("bondhu_tab_doctors"),
            )
            FilterChip(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                label = {
                    Text(
                        if (isBn) "আমার অ্যাপয়েন্টমেন্ট (${bookings.size})"
                        else "My Appointments (${bookings.size})"
                    )
                },
                modifier = Modifier.testTag("bondhu_tab_bookings"),
            )
        }

        if (subTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Lavender,
                            contentColor = InkPrimary,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = R.drawable.img_bondhuhelp_care),
                                contentDescription = "BondhuHelp verified consultation",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(128.dp)
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                                contentScale = ContentScale.Crop,
                            )
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(Icons.Rounded.Verified, contentDescription = null, tint = SafetyGreen, modifier = Modifier.size(18.dp))
                                    Text(
                                        text = if (isBn) "BMDC-যাচাইকৃত নারী স্বাস্থ্য বিশেষজ্ঞ" else "BMDC-Verified Gynaecologists (Bangladesh)",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = DeepPlumDark,
                                    )
                                }
                                Text(
                                    text = if (isBn) {
                                        "স্বচ্ছ ফি, অনলাইন ও চেম্বার স্লট এবং কেবল আপনার অনুমতিতে তথ্য শেয়ার।"
                                    } else {
                                        "Verified credentials, upfront BDT fees, and zero data shared unless you explicitly check the box."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary,
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        placeholder = {
                            Text(if (isBn) "নাম, এলাকা (ধানমন্ডি, বনানী) বা ক্লিনিক..." else "Search name, area (Dhanmondi, Banani), clinic...")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("bondhu_search_input"),
                    )

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BondhuHelpCatalog.cities.forEach { city ->
                            FilterChip(
                                selected = selectedCity == city,
                                onClick = { selectedCity = city },
                                label = { Text(city) },
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = onlineOnly,
                            onClick = { onlineOnly = !onlineOnly },
                            label = { Text(if (isBn) "অনলাইন কনসালটেশন" else "Online Only") },
                            leadingIcon = { Icon(Icons.Rounded.VideoCall, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        )
                        BondhuHelpCatalog.expertiseOptions.forEach { exp ->
                            FilterChip(
                                selected = selectedExpertise == exp,
                                onClick = { selectedExpertise = exp },
                                label = { Text(exp) },
                            )
                        }
                    }
                }

                if (filteredDoctors.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    if (isBn) "কোনো ডাক্তার খুঁজে পাওয়া যায়নি" else "No doctors matched your filters",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                TextButton(onClick = {
                                    searchQuery = ""
                                    selectedCity = "All Cities"
                                    selectedExpertise = "All Expertise"
                                    onlineOnly = false
                                }) {
                                    Text(if (isBn) "ফিল্টার রিসেট করুন" else "Reset All Filters")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredDoctors, key = { it.doctorId }) { doc ->
                        DoctorDirectoryCard(
                            doctor = doc,
                            language = language,
                            onSelect = { selectedDoctor = doc },
                        )
                    }
                }
            }
        } else {
            AppointmentsHistoryTab(
                language = language,
                bookings = bookings,
                onUpdateBookingStatus = onUpdateBookingStatus,
            )
        }
    }
}

@Composable
private fun DoctorDirectoryCard(
    doctor: DoctorProfile,
    language: AppLanguage,
    onSelect: () -> Unit,
) {
    val isBn = language == AppLanguage.BN
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
            .clickable(onClick = onSelect),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBn) doctor.nameBn else doctor.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = doctor.qualifications,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = doctor.specialty,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Surface(color = SafetyGreenContainer, contentColor = SafetyGreen, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = doctor.bmdcNumber,
                        style = MaterialTheme.typography.labelSmall,
                        color = SafetyGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "${doctor.clinicName} • ${doctor.area}, ${doctor.city}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text(
                text = if (isBn) {
                    "অভিজ্ঞতা: ${doctor.experienceYears} বছর • অনলাইন: ৳${doctor.onlineFeeBdt} | চেম্বার: ৳${doctor.inPersonFeeBdt}"
                } else {
                    "${doctor.experienceYears} yrs exp • Online ৳${doctor.onlineFeeBdt} | In-Person ৳${doctor.inPersonFeeBdt}"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (isBn) "পরবর্তী স্লট: ${doctor.availableSlots.firstOrNull().orEmpty()}"
                    else "Next: ${doctor.availableSlots.firstOrNull().orEmpty()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = onSelect,
                    modifier = Modifier.testTag("doctor_book_${doctor.doctorId}"),
                ) {
                    Text(if (isBn) "বুক করুন" else "View & Book")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DoctorBookingDetailScreen(
    doctor: DoctorProfile,
    language: AppLanguage,
    healthRecords: List<HealthRecord>,
    summaryPrepState: SummaryPrepState,
    aiConsentGranted: Boolean,
    onGenerateSummary: () -> Unit,
    onBack: () -> Unit,
    onConfirmBooking: (
        consultationType: String,
        slotLabel: String,
        shareHealthSummary: Boolean,
        selectedRecordIds: List<String>,
        notesForDoctor: String,
    ) -> Unit,
) {
    BackHandler { onBack() }
    val isBn = language == AppLanguage.BN
    var consultType by remember { mutableStateOf(ConsultationType.ONLINE.name) }
    var selectedSlot by remember { mutableStateOf(doctor.availableSlots.firstOrNull() ?: "Tomorrow • 10:30 AM") }
    var shareSummary by remember { mutableStateOf(false) }
    val selectedRecordIds = remember { mutableStateListOf<String>() }
    var notesForDoctor by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("booking_back_btn")) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (isBn) doctor.nameBn else doctor.name,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Lavender,
                contentColor = InkPrimary,
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(doctor.qualifications, style = MaterialTheme.typography.titleSmall, color = DeepPlumDark)
                Text("${doctor.bmdcNumber} • ${doctor.experienceYears} years experience", style = MaterialTheme.typography.bodySmall, color = InkPrimary)
                Text("${doctor.clinicName} — ${doctor.clinicDirections}", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
                Text("Languages: ${doctor.languages.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = InkSecondary)
            }
        }

        Text(if (isBn) "পরামর্শের ধরন বেছে নিন" else "Consultation Format", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = consultType == ConsultationType.ONLINE.name,
                onClick = { consultType = ConsultationType.ONLINE.name },
                label = { Text("Online (৳${doctor.onlineFeeBdt})") },
            )
            FilterChip(
                selected = consultType == ConsultationType.IN_PERSON.name,
                onClick = { consultType = ConsultationType.IN_PERSON.name },
                label = { Text("In-Person (৳${doctor.inPersonFeeBdt})") },
            )
        }

        Text(if (isBn) "সময়সূচি (স্লট) নির্বাচন করুন" else "Available Time Slots", style = MaterialTheme.typography.titleMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            doctor.availableSlots.forEach { slot ->
                FilterChip(
                    selected = selectedSlot == slot,
                    onClick = { selectedSlot = slot },
                    label = { Text(slot) },
                )
            }
        }

        if (aiConsentGranted) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Rounded.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                if (isBn) "ভিজিট প্রস্তুতি সহায়ক" else "Prepare Visit Summary & Questions",
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                        TextButton(onClick = onGenerateSummary) {
                            Text(if (isBn) "তৈরি করুন" else "Generate")
                        }
                    }
                    when (summaryPrepState) {
                        SummaryPrepState.Loading -> CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        is SummaryPrepState.Ready -> {
                            Text(summaryPrepState.summary, style = MaterialTheme.typography.bodySmall)
                            summaryPrepState.questions.forEach { q ->
                                Text("• $q", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        is SummaryPrepState.Error -> Text(summaryPrepState.message, style = MaterialTheme.typography.bodySmall, color = SafetyRed)
                        SummaryPrepState.Idle -> Unit
                    }
                }
            }
        }

        // Explicit Consent-Based Health Record Sharing (PRD Section 12 & 15)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (isBn) "ডাক্তারের সাথে স্বাস্থ্য রেকর্ড শেয়ার করুন" else "Share Health Records with This Doctor",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            if (isBn) "বন্ধ থাকলে কোনো ব্যক্তিগত তথ্য শেয়ার হবে না।" else "Off by default. Select exactly which records to share.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = shareSummary,
                        onCheckedChange = { shareSummary = it },
                        modifier = Modifier.testTag("booking_share_switch"),
                    )
                }

                if (shareSummary && healthRecords.isNotEmpty()) {
                    healthRecords.forEach { rec ->
                        val checked = selectedRecordIds.contains(rec.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (checked) selectedRecordIds.remove(rec.id) else selectedRecordIds.add(rec.id)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    if (it) selectedRecordIds.add(rec.id) else selectedRecordIds.remove(rec.id)
                                },
                            )
                            Text("${rec.title} (${rec.recordType})", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else if (shareSummary) {
                    Text(
                        if (isBn) "চক্র ও লক্ষণের সারাংশ শেয়ার করার জন্য নির্বাচিত হয়েছে।"
                        else "Your cycle & symptom summary will be shared for this appointment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SafetyGreen,
                    )
                }
            }
        }

        OutlinedTextField(
            value = notesForDoctor,
            onValueChange = { notesForDoctor = it.take(500) },
            label = { Text(if (isBn) "ডাক্তারের জন্য সংক্ষিপ্ত নোট (ঐচ্ছিক)" else "Notes for Doctor (Optional)") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                onConfirmBooking(
                    consultType,
                    selectedSlot,
                    shareSummary,
                    if (shareSummary) selectedRecordIds.toList() else emptyList(),
                    notesForDoctor,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("confirm_booking_btn"),
        ) {
            Text(
                if (isBn) "অ্যাপয়েন্টমেন্ট নিশ্চিত করুন"
                else "Confirm Appointment • ৳${if (consultType == ConsultationType.ONLINE.name) doctor.onlineFeeBdt else doctor.inPersonFeeBdt}"
            )
        }
    }
}

@Composable
private fun AppointmentsHistoryTab(
    language: AppLanguage,
    bookings: List<AppointmentBooking>,
    onUpdateBookingStatus: (bookingId: String, status: BookingStatus, slotLabel: String) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    var rescheduleTarget by remember { mutableStateOf<AppointmentBooking?>(null) }
    var cancelTarget by remember { mutableStateOf<AppointmentBooking?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (bookings.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            if (isBn) "এখনো কোনো অ্যাপয়েন্টমেন্ট নেই" else "No Appointments Booked Yet",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            if (isBn) "আপনি যখন প্রস্তুত বোধ করবেন, যাচাইকৃত ডাক্তারদের তালিকা থেকে একটি সময় বেছে নিন।"
                            else "Browse verified gynaecologists above whenever you'd like to book a consultation.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        } else {
            items(bookings, key = { it.id }) { booking ->
                val statusEnum = BookingStatus.fromName(booking.status)
                val isActive = statusEnum == BookingStatus.CONFIRMED || statusEnum == BookingStatus.RESCHEDULE_REQUESTED
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(booking.doctorName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Surface(color = Lavender, contentColor = DeepPlumDark, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = if (isBn) statusEnum.bnLabel else statusEnum.enLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DeepPlumDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                        Text("${booking.clinicName}, ${booking.city}", style = MaterialTheme.typography.bodySmall)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Rounded.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Text("${booking.slotLabel} • ${booking.consultationType} • ৳${booking.feeBdt}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (booking.sharedSummaryGranted) {
                            Text(
                                text = if (isBn) "সম্মতিসহ ${booking.sharedRecordCount}টি রেকর্ড শেয়ার করা হয়েছে"
                                else "Shared summary + ${booking.sharedRecordCount} record(s) with consent",
                                style = MaterialTheme.typography.bodySmall,
                                color = SafetyGreen,
                            )
                        }
                        if (isActive) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = { rescheduleTarget = booking }) {
                                    Text(if (isBn) "সময় পরিবর্তন" else "Reschedule")
                                }
                                OutlinedButton(
                                    onClick = { cancelTarget = booking },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SafetyRed),
                                ) {
                                    Text(if (isBn) "বাতিল" else "Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    rescheduleTarget?.let { target ->
        val doctor = BondhuHelpCatalog.doctors.firstOrNull { it.doctorId == target.doctorId }
        val slots = doctor?.availableSlots ?: listOf("Tomorrow • 11:00 AM", "Mon • 04:00 PM")
        var pickedSlot by remember(target.id) { mutableStateOf(slots.first()) }
        AlertDialog(
            onDismissRequest = { rescheduleTarget = null },
            title = { Text(if (isBn) "নতুন সময় বেছে নিন" else "Choose New Slot") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    slots.forEach { slot ->
                        FilterChip(
                            selected = pickedSlot == slot,
                            onClick = { pickedSlot = slot },
                            label = { Text(slot) },
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateBookingStatus(target.id, BookingStatus.RESCHEDULE_REQUESTED, pickedSlot)
                    rescheduleTarget = null
                }) {
                    Text(if (isBn) "নিশ্চিত" else "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { rescheduleTarget = null }) {
                    Text(if (isBn) "বাতিল" else "Close")
                }
            },
        )
    }

    cancelTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { cancelTarget = null },
            title = { Text(if (isBn) "অ্যাপয়েন্টমেন্ট বাতিল করবেন?" else "Cancel Appointment?") },
            text = { Text("${target.doctorName} • ${target.slotLabel}") },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateBookingStatus(target.id, BookingStatus.CANCELLED, target.slotLabel)
                        cancelTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyRed),
                ) {
                    Text(if (isBn) "হ্যাঁ, বাতিল করুন" else "Cancel Booking")
                }
            },
            dismissButton = {
                TextButton(onClick = { cancelTarget = null }) {
                    Text(if (isBn) "ফিরে যান" else "Keep")
                }
            },
        )
    }
}
