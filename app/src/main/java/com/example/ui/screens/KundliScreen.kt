package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainUiState
import com.example.ui.components.NorthIndianChart
import com.example.ui.components.SouthIndianChart
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldLight

@Composable
fun KundliScreen(
    state: MainUiState,
    onToggleChartStyle: (Boolean) -> Unit,
    onToggleD9: (Boolean) -> Unit,
    onHouseSelected: (Int) -> Unit,
    onDownloadPdf: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val chart = state.chartData ?: return

    val currentHouses = if (state.isD9Navamsha) chart.navamshaHouses else chart.houses
    val selectedHouseData = currentHouses.firstOrNull { it.houseNumber == (state.selectedHouse ?: 1) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("kundli_screen_scroll"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Top Profile Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(SacredGold.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chart.lagnaSign.symbol,
                            fontSize = 24.sp,
                            color = SacredGold
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${chart.profile.name} ki Kundli",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${chart.profile.birthDate} • ${chart.profile.birthTime} • ${chart.profile.birthPlace}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Text(
                            text = "Lagna: ${chart.lagnaSign.sanskritName} • Rashi: ${chart.moonSign.sanskritName} (${chart.ascendant.nakshatra.name})",
                            style = MaterialTheme.typography.bodySmall,
                            color = SacredGoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Prominent Download Kundli PDF Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("download_pdf_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SacredGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(SacredGold.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📄", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Kundli PDF Download (100% Free)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SacredGold
                            )
                            Text(
                                text = "Complete Janam Patrika phone storage mein bilkul free download karein",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onDownloadPdf,
                        enabled = !state.isGeneratingPdf,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SacredGold,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.testTag("download_kundli_pdf_button")
                    ) {
                        if (state.isGeneratingPdf) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Saving...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Free PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Chart Controls (North/South toggle & D1/D9 toggle)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // North vs South Indian Switcher
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = state.isNorthIndianChart,
                        onClick = { onToggleChartStyle(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("Uttar Bhartiya (Diamond)")
                    }
                    SegmentedButton(
                        selected = !state.isNorthIndianChart,
                        onClick = { onToggleChartStyle(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("Dakshin (Chaukor)")
                    }
                }

                // D1 vs D9 Navamsha Switcher
                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = !state.isD9Navamsha,
                        onClick = { onToggleD9(false) },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("D1 Lagna")
                    }
                    SegmentedButton(
                        selected = state.isD9Navamsha,
                        onClick = { onToggleD9(true) },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("D9 Navamsha")
                    }
                }
            }
        }

        // Interactive Kundli Chart Canvas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (state.isD9Navamsha) "D9 Navamsha Chakra (Jeevansathi aur Bhagya)" else "D1 Janma Lagna Chakra (Mukhya Kundli)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Text(
                        text = "Kisi bhi ghar (house) par tap karke grahon ka vishleshan dekhein",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.isNorthIndianChart) {
                        NorthIndianChart(
                            houses = currentHouses,
                            selectedHouse = state.selectedHouse,
                            onHouseSelected = onHouseSelected
                        )
                    } else {
                        SouthIndianChart(
                            houses = currentHouses,
                            planets = chart.planets,
                            lagnaSign = if (state.isD9Navamsha) chart.ascendant.navamshaSign else chart.lagnaSign,
                            selectedSign = state.selectedSouthSign,
                            onSignSelected = { sign ->
                                val matchingH = currentHouses.firstOrNull { it.sign == sign }
                                if (matchingH != null) onHouseSelected(matchingH.houseNumber)
                            }
                        )
                    }
                }
            }
        }

        // Selected House Inspector Card
        if (selectedHouseData != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("house_inspector_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Ghar ${selectedHouseData.houseNumber}: ${selectedHouseData.bhavaName}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SacredGold
                            )
                            Badge(
                                containerColor = SacredGold.copy(alpha = 0.2f),
                                contentColor = SacredGoldLight
                            ) {
                                Text(
                                    text = "${selectedHouseData.sign.symbol} ${selectedHouseData.sign.sanskritName} (Swami: ${selectedHouseData.sign.ruler.sanskritName})",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Karkatwa: ${selectedHouseData.significance}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Text(
                            text = "Sharirik Ang: ${selectedHouseData.bodyPart}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Is Ghar mein Virajman Grah (${selectedHouseData.planets.size}):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )

                        if (selectedHouseData.planets.isEmpty()) {
                            Text(
                                text = "Koi grah sthit nahi hai. Is ghar par iske swami ${selectedHouseData.sign.ruler.sanskritName} Dev ka mukhya prabhav hai.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(top = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedHouseData.planets.forEach { p ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = CosmicSurface,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(p.planet.color, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = "${p.planet.sanskritName} ${if (p.isRetrograde) "(Vakri)" else ""}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "${p.degreeFormatted} • ${p.dignity.title}",
                                                    fontSize = 10.sp,
                                                    color = p.dignity.color
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

        // Vimshottari Dasha Timeline Card
        item {
            val dasha = chart.dashaResult
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
                        Text(
                            text = "Vimshottari Dasha Pranali",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGold
                        )
                        Text(
                            text = "120-Varshiya Dasha Chakra",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Dasha highlight
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Chalti Mahadasha: ${dasha.currentMahadasha.sanskritName} Dev",
                                        fontWeight = FontWeight.Bold,
                                        color = SacredGoldLight,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Antardasha: ${dasha.currentAntardasha.sanskritName} (${dasha.antardashaEndDate} tak)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.LightGray
                                    )
                                }
                                Text(
                                    text = "${(dasha.progressPercent * 100).toInt()}%",
                                    fontWeight = FontWeight.Bold,
                                    color = SacredGold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { dasha.progressPercent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = SacredGold,
                                trackColor = Color.DarkGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Jeevan ki Mahadasha Sequence:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dasha.allMahadashas.forEach { p ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (p.isCurrent) SacredGold.copy(alpha = 0.25f) else CosmicSurfaceVariant,
                                border = if (p.isCurrent) androidx.compose.foundation.BorderStroke(1.dp, SacredGold) else null
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(
                                        text = p.planet.sanskritName,
                                        fontWeight = if (p.isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp,
                                        color = if (p.isCurrent) SacredGold else Color.White
                                    )
                                    Text(
                                        text = "${p.durationYears.toInt()} saal",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                    Text(
                                        text = p.endDateStr.take(4),
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Full Planetary Positions Table Card
        item {
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
                    Text(
                        text = "Graha Sphuta (Grahon ki Sthiti)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Text(
                        text = "Nirayana Sidereal Sthiti • Lahiri Ayanamsha (${"%.2f".format(chart.ayanamshaDegrees)}°)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CosmicSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Graha", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SacredGold, modifier = Modifier.weight(1.3f))
                        Text("Rashi", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SacredGold, modifier = Modifier.weight(1.3f))
                        Text("Degree", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SacredGold, modifier = Modifier.weight(1.3f))
                        Text("Ghar", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SacredGold, modifier = Modifier.weight(0.8f))
                        Text("Awastha", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SacredGold, modifier = Modifier.weight(1.2f))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    chart.planets.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1.3f), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(p.planet.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${p.planet.sanskritName} ${if (p.isRetrograde) "(V)" else ""}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "${p.sign.symbol} ${p.sign.sanskritName}",
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                modifier = Modifier.weight(1.3f)
                            )
                            Text(
                                text = p.degreeFormatted,
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.weight(1.3f)
                            )
                            Text(
                                text = "H${p.house}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SacredGoldLight,
                                modifier = Modifier.weight(0.8f)
                            )
                            Text(
                                text = p.dignity.title.split("(")[0].trim(),
                                fontSize = 10.sp,
                                color = p.dignity.color,
                                modifier = Modifier.weight(1.2f)
                            )
                        }
                        HorizontalDivider(color = Color(0x15FFFFFF))
                    }
                }
            }
        }
    }
}
