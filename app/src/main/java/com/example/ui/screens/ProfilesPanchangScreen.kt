package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KundliProfile
import com.example.ui.MainUiState
import com.example.ui.theme.CelestialSaffron
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldLight

@Composable
fun ProfilesPanchangScreen(
    state: MainUiState,
    onAddProfileClicked: () -> Unit,
    onEditProfileClicked: (KundliProfile) -> Unit,
    onDeleteProfileClicked: (KundliProfile) -> Unit,
    onSelectProfile: (KundliProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val panchang = state.chartData?.panchang

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("profiles_panchang_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Top Header Row with Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Surakshit Kundli Profiles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Text(
                        text = "Parivaar aur jano ki Kundli manage karein",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                FilledTonalButton(
                    onClick = onAddProfileClicked,
                    modifier = Modifier.testTag("add_new_profile_button"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = SacredGold,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nayi Kundli", fontWeight = FontWeight.Bold)
                }
            }
        }

        // List of Profiles
        items(state.profiles) { profile ->
            val isActive = profile.id == state.activeProfile.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectProfile(profile) }
                    .testTag("profile_item_${profile.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) CosmicSurfaceVariant else CosmicSurface
                ),
                border = if (isActive) androidx.compose.foundation.BorderStroke(1.5.dp, SacredGold) else null
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = if (isActive) SacredGold else Color.DarkGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.take(1).uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.Black else Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (isActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Badge(
                                    containerColor = SacredGold.copy(alpha = 0.2f),
                                    contentColor = SacredGold
                                ) {
                                    Text("Active", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = "${profile.birthDate} • ${profile.birthTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                        Text(
                            text = profile.birthPlace,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    // Action buttons
                    IconButton(
                        onClick = { onEditProfileClicked(profile) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (state.profiles.size > 1) {
                        IconButton(
                            onClick = { onDeleteProfileClicked(profile) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF8A80),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Today's Vedic Panchang Section
        if (panchang != null) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vedic Panchang (दैनिक पंचांग)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SacredGold
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("panchang_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        PanchangItemRow("Tithi (तिथि)", panchang.tithi)
                        HorizontalDivider(color = Color(0x15FFFFFF), modifier = Modifier.padding(vertical = 8.dp))
                        PanchangItemRow("Vaar (वार)", panchang.vaar)
                        HorizontalDivider(color = Color(0x15FFFFFF), modifier = Modifier.padding(vertical = 8.dp))
                        PanchangItemRow("Nakshatra (नक्षत्र)", panchang.nakshatra)
                        HorizontalDivider(color = Color(0x15FFFFFF), modifier = Modifier.padding(vertical = 8.dp))
                        PanchangItemRow("Yoga (योग)", panchang.yoga)
                        HorizontalDivider(color = Color(0x15FFFFFF), modifier = Modifier.padding(vertical = 8.dp))
                        PanchangItemRow("Karana (करण)", panchang.karana)
                        HorizontalDivider(color = Color(0x15FFFFFF), modifier = Modifier.padding(vertical = 8.dp))
                        PanchangItemRow("Abhijit Muhurat", panchang.shubhMuhurat, isAuspicious = true)
                        HorizontalDivider(color = Color(0x15FFFFFF), modifier = Modifier.padding(vertical = 8.dp))
                        PanchangItemRow("Rahu Kaal (अशुभ समय)", panchang.rahuKaal, isWarning = true)
                    }
                }
            }
        }

        // Vedic Astrology Wisdom Note
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = SacredGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Shastrokt Vedic Ganana ke Baare Mein", fontWeight = FontWeight.Bold, color = SacredGoldLight, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Yeh app Lahiri Chitrapaksha Ayanamsha, Surya Siddhanta aur Brihat Parashara Hora Shastra (BPHS) ke pramaanik niyam anusar shuddh grah sthiti, nakshatra charan, Vimshottari dasha aur dosh nivaran ka hisaab lagata hai.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PanchangItemRow(label: String, value: String, isAuspicious: Boolean = false, isWarning: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = when {
                isAuspicious -> Color(0xFF69F0AE)
                isWarning -> CelestialSaffron
                else -> Color.White
            }
        )
    }
}
