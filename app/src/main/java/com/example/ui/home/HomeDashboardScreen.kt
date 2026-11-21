package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.FolderShared
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.AppStrings
import com.example.core.CycleStatistics
import com.example.core.EpochDateUtil
import com.example.core.TierEntitlements
import com.example.data.AppLanguage
import com.example.data.AppointmentBooking
import com.example.data.BookingStatus
import com.example.data.SafetyTier
import com.example.data.ServiceTier
import com.example.data.UserProfile
import com.example.ui.profile.ActiveTierStatusPill
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SafetyAmberContainer
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.SafetyRedContainer
import com.example.ui.theme.WarmIvory
import kotlin.math.roundToInt

enum class HealthSubTab {
    CYCLE, PCOS_PMS, WELLNESS, LIFESTYLE_RX, LIBRARY, RECORDS
}

@Composable
fun HomeDashboardScreen(
    profile: UserProfile?,
    language: AppLanguage,
    discreetMode: Boolean,
    cycleStats: CycleStatistics,
    recentSafetyTier: SafetyTier,
    entitlements: TierEntitlements,
    weeklyTokensRemaining: Int,
    upcomingBookings: List<AppointmentBooking>,
    onToggleLanguage: (AppLanguage) -> Unit,
    onToggleDiscreet: (Boolean) -> Unit,
    onOpenPlans: () -> Unit,
    onQuickLogPeriod: () -> Unit,
    onQuickLogSymptom: () -> Unit,
    onOpenAi: () -> Unit,
    onOpenBondhuHelp: () -> Unit,
    onOpenHealthModule: (HealthSubTab) -> Unit,
) {
    val copy = AppStrings.of(language)
    val isBn = language == AppLanguage.BN
    val firstName = profile?.displayName?.substringBefore(" ").orEmpty().ifBlank {
        if (isBn) "বন্ধু" else "Friend"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Greeting & Discreet / Language controls
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DeepPlum,
                    contentColor = WarmIvory,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(DeepPlum, Color(0xFF6E4B6C))
                            )
                        )
                        .padding(20.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) "স্বাগতম, $firstName" else "Hello, $firstName",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = WarmIvory,
                                )
                                Text(
                                    text = copy.tagline,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Lavender,
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = language == AppLanguage.BN,
                                    onClick = {
                                        onToggleLanguage(if (isBn) AppLanguage.EN else AppLanguage.BN)
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color.White.copy(alpha = 0.18f),
                                        labelColor = WarmIvory,
                                        selectedContainerColor = WarmIvory,
                                        selectedLabelColor = DeepPlumDark,
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = language == AppLanguage.BN,
                                        borderColor = WarmIvory.copy(alpha = 0.45f),
                                        selectedBorderColor = WarmIvory,
                                    ),
                                    label = {
                                        Text(
                                            if (isBn) "EN" else "বাংলা",
                                            fontWeight = FontWeight.Bold,
                                        )
                                    },
                                    modifier = Modifier.testTag("home_lang_toggle"),
                                )
                            }
                        }

                        // 1-Tap Discreet Privacy Veil toggle
                        Surface(
                            color = Color.White.copy(alpha = 0.16f),
                            contentColor = WarmIvory,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleDiscreet(!discreetMode) }
                                .testTag("home_discreet_toggle"),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(
                                        imageVector = if (discreetMode) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                        contentDescription = null,
                                        tint = WarmIvory,
                                    )
                                    Text(
                                        text = if (discreetMode) copy.discreetOn else copy.discreetOff,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = WarmIvory,
                                    )
                                }
                                Text(
                                    text = if (isBn) "ট্যাপ করুন" else "Tap to switch",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Lavender,
                                )
                            }
                        }

                        // Active Care Tier pill + Privacy guarantee note
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ActiveTierStatusPill(
                                tier = entitlements.tier,
                                weeklyTokensRemaining = weeklyTokensRemaining,
                                language = language,
                                onClick = onOpenPlans,
                            )
                            TextButton(
                                onClick = onOpenPlans,
                                colors = ButtonDefaults.textButtonColors(contentColor = WarmIvory),
                                modifier = Modifier.testTag("home_manage_plans_btn"),
                            ) {
                                Text(if (isBn) "প্ল্যান দেখুন" else "All Plans")
                            }
                        }
                    }
                }
            }
        }

        if (discreetMode) {
            item {
                Surface(
                    color = Lavender,
                    contentColor = DeepPlumDark,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Rounded.Shield, contentDescription = null, tint = DeepPlumDark)
                        Text(copy.discreetHiddenBanner, style = MaterialTheme.typography.bodySmall, color = DeepPlumDark)
                    }
                }
            }
        }

        // Proactive safety escalation banner (PRD Section 17.2: essential safety guidance always free & visible)
        if (recentSafetyTier != SafetyTier.GREEN) {
            item {
                val isRed = recentSafetyTier == SafetyTier.RED
                Surface(
                    color = if (isRed) SafetyRedContainer else SafetyAmberContainer,
                    contentColor = InkPrimary,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, if (isRed) SafetyRed else SafetyAmber, RoundedCornerShape(18.dp)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                Icons.Rounded.WarningAmber,
                                contentDescription = null,
                                tint = if (isRed) SafetyRed else SafetyAmber,
                            )
                            Text(
                                text = copy.safetyBannerTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isRed) SafetyRed else SafetyAmber,
                            )
                        }
                        Text(
                            text = if (isRed) {
                                if (isBn) "আপনার সাম্প্রতিক নোটে জরুরি লক্ষণ দেখা যাচ্ছে। অনুগ্রহ করে দ্রুত জরুরি চিকিৎসকের পরামর্শ নিন বা ৯৯৯-এ কল করুন।"
                                else "Your recent symptom logs mention potentially urgent signs. Please seek urgent medical attention or call 999."
                            } else {
                                if (isBn) "আপনার সাম্প্রতিক কিছু তীব্র লক্ষণ নথিভুক্ত হয়েছে। একজন যোগ্য স্ত্রীরোগ বিশেষজ্ঞের পরামর্শ নেওয়ার অনুরোধ করা হচ্ছে।"
                                else "You logged recurring or severe symptoms recently. Consulting a verified gynaecologist on BondhuHelp is recommended."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkPrimary,
                        )
                        if (!isRed) {
                            Button(
                                onClick = onOpenBondhuHelp,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SafetyAmber,
                                    contentColor = Color.White,
                                ),
                            ) {
                                Text(copy.consultDoctorBtn)
                            }
                        }
                    }
                }
            }
        }

        // Easy Daily Quick Actions (Designed for zero tech expertise)
        item {
            Text(
                text = copy.quickActions,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                QuickActionTile(
                    icon = Icons.Rounded.CalendarMonth,
                    label = if (discreetMode) (if (isBn) "দৈনিক এন্ট্রি" else "Daily Log") else copy.logPeriod,
                    containerColor = MutedRoseContainer,
                    onClick = onQuickLogPeriod,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_log_period"),
                )
                QuickActionTile(
                    icon = Icons.Rounded.EditNote,
                    label = copy.logSymptom,
                    containerColor = Lavender,
                    onClick = onQuickLogSymptom,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_log_symptom"),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                QuickActionTile(
                    icon = Icons.Rounded.Psychology,
                    label = copy.askAi,
                    containerColor = Lavender,
                    onClick = onOpenAi,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_ask_ai"),
                )
                QuickActionTile(
                    icon = Icons.Rounded.LocalHospital,
                    label = copy.findDoctor,
                    containerColor = MutedRoseContainer,
                    onClick = onOpenBondhuHelp,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_find_doctor"),
                )
            }
        }

        // Menstrual & Cycle Overview Card (respects Discreet Mode!)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (discreetMode) (if (isBn) "ব্যক্তিগত স্বাস্থ্য সারাংশ" else "Personal Health Summary") else copy.cycleOverview,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        TextButton(onClick = { onOpenHealthModule(HealthSubTab.CYCLE) }) {
                            Text(if (isBn) "বিস্তারিত" else "Open Calendar")
                        }
                    }

                    if (discreetMode) {
                        Text(
                            text = if (isBn) "গোপনীয় মোড চালু আছে। আপনার ক্যালেন্ডার দেখতে 'গোপনীয় মোড বন্ধ' করুন বা ক্যালেন্ডার খুলুন।"
                            else "Details are gently hidden while Discreet Mode is on. Tap Open Calendar anytime.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else if (!entitlements.hasCycleAnalysis) {
                        Text(
                            text = if (isBn) {
                                "নথিভুক্ত পিরিয়ড এন্ট্রি: ${cycleStats.recordedPeriodCount}টি (ফ্রি প্ল্যানে ক্যালেন্ডার চালু আছে)।"
                            } else {
                                "Recorded cycles: ${cycleStats.recordedPeriodCount} (Period Calendar & Logs included in Free Plan)."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Surface(
                            color = MutedRoseContainer,
                            contentColor = DeepPlumDark,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = if (isBn) {
                                        "গভীর চক্র বিশ্লেষণ ও সম্ভাব্য পূর্বাভাস — টিয়ার ১ (৳২০০/মাস)"
                                    } else {
                                        "Unlock Cycle Analysis & Predictions in Tier 1 (BDT 200/mo)"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DeepPlumDark,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f),
                                )
                                TextButton(onClick = onOpenPlans) {
                                    Text(if (isBn) "আপগ্রেড" else "Upgrade", color = DeepPlum, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        if (cycleStats.estimatedWindowStartEpochDay != null && cycleStats.estimatedWindowEndEpochDay != null) {
                            val startStr = EpochDateUtil.formatEpochDay(cycleStats.estimatedWindowStartEpochDay, isBn)
                            val endStr = EpochDateUtil.formatEpochDay(cycleStats.estimatedWindowEndEpochDay, isBn)
                            Text(
                                text = if (isBn) "সম্ভাব্য পরবর্তী সময়: $startStr – $endStr" else "Estimated next window: $startStr – $endStr",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = if (isBn) {
                                    "নিশ্চয়তা: ${cycleStats.confidence.bnLabel} • গড়: ${cycleStats.averageLengthDays?.roundToInt()} দিন"
                                } else {
                                    "${cycleStats.confidence.enLabel} • Median: ${cycleStats.medianLengthDays?.roundToInt()} days (${cycleStats.minLengthDays}–${cycleStats.maxLengthDays}d)"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(
                            text = if (isBn) cycleStats.uncertaintyNoteBn else cycleStats.uncertaintyNoteEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        // Reminders & Upcoming Appointments
        val activeBookings = upcomingBookings.filter {
            it.status == BookingStatus.CONFIRMED.name || it.status == BookingStatus.RESCHEDULE_REQUESTED.name
        }
        if (profile?.remindersEnabled == true || activeBookings.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Lavender,
                        contentColor = InkPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = DeepPlumDark)
                            Text(
                                text = if (isBn) "রিমাইন্ডার ও আসন্ন পরামর্শ" else "Reminders & Upcoming Care",
                                style = MaterialTheme.typography.titleMedium,
                                color = DeepPlumDark,
                            )
                        }
                        if (activeBookings.isNotEmpty()) {
                            val next = activeBookings.first()
                            Text(
                                text = "${next.doctorName} • ${next.slotLabel} (${next.consultationType})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = InkPrimary,
                            )
                        } else {
                            Text(
                                text = if (isBn) "সপ্তাহে একবার লক্ষণ বা সুস্থতার নোট আপডেট করতে পারেন।"
                                else "Discreet reminder: take 30 seconds to log today's wellness or symptoms.",
                                style = MaterialTheme.typography.bodySmall,
                                color = InkSecondary,
                            )
                        }
                    }
                }
            }
        }

        // All Comprehensive Health Modules (PRD Section 3 & 13.1 — not just period tracking!)
        item {
            Text(
                text = copy.modulesTitle,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ModuleCard(
                    icon = Icons.Rounded.CalendarMonth,
                    title = if (isBn) "মাসিক ও চক্র ট্র্যাকিং" else "Menstrual Health & Cycle History",
                    subtitle = if (isBn) "শুরু-শেষ, স্পটিং এবং পরিবর্তনশীল চক্রের হিসাব" else "Period calendar, separate spotting, and range-based cycle stats",
                    onClick = { onOpenHealthModule(HealthSubTab.CYCLE) },
                )
                ModuleCard(
                    icon = Icons.Rounded.Spa,
                    title = if (isBn) "পিসিওএস ও পিএমএস সহায়তা" else "PCOS & PMS Specialised Support",
                    subtitle = if (isBn) "টিয়ার ১ (৳২০০/মাস): ব্যক্তিগত লক্ষণ ও ভিজিট প্রস্তুতি" else "Tier 1 (BDT 200/mo): Personalised pattern tracking & appointment prep",
                    onClick = { onOpenHealthModule(HealthSubTab.PCOS_PMS) },
                )
                ModuleCard(
                    icon = Icons.Rounded.Medication,
                    title = if (isBn) "ই-প্রেসক্রিপশন ও জীবনযাত্রা গাইড" else "E-Prescription & Lifestyle Guide",
                    subtitle = if (isBn) "টিয়ার ৩ (৳৯০০/মাস): ডাক্তার Rx ও ব্যক্তিগত জীবনযাত্রা গাইড" else "Tier 3 (BDT 900/mo): Clinician E-Rx vault & tailored lifestyle guide",
                    onClick = { onOpenHealthModule(HealthSubTab.LIFESTYLE_RX) },
                )
                ModuleCard(
                    icon = Icons.Rounded.SelfImprovement,
                    title = if (isBn) "ব্যক্তিগত সুস্থতা (ঘুম, মন, পানি)" else "Personal Wellness Tracker",
                    subtitle = if (isBn) "ঘুম, মেজাজ, মানসিক চাপ, শক্তি, পুষ্টি ও পানি" else "Sleep, mood, stress, energy, activity, nutrition & hydration",
                    onClick = { onOpenHealthModule(HealthSubTab.WELLNESS) },
                )
                ModuleCard(
                    icon = Icons.AutoMirrored.Rounded.MenuBook,
                    title = if (isBn) "গোপন ও সাধারণ নারী স্বাস্থ্য পাঠাগার" else "Intimate, Reproductive & General Health Library",
                    subtitle = if (isBn) "ডাক্তার-পর্যালোচিত সহজ বাংলা ও ইংরেজি গাইড" else "Clinician-reviewed articles with references & read-aloud audio",
                    onClick = { onOpenHealthModule(HealthSubTab.LIBRARY) },
                )
                ModuleCard(
                    icon = Icons.Rounded.FolderShared,
                    title = if (isBn) "ব্যক্তিগত স্বাস্থ্য রেকর্ড ও সারাংশ" else "Personal Health Records & Reports",
                    subtitle = if (isBn) "ওষুধ, অ্যালার্জি, ল্যাব/আল্ট্রাসাউন্ড স্ক্যান ও শেয়ার" else "Diagnoses, meds, allergies, photo report scan & doctor summary",
                    onClick = { onOpenHealthModule(HealthSubTab.RECORDS) },
                )
                ModuleCard(
                    icon = Icons.Rounded.HealthAndSafety,
                    title = "NariBondhu AI & BondhuHelp",
                    subtitle = if (isBn) "ফ্রি: ১০ AI টোকেন/সপ্তাহ • টিয়ার ২/৩: ডাক্তার কনসালটেশন" else "Free: 10 AI tokens/wk • Tier 2/3: Online & Offline doctor care",
                    onClick = onOpenBondhuHelp,
                )
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun QuickActionTile(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = containerColor,
        contentColor = DeepPlumDark,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = DeepPlumDark)
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = DeepPlumDark,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun ModuleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Lavender),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = DeepPlumDark)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
