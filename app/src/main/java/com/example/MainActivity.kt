package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.consultation.ui.BookingAndPaymentDialog
import com.example.consultation.ui.ConsultAstrologerScreen
import com.example.consultation.ui.LiveConsultationScreen
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AddEditProfileDialog
import com.example.ui.screens.*
import com.example.ui.theme.CosmicNavy
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SacredGold

enum class AppTab(val title: String, val icon: ImageVector, val tag: String) {
    KUNDLI("Kundli", Icons.Default.Brightness7, "tab_kundli"),
    DOSHAS("Dosh Vichaar", Icons.Default.Shield, "tab_doshas"),
    RASHIFAL("Rashifal", Icons.Default.AutoAwesome, "tab_rashifal"),
    CONSULT("Jyotishi Se Milein", Icons.Default.SupportAgent, "tab_consult"),
    AI_PANDIT("AI Panditji", Icons.Default.Psychology, "tab_ai_pandit")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                KundliApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KundliApp(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(AppTab.KUNDLI) }
    var showPanchangProfilesSheet by remember { mutableStateOf(false) }

    // If Live Session is active, display full-screen consultation
    if (state.isLiveSessionActive && state.activeBooking != null) {
        BackHandler {
            viewModel.endLiveConsultationSession()
        }
        LiveConsultationScreen(
            booking = state.activeBooking!!,
            chartData = state.chartData,
            onEndConsultation = { viewModel.endLiveConsultationSession() }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CosmicNavy,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🕉️ Kundli Jyotish",
                            fontWeight = FontWeight.Bold,
                            color = SacredGold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0x3000E676),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "100% FREE",
                                color = Color(0xFF69F0AE),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Panchang & Profiles icon button
                    IconButton(
                        onClick = { showPanchangProfilesSheet = true },
                        modifier = Modifier.testTag("open_panchang_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Panchang & Profiles",
                            tint = SacredGold
                        )
                    }

                    // Active Profile Badge Button
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = CosmicSurface,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("active_profile_button"),
                        onClick = { viewModel.openAddProfileDialog() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Active Native",
                                tint = SacredGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = state.activeProfile.name.split(" ").firstOrNull() ?: "Native",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = SacredGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CosmicNavy,
                    titleContentColor = SacredGold
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CosmicSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                AppTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = SacredGold,
                            indicatorColor = SacredGold,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.KUNDLI -> KundliScreen(
                    state = state,
                    onToggleChartStyle = { viewModel.toggleChartStyle(it) },
                    onToggleD9 = { viewModel.toggleD9Navamsha(it) },
                    onHouseSelected = { viewModel.selectHouse(it) }
                )
                AppTab.DOSHAS -> DoshaScreen(
                    state = state
                )
                AppTab.RASHIFAL -> RashifalScreen(
                    state = state,
                    onSignSelected = { viewModel.selectRashifalSign(it) }
                )
                AppTab.CONSULT -> ConsultAstrologerScreen(
                    activeBooking = state.activeBooking,
                    onOpenBookingDialog = { ast, type ->
                        viewModel.openConsultationBooking(ast, type)
                    },
                    onJoinActiveSession = {
                        viewModel.enterLiveConsultationSession()
                    }
                )
                AppTab.AI_PANDIT -> AiPanditScreen(
                    state = state,
                    onSendMessage = { viewModel.askPanditji(it) }
                )
            }

            // Booking & Payment Dialog
            if (state.bookingDialogAstrologer != null) {
                BookingAndPaymentDialog(
                    astrologer = state.bookingDialogAstrologer!!,
                    initialType = state.bookingDialogType,
                    activeProfile = state.activeProfile,
                    allProfiles = state.profiles,
                    onDismiss = { viewModel.dismissConsultationBooking() },
                    onBookingConfirmed = { booking ->
                        viewModel.confirmConsultationBooking(booking)
                    }
                )
            }

            // Birth Details Dialog
            if (state.showAddEditDialog) {
                AddEditProfileDialog(
                    initialProfile = state.editingProfile,
                    onDismiss = { viewModel.dismissAddEditDialog() },
                    onSave = { viewModel.saveProfile(it) }
                )
            }

            // Panchang & Profiles Bottom Sheet Modal
            if (showPanchangProfilesSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showPanchangProfilesSheet = false },
                    containerColor = CosmicSurface
                ) {
                    ProfilesPanchangScreen(
                        state = state,
                        onAddProfileClicked = {
                            showPanchangProfilesSheet = false
                            viewModel.openAddProfileDialog()
                        },
                        onEditProfileClicked = {
                            showPanchangProfilesSheet = false
                            viewModel.openEditProfileDialog(it)
                        },
                        onDeleteProfileClicked = { viewModel.deleteProfile(it) },
                        onSelectProfile = {
                            viewModel.switchActiveProfile(it)
                            showPanchangProfilesSheet = false
                        },
                        modifier = Modifier.fillMaxHeight(0.85f)
                    )
                }
            }
        }
    }
}
