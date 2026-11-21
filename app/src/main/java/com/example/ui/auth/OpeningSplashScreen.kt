package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AppLanguage
import com.example.ui.theme.DeepPlum
import com.example.ui.theme.DeepPlumDark
import com.example.ui.theme.Lavender
import com.example.ui.theme.MutedRose
import com.example.ui.theme.WarmIvory
import kotlinx.coroutines.delay

@Composable
fun NariBondhuOpeningSplash(
    language: AppLanguage,
    onContinue: () -> Unit,
) {
    var emblemVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var footerVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        emblemVisible = true
        delay(220)
        titleVisible = true
        delay(260)
        footerVisible = true
        delay(2100)
        onContinue()
    }

    val emblemScale by animateFloatAsState(
        targetValue = if (emblemVisible) 1f else 0.72f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "emblemScale",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "haloPulse")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "haloScale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepPlumDark,
                        DeepPlum,
                        Color(0xFF6F486C),
                        Color(0xFF472C46),
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onContinue,
            ),
    ) {
        // Soft luminous botanical backdrop rings drawn via Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.5f, size.height * 0.36f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MutedRose.copy(alpha = 0.28f),
                        Lavender.copy(alpha = 0.10f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = size.minDimension * 0.52f * haloScale,
                ),
                radius = size.minDimension * 0.52f * haloScale,
                center = center,
            )
            drawCircle(
                color = WarmIvory.copy(alpha = 0.08f),
                radius = size.minDimension * 0.28f * haloScale,
                center = center,
            )
            drawCircle(
                color = MutedRose.copy(alpha = 0.14f),
                radius = size.minDimension * 0.21f,
                center = center,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top subtle bilingual badge
            Surface(
                color = WarmIvory.copy(alpha = 0.14f),
                contentColor = WarmIvory,
                shape = RoundedCornerShape(50),
                modifier = Modifier.border(1.dp, WarmIvory.copy(alpha = 0.28f), RoundedCornerShape(50)),
            ) {
                Text(
                    text = "নারীবন্ধু • NariBondhu",
                    style = MaterialTheme.typography.labelLarge,
                    color = WarmIvory,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            // Center emblem + bilingual welcome title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(144.dp)
                        .scale(emblemScale)
                        .clip(CircleShape)
                        .background(WarmIvory.copy(alpha = 0.14f))
                        .border(2.dp, MutedRose.copy(alpha = 0.7f), CircleShape)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_naribondhu_fg),
                        contentDescription = "NariBondhu emblem",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                }

                AnimatedVisibility(
                    visible = titleVisible,
                    enter = fadeIn(tween(550)) + slideInVertically(tween(550)) { it / 4 },
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "নারীবন্ধু" else "NariBondhu",
                            style = MaterialTheme.typography.displaySmall,
                            color = WarmIvory,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = if (language == AppLanguage.BN) "নারীবন্ধু — আপনার ব্যক্তিগত স্বাস্থ্য সঙ্গী" else "নারীবন্ধু • Women's Health Companion",
                            style = MaterialTheme.typography.titleMedium,
                            color = MutedRose,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = if (language == AppLanguage.BN) {
                                "নিজের শরীরকে জানুন। প্রয়োজন বুঝুন। সঠিক যত্নে পৌঁছান।"
                            } else {
                                "Understand Your Body. Recognise Your Needs. Reach the Right Care."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Lavender,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                }
            }

            // Bottom high-contrast privacy pill + immediate Enter button
            AnimatedVisibility(
                visible = footerVisible,
                enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 3 },
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Surface(
                        color = Lavender,
                        contentColor = DeepPlumDark,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = DeepPlumDark,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.BN) {
                                    "গোপনীয় • সুরক্ষিত • সম্মতি ছাড়া কোনো শেয়ার নয়"
                                } else {
                                    "Private • Encrypted • Strictly Consent-First"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = DeepPlumDark,
                            )
                        }
                    }

                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarmIvory,
                            contentColor = DeepPlumDark,
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("opening_skip_button"),
                    ) {
                        Text(
                            text = if (language == AppLanguage.BN) "শুরু করুন" else "Enter NariBondhu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}
