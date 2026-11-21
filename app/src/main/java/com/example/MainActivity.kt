package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.core.AppStrings
import com.example.core.EpochDateUtil
import com.example.core.LocalPrivacyPreferences
import com.example.core.configureAppCheck
import com.example.data.AppLanguage
import com.example.data.ConsentType
import com.example.data.ConsultationType
import com.example.data.MenstrualPeriod
import com.example.data.NariBondhuAiRepository
import com.example.data.NariBondhuRepository
import com.example.data.SymptomCategory
import com.example.ui.MainViewModel
import com.example.ui.UiState
import com.example.ui.ai.NariBondhuAiScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.DiscreetPinLockScreen
import com.example.ui.auth.NariBondhuOpeningSplash
import com.example.ui.auth.OnboardingFlowScreen
import com.example.ui.auth.signOut
import com.example.ui.bondhuhelp.BondhuHelpScreen
import com.example.ui.health.LogPeriodDialog
import com.example.ui.health.LogSymptomDialog
import com.example.ui.health.LogWellnessDialog
import com.example.ui.health.MyHealthScreen
import com.example.ui.home.HealthSubTab
import com.example.ui.home.HomeDashboardScreen
import com.example.ui.profile.ProfileAndPrivacyScreen
import com.example.ui.profile.TierPlansDialog
import com.example.ui.theme.NariBondhuTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, MY_HEALTH, AI, BONDHU_HELP, PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // App Check MUST be configured before any Firebase.ai or Firestore calls.
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            NariBondhuTheme {
                NariBondhuRootApp()
            }
        }
    }
}

private fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
private fun NariBondhuRootApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val privacyPrefs = remember(context) { LocalPrivacyPreferences(context) }
    val credentialManager = remember(context) { CredentialManager.create(context) }

    val localLanguage by privacyPrefs.languageFlow.collectAsStateWithLifecycle(initialValue = AppLanguage.EN)
    val pinEnabled by privacyPrefs.pinEnabledFlow.collectAsStateWithLifecycle(initialValue = false)
    val pinCode by privacyPrefs.pinCodeFlow.collectAsStateWithLifecycle(initialValue = "")

    val currentUser by remember { Firebase.auth.authStateFlow() }
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)

    var showOpening by remember { mutableStateOf(true) }
    var pinUnlocked by remember(currentUser?.uid) { mutableStateOf(false) }

    Crossfade(
        targetState = showOpening,
        animationSpec = tween(durationMillis = 450),
        label = "openingTransition",
    ) { isShowingOpening ->
        if (isShowingOpening) {
            NariBondhuOpeningSplash(
                language = localLanguage,
                onContinue = { showOpening = false },
            )
        } else {
            if (currentUser == null) {
                AuthScreen(
                    language = localLanguage,
                    onLanguageChange = { lang -> scope.launch { privacyPrefs.setLanguage(lang) } },
                    onAuthSuccess = { /* authStateFlow updates automatically */ },
                )
            } else {
                val uid = currentUser!!.uid
    key(uid) {
        if (pinEnabled && pinCode.length == 4 && !pinUnlocked) {
            DiscreetPinLockScreen(
                expectedPin = pinCode,
                language = localLanguage,
                onUnlocked = { pinUnlocked = true },
            )
        } else {
            val viewModel: MainViewModel = viewModel(
            key = uid,
            factory = viewModelFactory {
                initializer {
                    MainViewModel(
                        repository = NariBondhuRepository(context.applicationContext),
                        aiRepository = NariBondhuAiRepository(),
                        currentUserId = uid,
                    )
                }
            }
        )

        val profileState by viewModel.profileState.collectAsStateWithLifecycle()
        val periodsState by viewModel.periodsState.collectAsStateWithLifecycle()
        val symptomsState by viewModel.symptomsState.collectAsStateWithLifecycle()
        val wellnessState by viewModel.wellnessState.collectAsStateWithLifecycle()
        val recordsState by viewModel.recordsState.collectAsStateWithLifecycle()
        val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
        val consentLogsState by viewModel.consentLogsState.collectAsStateWithLifecycle()
        val cycleStats by viewModel.cycleStats.collectAsStateWithLifecycle()
        val recentSafetyTier by viewModel.recentSymptomSafetyTier.collectAsStateWithLifecycle()
        val activeServiceTier by viewModel.activeServiceTier.collectAsStateWithLifecycle()
        val tierEntitlements by viewModel.tierEntitlements.collectAsStateWithLifecycle()
        val weeklyTokensRemaining by viewModel.weeklyTokensRemaining.collectAsStateWithLifecycle()
        val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
        val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
        val aiStreamState by viewModel.aiStreamState.collectAsStateWithLifecycle()
        val extractionState by viewModel.extractionState.collectAsStateWithLifecycle()
        val summaryPrepState by viewModel.summaryPrepState.collectAsStateWithLifecycle()

        when (val pState = profileState) {
            UiState.Loading -> {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CircularProgressIndicator()
                            Text("Opening your private NariBondhu space...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            is UiState.Error -> {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("Could not connect to your health space", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(8.dp))
                        Text(pState.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(onClick = { signOut(credentialManager, scope) }) {
                            Text("Sign Out")
                        }
                    }
                }
            }

            is UiState.Success -> {
                val profile = pState.data
                if (profile == null || !profile.onboardingCompleted) {
                    OnboardingFlowScreen(
                        initialName = currentUser?.displayName.orEmpty(),
                        language = localLanguage,
                        onLanguageChange = { lang -> scope.launch { privacyPrefs.setLanguage(lang) } },
                        onComplete = { displayName, lang, city, age18Plus, interests, aiConsent, reminders, cloudSync ->
                            scope.launch { privacyPrefs.setLanguage(lang) }
                            viewModel.completeOnboarding(
                                displayName = displayName,
                                language = lang,
                                city = city,
                                ageConfirmed18Plus = age18Plus,
                                healthInterests = interests,
                                aiConsentGranted = aiConsent,
                                remindersEnabled = reminders,
                                cloudSyncConsentGranted = cloudSync,
                            )
                        },
                    )
                } else {
                    val activeLanguage = AppLanguage.fromCode(profile.preferredLanguage)
                    val copy = AppStrings.of(activeLanguage)
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(statusBanner) {
                        val msg = statusBanner
                        if (!msg.isNullOrBlank()) {
                            snackbarHostState.showSnackbar(msg)
                            viewModel.clearStatusBanner()
                        }
                    }

                    var currentTab by remember { mutableStateOf(MainTab.HOME) }
                    var selectedHealthTab by remember { mutableStateOf(HealthSubTab.CYCLE) }
                    var editingPeriod by remember { mutableStateOf<MenstrualPeriod?>(null) }
                    var periodDefaultDay by remember { mutableStateOf(EpochDateUtil.todayEpochDay()) }
                    var showPeriodDialog by remember { mutableStateOf(false) }
                    var showSymptomDialog by remember { mutableStateOf(false) }
                    var symptomDefaultCategory by remember { mutableStateOf(SymptomCategory.GENERAL) }
                    var showWellnessDialog by remember { mutableStateOf(false) }
                    var showPlansDialog by remember { mutableStateOf(false) }

                    BackHandler(enabled = currentTab != MainTab.HOME) {
                        currentTab = MainTab.HOME
                    }

                    val navItems = listOf(
                        Triple(MainTab.HOME, Icons.Rounded.Home, copy.navHome),
                        Triple(MainTab.MY_HEALTH, Icons.Rounded.Favorite, copy.navMyHealth),
                        Triple(MainTab.AI, Icons.Rounded.Psychology, copy.navAi),
                        Triple(MainTab.BONDHU_HELP, Icons.Rounded.LocalHospital, copy.navBondhuHelp),
                        Triple(MainTab.PROFILE, Icons.Rounded.Person, copy.navProfile),
                    )

                    val periods = (periodsState as? UiState.Success)?.data.orEmpty()
                    val symptoms = (symptomsState as? UiState.Success)?.data.orEmpty()
                    val wellnessLogs = (wellnessState as? UiState.Success)?.data.orEmpty()
                    val healthRecords = (recordsState as? UiState.Success)?.data.orEmpty()
                    val bookings = (bookingsState as? UiState.Success)?.data.orEmpty()
                    val consentLogs = (consentLogsState as? UiState.Success)?.data.orEmpty()

                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val useRail = maxWidth >= 600.dp

                        Scaffold(
                            contentWindowInsets = WindowInsets.safeDrawing,
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            bottomBar = {
                                if (!useRail) {
                                    NavigationBar {
                                        navItems.forEach { (tab, icon, label) ->
                                            NavigationBarItem(
                                                selected = currentTab == tab,
                                                onClick = { currentTab = tab },
                                                icon = { Icon(icon, contentDescription = label) },
                                                label = { Text(label, maxLines = 1) },
                                                modifier = Modifier.testTag("nav_${tab.name.lowercase()}"),
                                            )
                                        }
                                    }
                                }
                            },
                        ) { innerPadding ->
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                            ) {
                                if (useRail) {
                                    NavigationRail(modifier = Modifier.fillMaxHeight()) {
                                        Spacer(Modifier.height(12.dp))
                                        navItems.forEach { (tab, icon, label) ->
                                            NavigationRailItem(
                                                selected = currentTab == tab,
                                                onClick = { currentTab = tab },
                                                icon = { Icon(icon, contentDescription = label) },
                                                label = { Text(label) },
                                                modifier = Modifier.testTag("rail_${tab.name.lowercase()}"),
                                            )
                                        }
                                    }
                                }

                                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                    when (currentTab) {
                                        MainTab.HOME -> HomeDashboardScreen(
                                            profile = profile,
                                            language = activeLanguage,
                                            discreetMode = profile.discreetMode,
                                            cycleStats = cycleStats,
                                            recentSafetyTier = recentSafetyTier,
                                            entitlements = tierEntitlements,
                                            weeklyTokensRemaining = weeklyTokensRemaining,
                                            upcomingBookings = bookings,
                                            onToggleLanguage = { lang ->
                                                scope.launch { privacyPrefs.setLanguage(lang) }
                                                viewModel.updateLanguage(lang)
                                            },
                                            onToggleDiscreet = { enabled -> viewModel.toggleDiscreetMode(enabled) },
                                            onOpenPlans = { showPlansDialog = true },
                                            onQuickLogPeriod = {
                                                editingPeriod = null
                                                periodDefaultDay = EpochDateUtil.todayEpochDay()
                                                showPeriodDialog = true
                                            },
                                            onQuickLogSymptom = {
                                                symptomDefaultCategory = SymptomCategory.GENERAL
                                                showSymptomDialog = true
                                            },
                                            onOpenAi = { currentTab = MainTab.AI },
                                            onOpenBondhuHelp = { currentTab = MainTab.BONDHU_HELP },
                                            onOpenHealthModule = { sub ->
                                                selectedHealthTab = sub
                                                currentTab = MainTab.MY_HEALTH
                                            },
                                        )

                                        MainTab.MY_HEALTH -> MyHealthScreen(
                                            selectedTab = selectedHealthTab,
                                            onSelectTab = { selectedHealthTab = it },
                                            language = activeLanguage,
                                            discreetMode = profile.discreetMode,
                                            entitlements = tierEntitlements,
                                            healthInterests = profile.healthInterests,
                                            periods = periods,
                                            cycleStats = cycleStats,
                                            symptoms = symptoms,
                                            wellnessLogs = wellnessLogs,
                                            healthRecords = healthRecords,
                                            bookings = bookings,
                                            extractionState = extractionState,
                                            summaryPrepState = summaryPrepState,
                                            aiConsentGranted = profile.aiConsentGranted,
                                            onOpenPlans = { showPlansDialog = true },
                                            onSavePeriod = { period -> viewModel.savePeriod(period) },
                                            onOpenPeriodDialog = { p, day ->
                                                editingPeriod = p
                                                periodDefaultDay = day
                                                showPeriodDialog = true
                                            },
                                            onDeletePeriod = { id -> viewModel.deletePeriod(id) },
                                            onOpenSymptomDialog = { cat ->
                                                symptomDefaultCategory = cat
                                                showSymptomDialog = true
                                            },
                                            onSavePcosCheckIn = { entries, onResult ->
                                                viewModel.addPcosDailyCheckIn(entries, onResult)
                                            },
                                            onDeleteSymptom = { id -> viewModel.deleteSymptom(id) },
                                            onOpenWellnessDialog = { showWellnessDialog = true },
                                            onOpenRecordDialog = {},
                                            onDeleteRecord = { id -> viewModel.deleteHealthRecord(id) },
                                            onScanReportBitmap = { bmp -> viewModel.extractReportFromBitmap(bmp) },
                                            onSaveScannedRecord = { rec -> viewModel.addHealthRecord(rec) },
                                            onResetExtraction = { viewModel.resetExtractionState() },
                                            onGenerateAppointmentSummary = {
                                                viewModel.generatePreConsultationSummary(activeLanguage)
                                            },
                                            onBuildPlainSummaryText = { viewModel.buildDeidentifiedSummaryText() },
                                            onSaveClinicianEPrescription = { booking, dateText, impression, regimen, followUp ->
                                                viewModel.saveClinicianEPrescription(booking, dateText, impression, regimen, followUp)
                                            },
                                            onOpenBondhuHelp = { currentTab = MainTab.BONDHU_HELP },
                                            onAskAiTopic = { topic ->
                                                currentTab = MainTab.AI
                                                if (profile.aiConsentGranted) {
                                                    val prompt = if (activeLanguage == AppLanguage.BN) {
                                                        "দয়া করে সহজ ভাষায় ব্যাখ্যা করুন: $topic"
                                                    } else {
                                                        "Please explain simply in plain language: $topic"
                                                    }
                                                    viewModel.sendAiMessage(prompt, activeLanguage, false)
                                                }
                                            },
                                        )

                                        MainTab.AI -> NariBondhuAiScreen(
                                            language = activeLanguage,
                                            aiConsentGranted = profile.aiConsentGranted,
                                            entitlements = tierEntitlements,
                                            weeklyTokensRemaining = weeklyTokensRemaining,
                                            chatMessages = chatMessages,
                                            aiStreamState = aiStreamState,
                                            onGrantAiConsent = {
                                                viewModel.updateConsent(ConsentType.AI_DATA_PROCESSING, true)
                                            },
                                            onOpenPlans = { showPlansDialog = true },
                                            onOpenLibrary = {
                                                selectedHealthTab = HealthSubTab.LIBRARY
                                                currentTab = MainTab.MY_HEALTH
                                            },
                                            onOpenBondhuHelp = { currentTab = MainTab.BONDHU_HELP },
                                            onSendMessage = { text, includeSummary ->
                                                viewModel.sendAiMessage(text, activeLanguage, includeSummary)
                                            },
                                        )

                                        MainTab.BONDHU_HELP -> BondhuHelpScreen(
                                            language = activeLanguage,
                                            userCity = profile.city,
                                            entitlements = tierEntitlements,
                                            preferredConsultMode = ConsultationType.fromName(profile.preferredConsultMode),
                                            healthRecords = healthRecords,
                                            bookings = bookings,
                                            summaryPrepState = summaryPrepState,
                                            aiConsentGranted = profile.aiConsentGranted,
                                            onSetPreferredConsultMode = { mode -> viewModel.setPreferredConsultMode(mode) },
                                            onOpenPlans = { showPlansDialog = true },
                                            onOpenLifestyleAndRx = {
                                                selectedHealthTab = HealthSubTab.LIFESTYLE_RX
                                                currentTab = MainTab.MY_HEALTH
                                            },
                                            onGenerateSummary = { viewModel.generatePreConsultationSummary(activeLanguage) },
                                            onBookAppointment = { doctor, cType, slot, share, recIds, notes ->
                                                viewModel.bookAppointment(doctor, cType, slot, share, recIds, notes)
                                            },
                                            onUpdateBookingStatus = { id, status, slot ->
                                                viewModel.updateBookingStatus(id, status, slot)
                                            },
                                        )

                                        MainTab.PROFILE -> ProfileAndPrivacyScreen(
                                            profile = profile,
                                            language = activeLanguage,
                                            discreetMode = profile.discreetMode,
                                            pinEnabled = pinEnabled,
                                            activeTier = activeServiceTier,
                                            weeklyTokensRemaining = weeklyTokensRemaining,
                                            consentLogs = consentLogs,
                                            onSelectTier = { tier -> viewModel.selectServiceTier(tier) },
                                            onLanguageChange = { lang ->
                                                scope.launch { privacyPrefs.setLanguage(lang) }
                                                viewModel.updateLanguage(lang)
                                            },
                                            onToggleDiscreetMode = { enabled -> viewModel.toggleDiscreetMode(enabled) },
                                            onSetPin = { newPin ->
                                                scope.launch { privacyPrefs.setDiscreetPin(newPin) }
                                            },
                                            onUpdateConsent = { type, granted -> viewModel.updateConsent(type, granted) },
                                            onExportJson = { viewModel.exportAllDataJson() },
                                            onDeleteAllDataAndSignOut = {
                                                viewModel.deleteAllDataAndSignOut {
                                                    scope.launch { privacyPrefs.clearAll() }
                                                    signOut(credentialManager, scope)
                                                }
                                            },
                                            onSignOut = { signOut(credentialManager, scope) },
                                        )
                                    }
                                }
                            }
                        }

                        if (showPeriodDialog) {
                            LogPeriodDialog(
                                initialPeriod = editingPeriod,
                                defaultStartEpochDay = periodDefaultDay,
                                language = activeLanguage,
                                onDismiss = { showPeriodDialog = false },
                                onSave = { period ->
                                    viewModel.savePeriod(period)
                                    showPeriodDialog = false
                                },
                                onDelete = { id ->
                                    viewModel.deletePeriod(id)
                                    showPeriodDialog = false
                                },
                            )
                        }

                        if (showSymptomDialog) {
                            LogSymptomDialog(
                                defaultCategory = symptomDefaultCategory,
                                language = activeLanguage,
                                onDismiss = { showSymptomDialog = false },
                                onSave = { entry ->
                                    viewModel.addSymptom(entry)
                                    showSymptomDialog = false
                                },
                            )
                        }

                        if (showWellnessDialog) {
                            LogWellnessDialog(
                                language = activeLanguage,
                                onDismiss = { showWellnessDialog = false },
                                onSave = { log ->
                                    viewModel.addWellnessLog(log)
                                    showWellnessDialog = false
                                },
                            )
                        }

                        if (showPlansDialog) {
                            TierPlansDialog(
                                activeTier = activeServiceTier,
                                weeklyTokensRemaining = weeklyTokensRemaining,
                                language = activeLanguage,
                                onSelectTier = { chosen -> viewModel.selectServiceTier(chosen) },
                                onDismiss = { showPlansDialog = false },
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
