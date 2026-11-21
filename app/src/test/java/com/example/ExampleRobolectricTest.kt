package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.ClinicalRulesEngine
import com.example.core.CycleCalculationEngine
import com.example.data.FlowIntensity
import com.example.data.MenstrualPeriod
import com.example.data.SafetyTier
import com.example.ui.health.CycleCalendarMode
import com.example.ui.health.CycleCalendarState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NariBondhu", appName)
  }

  @Test
  fun `cycle calculation excludes spotting-only entries`() {
    val entries = listOf(
      MenstrualPeriod(id = "3", startDateEpochDay = 20062L, spottingOnly = false, flowIntensity = FlowIntensity.MEDIUM.name),
      MenstrualPeriod(id = "2", startDateEpochDay = 20045L, spottingOnly = true, flowIntensity = FlowIntensity.SPOTTING.name),
      MenstrualPeriod(id = "1", startDateEpochDay = 20030L, spottingOnly = false, flowIntensity = FlowIntensity.MEDIUM.name),
    )
    val stats = CycleCalculationEngine.calculate(entries)
    assertEquals(2, stats.recordedPeriodCount)
    assertEquals(listOf(32), stats.individualCycleLengths)
  }

  @Test
  fun `clinical safety rules trigger red emergency and amber doctor pathways`() {
    val red = ClinicalRulesEngine.evaluateMessage("I am fainting with severe abdominal pain and soaking a pad every hour", emptyList())
    assertEquals(SafetyTier.RED, red.tier)
    assertTrue(red.requireEmergency)

    val amber = ClinicalRulesEngine.evaluateMessage("I have had missed periods for 4 months and heavy bleeding", emptyList())
    assertEquals(SafetyTier.AMBER, amber.tier)
    assertTrue(amber.recommendBondhuHelp)

    val green = ClinicalRulesEngine.evaluateMessage("What are iron-rich foods during my cycle?", emptyList())
    assertEquals(SafetyTier.GREEN, green.tier)
  }

  @Test
  fun `cycle calendar state supports two-tap range selection, reverse order, and ongoing toggle`() {
    val state = CycleCalendarState(initialYear = 2026, initialMonthZeroBased = 10)
    state.startNewLogMode()
    assertTrue(state.mode is CycleCalendarMode.SelectingDates)
    assertFalse(state.isComposerVisible)

    // Tap end-like day first, then earlier day -> automatically orders start..end
    state.onDayClick(20770L, emptyList())
    assertTrue(state.isComposerVisible)
    assertEquals(20770L, state.activeRangeStart)
    assertNull(state.activeRangeEnd)

    state.onDayClick(20766L, emptyList())
    assertEquals(20766L, state.activeRangeStart)
    assertEquals(20770L, state.activeRangeEnd)

    state.setFlowIntensity(FlowIntensity.HEAVY)
    state.setNotes("Heavy first two days")
    val saved = state.buildPeriodToSave()
    assertEquals(20766L, saved?.startDateEpochDay)
    assertEquals(20770L, saved?.endDateEpochDay)
    assertEquals(FlowIntensity.HEAVY.name, saved?.flowIntensity)
    assertFalse(saved?.spottingOnly ?: true)

    // Toggling ongoing clears end date while keeping start date
    state.setOngoing(true)
    val ongoingPeriod = state.buildPeriodToSave()
    assertEquals(20766L, ongoingPeriod?.startDateEpochDay)
    assertNull(ongoingPeriod?.endDateEpochDay)
  }

  @Test
  fun `tapping logged period on calendar enters editing mode with prefilled details`() {
    val existing = MenstrualPeriod(
      id = "p_1",
      userId = "u_1",
      startDateEpochDay = 20760L,
      endDateEpochDay = 20764L,
      flowIntensity = FlowIntensity.LIGHT.name,
      spottingOnly = false,
      notes = "Mild cramps",
    )
    val state = CycleCalendarState(initialYear = 2026, initialMonthZeroBased = 10)
    state.onDayClick(20762L, listOf(existing))

    assertTrue(state.mode is CycleCalendarMode.Editing)
    assertEquals("p_1", state.editingPeriodId)
    assertEquals(20760L, state.activeRangeStart)
    assertEquals(20764L, state.activeRangeEnd)
    assertEquals(FlowIntensity.LIGHT, state.draftFlowIntensity)
    assertEquals("Mild cramps", state.draftNotes)
  }
}
