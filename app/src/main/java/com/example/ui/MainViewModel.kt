package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAstrologerService
import com.example.astrology.calc.AstrologyEngine
import com.example.astrology.calc.KundliChartData
import com.example.astrology.calc.ZodiacSign
import com.example.astrology.dosha.AllDoshasSummary
import com.example.astrology.dosha.DoshaAnalyzer
import com.example.astrology.prediction.DailyRashifal
import com.example.astrology.prediction.PersonalizedPredictions
import com.example.astrology.prediction.RashifalGenerator
import com.example.consultation.model.Astrologer
import com.example.consultation.model.ConsultationBooking
import com.example.consultation.model.ConsultationType
import com.example.data.AppDatabase
import com.example.data.KundliRepository
import com.example.data.model.KundliProfile
import com.example.data.model.StandardCities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class MainUiState(
    val profiles: List<KundliProfile> = emptyList(),
    val activeProfile: KundliProfile = StandardCities.defaultSampleProfile,
    val chartData: KundliChartData? = null,
    val doshas: AllDoshasSummary? = null,
    val personalizedPredictions: PersonalizedPredictions? = null,
    val dailyRashifals: List<DailyRashifal> = emptyList(),
    val selectedRashifalSign: ZodiacSign = ZodiacSign.ARIES,
    val isNorthIndianChart: Boolean = true,
    val isD9Navamsha: Boolean = false,
    val selectedHouse: Int? = 1,
    val selectedSouthSign: ZodiacSign? = ZodiacSign.ARIES,
    val chatMessages: List<ChatMessage> = emptyList(),
    val isChatLoading: Boolean = false,
    val showAddEditDialog: Boolean = false,
    val editingProfile: KundliProfile? = null,
    val activeBooking: ConsultationBooking? = null,
    val bookingDialogAstrologer: Astrologer? = null,
    val bookingDialogType: ConsultationType = ConsultationType.CHAT,
    val isLiveSessionActive: Boolean = false,
    val pastBookings: List<ConsultationBooking> = emptyList(),
    val isGeneratingPdf: Boolean = false,
    val generatedPdfFile: java.io.File? = null,
    val pdfStatusMessage: String? = null,
    val showPdfSuccessDialog: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KundliRepository
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = KundliRepository(db.kundliDao())

        // Initial default calculation
        computeAstrologyForProfile(StandardCities.defaultSampleProfile)

        // Observe Room DB profiles
        viewModelScope.launch {
            repository.allProfiles.collectLatest { list ->
                if (list.isNotEmpty()) {
                    val currentActive = list.firstOrNull { it.isDefault } ?: list.first()
                    _uiState.value = _uiState.value.copy(
                        profiles = list,
                        activeProfile = currentActive
                    )
                    computeAstrologyForProfile(currentActive)
                } else {
                    // Seed default profile
                    repository.insertProfile(StandardCities.defaultSampleProfile)
                }
            }
        }

        // Initialize welcome message in Panditji chat
        _uiState.value = _uiState.value.copy(
            chatMessages = listOf(
                ChatMessage(
                    isUser = false,
                    text = "Pranam! Main aapka Vedic AI Panditji hoon. Maine aapki Janam Kundli aur sabhi grahon ki sthiti dekh li hai. Aaj aap Career, Vivah, Dasha, Bhagya Ratna ya Dosh Nivaran ke vishay mein koi bhi sawal poochh sakte hain."
                )
            )
        )
    }

    private fun computeAstrologyForProfile(profile: KundliProfile) {
        val chart = AstrologyEngine.calculateKundli(profile)
        val doshas = DoshaAnalyzer.analyzeAllDoshas(chart)
        val predictions = RashifalGenerator.generatePersonalizedPredictions(chart)
        val allRashifals = RashifalGenerator.getAllDailyRashifals()

        _uiState.value = _uiState.value.copy(
            activeProfile = profile,
            chartData = chart,
            doshas = doshas,
            personalizedPredictions = predictions,
            dailyRashifals = allRashifals,
            selectedRashifalSign = chart.moonSign,
            selectedHouse = 1,
            selectedSouthSign = chart.lagnaSign
        )
    }

    fun switchActiveProfile(profile: KundliProfile) {
        viewModelScope.launch {
            repository.setDefaultProfile(profile.id)
            computeAstrologyForProfile(profile)
        }
    }

    fun saveProfile(profile: KundliProfile) {
        viewModelScope.launch {
            if (profile.id == 0L) {
                val newId = repository.insertProfile(profile)
                repository.setDefaultProfile(newId)
            } else {
                repository.updateProfile(profile)
            }
            _uiState.value = _uiState.value.copy(showAddEditDialog = false, editingProfile = null)
        }
    }

    fun deleteProfile(profile: KundliProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
        }
    }

    fun openAddProfileDialog() {
        _uiState.value = _uiState.value.copy(showAddEditDialog = true, editingProfile = null)
    }

    fun openEditProfileDialog(profile: KundliProfile) {
        _uiState.value = _uiState.value.copy(showAddEditDialog = true, editingProfile = profile)
    }

    fun dismissAddEditDialog() {
        _uiState.value = _uiState.value.copy(showAddEditDialog = false, editingProfile = null)
    }

    fun toggleChartStyle(isNorth: Boolean) {
        _uiState.value = _uiState.value.copy(isNorthIndianChart = isNorth)
    }

    fun toggleD9Navamsha(isD9: Boolean) {
        _uiState.value = _uiState.value.copy(isD9Navamsha = isD9)
    }

    fun selectHouse(houseNum: Int) {
        _uiState.value = _uiState.value.copy(selectedHouse = houseNum)
    }

    fun selectSouthSign(sign: ZodiacSign) {
        _uiState.value = _uiState.value.copy(selectedSouthSign = sign)
    }

    fun selectRashifalSign(sign: ZodiacSign) {
        _uiState.value = _uiState.value.copy(selectedRashifalSign = sign)
    }

    fun openConsultationBooking(astrologer: Astrologer, type: ConsultationType) {
        _uiState.value = _uiState.value.copy(
            bookingDialogAstrologer = astrologer,
            bookingDialogType = type
        )
    }

    fun dismissConsultationBooking() {
        _uiState.value = _uiState.value.copy(
            bookingDialogAstrologer = null
        )
    }

    fun confirmConsultationBooking(booking: ConsultationBooking) {
        val updatedPast = _uiState.value.pastBookings + booking
        _uiState.value = _uiState.value.copy(
            activeBooking = booking,
            bookingDialogAstrologer = null,
            isLiveSessionActive = true,
            pastBookings = updatedPast
        )
    }

    fun enterLiveConsultationSession() {
        _uiState.value = _uiState.value.copy(isLiveSessionActive = true)
    }

    fun endLiveConsultationSession() {
        _uiState.value = _uiState.value.copy(
            isLiveSessionActive = false,
            activeBooking = null
        )
    }

    fun askPanditji(question: String) {
        if (question.isBlank()) return

        val userMsg = ChatMessage(isUser = true, text = question)
        val currentList = _uiState.value.chatMessages + userMsg
        _uiState.value = _uiState.value.copy(
            chatMessages = currentList,
            isChatLoading = true
        )

        viewModelScope.launch {
            val chart = _uiState.value.chartData ?: AstrologyEngine.calculateKundli(_uiState.value.activeProfile)
            val doshas = _uiState.value.doshas ?: DoshaAnalyzer.analyzeAllDoshas(chart)

            val answer = GeminiAstrologerService.askAstrologer(question, chart, doshas)

            val aiMsg = ChatMessage(isUser = false, text = answer)
            _uiState.value = _uiState.value.copy(
                chatMessages = _uiState.value.chatMessages + aiMsg,
                isChatLoading = false
            )
        }
    }

    fun downloadKundliPdf(context: android.content.Context) {
        val chart = _uiState.value.chartData ?: AstrologyEngine.calculateKundli(_uiState.value.activeProfile)
        val doshas = _uiState.value.doshas ?: DoshaAnalyzer.analyzeAllDoshas(chart)
        val predictions = _uiState.value.personalizedPredictions

        _uiState.value = _uiState.value.copy(isGeneratingPdf = true, pdfStatusMessage = "Kundli PDF taiyaar ho rahi hai...")

        viewModelScope.launch {
            val result = com.example.pdf.KundliPdfGenerator.generateAndSaveKundliPdf(
                context = context,
                chart = chart,
                doshas = doshas,
                predictions = predictions
            )

            result.onSuccess { file ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingPdf = false,
                    generatedPdfFile = file,
                    showPdfSuccessDialog = true,
                    pdfStatusMessage = "Kundli PDF Downloads folder mein safaltapoorvak save ho gayi hai!"
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isGeneratingPdf = false,
                    pdfStatusMessage = "PDF banane mein asafalta: ${error.message}"
                )
            }
        }
    }

    fun dismissPdfDialog() {
        _uiState.value = _uiState.value.copy(showPdfSuccessDialog = false)
    }

    fun openDownloadedPdf(context: android.content.Context) {
        val file = _uiState.value.generatedPdfFile ?: return
        com.example.pdf.KundliPdfGenerator.openOrSharePdf(context, file)
    }

    fun shareDownloadedPdf(context: android.content.Context) {
        val file = _uiState.value.generatedPdfFile ?: return
        com.example.pdf.KundliPdfGenerator.sharePdf(context, file)
    }
}
