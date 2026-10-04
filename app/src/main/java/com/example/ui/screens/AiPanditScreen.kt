package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatMessage
import com.example.ui.MainUiState
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.SacredGold
import com.example.ui.theme.SacredGoldLight

@Composable
fun AiPanditScreen(
    state: MainUiState,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val chart = state.chartData
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val quickQuestions = listOf(
        "Career mein kab bada badlav aur safalta milegi?",
        "7th House ke anusar jeevansathi ka swabhav kaisa hoga?",
        "Meri Kundli ke anusaar shubh bhagya ratna kaunsa hai?",
        "Chalti Dasha ke shubh niyam aur saral upaay batayein?",
        "Shani Sade Sati ya Dhaiya ka kaisa prabhav rahega?",
        "Mera Janam Nakshatra mere baare mein kya batata hai?"
    )

    // Auto-scroll on new message
    LaunchedEffect(state.chatMessages.size, state.isChatLoading) {
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("ai_pandit_screen")
    ) {
        // Top Context Badge
        if (chart != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CosmicSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = SacredGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Vedic AI Panditji ${chart.profile.name} ki Kundli ka adhyayan kar rahe hain (${chart.lagnaSign.sanskritName} Lagna • ${chart.moonSign.sanskritName} Rashi)",
                        style = MaterialTheme.typography.bodySmall,
                        color = SacredGoldLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickQuestions.forEach { q ->
                SuggestionChip(
                    onClick = { onSendMessage(q) },
                    label = { Text(q, fontSize = 11.sp, color = Color.White) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = CosmicSurfaceVariant),
                    border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = SacredGold.copy(alpha = 0.3f))
                )
            }
        }

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.chatMessages) { msg ->
                ChatBubble(message = msg)
            }

            if (state.isChatLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(SacredGold.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🕉️", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = CosmicSurface,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = SacredGold,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Panditji is consulting planetary transits...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SacredGoldLight
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Bar
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CosmicSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp, top = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Panditji se Career, Vivah, Dasha ya Ratna ke baare mein poochhein...", fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                IconButton(
                    onClick = {
                        val q = textInput.trim()
                        if (q.isNotEmpty()) {
                            textInput = ""
                            onSendMessage(q)
                        }
                    },
                    modifier = Modifier.testTag("send_chat_button"),
                    enabled = !state.isChatLoading && textInput.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (textInput.isNotBlank()) SacredGold else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(SacredGold.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🕉️", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) SacredGold else CosmicSurface,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!message.isUser) {
                    Text(
                        text = "Panditji (Vedic Astrologer)",
                        fontWeight = FontWeight.Bold,
                        color = SacredGoldLight,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isUser) Color.Black else Color.White,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
