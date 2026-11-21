package com.example.ui.health

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.CycleStatistics
import com.example.core.EpochDateUtil
import com.example.data.FlowIntensity
import com.example.data.MenstrualPeriod
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.MutedRoseDark
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SafetyAmberContainer
import java.util.Calendar
import kotlin.math.max
import kotlin.math.min

sealed interface CycleCalendarMode {
    data object Browsing : CycleCalendarMode
    data class SelectingDates(
        val startEpochDay: Long? = null,
        val endEpochDay: Long? = null,
    ) : CycleCalendarMode

    data class Editing(
        val periodId: String,
        val originalUserId: String,
        val startEpochDay: Long,
        val endEpochDay: Long?,
    ) : CycleCalendarMode
}

@Stable
class CycleCalendarState(
    initialYear: Int,
    initialMonthZeroBased: Int,
) {
    var displayYear by mutableIntStateOf(initialYear)
        private set

    var displayMonthZeroBased by mutableIntStateOf(initialMonthZeroBased)
        private set

    var mode by mutableStateOf<CycleCalendarMode>(CycleCalendarMode.Browsing)
        private set

    var draftFlowIntensity by mutableStateOf(FlowIntensity.MEDIUM)
        private set

    var draftSpottingOnly by mutableStateOf(false)
        private set

    var draftOngoing by mutableStateOf(false)
        private set

    var draftNotes by mutableStateOf("")
        private set

    val isComposerVisible: Boolean
        get() = when (val m = mode) {
            is CycleCalendarMode.Browsing -> false
            is CycleCalendarMode.SelectingDates -> m.startEpochDay != null
            is CycleCalendarMode.Editing -> true
        }

    val activeRangeStart: Long?
        get() = when (val m = mode) {
            is CycleCalendarMode.Browsing -> null
            is CycleCalendarMode.SelectingDates -> m.startEpochDay
            is CycleCalendarMode.Editing -> m.startEpochDay
        }

    val activeRangeEnd: Long?
        get() = when (val m = mode) {
            is CycleCalendarMode.Browsing -> null
            is CycleCalendarMode.SelectingDates -> if (draftOngoing) null else m.endEpochDay
            is CycleCalendarMode.Editing -> if (draftOngoing) null else m.endEpochDay
        }

    val editingPeriodId: String?
        get() = (mode as? CycleCalendarMode.Editing)?.periodId

    fun previousMonth() {
        if (displayMonthZeroBased == 0) {
            displayMonthZeroBased = 11
            displayYear -= 1
        } else {
            displayMonthZeroBased -= 1
        }
    }

    fun nextMonth() {
        if (displayMonthZeroBased == 11) {
            displayMonthZeroBased = 0
            displayYear += 1
        } else {
            displayMonthZeroBased += 1
        }
    }

    fun jumpToToday(todayEpochDay: Long = EpochDateUtil.todayEpochDay()) {
        jumpToEpochDay(todayEpochDay)
    }

    fun jumpToEpochDay(epochDay: Long) {
        val (year, month, _) = EpochDateUtil.epochDayToYearMonthDay(epochDay)
        displayYear = year
        displayMonthZeroBased = month
    }

    fun startNewLogMode() {
        draftFlowIntensity = FlowIntensity.MEDIUM
        draftSpottingOnly = false
        draftOngoing = false
        draftNotes = ""
        mode = CycleCalendarMode.SelectingDates(startEpochDay = null, endEpochDay = null)
    }

    fun editPeriod(period: MenstrualPeriod) {
        draftFlowIntensity = FlowIntensity.fromName(period.flowIntensity)
        draftSpottingOnly = period.spottingOnly
        draftOngoing = period.endDateEpochDay == null
        draftNotes = period.notes
        mode = CycleCalendarMode.Editing(
            periodId = period.id,
            originalUserId = period.userId,
            startEpochDay = period.startDateEpochDay,
            endEpochDay = period.endDateEpochDay,
        )
        jumpToEpochDay(period.startDateEpochDay)
    }

    fun onDayClick(epochDay: Long, existingPeriods: List<MenstrualPeriod>) {
        when (val current = mode) {
            is CycleCalendarMode.Browsing -> {
                val match = existingPeriods.firstOrNull { period ->
                    val end = period.endDateEpochDay ?: period.startDateEpochDay
                    epochDay in period.startDateEpochDay..end
                }
                if (match != null) {
                    editPeriod(match)
                } else {
                    draftFlowIntensity = FlowIntensity.MEDIUM
                    draftSpottingOnly = false
                    draftOngoing = false
                    draftNotes = ""
                    mode = CycleCalendarMode.SelectingDates(
                        startEpochDay = epochDay,
                        endEpochDay = null,
                    )
                }
            }

            is CycleCalendarMode.SelectingDates -> {
                val currentStart = current.startEpochDay
                val currentEnd = current.endEpochDay
                when {
                    currentStart == null -> {
                        mode = CycleCalendarMode.SelectingDates(
                            startEpochDay = epochDay,
                            endEpochDay = null,
                        )
                    }

                    currentEnd == null -> {
                        val orderedStart = min(currentStart, epochDay)
                        val orderedEnd = max(currentStart, epochDay)
                        draftOngoing = false
                        mode = CycleCalendarMode.SelectingDates(
                            startEpochDay = orderedStart,
                            endEpochDay = orderedEnd,
                        )
                    }

                    else -> {
                        draftOngoing = false
                        mode = CycleCalendarMode.SelectingDates(
                            startEpochDay = epochDay,
                            endEpochDay = null,
                        )
                    }
                }
            }

            is CycleCalendarMode.Editing -> {
                if (epochDay < current.startEpochDay) {
                    val newEnd = current.endEpochDay ?: current.startEpochDay
                    draftOngoing = false
                    mode = current.copy(
                        startEpochDay = epochDay,
                        endEpochDay = newEnd,
                    )
                } else {
                    draftOngoing = false
                    mode = current.copy(endEpochDay = epochDay)
                }
            }
        }
    }

    fun shiftStartDate(deltaDays: Long) {
        when (val current = mode) {
            is CycleCalendarMode.SelectingDates -> {
                val start = current.startEpochDay ?: return
                val nextStart = (start + deltaDays).coerceIn(0L, 100000L)
                val nextEnd = current.endEpochDay?.let { max(nextStart, it) }
                mode = current.copy(startEpochDay = nextStart, endEpochDay = nextEnd)
            }

            is CycleCalendarMode.Editing -> {
                val nextStart = (current.startEpochDay + deltaDays).coerceIn(0L, 100000L)
                val nextEnd = current.endEpochDay?.let { max(nextStart, it) }
                mode = current.copy(startEpochDay = nextStart, endEpochDay = nextEnd)
            }

            CycleCalendarMode.Browsing -> Unit
        }
    }

    fun shiftEndDate(deltaDays: Long) {
        when (val current = mode) {
            is CycleCalendarMode.SelectingDates -> {
                val start = current.startEpochDay ?: return
                val baseEnd = current.endEpochDay ?: start
                val nextEnd = max(start, (baseEnd + deltaDays).coerceIn(0L, 100000L))
                draftOngoing = false
                mode = current.copy(endEpochDay = nextEnd)
            }

            is CycleCalendarMode.Editing -> {
                val baseEnd = current.endEpochDay ?: current.startEpochDay
                val nextEnd = max(current.startEpochDay, (baseEnd + deltaDays).coerceIn(0L, 100000L))
                draftOngoing = false
                mode = current.copy(endEpochDay = nextEnd)
            }

            CycleCalendarMode.Browsing -> Unit
        }
    }

    fun setOngoing(ongoing: Boolean) {
        draftOngoing = ongoing
        if (!ongoing) {
            when (val current = mode) {
                is CycleCalendarMode.SelectingDates -> {
                    val start = current.startEpochDay ?: return
                    if (current.endEpochDay == null) {
                        mode = current.copy(endEpochDay = start)
                    }
                }

                is CycleCalendarMode.Editing -> {
                    if (current.endEpochDay == null) {
                        mode = current.copy(endEpochDay = current.startEpochDay)
                    }
                }

                CycleCalendarMode.Browsing -> Unit
            }
        }
    }

    fun markEndedToday(todayEpochDay: Long = EpochDateUtil.todayEpochDay()) {
        draftOngoing = false
        when (val current = mode) {
            is CycleCalendarMode.SelectingDates -> {
                val start = current.startEpochDay ?: todayEpochDay
                mode = CycleCalendarMode.SelectingDates(
                    startEpochDay = min(start, todayEpochDay),
                    endEpochDay = max(start, todayEpochDay),
                )
            }

            is CycleCalendarMode.Editing -> {
                mode = current.copy(
                    startEpochDay = min(current.startEpochDay, todayEpochDay),
                    endEpochDay = max(current.startEpochDay, todayEpochDay),
                )
            }

            CycleCalendarMode.Browsing -> Unit
        }
    }

    fun setFlowIntensity(flow: FlowIntensity) {
        draftFlowIntensity = flow
        draftSpottingOnly = (flow == FlowIntensity.SPOTTING)
    }

    fun setSpottingOnly(spotting: Boolean) {
        draftSpottingOnly = spotting
        if (spotting) {
            draftFlowIntensity = FlowIntensity.SPOTTING
        } else if (draftFlowIntensity == FlowIntensity.SPOTTING) {
            draftFlowIntensity = FlowIntensity.MEDIUM
        }
    }

    fun setNotes(value: String) {
        draftNotes = value.take(500)
    }

    fun cancelSelection() {
        mode = CycleCalendarMode.Browsing
    }

    fun buildPeriodToSave(): MenstrualPeriod? {
        return when (val current = mode) {
            is CycleCalendarMode.Browsing -> null
            is CycleCalendarMode.SelectingDates -> {
                val start = current.startEpochDay ?: return null
                val end = if (draftOngoing) null else (current.endEpochDay ?: start).coerceAtLeast(start)
                MenstrualPeriod(
                    startDateEpochDay = start,
                    endDateEpochDay = end,
                    flowIntensity = draftFlowIntensity.name,
                    spottingOnly = draftSpottingOnly,
                    notes = draftNotes.trim(),
                )
            }

            is CycleCalendarMode.Editing -> {
                val end = if (draftOngoing) null else (current.endEpochDay ?: current.startEpochDay).coerceAtLeast(current.startEpochDay)
                MenstrualPeriod(
                    id = current.periodId,
                    userId = current.originalUserId,
                    startDateEpochDay = current.startEpochDay,
                    endDateEpochDay = end,
                    flowIntensity = draftFlowIntensity.name,
                    spottingOnly = draftSpottingOnly,
                    notes = draftNotes.trim(),
                )
            }
        }
    }
}

