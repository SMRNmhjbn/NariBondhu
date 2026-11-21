package com.example.ui.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.core.AppStrings
import com.example.core.rememberTtsSpeaker
import com.example.data.AppLanguage
import com.example.data.BondhuHelpCatalog
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.WarmIvory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope,
) {
    if (Firebase.auth.currentUser != null) {
        onAuthSuccess()
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (_: Exception) {
        onUnauthenticated()
        return
    }
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()
    scope.launch {
        try {
            val cred = credentialManager.getCredential(context, request).credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                onAuthSuccess()
            } else {
                onUnauthenticated()
            }
        } catch (_: Exception) {
            onUnauthenticated()
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {},
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (_: Exception) {
        onAuthError("Google Sign-In configuration missing: default_web_client_id not found")
        return
    }
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()
    scope.launch {
        try {
            val activityContext = context as? Activity ?: run {
                onAuthError("Unable to open Google Sign-In sheet from this context.")
                return@launch
            }
            val cred = credentialManager.getCredential(activityContext, request).credential
            if (cred is CustomCredential && cred.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(cred.data).idToken
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
                onAuthSuccess()
            } else {
                onAuthError("Unexpected credential type returned.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In flow cancelled or dismissed: ${e.message}", e)
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In failed", e)
            onAuthError(e.localizedMessage ?: "Sign in failed. Please try again.")
        }
    }
}

fun signOut(
    credentialManager: CredentialManager,
    scope: CoroutineScope,
    onSignOutComplete: () -> Unit = {},
) {
    scope.launch {
        withContext(NonCancellable) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w("Auth", "Failed to clear credential state: ${e.message}")
            } finally {
                Firebase.auth.signOut()
                onSignOutComplete()
            }
        }
    }
}

@Composable
fun AuthScreen(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onAuthSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val copy = AppStrings.of(language)
    val speaker = rememberTtsSpeaker()

    var isSigningIn by remember { mutableStateOf(false) }
    var authError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = onAuthSuccess,
            onUnauthenticated = {},
            scope = scope,
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmIvory),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .widthIn(max = 560.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top language switcher & Listen button for effortless accessibility
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLanguage.entries.forEach { lang ->
                        FilterChip(
                            selected = language == lang,
                            onClick = { onLanguageChange(lang) },
                            label = { Text(lang.nativeLabel) },
                            modifier = Modifier.testTag("lang_chip_${lang.code}"),
                        )
                    }
                }
                TextButton(
                    onClick = {
                        if (speaker.isSpeaking) {
                            speaker.stop()
                        } else {
                            speaker.speak("${copy.welcomeTitle}. ${copy.welcomeSubtitle}. ${copy.privacyFirstBadge}", language)
                        }
                    },
                    modifier = Modifier.testTag("auth_listen_button"),
                ) {
                    Icon(
                        imageVector = if (speaker.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = copy.listenBtn,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (speaker.isSpeaking) copy.stopListenBtn else copy.listenBtn)
                }
            }

            Spacer(Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.img_naribondhu_welcome),
                contentDescription = "NariBondhu calm botanical illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop,
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = copy.appName,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = copy.tagline,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            Surface(
                color = Lavender,
                contentColor = DeepPlumDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shield,
                        contentDescription = null,
                        tint = DeepPlumDark,
                    )
                    Text(
                        text = copy.privacyFirstBadge,
                        style = MaterialTheme.typography.labelLarge,
                        color = DeepPlumDark,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = copy.welcomeTitle,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = copy.welcomeSubtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (authError != null) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = authError.orEmpty(),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    authError = null
                    isSigningIn = true
                    onGoogleSignInClicked(
                        context = context,
                        credentialManager = credentialManager,
                        onAuthSuccess = {
                            isSigningIn = false
                            onAuthSuccess()
                        },
                        onAuthError = { msg ->
                            isSigningIn = false
                            authError = msg
                        },
                        scope = scope,
                        onAuthCancelled = { isSigningIn = false },
                    )
                },
                enabled = !isSigningIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("google_sign_in_button"),
                shape = RoundedCornerShape(16.dp),
            ) {
                if (isSigningIn) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(Icons.Rounded.Lock, contentDescription = null)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = copy.signInWithGoogle,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = if (language == AppLanguage.BN) {
                    "পরবর্তী ধাপে আপনি প্রতিটি প্রাইভেসি অনুমতি নিজে বেছে নিতে পারবেন।"
                } else {
                    "Next step: choose your exact privacy & AI consent settings before tracking."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingFlowScreen(
    initialName: String,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onComplete: (
        displayName: String,
        language: AppLanguage,
        city: String,
        age18Plus: Boolean,
        interests: List<String>,
        aiConsent: Boolean,
        reminders: Boolean,
        cloudSyncConsent: Boolean,
    ) -> Unit,
) {
    val copy = AppStrings.of(language)
    val speaker = rememberTtsSpeaker()
    var step by remember { mutableIntStateOf(0) }

    var name by remember { mutableStateOf(initialName.ifBlank { "" }) }
    var selectedCity by remember { mutableStateOf("Dhaka") }
    var age18Plus by remember { mutableStateOf(false) }

    var cloudConsent by remember { mutableStateOf(true) }
    var aiConsent by remember { mutableStateOf(true) }
    var remindersConsent by remember { mutableStateOf(false) }

    val availableInterests = listOf(
        "Menstrual Tracking",
        "PCOS Support",
        "PMS Support",
        "Intimate & Reproductive Health",
        "General Women's Health",
        "Personal Wellness",
    )
    val selectedInterests = remember { mutableStateListOf("General Women's Health") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .widthIn(max = 600.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (step > 0) {
                    IconButton(onClick = { step -= 1 }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = copy.backBtn)
                    }
                } else {
                    Spacer(Modifier.width(48.dp))
                }
                Text(
                    text = "${step + 1} / 3",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppLanguage.entries.forEach { lang ->
                        FilterChip(
                            selected = language == lang,
                            onClick = { onLanguageChange(lang) },
                            label = { Text(lang.nativeLabel) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            when (step) {
                0 -> {
                    Text(
                        text = if (language == AppLanguage.BN) "আপনার প্রাথমিক তথ্য" else "Let's Set Up Your Space",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (language == AppLanguage.BN) {
                            "সহজভাবে শুরু করতে আপনার নাম ও শহর বেছে নিন।"
                        } else {
                            "Enter a name you'd like us to greet you with, and your city for BondhuHelp."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (language == AppLanguage.BN) "আপনার নাম বা ডাকনাম" else "Your Name or Nickname") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input"),
                    )

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (language == AppLanguage.BN) "শহর নির্বাচন করুন" else "Preferred City (Bangladesh)",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        BondhuHelpCatalog.cities.drop(1).forEach { city ->
                            FilterChip(
                                selected = selectedCity == city,
                                onClick = { selectedCity = city },
                                label = { Text(city) },
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Lavender,
                            contentColor = InkPrimary,
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { age18Plus = !age18Plus },
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = age18Plus,
                                onCheckedChange = { age18Plus = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DeepPlum,
                                    checkmarkColor = Color.White,
                                    uncheckedColor = DeepPlumDark,
                                ),
                                modifier = Modifier.testTag("adult_confirm_checkbox"),
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(copy.consentAdultTitle, style = MaterialTheme.typography.titleSmall, color = InkPrimary)
                                Text(
                                    copy.consentAdultDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkSecondary,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { step = 1 },
                        enabled = age18Plus,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("onboarding_next_1"),
                    ) {
                        Text(copy.continueBtn)
                    }
                }

                1 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = copy.onboardingPrivacyTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = {
                            if (speaker.isSpeaking) speaker.stop()
                            else speaker.speak(
                                "${copy.onboardingPrivacyTitle}. ${copy.onboardingPrivacyDesc}. ${copy.consentStorageTitle}. ${copy.consentAiTitle}",
                                language
                            )
                        }) {
                            Icon(
                                if (speaker.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = copy.listenBtn
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(if (speaker.isSpeaking) copy.stopListenBtn else copy.listenBtn)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = copy.onboardingPrivacyDesc,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))

                    ConsentToggleCard(
                        icon = Icons.Rounded.VerifiedUser,
                        title = copy.consentStorageTitle,
                        description = copy.consentStorageDesc,
                        checked = cloudConsent,
                        onCheckedChange = { cloudConsent = it },
                        tag = "consent_storage_switch",
                    )
                    Spacer(Modifier.height(12.dp))
                    ConsentToggleCard(
                        icon = Icons.Rounded.Psychology,
                        title = copy.consentAiTitle,
                        description = copy.consentAiDesc,
                        checked = aiConsent,
                        onCheckedChange = { aiConsent = it },
                        tag = "consent_ai_switch",
                    )
                    Spacer(Modifier.height(12.dp))
                    ConsentToggleCard(
                        icon = Icons.Rounded.NotificationsNone,
                        title = copy.consentReminderTitle,
                        description = copy.consentReminderDesc,
                        checked = remindersConsent,
                        onCheckedChange = { remindersConsent = it },
                        tag = "consent_reminder_switch",
                    )

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("onboarding_next_2"),
                    ) {
                        Text(copy.continueBtn)
                    }
                }

                2 -> {
                    Text(
                        text = copy.interestsTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = copy.interestsDesc,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(18.dp))

                    availableInterests.forEach { interest ->
                        val selected = selectedInterests.contains(interest)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) Lavender else MaterialTheme.colorScheme.surface,
                                contentColor = if (selected) InkPrimary else MaterialTheme.colorScheme.onSurface,
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) DeepPlum else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .clickable {
                                    if (selected) selectedInterests.remove(interest)
                                    else selectedInterests.add(interest)
                                },
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = if (selected) Icons.Rounded.CheckCircle else Icons.Rounded.Favorite,
                                    contentDescription = null,
                                    tint = if (selected) DeepPlumDark else MaterialTheme.colorScheme.outline,
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = interest,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (selected) DeepPlumDark else MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            onComplete(
                                name,
                                language,
                                selectedCity,
                                age18Plus,
                                selectedInterests.toList(),
                                aiConsent,
                                remindersConsent,
                                cloudConsent,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("onboarding_finish_button"),
                    ) {
                        Text(copy.finishSetup, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsentToggleCard(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (checked) Lavender else MutedRoseContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = DeepPlumDark)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(tag),
            )
        }
    }
}

@Composable
fun DiscreetPinLockScreen(
    expectedPin: String,
    language: AppLanguage,
    onUnlocked: () -> Unit,
) {
    var entered by remember { mutableStateOf("") }
    var wrongPin by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(DeepPlum, MaterialTheme.colorScheme.tertiary))),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.Lock,
                    contentDescription = "Private PIN lock",
                    tint = WarmIvory,
                    modifier = Modifier.size(34.dp),
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = if (language == AppLanguage.BN) "নারীবন্ধু প্রাইভেসি পিন" else "NariBondhu Privacy Lock",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (language == AppLanguage.BN) "আপনার ব্যক্তিগত স্বাস্থ্য তথ্য দেখতে ৪ সংখ্যার পিন দিন" else "Enter your 4-digit discreet PIN to unlock your space",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                repeat(4) { idx ->
                    val filled = idx < entered.length
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
            if (wrongPin) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = if (language == AppLanguage.BN) "ভুল পিন, আবার চেষ্টা করুন" else "Incorrect PIN, please try again",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.height(24.dp))
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "⌫"),
            )
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 6.dp),
                ) {
                    row.forEach { key ->
                        OutlinedButton(
                            onClick = {
                                wrongPin = false
                                when (key) {
                                    "C" -> entered = ""
                                    "⌫" -> if (entered.isNotEmpty()) entered = entered.dropLast(1)
                                    else -> if (entered.length < 4) {
                                        val next = entered + key
                                        entered = next
                                        if (next.length == 4) {
                                            if (next == expectedPin) {
                                                onUnlocked()
                                            } else {
                                                wrongPin = true
                                                entered = ""
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.size(68.dp),
                            shape = CircleShape,
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}
