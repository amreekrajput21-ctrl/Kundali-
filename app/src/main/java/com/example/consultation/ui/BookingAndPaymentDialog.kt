package com.example.consultation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.window.Dialog
import com.example.consultation.model.*
import com.example.data.model.KundliProfile
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingAndPaymentDialog(
    astrologer: Astrologer,
    initialType: ConsultationType = ConsultationType.CHAT,
    activeProfile: KundliProfile,
    allProfiles: List<KundliProfile>,
    onDismiss: () -> Unit,
    onBookingConfirmed: (ConsultationBooking) -> Unit
) {
    var consultationType by remember { mutableStateOf(initialType) }
    var durationMinutes by remember { mutableStateOf(15) }
    var selectedSlot by remember { mutableStateOf(astrologer.availableSlots.first()) }
    var selectedProfile by remember { mutableStateOf(activeProfile) }

    // Booking state
    var isProcessing by remember { mutableStateOf(false) }
    var isBookingSuccess by remember { mutableStateOf(false) }
    var confirmedBooking by remember { mutableStateOf<ConsultationBooking?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("booking_payment_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicSurface)
        ) {
            if (isBookingSuccess && confirmedBooking != null) {
                // Success Confirmation Screen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFF00E676).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nishulk Consultation Book Hua!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Text(
                        text = "100% Free • Koi Subscription Ya Shulk Nahi",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF69F0AE),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CosmicSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Consultation ka Pura Vivaran:",
                                fontWeight = FontWeight.Bold,
                                color = SacredGoldLight,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Jyotishi: ${confirmedBooking!!.astrologer.name}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "• Madhyam: ${confirmedBooking!!.consultationType.title} (${confirmedBooking!!.durationMinutes} Minute)",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "• Nirdharit Samay: ${confirmedBooking!!.slotTime}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "• Kundli Profile: ${confirmedBooking!!.targetNativeName}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "• Shulk: ₹0 (100% Muft Nishulk Seva)",
                                color = Color(0xFF69F0AE),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onBookingConfirmed(confirmedBooking!!)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("start_consultation_now_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SacredGold,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = if (confirmedBooking!!.consultationType == ConsultationType.CHAT) Icons.AutoMirrored.Filled.Chat else Icons.Default.Videocam,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Abhi Live Session Mein Judein",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                // Booking Steps Form
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Title Bar
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Nishulk Paramarsh (100% Free)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SacredGold
                                )
                                Text(
                                    text = "Acharya ${astrologer.name} ji ke sath",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Band Karein", tint = Color.LightGray)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 100% Free Promise Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x2500E676),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF69F0AE),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "100% Free • No Subscription Needed",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF69F0AE),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Shastrokt Vedic paramarsh sabhi ke liye nishulk uplabdh hai.",
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 1. Consultation Type Selection (Chat or Video)
                    item {
                        Text(
                            text = "1. Paramarsh ka Madhyam (Mode)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGoldLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Chat
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (consultationType == ConsultationType.CHAT) SacredGold.copy(alpha = 0.2f) else CosmicSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (consultationType == ConsultationType.CHAT) 1.5.dp else 1.dp,
                                    color = if (consultationType == ConsultationType.CHAT) SacredGold else Color.DarkGray
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { consultationType = ConsultationType.CHAT }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = null,
                                        tint = if (consultationType == ConsultationType.CHAT) SacredGold else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Live Chat", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                    Text("100% Free", fontSize = 10.sp, color = Color(0xFF69F0AE), fontWeight = FontWeight.Bold)
                                }
                            }

                            // Video Call
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (consultationType == ConsultationType.VIDEO) SacredGold.copy(alpha = 0.2f) else CosmicSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (consultationType == ConsultationType.VIDEO) 1.5.dp else 1.dp,
                                    color = if (consultationType == ConsultationType.VIDEO) SacredGold else Color.DarkGray
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { consultationType = ConsultationType.VIDEO }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = if (consultationType == ConsultationType.VIDEO) SacredGold else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("HD Video Call", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                    Text("100% Free", fontSize = 10.sp, color = Color(0xFF69F0AE), fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 2. Duration Selector
                    item {
                        Text(
                            text = "2. Samay Awadhi Chunein (Duration)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGoldLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(15, 30, 45).forEach { minutes ->
                                val isSelected = durationMinutes == minutes
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) SacredGold else CosmicSurfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { durationMinutes = minutes }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "$minutes Min",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.Black else Color.White
                                        )
                                        Text(
                                            text = "Free",
                                            fontSize = 9.sp,
                                            color = if (isSelected) Color.Black else Color(0xFF69F0AE),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 3. Select Kundli Profile
                    item {
                        Text(
                            text = "3. Kiski Kundli ke Vishay Mein Charcha Karni Hai?",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGoldLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            allProfiles.forEach { profile ->
                                val isSelected = selectedProfile.id == profile.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedProfile = profile }
                                        .background(
                                            if (isSelected) SacredGold.copy(alpha = 0.15f) else CosmicSurfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedProfile = profile },
                                        colors = RadioButtonDefaults.colors(selectedColor = SacredGold)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = profile.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${profile.birthPlace} • ${profile.birthDate}",
                                            fontSize = 10.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 4. Slot Selection
                    item {
                        Text(
                            text = "4. Nirdharit Slot Chunein",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SacredGoldLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            astrologer.availableSlots.take(3).forEach { slot ->
                                val isSelected = selectedSlot == slot
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedSlot = slot },
                                    label = { Text(slot, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SacredGold,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // 5. Zero-Cost Confirmation Card
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CosmicSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ThumbUp,
                                            contentDescription = null,
                                            tint = Color(0xFF69F0AE),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "100% Nishulk Vedic Seva",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = SacredGold
                                        )
                                    }
                                    Badge(containerColor = Color(0x3000E676), contentColor = Color(0xFF69F0AE)) {
                                        Text("BILKUL FREE", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Paramarsh Awadhi ($durationMinutes min)", fontSize = 12.sp, color = Color.LightGray)
                                    Text("Free", fontSize = 12.sp, color = Color(0xFF69F0AE), fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subscription Fees", fontSize = 12.sp, color = Color.LightGray)
                                    Text("₹0 (Nahi)", fontSize = 12.sp, color = Color(0xFF69F0AE), fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Kul Rashi (Total)", fontWeight = FontWeight.Bold, color = SacredGold, fontSize = 14.sp)
                                    Text("₹0 (100% Free)", fontWeight = FontWeight.Bold, color = Color(0xFF69F0AE), fontSize = 15.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Free Connect Button
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF69F0AE), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Bina Kisi Shulk Ke Seedha Jyotishi Se Judin",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                isProcessing = true
                                coroutineScope.launch {
                                    delay(400) // Fast instant lag-free connect
                                    val booking = ConsultationBooking(
                                        bookingId = "KNDL-FREE-${System.currentTimeMillis() % 100000}",
                                        astrologer = astrologer,
                                        consultationType = consultationType,
                                        durationMinutes = durationMinutes,
                                        totalAmount = 0.0,
                                        slotTime = selectedSlot,
                                        targetNativeName = selectedProfile.name,
                                        paymentMethod = PaymentMethod.WALLET,
                                        paymentTransactionId = "FREE_VEDIC_SEVA",
                                        status = BookingStatus.LIVE_NOW
                                    )
                                    confirmedBooking = booking
                                    isProcessing = false
                                    isBookingSuccess = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("pay_consultation_button"),
                            enabled = !isProcessing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SacredGold,
                                contentColor = Color.Black
                            )
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Session Connect Ho Raha Hai...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = if (consultationType == ConsultationType.CHAT) Icons.AutoMirrored.Filled.Chat else Icons.Default.Videocam,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Nishulk Session Shuru Karein (Free)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
