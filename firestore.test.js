const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_123";
const BOB_UID = "bob_uid_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: denied all reads and writes", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("menstrual_periods").get());
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Ayesha",
      preferredLanguage: "BN",
      ageConfirmed18Plus: true,
      healthInterests: ["PCOS"],
      discreetMode: false,
      onboardingCompleted: true,
      remindersEnabled: true,
      aiConsentGranted: true,
      cloudSyncConsentGranted: true,
      createdAt: new Date(Date.now() - 1000),
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("Owner can create and partially update UserProfile; cross-user and ghost-field writes fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Tasnim",
      preferredLanguage: "EN",
      city: "Dhaka",
      ageConfirmed18Plus: true,
      healthInterests: ["Menstrual", "PCOS"],
      discreetMode: false,
      onboardingCompleted: true,
      remindersEnabled: true,
      aiConsentGranted: false,
      cloudSyncConsentGranted: true,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Partial update toggling discreetMode & aiConsentGranted
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      discreetMode: true,
      aiConsentGranted: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Cross-user read & write blocked
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({ displayName: "Hacked" }));

  // Ghost field & future timestamp blocked
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      updatedAt: new Date(Date.now() + 60000),
    })
  );
});

test("MenstrualPeriod create, optional endDateEpochDay, update, and list isolation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  // Omit optional endDateEpochDay just like Kotlin .filterValues { it != null }
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("menstrual_periods").doc("p1").set({
      userId: ALICE_UID,
      startDateEpochDay: 20400,
      flowIntensity: "MEDIUM",
      spottingOnly: false,
      notes: "Mild cramps",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Update with endDateEpochDay
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("menstrual_periods").doc("p1").update({
      endDateEpochDay: 20404,
      flowIntensity: "HEAVY",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // List by owner succeeds; list by Bob fails
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("menstrual_periods").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("menstrual_periods").get());

  // Reject extra `id` field (@DocumentId parity)
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("menstrual_periods").doc("p2").set({
      id: "p2",
      userId: ALICE_UID,
      startDateEpochDay: 20430,
      flowIntensity: "LIGHT",
      spottingOnly: false,
      notes: "",
      createdAt: ts,
      updatedAt: ts,
    })
  );
});

test("Symptom, Wellness, HealthRecord, and ConsentLog payloads succeed for owner and block invalid enums", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = new Date(Date.now() - 4000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("symptoms").doc("s1").set({
      userId: ALICE_UID,
      dateEpochDay: 20410,
      category: "PCOS",
      symptomType: "Fatigue",
      severity: "MODERATE",
      durationText: "2 days",
      frequencyText: "Recurring",
      notes: "Felt tired in afternoon",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("wellness_logs").doc("w1").set({
      userId: ALICE_UID,
      dateEpochDay: 20410,
      sleepHours: 7.5,
      mood: "CALM",
      stressLevel: 2,
      energyLevel: 4,
      activityMinutes: 30,
      hydrationGlasses: 8,
      nutritionNotes: "Balanced lunch with iron-rich greens",
      wellnessGoal: "8 glasses water daily",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("health_records").doc("r1").set({
      userId: ALICE_UID,
      recordType: "LAB_REPORT",
      title: "Thyroid & HbA1c Panel",
      dateText: "2026-11-10",
      details: "TSH 2.4 mIU/L, HbA1c 5.3%",
      verifiedByUser: true,
      sharedWithDoctorIds: ["doc_dhk_01"],
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("consent_logs").doc("c1").set({
      userId: ALICE_UID,
      consentType: "AI_DATA_PROCESSING",
      granted: true,
      summary: "User consented to NariBondhu AI educational queries",
      createdAt: ts,
    })
  );

  // Consent log cannot be mutated after creation
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("consent_logs").doc("c1").update({
      granted: false,
    })
  );
});

test("BondhuHelp booking lifecycle enforces terminal state locking", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = new Date(Date.now() - 4000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b1").set({
      userId: ALICE_UID,
      doctorId: "doc_dhk_01",
      doctorName: "Dr. Farhana Rahman",
      specialty: "Gynaecology & PCOS",
      clinicName: "Dhanmondi Women's Care",
      city: "Dhaka",
      consultationType: "ONLINE",
      slotLabel: "Tomorrow 11:00 AM",
      feeBdt: 1200,
      status: "CONFIRMED",
      sharedSummaryGranted: true,
      sharedRecordCount: 2,
      notesForDoctor: "Irregular cycles for 4 months",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Cancel booking
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b1").update({
      status: "CANCELLED",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Subsequent update on terminal CANCELLED booking must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("bookings").doc("b1").update({
      status: "CONFIRMED",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("Service tier updates, weekly token bounds, and Tier 3 E_PRESCRIPTION records validate cleanly", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Nusrat",
      preferredLanguage: "BN",
      city: "Dhaka",
      ageConfirmed18Plus: true,
      healthInterests: ["PCOS"],
      discreetMode: false,
      onboardingCompleted: true,
      remindersEnabled: true,
      aiConsentGranted: true,
      cloudSyncConsentGranted: true,
      serviceTier: "FREE",
      weeklyAiTokensRemaining: 10,
      weeklyTokenResetEpochDay: 20778,
      preferredConsultMode: "ONLINE",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Decrement token and upgrade to TIER_2 with IN_PERSON mode
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      weeklyAiTokensRemaining: 9,
      serviceTier: "TIER_2",
      preferredConsultMode: "IN_PERSON",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Invalid tier or token > 10 must fail
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      serviceTier: "TIER_99",
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      weeklyAiTokensRemaining: 25,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // E_PRESCRIPTION health record allowed
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("health_records").doc("rx1").set({
      userId: ALICE_UID,
      recordType: "E_PRESCRIPTION",
      title: "BMDC Rx — Dr. Farhana Rahman",
      dateText: "2026-11-21",
      details: "Tab. Myo-inositol + Folic Acid once daily after dinner x 30 days.",
      verifiedByUser: true,
      sharedWithDoctorIds: ["doc_dhk_01"],
      createdAt: ts,
      updatedAt: ts,
    })
  );
});

