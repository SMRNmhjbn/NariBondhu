package com.example.ui.ai

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.AppStrings
import com.example.core.ServiceTierEngine
import com.example.core.TierEntitlements
import com.example.core.TtsSpeaker
import com.example.core.rememberTtsSpeaker
import com.example.data.AppLanguage
import com.example.data.ChatMessage
import com.example.data.SafetyTier
import com.example.data.ServiceTier
import com.example.ui.AiStreamState
import com.example.ui.profile.TierLockedFeatureCard
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.SafetyAmber
import com.example.ui.theme.SafetyAmberContainer
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyGreenContainer
import com.example.ui.theme.SafetyRed
import com.example.ui.theme.SafetyRedContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NariBondhuAiScreen(
    language: AppLanguage,
    aiConsentGranted: Boolean,
    entitlements: TierEntitlements,
    weeklyTokensRemaining: Int,
    chatMessages: List<ChatMessage>,
    aiStreamState: AiStreamState,
    onGrantAiConsent: () -> Unit,
    onOpenPlans: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenBondhuHelp: () -> Unit,
    onSendMessage: (text: String, includeDeidentifiedSummary: Boolean) -> Unit,
) {
    val isBn = language == AppLanguage.BN
    val copy = AppStrings.of(language)
    val context = LocalContext.current
    val speaker = rememberTtsSpeaker()
    var inputText by remember { mutableStateOf("") }
    var includeSummary by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size, aiStreamState) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.lastIndex)
        }
    }

    val quickQuestions = if (isBn) {
        listOf(
            "পিসিওএস-এর সাধারণ লক্ষণ কী কী এবং কখন ডাক্তার দেখাব?",
            "মাসিকের আগে মন খারাপ ও ক্লান্তি কেন হয়?",
            "মাসিক অনিয়মিত হলে কী করব?",
            "সাদাস্রাব বা চুলকানি হলে কখন ডাক্তার দেখানো উচিত?",
            "কোন কোন লক্ষণ দেখলে দ্রুত জরুরি চিকিৎসা নিতে হবে?"
        )
    } else {
        listOf(
            "What are common signs of PCOS and when should I see a doctor?",
            "Why do cramps and mood changes happen before my period?",
            "When should I consult a doctor for irregular cycles?",
            "How can I tell if vaginal discharge or itching needs medical care?",
            "What urgent symptoms mean I should seek immediate care?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        // Prominent non-diagnostic disclaimer (PRD Section 10 & 16)
        Surface(
            color = Lavender,
            contentColor = DeepPlumDark,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = DeepPlumDark)
                Text(
                    text = copy.aiDisclaimer,
                    style = MaterialTheme.typography.bodySmall,
                    color = DeepPlumDark,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Care Tier Token Balance & Privacy Status Bar
        val hasTokens = entitlements.hasUnlimitedAiTokens || weeklyTokensRemaining > 0
        Surface(
            color = if (entitlements.hasUnlimitedAiTokens) SafetyGreenContainer else MutedRoseContainer,
            contentColor = InkPrimary,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_token_status_banner"),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (entitlements.hasUnlimitedAiTokens) {
                            if (isBn) "${entitlements.tier.bnName} • আনলিমিটেড AI টোকেন"
                            else "${entitlements.tier.enName} • Unlimited AI Tokens"
                        } else {
                            if (isBn) "ফ্রি প্ল্যান: সপ্তাহে $weeklyTokensRemaining/${ServiceTierEngine.FREE_WEEKLY_AI_TOKENS}টি AI টোকেন বাকি"
                            else "Free Plan: $weeklyTokensRemaining/${ServiceTierEngine.FREE_WEEKLY_AI_TOKENS} AI tokens left this week"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = if (entitlements.hasUnlimitedAiTokens) SafetyGreen else DeepPlumDark,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (isBn) "প্রাইভেসি ও জরুরি ৯৯৯ সহায়তা সবসময় ফ্রি" else "Full privacy & emergency 999 help always included",
                        style = MaterialTheme.typography.labelSmall,
                        color = InkSecondary,
                    )
                }
                if (!entitlements.hasUnlimitedAiTokens) {
                    TextButton(onClick = onOpenPlans, modifier = Modifier.testTag("ai_upgrade_tier1_btn")) {
                        Text(
                            text = if (isBn) "টিয়ার ১ (৳২০০)" else "Tier 1 (৳200)",
                            color = DeepPlum,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        if (!aiConsentGranted) {
            AiConsentGateCard(
                language = language,
                onGrantAiConsent = onGrantAiConsent,
                onOpenLibrary = onOpenLibrary,
            )
        } else {
        // Explicit opt-in toggle for optional de-identified context
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(Icons.Rounded.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Column {
                        Text(
                            text = if (isBn) "চক্র ও লক্ষণের সংক্ষিপ্ত সারাংশ যুক্ত করুন" else "Include de-identified cycle & symptom summary",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Text(
                            text = if (isBn) "নাম বা রিপোর্ট কখনো পাঠানো হয় না" else "Your name & files are never sent",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Switch(
                    checked = includeSummary,
                    onCheckedChange = { includeSummary = it },
                    modifier = Modifier.testTag("ai_include_summary_switch"),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(Icons.Rounded.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = if (isBn) "সহজ প্রশ্ন দিয়ে শুরু করুন" else "Tap a Question to Begin",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            Text(
                                text = if (isBn) {
                                    "আপনি পিসিওএস, পিএমএস, মাসিক, গোপন স্বাস্থ্য বা ডাক্তারের পরামর্শ নেওয়ার সময় সম্পর্কে সহজ ভাষায় জানতে পারেন।"
                                } else {
                                    "Ask about cycles, PCOS, PMS, intimate wellness, or when to consult a BondhuHelp gynaecologist."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                quickQuestions.forEachIndexed { idx, prompt ->
                                    AssistChip(
                                        onClick = { onSendMessage(prompt, includeSummary) },
                                        enabled = hasTokens || idx == quickQuestions.lastIndex,
                                        label = { Text(prompt) },
                                        modifier = Modifier.testTag("ai_quick_chip_$idx"),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!hasTokens) {
                item {
                    TierLockedFeatureCard(
                        requiredTier = ServiceTier.TIER_1,
                        title = if (isBn) "এই সপ্তাহের ১০টি ফ্রি AI টোকেন শেষ" else "10 Free Weekly AI Tokens Used",
                        description = if (isBn) {
                            "প্রতি সপ্তাহে ১০টি ফ্রি টোকেন স্বয়ংক্রিয়ভাবে রিসেট হয়। এখনই আনলিমিটেড AI, ব্যক্তিগত PCOS/PMS কেয়ার ও চক্র বিশ্লেষণের জন্য টিয়ার ১ (৳২০০/মাস) চালু করুন, অথবা ফ্রি স্বাস্থ্য পাঠাগার পড়ুন।"
                        } else {
                            "Your 10 free weekly AI tokens reset automatically every 7 days. Upgrade to Tier 1 (BDT 200/month) for unlimited AI tokens, personalised PCOS/PMS care, and full Cycle Analysis — or read the free Health Library anytime."
                        },
                        highlights = if (isBn) {
                            listOf(
                                "আনলিমিটেড নারীবন্ধু AI প্রশ্নোত্তর",
                                "ব্যক্তিগত PCOS ও PMS যত্ন + চক্র বিশ্লেষণ",
                                "জরুরি ৯৯৯ সহায়তা ও প্রাইভেসি সবসময় ফ্রি",
                            )
                        } else {
                            listOf(
                                "Unlimited NariBondhu AI chatbot tokens",
                                "Personalised PCOS/PMS care & full Cycle Analysis",
                                "Emergency 999 triage & Health Library always free",
                            )
                        },
                        language = language,
                        onOpenPlans = onOpenPlans,
                        testTag = "ai_weekly_quota_card",
                    )
                }
            }

            items(chatMessages, key = { it.id }) { msg ->
                ChatBubbleItem(
                    message = msg,
                    language = language,
                    speaker = speaker,
                    onCallEmergency = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:999"))
                        context.startActivity(dialIntent)
                    },
                    onOpenBondhuHelp = onOpenBondhuHelp,
                )
            }

            when (val stream = aiStreamState) {
                is AiStreamState.Loading -> {
                    item {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Text(
                                text = if (isBn) "নারীবন্ধু AI সহজ ভাষায় উত্তর প্রস্তুত করছে..." else "NariBondhu AI is preparing clear educational guidance...",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                is AiStreamState.Streaming -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(18.dp)),
                        ) {
                            Text(
                                text = stream.text,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                    }
                }
                is AiStreamState.Error -> {
                    item {
                        Surface(
                            color = SafetyRedContainer,
                            contentColor = InkPrimary,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stream.message, color = SafetyRed, style = MaterialTheme.typography.bodyMedium)
                                OutlinedButton(
                                    onClick = onOpenLibrary,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SafetyRed),
                                    border = BorderStroke(1.dp, SafetyRed),
                                ) {
                                    Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (isBn) "অফলাইন স্বাস্থ্য পাঠাগার দেখুন" else "Read Offline Health Library")
                                }
                            }
                        }
                    }
                }
                AiStreamState.Idle -> Unit
            }
        }

        Spacer(Modifier.height(6.dp))

        // Composer Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it.take(600) },
                placeholder = {
                    Text(
                        if (!hasTokens) {
                            if (isBn) "ফ্রি টোকেন শেষ • টিয়ার ১-এ আনলিমিটেড AI" else "0 tokens left • Upgrade to Tier 1 or type urgent signs"
                        } else {
                            if (isBn) "আপনার স্বাস্থ্য প্রশ্ন লিখুন..." else "Ask an educational health question..."
                        }
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_prompt_input"),
            )
            FilledIconButton(
                onClick = {
                    val text = inputText.trim()
                    if (text.isNotEmpty()) {
                        inputText = ""
                        onSendMessage(text, includeSummary)
                    }
                },
                enabled = inputText.isNotBlank() && aiStreamState !is AiStreamState.Loading,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = DeepPlum),
                modifier = Modifier
                    .size(52.dp)
                    .testTag("ai_send_button"),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = if (isBn) "পাঠান" else "Send",
                )
            }
        }
        }
    }
}

@Composable
private fun AiConsentGateCard(
    language: AppLanguage,
    onGrantAiConsent: () -> Unit,
    onOpenLibrary: () -> Unit,
) {
    val isBn = language == AppLanguage.BN
    val copy = AppStrings.of(language)
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Lavender),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = DeepPlumDark)
            }
            Text(copy.aiConsentRequiredTitle, style = MaterialTheme.typography.titleLarge)
            Text(
                copy.aiConsentRequiredDesc,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onGrantAiConsent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("ai_enable_consent_btn"),
            ) {
                Text(copy.grantAiConsentBtn)
            }
            OutlinedButton(
                onClick = onOpenLibrary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("ai_open_library_fallback"),
            ) {
                Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isBn) "ডাক্তার-পর্যালোচিত পাঠাগার খুলুন" else "Open Clinician-Reviewed Library")
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    language: AppLanguage,
    speaker: TtsSpeaker,
    onCallEmergency: () -> Unit,
    onOpenBondhuHelp: () -> Unit,
) {
    val isBn = language == AppLanguage.BN
    val copy = AppStrings.of(language)

    if (message.isUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Surface(
                color = DeepPlum,
                shape = RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp),
                modifier = Modifier.widthIn(max = 310.dp),
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.padding(14.dp),
                )
            }
        }
        return
    }

    val tierBg = when (message.safetyTier) {
        SafetyTier.RED -> SafetyRedContainer
        SafetyTier.AMBER -> SafetyAmberContainer
        SafetyTier.GREEN -> SafetyGreenContainer
    }
    val tierFg = when (message.safetyTier) {
        SafetyTier.RED -> SafetyRed
        SafetyTier.AMBER -> SafetyAmber
        SafetyTier.GREEN -> SafetyGreen
    }
    val tierLabel = when (message.safetyTier) {
        SafetyTier.RED -> if (isBn) "জরুরি সতর্কতা (৯৯৯)" else "Urgent Medical Care (Red)"
        SafetyTier.AMBER -> if (isBn) "ডাক্তারের পরামর্শ প্রয়োজন" else "Doctor Consultation Recommended"
        SafetyTier.GREEN -> if (isBn) "সাধারণ স্বাস্থ্য তথ্য" else "Educational Guidance"
    }

    val isRed = message.safetyTier == SafetyTier.RED
    Card(
        shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRed) SafetyRedContainer else MaterialTheme.colorScheme.surface,
            contentColor = if (isRed) InkPrimary else MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (message.safetyTier == SafetyTier.GREEN) MaterialTheme.colorScheme.outlineVariant else tierFg,
                shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(color = tierBg, contentColor = tierFg, shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = tierLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = tierFg,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }

            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isRed) InkPrimary else MaterialTheme.colorScheme.onSurface,
            )

            if (isRed) {
                Button(
                    onClick = onCallEmergency,
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyRed, contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("ai_call_999_btn"),
                ) {
                    Icon(Icons.Rounded.Call, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(copy.emergencyCall999)
                }
            }

            if (message.showBondhuHelpCta || message.safetyTier == SafetyTier.AMBER) {
                Button(
                    onClick = onOpenBondhuHelp,
                    colors = ButtonDefaults.buttonColors(containerColor = SafetyAmber, contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("ai_open_bondhuhelp_btn"),
                ) {
                    Icon(Icons.Rounded.LocalHospital, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(copy.consultDoctorBtn)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (message.sourcesCited.isNotEmpty()) {
                        message.sourcesCited.joinToString(" • ")
                    } else "NariBondhu Health Library",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isRed) InkSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                TextButton(
                    onClick = {
                        if (speaker.isSpeaking) speaker.stop() else speaker.speak(message.text, language)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isRed) SafetyRed else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("ai_listen_${message.id}"),
                ) {
                    Icon(
                        imageVector = if (speaker.isSpeaking) Icons.Rounded.Stop else Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = copy.listenBtn,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (speaker.isSpeaking) copy.stopListenBtn else copy.listenBtn)
                }
            }
        }
    }
}
