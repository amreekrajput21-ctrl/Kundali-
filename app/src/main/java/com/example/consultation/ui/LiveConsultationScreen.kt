package com.example.consultation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrology.calc.KundliChartData
import com.example.consultation.model.Astrologer
import com.example.consultation.model.ConsultationBooking
import com.example.consultation.model.ConsultationType
import com.example.ui.ChatMessage
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveConsultationScreen(
    booking: ConsultationBooking,
    chartData: KundliChartData?,
    onEndConsultation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsRemaining by remember { mutableIntStateOf(booking.durationMinutes * 60) }
    var isMicMuted by remember { mutableStateOf(false) }
    var isVideoCameraOff by remember { mutableStateOf(false) }
    var showKundliSheet by remember { mutableStateOf(false) }

    // Chat messages specific to this astrologer session
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage(
                isUser = false,
                text = "Namaste ${booking.targetNativeName} ji. Main Acharya ${booking.astrologer.name} bol raha hoon. Main aapki ${chartData?.lagnaSign?.sanskritName ?: "Janam"} Lagna Kundli aur sabhi grahon ki sthiti dekh raha hoon. Kahiye, aaj aap kis vishay (career, vivah, dhan, swasthya ya dasha shanti) par margdarshan chahte hain?"
            )
        )
    }
    var chatInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Countdown Timer
    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
    }

    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timerText = "%02d:%02d".format(minutes, seconds)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_consultation_screen"),
        containerColor = CosmicNavy,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(booking.astrologer.avatarBgColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = booking.astrologer.avatarInitial,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = booking.astrologer.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF69F0AE),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "${booking.consultationType.title} • Live Session",
                                style = MaterialTheme.typography.bodySmall,
                                color = SacredGoldLight,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onEndConsultation) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Timer Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (secondsRemaining < 120) MysticCrimson.copy(alpha = 0.2f) else SacredGold.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (secondsRemaining < 120) MysticCrimson else SacredGold),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (secondsRemaining < 120) MysticCrimson else SacredGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = timerText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (secondsRemaining < 120) MysticCrimson else SacredGold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CosmicSurface)
            )
        }
    ) { innerPadding ->
        if (booking.consultationType == ConsultationType.VIDEO) {
            // Video Call Interface
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Astrologer Video Stream Background Simulation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E1435), Color(0xFF0F0B1E), Color(0xFF0A0714))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .border(3.dp, SacredGold, CircleShape)
                                .background(booking.astrologer.avatarBgColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = booking.astrologer.avatarInitial,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = booking.astrologer.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Acharya ji ka Video Feed (HD Active)",
                            style = MaterialTheme.typography.bodySmall,
                            color = SacredGoldLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Pulsing Audio Wave indicator
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { i ->
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height((12 + (i % 3) * 8).dp)
                                        .background(SacredGold, CircleShape)
                                )
                            }
                        }
                    }
                }

                // Native Self Camera PiP in top-right
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(width = 110.dp, height = 150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SacredGold)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isVideoCameraOff) {
                            Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.Gray)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = SacredGold, modifier = Modifier.size(36.dp))
                                Text(booking.targetNativeName.split(" ").first(), fontSize = 10.sp, color = Color.White)
                                Text("Aap (HD)", fontSize = 9.sp, color = SacredGoldLight)
                            }
                        }
                    }
                }

                // Bottom Call Control Action Bar
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = CosmicSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mic Mute Toggle
                        IconButton(
                            onClick = { isMicMuted = !isMicMuted },
                            modifier = Modifier
                                .size(50.dp)
                                .background(if (isMicMuted) MysticCrimson else CosmicSurfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mic",
                                tint = Color.White
                            )
                        }

                        // Video Camera Toggle
                        IconButton(
                            onClick = { isVideoCameraOff = !isVideoCameraOff },
                            modifier = Modifier
                                .size(50.dp)
                                .background(if (isVideoCameraOff) MysticCrimson else CosmicSurfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isVideoCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = "Camera",
                                tint = Color.White
                            )
                        }

                        // Kundli Quick View
                        IconButton(
                            onClick = { showKundliSheet = true },
                            modifier = Modifier
                                .size(50.dp)
                                .background(CosmicSurfaceVariant, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Article, contentDescription = "Kundli", tint = SacredGold)
                        }

                        // End Call Button
                        IconButton(
                            onClick = onEndConsultation,
                            modifier = Modifier
                                .size(56.dp)
                                .background(MysticCrimson, CircleShape)
                                .testTag("end_consultation_button")
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White)
                        }
                    }
                }
            }
        } else {
            // Live Chat Consultation Interface
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Top Kundli Context Reminder
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CosmicSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Jyotishi aapki Kundli dekh rahe hain: ${booking.targetNativeName}",
                            fontSize = 11.sp,
                            color = SacredGoldLight
                        )
                        TextButton(
                            onClick = { showKundliSheet = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Kundli Dekhein", fontSize = 11.sp, color = SacredGold)
                        }
                    }
                }

                // Chat Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(chatMessages) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                    bottomEnd = if (msg.isUser) 4.dp else 16.dp
                                ),
                                color = if (msg.isUser) SacredGold else CosmicSurface,
                                modifier = Modifier.widthIn(max = 290.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    if (!msg.isUser) {
                                        Text(
                                            text = booking.astrologer.name,
                                            fontWeight = FontWeight.Bold,
                                            color = SacredGoldLight,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                    }
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (msg.isUser) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Prompt Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Career mein safalta kab?", "Vaivahik jeevan kaisa rahega?", "Shubh bhagya ratna kaunsa?").forEach { chipText ->
                        SuggestionChip(
                            onClick = {
                                chatMessages.add(ChatMessage(isUser = true, text = chipText))
                                coroutineScope.launch {
                                    listState.animateScrollToItem(chatMessages.size - 1)
                                    delay(1200)
                                    val reply = generateAstrologerReply(chipText, booking.astrologer, chartData)
                                    chatMessages.add(ChatMessage(isUser = false, text = reply))
                                    listState.animateScrollToItem(chatMessages.size - 1)
                                }
                            },
                            label = { Text(chipText, fontSize = 10.sp, color = Color.White) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = CosmicSurfaceVariant)
                        )
                    }
                }

                // Chat Input Field
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CosmicSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp, top = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("Acharya ${booking.astrologer.name} ji se poochhein...", fontSize = 12.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("consultation_chat_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        IconButton(
                            onClick = {
                                val text = chatInput.trim()
                                if (text.isNotBlank()) {
                                    chatInput = ""
                                    chatMessages.add(ChatMessage(isUser = true, text = text))
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(chatMessages.size - 1)
                                        delay(1500)
                                        val reply = generateAstrologerReply(text, booking.astrologer, chartData)
                                        chatMessages.add(ChatMessage(isUser = false, text = reply))
                                        listState.animateScrollToItem(chatMessages.size - 1)
                                    }
                                }
                            },
                            modifier = Modifier.testTag("send_consultation_msg_button"),
                            enabled = chatInput.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (chatInput.isNotBlank()) SacredGold else Color.Gray
                            )
                        }

                        IconButton(
                            onClick = onEndConsultation,
                            modifier = Modifier.testTag("end_chat_session_button")
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = "End", tint = MysticCrimson)
                        }
                    }
                }
            }
        }

        // Kundli Quick View Bottom Sheet
        if (showKundliSheet && chartData != null) {
            ModalBottomSheet(
                onDismissRequest = { showKundliSheet = false },
                containerColor = CosmicSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Kundli ka Sankshipt Vivaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SacredGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• Naam: ${chartData.profile.name}", color = Color.White)
                    Text("• Lagna: ${chartData.lagnaSign.sanskritName} (${chartData.lagnaSign.englishName})", color = Color.White)
                    Text("• Chandra Rashi: ${chartData.moonSign.sanskritName} (${chartData.ascendant.nakshatra.name})", color = Color.White)
                    Text("• Chalti Dasha: ${chartData.dashaResult.currentMahadasha.sanskritName} - ${chartData.dashaResult.currentAntardasha.sanskritName}", color = SacredGoldLight)
                    Text("• Surya Rashi: ${chartData.sunSign.sanskritName} (${chartData.sunSign.englishName})", color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showKundliSheet = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SacredGold, contentColor = Color.Black)
                    ) {
                        Text("Wapas Session Mein Chalein", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun generateAstrologerReply(query: String, astrologer: Astrologer, chart: KundliChartData?): String {
    val q = query.lowercase()
    val lagna = chart?.lagnaSign?.sanskritName ?: "Mesh"
    val moon = chart?.moonSign?.sanskritName ?: "Vrishabh"
    val dasha = chart?.dashaResult?.currentMahadasha?.sanskritName ?: "Brihaspati"

    return when {
        q.contains("career") || q.contains("job") || q.contains("naukri") || q.contains("promotion") || q.contains("safal") ->
            "Main aapki Kundli ka 10th House (Karma Bhava) aur vartaman mein chalti ${dasha} Dev ki Mahadasha dekh raha hoon. Aane wale 4 se 6 mahino mein aapke karyakshetra mein pad-pratishtha aur aarthik unnati ke bahut shubh yoga hain. Roz subah Surya Dev ko taambe ke lote se jal zaroor arpit karein."
        q.contains("marriage") || q.contains("shadi") || q.contains("vivah") || q.contains("love") || q.contains("spouse") || q.contains("risht") ->
            "Aapke 7th House (Jeevansathi Bhava) aur Shukra Dev ki sthiti ke anusaar, aapka jeevansathi sanskari, samajhdar aur parivarik bhavnaon ko samajhne wala hoga. Vaivahik sukh mein vriddhi ke liye Shukravaar ko safed mishri ya doodh ka daan karna laabhdayak rahega."
        q.contains("gemstone") || q.contains("stone") || q.contains("ratna") ->
            "Aapke $lagna Lagna ke adhyayan ke anusaar, aapka mukhya jeevan ratna ${if (lagna in listOf("Mesh", "Vrishchik")) "Laal Moonga (Red Coral)" else if (lagna in listOf("Vrishabh", "Tula")) "Heera ya Safed Pukhraj" else if (lagna in listOf("Mithun", "Kanya")) "Panna (Emerald)" else "Peela Pukhraj (Yellow Sapphire)"} hai. Yeh aapke aatm-vishwas ko badhayega aur grahon ke shubh fal ko tezi se aakarshit karega."
        else ->
            "Maine aapki Kundli ka dhyan se vishleshan kiya hai. $lagna Lagna aur $moon Chandra rashi hone ki wajah se aapka aatm-bal aur buddhi aapki sabse badi shakti hain. Sahi samay par nirnay lein aur poori lagan se mehnat karein, Ishwar ka ashirwad aapke sath hai."
    }
}
