package com.example.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.ServiceTierEngine
import com.example.core.TierFeatureSummary
import com.example.data.AppLanguage
import com.example.data.ServiceTier
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.InkPrimary
import com.example.ui.theme.InkSecondary
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRoseContainer
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyGreenContainer
import com.example.ui.theme.WarmIvory

@Composable
fun ActiveTierStatusPill(
    tier: ServiceTier,
    weeklyTokensRemaining: Int,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isBn = language == AppLanguage.BN
    val label = when (tier) {
        ServiceTier.FREE -> if (isBn) {
            "ফ্রি প্ল্যান • $weeklyTokensRemaining/${ServiceTierEngine.FREE_WEEKLY_AI_TOKENS} AI টোকেন"
        } else {
            "Free Plan • $weeklyTokensRemaining/${ServiceTierEngine.FREE_WEEKLY_AI_TOKENS} AI tokens"
        }
        else -> if (isBn) "${tier.bnName} (${tier.bnPriceLabel})" else "${tier.enName} (${tier.enPriceLabel})"
    }

    Surface(
        color = WarmIvory,
        contentColor = DeepPlumDark,
        shape = RoundedCornerShape(999.dp),
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .testTag("active_tier_pill"),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.WorkspacePremium,
                contentDescription = null,
                tint = DeepPlumDark,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = DeepPlumDark,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun PrivacyIncludedBanner(
    language: AppLanguage,
    modifier: Modifier = Modifier,
) {
    val isBn = language == AppLanguage.BN
    Surface(
        color = SafetyGreenContainer,
        contentColor = InkPrimary,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Shield,
                contentDescription = null,
                tint = SafetyGreen,
                modifier = Modifier.size(22.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isBn) "সকল প্ল্যানেই প্রাইভেসি নিশ্চিত ও বিনামূল্যে অন্তর্ভুক্ত" else "Privacy Is Included in All Tiers",
                    style = MaterialTheme.typography.titleSmall,
                    color = SafetyGreen,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (isBn) {
                        "গোপনীয় মোড, ৪-সংখ্যার পিন লক, সম্মতি নিয়ন্ত্রণ, ডেটা এক্সপোর্ট ও অ্যাকাউন্ট মুছে ফেলার সুবিধা প্রতিটি প্ল্যানে চিরকাল ফ্রি।"
                    } else {
                        "Discreet Mode, 4-digit PIN lock, granular consent toggles, JSON export, account deletion, and emergency safety alerts are built into every tier."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = InkPrimary,
                )
            }
        }
    }
}

@Composable
fun TierLockedFeatureCard(
    requiredTier: ServiceTier,
    title: String,
    description: String,
    highlights: List<String>,
    language: AppLanguage,
    onOpenPlans: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "locked_feature_${requiredTier.code.lowercase()}",
) {
    val isBn = language == AppLanguage.BN
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MutedRoseContainer,
            contentColor = InkPrimary,
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DeepPlum.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .testTag(testTag),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    color = DeepPlum,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = if (isBn) {
                                "${requiredTier.bnName} • ${requiredTier.bnPriceLabel}"
                            } else {
                                "${requiredTier.enName} • ${requiredTier.enPriceLabel}"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = DeepPlumDark,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = InkPrimary,
            )

            highlights.forEach { bullet ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = SafetyGreen,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(16.dp),
                    )
                    Text(
                        text = bullet,
                        style = MaterialTheme.typography.bodySmall,
                        color = InkPrimary,
                    )
                }
            }

            Button(
                onClick = onOpenPlans,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DeepPlum,
                    contentColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("${testTag}_cta"),
            ) {
                Icon(Icons.Rounded.WorkspacePremium, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isBn) "প্ল্যান দেখুন ও আপগ্রেড করুন (${requiredTier.bnPriceLabel})"
                    else "Unlock with ${requiredTier.enName} (${requiredTier.enPriceLabel})"
                )
            }
        }
    }
}

