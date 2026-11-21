package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.ClinicalRulesEngine
import com.example.core.ConsultationAccessMode
import com.example.core.CycleCalculationEngine
import com.example.core.ServiceTierEngine
import com.example.data.AppointmentBooking
import com.example.data.ConsultationType
import com.example.data.FlowIntensity
import com.example.data.HealthRecordType
import com.example.data.MenstrualPeriod
import com.example.data.SafetyTier
import com.example.data.ServiceTier
import com.example.data.SymptomCategory
import com.example.data.SymptomSeverity
import com.example.data.UserProfile
import com.example.ui.health.CycleCalendarMode
import com.example.ui.health.CycleCalendarState
import com.example.ui.health.DailyPcosFormState
import com.example.ui.health.PcosMarkerPreset
import com.example.ui.health.PcosPatternFrequency
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

  @Test
  fun `daily pcos form captures hair growth, acne, fatigue and builds firestore entries`() {
    val form = DailyPcosFormState(initialEpochDay = 20778L)
    form.toggleMarker(PcosMarkerPreset.HAIR_GROWTH)
    form.setMarkerSeverity(PcosMarkerPreset.HAIR_GROWTH, SymptomSeverity.MODERATE)
    form.toggleMarker(PcosMarkerPreset.ACNE)
    form.setMarkerSeverity(PcosMarkerPreset.ACNE, SymptomSeverity.SEVERE)
    form.toggleMarker(PcosMarkerPreset.FATIGUE)
    form.updateCustomMarkerText("Dark skin patch")
    form.updateCustomMarkerSeverity(SymptomSeverity.MILD)
    form.updateFrequency(PcosPatternFrequency.RECURRING)
    form.updateNotes("Worse after poor sleep")

    val built = form.buildSymptomEntries()
    assertEquals(4, built.size)
    assertTrue(built.all { it.category == SymptomCategory.PCOS.name })
    assertTrue(built.all { it.dateEpochDay == 20778L })
    assertTrue(built.all { it.frequencyText == PcosPatternFrequency.RECURRING.enLabel })
    assertEquals(SymptomSeverity.SEVERE.name, built.first { it.symptomType == PcosMarkerPreset.ACNE.enTitle }.severity)
    assertEquals("Worse after poor sleep", built.first().notes)

    form.resetAfterSave()
    assertEquals(0, form.selectedCount)
    assertEquals("", form.notes)
  }

  @Test
  fun `service tiers enforce pricing, entitlements, weekly 10-token replenishment, and always-on privacy`() {
    val free = ServiceTierEngine.entitlementsFor(ServiceTier.FREE)
    assertEquals(0, ServiceTier.FREE.monthlyPriceBdt)
    assertTrue(free.hasPeriodTracker)
    assertEquals(10, free.weeklyFreeAiTokens)
    assertFalse(free.hasUnlimitedAiTokens)
    assertFalse(free.hasPersonalisedPcosPmsCare)
    assertFalse(free.hasCycleAnalysis)
    assertEquals(ConsultationAccessMode.NONE, free.consultationAccess)
    assertFalse(free.hasEPrescription)
    assertFalse(free.hasLifestyleGuide)
    assertTrue(free.hasAlwaysOnPrivacy)

    val tier1 = ServiceTierEngine.entitlementsFor(ServiceTier.TIER_1)
    assertEquals(200, ServiceTier.TIER_1.monthlyPriceBdt)
    assertTrue(tier1.hasUnlimitedAiTokens)
    assertTrue(tier1.hasPersonalisedPcosPmsCare)
    assertTrue(tier1.hasCycleAnalysis)
    assertEquals(ConsultationAccessMode.NONE, tier1.consultationAccess)
    assertTrue(tier1.hasAlwaysOnPrivacy)

    val tier2 = ServiceTierEngine.entitlementsFor(ServiceTier.TIER_2)
    assertEquals(450, ServiceTier.TIER_2.monthlyPriceBdt)
    assertTrue(tier2.hasUnlimitedAiTokens)
    assertTrue(tier2.hasPersonalisedPcosPmsCare)
    assertTrue(tier2.hasCycleAnalysis)
    assertEquals(ConsultationAccessMode.ONLINE_OR_OFFLINE, tier2.consultationAccess)
    assertTrue(tier2.canUseConsultationType(ConsultationType.ONLINE, ConsultationType.ONLINE))
    assertFalse(tier2.canUseConsultationType(ConsultationType.IN_PERSON, ConsultationType.ONLINE))
    assertTrue(tier2.canUseConsultationType(ConsultationType.IN_PERSON, ConsultationType.IN_PERSON))
    assertFalse(tier2.hasEPrescription)
    assertFalse(tier2.hasLifestyleGuide)
    assertTrue(tier2.hasAlwaysOnPrivacy)

    val tier3 = ServiceTierEngine.entitlementsFor(ServiceTier.TIER_3)
    assertEquals(900, ServiceTier.TIER_3.monthlyPriceBdt)
    assertEquals(ConsultationAccessMode.ONLINE_AND_OFFLINE, tier3.consultationAccess)
    assertTrue(tier3.canUseConsultationType(ConsultationType.ONLINE, ConsultationType.ONLINE))
    assertTrue(tier3.canUseConsultationType(ConsultationType.IN_PERSON, ConsultationType.ONLINE))
    assertTrue(tier3.hasEPrescription)
    assertTrue(tier3.hasLifestyleGuide)
    assertTrue(tier3.hasAlwaysOnPrivacy)

    // Weekly token replenishment on Free tier after 7 days
    val profileMidWeek = UserProfile(
      userId = "u_1",
      serviceTier = ServiceTier.FREE.code,
      weeklyAiTokensRemaining = 3L,
      weeklyTokenResetEpochDay = 20700L,
    )
    val sameWeekStatus = ServiceTierEngine.resolveWeeklyTokenStatus(profileMidWeek, todayEpochDay = 20704L)
    assertEquals(3, sameWeekStatus.tokensRemaining)
    assertFalse(sameWeekStatus.needsFirestoreSync)

    val nextWeekStatus = ServiceTierEngine.resolveWeeklyTokenStatus(profileMidWeek, todayEpochDay = 20707L)
    assertEquals(10, nextWeekStatus.tokensRemaining)
    assertEquals(20707L, nextWeekStatus.resetEpochDay)
    assertTrue(nextWeekStatus.needsFirestoreSync)

    // Tier 3 E-Prescription record & Lifestyle Guide
    val rx = ServiceTierEngine.buildClinicianEPrescriptionRecord(
      userId = "u_1",
      booking = AppointmentBooking(
        doctorId = "doc_dhk_01",
        doctorName = "Dr. Farhana Rahman",
        clinicName = "Dhanmondi Care",
        city = "Dhaka",
        consultationType = ConsultationType.ONLINE.name,
        slotLabel = "Tomorrow 10:30 AM",
      ),
      issuedDateText = "2026-11-21",
      clinicalImpression = "PCOS follow-up",
      regimenNotes = "Inositol 1 sachet daily",
      followUpInstructions = "Follow up in 4 weeks",
    )
    assertEquals(HealthRecordType.E_PRESCRIPTION.name, rx.recordType)
    assertTrue(rx.title.contains("Dr. Farhana Rahman"))
    assertTrue(ServiceTierEngine.personalisedLifestyleGuide(listOf("PCOS"), emptyList()).size >= 4)
  }
}
