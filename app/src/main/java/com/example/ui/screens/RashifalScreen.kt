package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrology.calc.ZodiacSign
import com.example.astrology.prediction.DailyRashifal
import com.example.ui.MainUiState
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldLight

@Composable
fun RashifalScreen(
    state: MainUiState,
    onSignSelected: (ZodiacSign) -> Unit,
    modifier: Modifier = Modifier
) {
    val chart = state.chartData ?: return
    val userMoonSign = chart.moonSign
    val allRashifals = state.dailyRashifals
    val selectedSign = state.selectedRashifalSign
    val currentRashifal = allRashifals.firstOrNull { it.rashi == selectedSign } ?: return
    val userRashifal = allRashifals.firstOrNull { it.rashi == userMoonSign } ?: currentRashifal

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("rashifal_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Today's Native Moon Sign Horoscope Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("personal_horoscope_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(SacredGold.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userMoonSign.symbol,
                                    fontSize = 22.sp,
                                    color = SacredGold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Aapki Janam Rashi: ${userMoonSign.sanskritName} (${userMoonSign.englishName})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${userMoonSign.sanskritName} Rashi • ${userRashifal.date}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SacredGoldLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userRashifal.headline,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userRashifal.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Category Star Ratings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CategoryStarBadge("Career (काम)", userRashifal.careerScore)
                        CategoryStarBadge("Prem (Love)", userRashifal.loveScore)
                        CategoryStarBadge("Dhan (Wealth)", userRashifal.wealthScore)
                        CategoryStarBadge("Sehat (Health)", userRashifal.healthScore)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0x20FFFFFF))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Lucky Parameters Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Shubh Ank", fontSize = 10.sp, color = Color.Gray)
                            Text("${userRashifal.luckyNumber}", fontWeight = FontWeight.Bold, color = SacredGold, fontSize = 14.sp)
                        }
                        Column {
                            Text("Shubh Rang", fontSize = 10.sp, color = Color.Gray)
                            Text(userRashifal.luckyColor.split("/")[0].trim(), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        }
                        Column {
                            Text("Shubh Muhurat", fontSize = 10.sp, color = Color.Gray)
                            Text(userRashifal.auspiciousTime.split("-")[0].trim(), fontWeight = FontWeight.Bold, color = SacredGoldLight, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 12 Signs Horoscope Selector Title
        item {
            Text(
                text = "Sabhi 12 Rashiyon ka Dainik Rashifal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SacredGold
            )
        }

        // Horizontal Zodiac Signs Selector Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ZodiacSign.entries.forEach { sign ->
                    val isSelected = sign == selectedSign
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SacredGold else CosmicSurface,
                        modifier = Modifier
                            .testTag("rashi_chip_${sign.name.lowercase()}")
                            .padding(vertical = 4.dp),
                        onClick = { onSignSelected(sign) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sign.symbol,
                                fontSize = 16.sp,
                                color = if (isSelected) Color.Black else SacredGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = sign.englishName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                                Text(
                                    text = sign.sanskritName,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color(0xFF3E2D00) else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Detailed Forecast for Selected Sign
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("selected_rashifal_detail_card"),
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
                        Text(
                            text = "${currentRashifal.rashi.sanskritName} (${currentRashifal.rashi.englishName}) Rashifal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGold
                        )
                        Badge(
                            containerColor = SacredGold.copy(alpha = 0.2f),
                            contentColor = SacredGoldLight
                        ) {
                            Text(
                                text = "Swami: ${currentRashifal.rashi.ruler.sanskritName} Dev",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PredictionSection(
                        title = "💼 Career aur Vyapaar",
                        content = currentRashifal.careerPrediction,
                        rating = currentRashifal.careerScore
                    )

                    PredictionSection(
                        title = "❤️ Prem aur Rishtey",
                        content = currentRashifal.lovePrediction,
                        rating = currentRashifal.loveScore
                    )

                    PredictionSection(
                        title = "💰 Dhan aur Aarthik Sthiti",
                        content = currentRashifal.wealthPrediction,
                        rating = currentRashifal.wealthScore
                    )

                    PredictionSection(
                        title = "🌿 Swasthya aur Urja",
                        content = currentRashifal.healthPrediction,
                        rating = currentRashifal.healthScore
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CosmicSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Tip",
                                tint = SacredGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Shubh Paramarsh: ${currentRashifal.positiveTip}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryStarBadge(title: String, score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 11.sp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(2.dp))
        Row {
            (1..5).forEach { i ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (i <= score) SacredGold else Color.DarkGray,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun PredictionSection(title: String, content: String, rating: Int) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SacredGoldLight
            )
            Row {
                (1..5).forEach { i ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i <= rating) SacredGold else Color.DarkGray,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}
