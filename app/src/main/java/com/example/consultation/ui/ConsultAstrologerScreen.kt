package com.example.consultation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.consultation.model.Astrologer
import com.example.consultation.model.AstrologerDirectory
import com.example.consultation.model.ConsultationBooking
import com.example.consultation.model.ConsultationType
import com.example.ui.theme.*

@Composable
fun ConsultAstrologerScreen(
    activeBooking: ConsultationBooking?,
    onOpenBookingDialog: (Astrologer, ConsultationType) -> Unit,
    onJoinActiveSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedBioId by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Sabhi", "Vedic Jyotish", "Vivah & Prem", "Career & Dhan", "Nadi / KP", "Dosh Nivaran", "Vastu Shastra")

    val filteredAstrologers = remember(searchQuery, selectedCategory) {
        AstrologerDirectory.astrologers.filter { ast ->
            val matchesSearch = ast.name.contains(searchQuery, ignoreCase = true) ||
                    ast.specializations.any { it.contains(searchQuery, ignoreCase = true) } ||
                    ast.languages.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesCategory = when (selectedCategory) {
                "Sabhi" -> true
                "Vedic Jyotish" -> ast.specializations.any { it.contains("Vedic", ignoreCase = true) }
                "Vivah & Prem" -> ast.specializations.any { it.contains("Marriage", ignoreCase = true) || it.contains("Love", ignoreCase = true) }
                "Career & Dhan" -> ast.specializations.any { it.contains("Career", ignoreCase = true) || it.contains("Wealth", ignoreCase = true) }
                "Nadi / KP" -> ast.specializations.any { it.contains("KP", ignoreCase = true) || it.contains("Nadi", ignoreCase = true) }
                "Dosh Nivaran" -> ast.specializations.any { it.contains("Dosha", ignoreCase = true) || it.contains("Shanti", ignoreCase = true) }
                "Vastu Shastra" -> ast.specializations.any { it.contains("Vastu", ignoreCase = true) }
                else -> true
            }

            matchesSearch && matchesCategory
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("consult_astrologer_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp)
    ) {
        // Active Ongoing Session Banner (if booked)
        if (activeBooking != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_session_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SacredGold.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SacredGold)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFF00E676), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Chalu Consultation: ${activeBooking.astrologer.name} ji ke sath",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${activeBooking.consultationType.title} • ${activeBooking.durationMinutes} minute ka package",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SacredGoldLight
                                )
                            }
                        }

                        Button(
                            onClick = onJoinActiveSession,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SacredGold,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("rejoin_session_button")
                        ) {
                            Text("Resume Karein", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Header Title Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨ 100% Free Vedic Jyotish Acharyas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SacredGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(containerColor = Color(0x3000E676), contentColor = Color(0xFF69F0AE)) {
                            Text("100% NISHULK", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100% Nishulk Vedic Jyotish Seva! Bilkul free live chat aur video consultation uplabdh hai. Koi subscription ya payment shulk nahi hai.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Astrologer ka naam, career, vivah ya vishay search karein...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SacredGold) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_astrologers_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SacredGold,
                    unfocusedBorderColor = CosmicSurfaceVariant,
                    focusedContainerColor = CosmicSurface,
                    unfocusedContainerColor = CosmicSurface
                )
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SacredGold,
                            selectedLabelColor = Color.Black,
                            containerColor = CosmicSurfaceVariant,
                            labelColor = Color.White
                        )
                    )
                }
            }
        }

        // Astrologers Count
        item {
            Text(
                text = "${filteredAstrologers.size} Verified Jyotishi Uplabdh Hain",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }

        // Astrologer Cards List
        items(filteredAstrologers) { astrologer ->
            val isExpanded = expandedBioId == astrologer.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("astrologer_card_${astrologer.id}"),
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
                        verticalAlignment = Alignment.Top
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(astrologer.avatarBgColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = astrologer.avatarInitial,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Info
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = astrologer.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF69F0AE),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = astrologer.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = SacredGoldLight
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Anubhav: ${astrologer.experienceYears} Saal • Bhasha: ${astrologer.languages.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )

                            // Rating and Orders
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = SacredGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${astrologer.rating}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${astrologer.totalConsultations}+ parmarsh)",
                                    color = Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Specialization Tags
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        astrologer.specializations.forEach { spec ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CosmicSurfaceVariant
                            ) {
                                Text(
                                    text = spec,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Bio (Collapsible)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isExpanded) astrologer.bio else astrologer.bio.take(80) + "...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        lineHeight = 16.sp,
                        modifier = Modifier.clickable {
                            expandedBioId = if (isExpanded) null else astrologer.id
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0x15FFFFFF))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Availability & Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Online Status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (astrologer.isOnline) Color(0xFF00E676) else Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = astrologer.nextAvailableTime,
                                fontSize = 11.sp,
                                color = if (astrologer.isOnline) Color(0xFF69F0AE) else Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Chat and Video Call Action Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Chat Button
                            OutlinedButton(
                                onClick = { onOpenBookingDialog(astrologer, ConsultationType.CHAT) },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SacredGold),
                                modifier = Modifier.testTag("chat_astrologer_${astrologer.id}")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = SacredGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat (Free)", fontSize = 11.sp, color = SacredGold, fontWeight = FontWeight.Bold)
                            }

                            // Video Call Button
                            Button(
                                onClick = { onOpenBookingDialog(astrologer, ConsultationType.VIDEO) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SacredGold, contentColor = Color.Black),
                                modifier = Modifier.testTag("call_astrologer_${astrologer.id}")
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Video (Free)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