@Composable
fun rememberCycleCalendarState(): CycleCalendarState {
    val today = remember { Calendar.getInstance() }
    return remember {
        CycleCalendarState(
            initialYear = today.get(Calendar.YEAR),
            initialMonthZeroBased = today.get(Calendar.MONTH),
        )
    }
}

@Composable
fun InteractiveCycleCalendarCard(
    state: CycleCalendarState,
    periods: List<MenstrualPeriod>,
    cycleStats: CycleStatistics,
    isBn: Boolean,
    discreetMode: Boolean,
    onDayClick: (Long) -> Unit,
) {
    val todayEpochDay = remember { EpochDateUtil.todayEpochDay() }
    val (todayYear, todayMonth, _) = remember(todayEpochDay) {
        EpochDateUtil.epochDayToYearMonthDay(todayEpochDay)
    }
    val isCurrentMonth = state.displayYear == todayYear && state.displayMonthZeroBased == todayMonth

    val monthNamesEn = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )
    val monthNamesBn = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর",
    )
    val weekdayLabels = if (isBn) {
        listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র", "শনি")
    } else {
        listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Top bar: Month switcher + Today chip + Start Range CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { state.previousMonth() },
                        modifier = Modifier.testTag("calendar_prev_month"),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = if (isBn) "আগের মাস" else "Previous month",
                        )
                    }
                    Text(
                        text = if (isBn) {
                            "${monthNamesBn[state.displayMonthZeroBased]} ${state.displayYear}"
                        } else {
                            "${monthNamesEn[state.displayMonthZeroBased]} ${state.displayYear}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    IconButton(
                        onClick = { state.nextMonth() },
                        modifier = Modifier.testTag("calendar_next_month"),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = if (isBn) "পরের মাস" else "Next month",
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (!isCurrentMonth) {
                        OutlinedButton(
                            onClick = { state.jumpToToday(todayEpochDay) },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("calendar_today_button"),
                        ) {
                            Icon(Icons.Rounded.Today, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (isBn) "আজ" else "Today", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    if (state.mode is CycleCalendarMode.Browsing) {
                        Button(
                            onClick = { state.startNewLogMode() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepPlum,
                                contentColor = Color.White,
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("calendar_start_range_button"),
                        ) {
                            Icon(Icons.Rounded.EditCalendar, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = when {
                                    discreetMode && isBn -> "তারিখ দিন"
                                    discreetMode -> "Log Dates"
                                    isBn -> "তারিখ দিন"
                                    else -> "Log Dates"
                                },
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    } else {
                        TextButton(
                            onClick = { state.cancelSelection() },
                            modifier = Modifier.testTag("calendar_cancel_mode_button"),
                        ) {
                            Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (isBn) "বন্ধ" else "Cancel")
                        }
                    }
                }
            }

            // Guided Step Banner (Designed for zero technical expertise)
            val bannerText = when (val m = state.mode) {
                is CycleCalendarMode.Browsing -> {
                    if (discreetMode) {
                        if (isBn) "যেকোনো তারিখে ট্যাপ করে শুরুর ও শেষের তারিখ লিখুন অথবা পূর্বের এন্ট্রি বদলান।"
                        else "Tap any date to log a start/end span, or tap a marked date to edit."
                    } else {
                        if (isBn) "শুরু ও শেষের তারিখ লিখতে যেকোনো তারিখে ট্যাপ করুন, অথবা পুরোনো চিহ্নে ট্যাপ করে বদলান।"
                        else "Tap any date to start logging cycle dates, or tap a marked span to edit."
                    }
                }

                is CycleCalendarMode.SelectingDates -> {
                    val s = m.startEpochDay
                    val e = m.endEpochDay
                    when {
                        s == null -> if (isBn) {
                            "ধাপ ১: ক্যালেন্ডারে শুরুর তারিখে ট্যাপ করুন"
                        } else {
                            "Step 1: Tap your cycle start date on the calendar"
                        }

                        state.draftOngoing -> {
                            val startLabel = EpochDateUtil.formatShortDate(s)
                            if (isBn) "$startLabel থেকে শুরু (চলমান) • নিচে সংরক্ষণ করুন বা শেষের তারিখ ট্যাপ করুন"
                            else "Started $startLabel (Ongoing) • Tap Save below or tap an end date"
                        }

                        e == null -> {
                            val startLabel = EpochDateUtil.formatShortDate(s)
                            if (isBn) "ধাপ ২: শুরু $startLabel • শেষের তারিখে ট্যাপ করুন (অথবা নিচে 'চলমান' বেছে নিন)"
                            else "Step 2: Start $startLabel • Tap end date (or toggle Ongoing below)"
                        }

                        else -> {
                            val daysCount = (e - s + 1).toInt()
                            val startLabel = EpochDateUtil.formatShortDate(s)
                            val endLabel = EpochDateUtil.formatShortDate(e)
                            if (isBn) "নির্বাচিত: $startLabel → $endLabel ($daysCount দিন) • পরিবর্তন করতে অন্য তারিখে ট্যাপ করুন"
                            else "Selected: $startLabel → $endLabel ($daysCount days) • Tap calendar to adjust"
                        }
                    }
                }

                is CycleCalendarMode.Editing -> {
                    val startLabel = EpochDateUtil.formatShortDate(m.startEpochDay)
                    val endLabel = if (state.draftOngoing || m.endEpochDay == null) {
                        if (isBn) "চলমান" else "Ongoing"
                    } else {
                        EpochDateUtil.formatShortDate(m.endEpochDay)
                    }
                    if (isBn) "সম্পাদনা: $startLabel → $endLabel • ক্যালেন্ডারে ট্যাপ করে তারিখ বদলান"
                    else "Editing: $startLabel → $endLabel • Tap dates on calendar to update span"
                }
            }

            Surface(
                color = Lavender,
                contentColor = DeepPlumDark,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = DeepPlumDark,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = bannerText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = DeepPlumDark,
                    )
                }
            }

            // Weekday Header Row
            Row(modifier = Modifier.fillMaxWidth()) {
                weekdayLabels.forEach { d ->
                    Text(
                        text = d,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Animated Month Grid with Continuous Start-End Span Ribbons
            AnimatedContent(
                targetState = state.displayYear to state.displayMonthZeroBased,
                transitionSpec = {
                    fadeIn(tween(180)) togetherWith fadeOut(tween(150))
                },
                label = "calendarMonthGrid",
            ) { (year, monthZeroBased) ->
                val cal = remember(year, monthZeroBased) {
                    Calendar.getInstance().apply {
                        clear()
                        set(year, monthZeroBased, 1)
                    }
                }
                val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val firstDayOfWeekOffset = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
                val totalCells = firstDayOfWeekOffset + daysInMonth
                val rows = (totalCells + 6) / 7

                val draftStart = state.activeRangeStart
                val draftEnd = state.activeRangeEnd ?: draftStart

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (r in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            for (c in 0 until 7) {
                                val cellIndex = r * 7 + c
                                val dayNum = cellIndex - firstDayOfWeekOffset + 1
                                if (dayNum in 1..daysInMonth) {
                                    val epochDay = EpochDateUtil.calendarToEpochDay(year, monthZeroBased, dayNum)
                                    val isToday = epochDay == todayEpochDay

                                    val inDraftRange = draftStart != null && draftEnd != null && epochDay in draftStart..draftEnd
                                    val draftPrevInSpan = inDraftRange && c > 0 && (epochDay - 1) in draftStart..draftEnd
                                    val draftNextInSpan = inDraftRange && c < 6 && (epochDay + 1) in draftStart..draftEnd
                                    val isDraftCap = inDraftRange && (epochDay == draftStart || epochDay == draftEnd)

                                    val matchingPeriod = periods.firstOrNull { p ->
                                        val pEnd = p.endDateEpochDay ?: p.startDateEpochDay
                                        epochDay in p.startDateEpochDay..pEnd
                                    }
                                    val editingSamePeriod = matchingPeriod != null && matchingPeriod.id.isNotBlank() && matchingPeriod.id == state.editingPeriodId

                                    val isSavedFullPeriod = !editingSamePeriod && matchingPeriod != null && !matchingPeriod.spottingOnly
                                    val isSavedSpotting = !editingSamePeriod && matchingPeriod != null && matchingPeriod.spottingOnly

                                    val savedPrevInSpan = if (isSavedFullPeriod) {
                                        val pEnd = matchingPeriod!!.endDateEpochDay ?: matchingPeriod.startDateEpochDay
                                        c > 0 && (epochDay - 1) in matchingPeriod.startDateEpochDay..pEnd
                                    } else false
                                    val savedNextInSpan = if (isSavedFullPeriod) {
                                        val pEnd = matchingPeriod!!.endDateEpochDay ?: matchingPeriod.startDateEpochDay
                                        c < 6 && (epochDay + 1) in matchingPeriod.startDateEpochDay..pEnd
                                    } else false
                                    val isSavedCap = isSavedFullPeriod && (
                                        epochDay == matchingPeriod!!.startDateEpochDay ||
                                            epochDay == (matchingPeriod.endDateEpochDay ?: matchingPeriod.startDateEpochDay)
                                        )

                                    val isPredicted = cycleStats.estimatedWindowStartEpochDay != null &&
                                        cycleStats.estimatedWindowEndEpochDay != null &&
                                        epochDay in cycleStats.estimatedWindowStartEpochDay..cycleStats.estimatedWindowEndEpochDay

                                    val scaleTarget = if (isDraftCap) 1.04f else 1.0f
                                    val dayScale by animateFloatAsState(
                                        targetValue = scaleTarget,
                                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                        label = "dayScale",
                                    )

                                    val spanShape = when {
                                        inDraftRange -> RoundedCornerShape(
                                            topStart = if (draftPrevInSpan) 0.dp else 22.dp,
                                            bottomStart = if (draftPrevInSpan) 0.dp else 22.dp,
                                            topEnd = if (draftNextInSpan) 0.dp else 22.dp,
                                            bottomEnd = if (draftNextInSpan) 0.dp else 22.dp,
                                        )

                                        isSavedFullPeriod -> RoundedCornerShape(
                                            topStart = if (savedPrevInSpan) 0.dp else 22.dp,
                                            bottomStart = if (savedPrevInSpan) 0.dp else 22.dp,
                                            topEnd = if (savedNextInSpan) 0.dp else 22.dp,
                                            bottomEnd = if (savedNextInSpan) 0.dp else 22.dp,
                                        )

                                        else -> CircleShape
                                    }

                                    val bgColor = when {
                                        inDraftRange && isDraftCap -> MutedRoseDark
                                        inDraftRange -> DeepPlum
                                        isSavedFullPeriod && isSavedCap -> DeepPlum
                                        isSavedFullPeriod -> DeepPlum.copy(alpha = 0.80f)
                                        isSavedSpotting -> MutedRoseContainer
                                        isPredicted -> Lavender
                                        else -> Color.Transparent
                                    }

                                    val textColor = when {
                                        inDraftRange -> Color.White
                                        isSavedFullPeriod -> Color.White
                                        isSavedSpotting -> InkPrimary
                                        isPredicted -> DeepPlumDark
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }

                                    val borderModifier = when {
                                        isToday && (inDraftRange || isSavedFullPeriod) ->
                                            Modifier.border(2.dp, Color.White, spanShape)
                                        isToday ->
                                            Modifier.border(2.dp, MaterialTheme.colorScheme.primary, spanShape)
                                        isSavedSpotting ->
                                            Modifier.border(1.dp, MutedRoseDark, spanShape)
                                        else -> Modifier
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .defaultMinSize(minHeight = 48.dp)
                                            .scale(dayScale)
                                            .clip(spanShape)
                                            .background(bgColor)
                                            .then(borderModifier)
                                            .clickable { onDayClick(epochDay) }
                                            .testTag("calendar_day_${year}_${monthZeroBased + 1}_$dayNum"),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$dayNum",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (inDraftRange || isSavedFullPeriod || isSavedSpotting || isPredicted || isToday) {
                                                    FontWeight.Bold
                                                } else {
                                                    FontWeight.Normal
                                                },
                                                color = textColor,
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .defaultMinSize(minHeight = 48.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CalendarLegendItem(DeepPlum, if (isBn) "মাসিক" else "Period")
                CalendarLegendItem(MutedRoseContainer, if (isBn) "স্পটিং" else "Spotting")
                CalendarLegendItem(Lavender, if (isBn) "সম্ভাব্য সময়" else "Est. Window")
                CalendarLegendItem(MutedRoseDark, if (isBn) "নির্বাচিত" else "Selected")
            }
        }
    }
}

@Composable
private fun CalendarLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CycleEntryComposerCard(
    state: CycleCalendarState,
    isBn: Boolean,
    discreetMode: Boolean,
    onSave: (MenstrualPeriod) -> Unit,
    onDelete: (String) -> Unit,
    onCancel: () -> Unit,
) {
    AnimatedVisibility(
        visible = state.isComposerVisible,
        enter = fadeIn(tween(200)) + expandVertically(tween(240)),
        exit = fadeOut(tween(150)) + shrinkVertically(tween(200)),
    ) {
        val startDay = state.activeRangeStart ?: return@AnimatedVisibility
        val endDay = state.activeRangeEnd
        val isEditingExisting = state.editingPeriodId != null
        val spanDays = if (!state.draftOngoing && endDay != null) {
            (endDay - startDay + 1).toInt().coerceAtLeast(1)
        } else {
            null
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .testTag("cycle_entry_composer"),
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
                    Text(
                        text = when {
                            isEditingExisting && isBn -> "চক্রের তারিখ সম্পাদনা"
                            isEditingExisting -> "Edit Cycle Span"
                            discreetMode && isBn -> "নতুন তারিখ সংরক্ষণ"
                            discreetMode -> "Save Date Span"
                            isBn -> "নতুন মাসিক / স্পটিং সংরক্ষণ"
                            else -> "Save Cycle Dates"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    if (spanDays != null) {
                        Surface(
                            color = Lavender,
                            contentColor = DeepPlumDark,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = if (isBn) "$spanDays দিন" else "$spanDays ${if (spanDays == 1) "day" else "days"}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepPlumDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    } else {
                        Surface(
                            color = MutedRoseContainer,
                            contentColor = InkPrimary,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Text(
                                text = if (isBn) "চলমান" else "Ongoing",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = InkPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            )
                        }
                    }
                }

                // Start & End Date Cards with fine-tune steppers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DateSpanBox(
                        label = if (isBn) "শুরুর তারিখ" else "Start Date",
                        dateValue = EpochDateUtil.formatEpochDay(startDay, isBn),
                        onPrev = { state.shiftStartDate(-1) },
                        onNext = { state.shiftStartDate(1) },
                        enabled = true,
                        modifier = Modifier.weight(1f),
                    )
                    DateSpanBox(
                        label = if (isBn) "শেষের তারিখ" else "End Date",
                        dateValue = when {
                            state.draftOngoing -> if (isBn) "এখনো চলছে" else "Ongoing"
                            endDay != null -> EpochDateUtil.formatEpochDay(endDay, isBn)
                            else -> if (isBn) "ক্যালেন্ডারে ট্যাপ করুন" else "Tap calendar"
                        },
                        onPrev = { state.shiftEndDate(-1) },
                        onNext = { state.shiftEndDate(1) },
                        enabled = !state.draftOngoing,
                        modifier = Modifier.weight(1f),
                    )
                }

                // Ongoing Toggle + Quick "Mark Ended Today" shortcut
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) "এখনো চলছে (শেষের তারিখ নেই)" else "Still Ongoing (No End Date Yet)",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = if (isBn) "পরে শেষ হলে ক্যালেন্ডারে ট্যাপ করে শেষের তারিখ যোগ করুন"
                            else "You can mark the end date anytime later",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = state.draftOngoing,
                        onCheckedChange = { state.setOngoing(it) },
                        modifier = Modifier.testTag("cycle_ongoing_switch"),
                    )
                }

                if (state.draftOngoing || endDay == null) {
                    OutlinedButton(
                        onClick = { state.markEndedToday() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cycle_mark_ended_today_button"),
                    ) {
                        Icon(Icons.Rounded.Today, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isBn) "আজ শেষ হয়েছে হিসেবে চিহ্নিত করুন" else "Mark Ended Today")
                    }
                }

                // Spotting-only switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) "এটি কেবল স্পটিং (পূর্ণ মাসিক নয়)" else "Spotting Only (Separate from Period)",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = if (isBn) "স্পটিং আলাদা রাখলে চক্রের গড় হিসাব সঠিক থাকে"
                            else "Excluded from full-cycle interval estimates",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Switch(
                        checked = state.draftSpottingOnly,
                        onCheckedChange = { state.setSpottingOnly(it) },
                        modifier = Modifier.testTag("cycle_spotting_switch"),
                    )
                }

                // Flow Intensity Chips
                Text(
                    text = if (isBn) "রক্তপাতের মাত্রা" else "Flow Intensity",
                    style = MaterialTheme.typography.titleSmall,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    FlowIntensity.entries.forEach { flow ->
                        FilterChip(
                            selected = state.draftFlowIntensity == flow,
                            onClick = { state.setFlowIntensity(flow) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepPlum,
                                selectedLabelColor = Color.White,
                            ),
                            label = { Text(if (isBn) flow.bnLabel else flow.enLabel) },
                            modifier = Modifier.testTag("flow_chip_${flow.name.lowercase()}"),
                        )
                    }
                }

                // Soft clinical safety prompt for unusually prolonged bleeding (> 14 days)
                if (spanDays != null && spanDays > 14) {
                    Surface(
                        color = SafetyAmberContainer,
                        contentColor = InkPrimary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SafetyAmber, RoundedCornerShape(12.dp)),
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Rounded.Info, contentDescription = null, tint = SafetyAmber)
                            Text(
                                text = if (isBn) {
                                    "১৪ দিনের বেশি সময় ধরে রক্তপাত চললে অনুগ্রহ করে বন্ধুহেল্প-এর মাধ্যমে স্ত্রীরোগ বিশেষজ্ঞের পরামর্শ নিন।"
                                } else {
                                    "Bleeding spanning over 14 days may benefit from consulting a verified gynaecologist on BondhuHelp."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = InkPrimary,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = state.draftNotes,
                    onValueChange = { state.setNotes(it) },
                    label = { Text(if (isBn) "ব্যক্তিগত নোট (ঐচ্ছিক)" else "Personal Notes (Optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Save / Delete / Cancel actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isEditingExisting && !state.editingPeriodId.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = {
                                val id = state.editingPeriodId!!
                                onDelete(id)
                                state.cancelSelection()
                            },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("cycle_composer_delete_button"),
                        ) {
                            Icon(
                                Icons.Rounded.DeleteOutline,
                                contentDescription = if (isBn) "মুছুন" else "Delete",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("cycle_composer_cancel_button"),
                    ) {
                        Text(if (isBn) "বাতিল" else "Cancel")
                    }

                    Button(
                        onClick = {
                            state.buildPeriodToSave()?.let { period ->
                                onSave(period)
                                state.cancelSelection()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepPlum,
                            contentColor = Color.White,
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("cycle_composer_save_button"),
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isBn) "সংরক্ষণ" else "Save Dates")
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSpanBox(
    label: String,
    dateValue: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = Lavender,
        contentColor = InkPrimary,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = DeepPlumDark,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = dateValue,
                style = MaterialTheme.typography.titleSmall,
                color = InkPrimary,
                fontWeight = FontWeight.Bold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = onPrev,
                    enabled = enabled,
                    border = BorderStroke(1.dp, DeepPlum.copy(alpha = if (enabled) 0.6f else 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPlumDark),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                ) {
                    Text("-1d", style = MaterialTheme.typography.labelSmall)
                }
                OutlinedButton(
                    onClick = onNext,
                    enabled = enabled,
                    border = BorderStroke(1.dp, DeepPlum.copy(alpha = if (enabled) 0.6f else 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPlumDark),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                ) {
                    Text("+1d", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
