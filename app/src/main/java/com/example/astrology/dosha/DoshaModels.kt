package com.example.astrology.dosha

import androidx.compose.ui.graphics.Color
import com.example.astrology.calc.ZodiacSign

enum class DoshaSeverity(val title: String, val devanagari: String, val color: Color) {
    NONE("Dosh Mukt (Bilkul Theek)", "दोष रहित", Color(0xFF00E676)),
    LOW("Anshik Dosh (Mild)", "आंशिक दोष", Color(0xFFFFB300)),
    MODERATE("Madhyam Dosh (Moderate)", "मध्यम दोष", Color(0xFFFF9800)),
    HIGH("Purna Dosh (Strong)", "पूर्ण दोष", Color(0xFFFF5252))
}

data class ManglikDoshaResult(
    val isManglik: Boolean,
    val severity: DoshaSeverity,
    val marsHouseFromLagna: Int,
    val marsHouseFromMoon: Int,
    val isCancelled: Boolean,
    val cancellations: List<String>,
    val analysis: String,
    val impactOnMarriage: String,
    val remedies: List<String>
)

data class KaalSarpResult(
    val hasKaalSarp: Boolean,
    val isPartial: Boolean,
    val typeName: String,
    val sanskritName: String,
    val severity: DoshaSeverity,
    val description: String,
    val effects: String,
    val remedies: List<String>
)

data class SadeSatiResult(
    val isUnderSadeSati: Boolean,
    val phase: String, // Rising / Peak / Setting / Inactive
    val phaseHindi: String,
    val isDhaiya: Boolean,
    val dhaiyaType: String, // Kantaka Shani, Ashtama Shani, None
    val natalMoonSign: ZodiacSign,
    val currentTransitSaturnSign: ZodiacSign,
    val analysis: String,
    val remedies: List<String>
)

data class PitraDoshaResult(
    val hasPitraDosha: Boolean,
    val severity: DoshaSeverity,
    val indicators: List<String>,
    val analysis: String,
    val remedies: List<String>
)

data class AllDoshasSummary(
    val manglik: ManglikDoshaResult,
    val kaalSarp: KaalSarpResult,
    val sadeSati: SadeSatiResult,
    val pitra: PitraDoshaResult,
    val overallHarmonyScore: Int // 0 to 100
)