@Composable
fun TierPlansComparisonSection(
    activeTier: ServiceTier,
    weeklyTokensRemaining: Int,
    language: AppLanguage,
    onSelectTier: (ServiceTier) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isBn = language == AppLanguage.BN
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PrivacyIncludedBanner(language = language)

        ServiceTierEngine.tierCatalog.forEach { summary ->
            TierPlanCard(
                summary = summary,
                isActive = summary.tier == activeTier,
                weeklyTokensRemaining = weeklyTokensRemaining,
                isBn = isBn,
                onSelect = { onSelectTier(summary.tier) },
            )
        }
    }
}

@Composable
private fun TierPlanCard(
    summary: TierFeatureSummary,
    isActive: Boolean,
    weeklyTokensRemaining: Int,
    isBn: Boolean,
    onSelect: () -> Unit,
) {
    val tier = summary.tier
    val cardContainer = when {
        isActive -> Lavender
        tier == ServiceTier.TIER_3 -> MutedRoseContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val primaryTextColor = if (isActive || tier == ServiceTier.TIER_3) DeepPlumDark else MaterialTheme.colorScheme.onSurface
    val bodyTextColor = if (isActive || tier == ServiceTier.TIER_3) InkPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardContainer,
            contentColor = primaryTextColor,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) DeepPlum else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(18.dp),
            )
            .testTag("tier_card_${tier.code.lowercase()}"),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBn) tier.bnName else tier.enName,
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryTextColor,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (isBn) tier.bnPriceLabel else tier.enPriceLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isActive || tier == ServiceTier.TIER_3) DeepPlum else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                if (isActive) {
                    Surface(
                        color = SafetyGreenContainer,
                        contentColor = SafetyGreen,
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = if (isBn) "সক্রিয় প্ল্যান" else "Active Plan",
                            style = MaterialTheme.typography.labelMedium,
                            color = SafetyGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                }
            }

            Text(
                text = if (isBn) summary.bnTagline else summary.enTagline,
                style = MaterialTheme.typography.bodySmall,
                color = bodyTextColor,
            )

            if (tier == ServiceTier.FREE && isActive) {
                Surface(
                    color = WarmIvory,
                    contentColor = DeepPlumDark,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = if (isBn) {
                            "এই সপ্তাহে বাকি AI টোকেন: $weeklyTokensRemaining / ${ServiceTierEngine.FREE_WEEKLY_AI_TOKENS}"
                        } else {
                            "Weekly AI Tokens Left: $weeklyTokensRemaining / ${ServiceTierEngine.FREE_WEEKLY_AI_TOKENS}"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = DeepPlumDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            val bullets = if (isBn) summary.bnBullets else summary.enBullets
            bullets.forEach { bullet ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = SafetyGreen,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(16.dp),
                    )
                    Text(
                        text = bullet,
                        style = MaterialTheme.typography.bodySmall,
                        color = bodyTextColor,
                    )
                }
            }

            if (!isActive) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepPlum,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("select_tier_${tier.code.lowercase()}"),
                ) {
                    Text(
                        if (isBn) "${tier.bnName} চালু করুন (${tier.bnPriceLabel})"
                        else "Choose ${tier.enName} (${tier.enPriceLabel})"
                    )
                }
            } else {
                OutlinedButton(
                    onClick = {},
                    enabled = false,
                    border = BorderStroke(1.dp, DeepPlum.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        disabledContentColor = DeepPlumDark,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                ) {
                    Text(if (isBn) "বর্তমানে এই প্ল্যানটি চালু আছে" else "Currently Active")
                }
            }
        }
    }
}

@Composable
fun TierPlansDialog(
    activeTier: ServiceTier,
    weeklyTokensRemaining: Int,
    language: AppLanguage,
    onSelectTier: (ServiceTier) -> Unit,
    onDismiss: () -> Unit,
) {
    val isBn = language == AppLanguage.BN
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) "নারীবন্ধু কেয়ার প্ল্যানসমূহ" else "NariBondhu Care Tiers",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (isBn) "আপনার প্রয়োজন অনুযায়ী সহজ মাসিক সেবা বেছে নিন" else "Transparent monthly care — privacy is always included",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_tier_dialog")) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                ) {
                    TierPlansComparisonSection(
                        activeTier = activeTier,
                        weeklyTokensRemaining = weeklyTokensRemaining,
                        language = language,
                        onSelectTier = { chosen ->
                            onSelectTier(chosen)
                            onDismiss()
                        },
                    )
                }
            }
        }
    }
}
