package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrology.dosha.DoshaSeverity
import com.example.ui.MainUiState
import com.example.ui.theme.*

@Composable
fun DoshaScreen(
    state: MainUiState,
    modifier: Modifier = Modifier
) {
    val doshas = state.doshas ?: return
    val predictions = state.personalizedPredictions

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dosha_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Overall Vedic Harmony Score Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                color = if (doshas.overallHarmonyScore >= 75) SacredGold.copy(alpha = 0.2f) else MysticCrimson.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${doshas.overallHarmonyScore}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (doshas.overallHarmonyScore >= 75) SacredGold else MysticCrimson
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Vedic Shubh-Ashubh Score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (doshas.overallHarmonyScore >= 80) "Bahut Shubh aur Kripamay Kundli"
                            else if (doshas.overallHarmonyScore >= 65) "Santulit Grah Sthiti"
                            else "Kuchh Upaay karna faydemand rahega",
                            style = MaterialTheme.typography.bodySmall,
                            color = SacredGoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Mangal, Kaal Sarp, Shani Sade Sati aur Pitra Dosh ka nishkarsh",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // 1. Manglik (Kuja) Dosha Card
        item {
            val mangal = doshas.manglik
            DoshaDetailCard(
                title = "Mangal (Kuja) Dosh",
                hindiTitle = "मंगल दोष एवं विवाह विचार",
                severity = mangal.severity,
                badgeLabel = if (!mangal.isManglik) "Dosh Mukt (दोष रहित)"
                else if (mangal.isCancelled) "Dosh Bhang / Anshik (आंशिक)"
                else mangal.severity.title,
                summary = mangal.analysis,
                secondaryTitle = "Vaivahik Jeevan par Asar:",
                secondaryText = mangal.impactOnMarriage,
                cancellations = mangal.cancellations,
                remedies = mangal.remedies
            )
        }

        // 2. Kaal Sarp Dosha Card
        item {
            val ks = doshas.kaalSarp
            DoshaDetailCard(
                title = "Kaal Sarp Dosh",
                hindiTitle = ks.sanskritName,
                severity = ks.severity,
                badgeLabel = if (!ks.hasKaalSarp) "Dosh Mukt"
                else if (ks.isPartial) "Anshik (${ks.typeName})"
                else "Sakriya (${ks.typeName})",
                summary = ks.description,
                secondaryTitle = "Jeevan par Asar:",
                secondaryText = ks.effects,
                cancellations = emptyList(),
                remedies = ks.remedies
            )
        }

        // 3. Shani Sade Sati & Dhaiya Card
        item {
            val ss = doshas.sadeSati
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Shani Sade Sati aur Dhaiya",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SacredGold
                            )
                            Text(
                                text = "शनि की साढ़ेसाती एवं ढैय्या",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Badge(
                            containerColor = if (ss.isUnderSadeSati || ss.isDhaiya) CelestialSaffron.copy(alpha = 0.2f) else Color(0xFF00E676).copy(alpha = 0.2f),
                            contentColor = if (ss.isUnderSadeSati || ss.isDhaiya) CelestialSaffron else Color(0xFF00E676)
                        ) {
                            Text(
                                text = if (ss.isUnderSadeSati) ss.phase
                                else if (ss.isDhaiya) "Dhaiya Chalu Hai"
                                else "Nahi Chal Rahi",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = ss.analysis,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Shani Dev ke Shubh Upaay:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ss.remedies.take(3).forEach { remedy ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("• ", color = SacredGold, fontWeight = FontWeight.Bold)
                            Text(remedy, style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }
                }
            }
        }

        // 4. Pitra Dosha Card
        item {
            val pitra = doshas.pitra
            DoshaDetailCard(
                title = "Pitra Dosh",
                hindiTitle = "पितृ दोष एवं पूर्वज ऋण",
                severity = pitra.severity,
                badgeLabel = if (!pitra.hasPitraDosha) "Pitron ka Ashirwad (दोष रहित)" else pitra.severity.title,
                summary = pitra.analysis,
                secondaryTitle = "Kundli Sanket:",
                secondaryText = if (pitra.indicators.isNotEmpty()) pitra.indicators.joinToString("; ") else "Kundli mein koi ashubh pitra yog nahi hai.",
                cancellations = emptyList(),
                remedies = pitra.remedies
            )
        }

        // 5. Recommended Lucky Gemstone & Mantra Card
        if (predictions != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Suraksha Kavach",
                                tint = SacredGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aapka Vedic Suraksha Kavach (Ratna & Mantra)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SacredGold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Shubh Bhagya Ratna: ${predictions.recommendedGemstone}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = predictions.gemstoneBenefits,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aapka Beej Mantra: ${predictions.favorableMantra}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGoldLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoshaDetailCard(
    title: String,
    hindiTitle: String,
    severity: DoshaSeverity,
    badgeLabel: String,
    summary: String,
    secondaryTitle: String,
    secondaryText: String,
    cancellations: List<String>,
    remedies: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Text(
                        text = hindiTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Badge(
                    containerColor = severity.color.copy(alpha = 0.2f),
                    contentColor = severity.color
                ) {
                    Text(
                        text = badgeLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )

            if (cancellations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x2000E676),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Vedic Dosh Bhang Niyam (Exceptions):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF69F0AE)
                        )
                        cancellations.forEach { c ->
                            Text(
                                text = "• $c",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = secondaryTitle,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = SacredGold
            )
            Text(
                text = secondaryText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Prabhavshali Vedic Upaay (${remedies.size})",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SacredGold
                )
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        text = if (expanded) "Chhupayein" else "Sabhi Dekhein",
                        color = SacredGoldLight,
                        fontSize = 12.sp
                    )
                }
            }

            val displayRemedies = if (expanded) remedies else remedies.take(2)
            displayRemedies.forEach { remedy ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("• ", color = SacredGold, fontWeight = FontWeight.Bold)
                    Text(remedy, style = MaterialTheme.typography.bodySmall, color = Color.White)
                }
            }
        }
    }
}
