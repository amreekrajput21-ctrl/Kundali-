package com.example.astrology.calc

import androidx.compose.ui.graphics.Color

enum class Planet(
    val englishName: String,
    val sanskritName: String,
    val symbol: String,
    val devanagari: String,
    val color: Color,
    val lordYears: Int // Vimshottari Mahadasha years
) {
    SUN("Sun", "Surya", "Su", "सूर्य", Color(0xFFFFB300), 6),
    MOON("Moon", "Chandra", "Mo", "चन्द्र", Color(0xFFE0E0E0), 10),
    MARS("Mars", "Mangal", "Ma", "मंगल", Color(0xFFEF5350), 7),
    MERCURY("Mercury", "Budha", "Me", "बुध", Color(0xFF66BB6A), 17),
    JUPITER("Jupiter", "Guru", "Ju", "बृहस्पति", Color(0xFFFFCA28), 16),
    VENUS("Venus", "Shukra", "Ve", "शुक्र", Color(0xFFCE93D8), 20),
    SATURN("Saturn", "Shani", "Sa", "शनि", Color(0xFF7E57C2), 19),
    RAHU("Rahu", "Rahu", "Ra", "राहु", Color(0xFF78909C), 18),
    KETU("Ketu", "Ketu", "Ke", "केतु", Color(0xFF8D6E63), 7),
    ASCENDANT("Ascendant", "Lagna", "Asc", "लग्न", Color(0xFFFFD54F), 0)
}

enum class ZodiacSign(
    val signNumber: Int, // 1 to 12
    val englishName: String,
    val sanskritName: String,
    val devanagari: String,
    val symbol: String,
    val ruler: Planet,
    val element: String
) {
    ARIES(1, "Aries", "Mesh", "मेष", "♈", Planet.MARS, "Agni (Fire)"),
    TAURUS(2, "Taurus", "Vrishabh", "वृषभ", "♉", Planet.VENUS, "Prithvi (Earth)"),
    GEMINI(3, "Gemini", "Mithun", "मिथुन", "♊", Planet.MERCURY, "Vayu (Air)"),
    CANCER(4, "Cancer", "Kark", "कर्क", "♋", Planet.MOON, "Jal (Water)"),
    LEO(5, "Leo", "Simha", "सिंह", "♌", Planet.SUN, "Agni (Fire)"),
    VIRGO(6, "Virgo", "Kanya", "कन्या", "♍", Planet.MERCURY, "Prithvi (Earth)"),
    LIBRA(7, "Libra", "Tula", "तुला", "♎", Planet.VENUS, "Vayu (Air)"),
    SCORPIO(8, "Scorpio", "Vrishchik", "वृश्चिक", "♏", Planet.MARS, "Jal (Water)"),
    SAGITTARIUS(9, "Sagittarius", "Dhanu", "धनु", "♐", Planet.JUPITER, "Agni (Fire)"),
    CAPRICORN(10, "Capricorn", "Makar", "मकर", "♑", Planet.SATURN, "Prithvi (Earth)"),
    AQUARIUS(11, "Aquarius", "Kumbh", "कुम्भ", "♒", Planet.SATURN, "Vayu (Air)"),
    PISCES(12, "Pisces", "Meen", "मीन", "♓", Planet.JUPITER, "Jal (Water)");

    companion object {
        fun fromNumber(num: Int): ZodiacSign {
            val normalized = ((num - 1) % 12 + 12) % 12 + 1
            return entries.first { it.signNumber == normalized }
        }
    }
}

enum class Dignity(val title: String, val devanagari: String, val color: Color) {
    EXALTED("Uccha (Exalted)", "उच्च", Color(0xFF00E676)),
    MOOLATRIKONA("Moolatrikona", "मूलत्रिकोण", Color(0xFF69F0AE)),
    OWN_SIGN("Swakshetra (Own)", "स्वराशि", Color(0xFF40C4FF)),
    FRIEND("Mitra Rashi", "मित्र", Color(0xFF80D8FF)),
    NEUTRAL("Sam Rashi", "सम", Color(0xFFB0BEC5)),
    ENEMY("Shatru Rashi", "शत्रु", Color(0xFFFFAB91)),
    DEBILITATED("Neecha (Debilitated)", "नीच", Color(0xFFFF5252))
}

data class Nakshatra(
    val index: Int, // 1 to 27
    val name: String,
    val devanagari: String,
    val lord: Planet,
    val deity: String,
    val gana: String // Deva, Manushya, Rakshasa
)

object NakshatraList {
    val all = listOf(
        Nakshatra(1, "Ashwini", "अश्विनी", Planet.KETU, "Ashwini Kumar", "Deva Gana"),
        Nakshatra(2, "Bharani", "भरणी", Planet.VENUS, "Yamaraj", "Manushya Gana"),
        Nakshatra(3, "Krittika", "कृत्तिका", Planet.SUN, "Agni Dev", "Rakshasa Gana"),
        Nakshatra(4, "Rohini", "रोहिणी", Planet.MOON, "Brahma Dev", "Manushya Gana"),
        Nakshatra(5, "Mrigashira", "मृगशिरा", Planet.MARS, "Soma (Chandra)", "Deva Gana"),
        Nakshatra(6, "Ardra", "आर्द्रा", Planet.RAHU, "Rudra Dev", "Manushya Gana"),
        Nakshatra(7, "Punarvasu", "पुनर्वसु", Planet.JUPITER, "Aditi Mata", "Deva Gana"),
        Nakshatra(8, "Pushya", "पुष्य", Planet.SATURN, "Brihaspati Dev", "Deva Gana"),
        Nakshatra(9, "Ashlesha", "अश्लेषा", Planet.MERCURY, "Sarpa Dev", "Rakshasa Gana"),
        Nakshatra(10, "Magha", "मघा", Planet.KETU, "Pitridev", "Rakshasa Gana"),
        Nakshatra(11, "Purva Phalguni", "पूर्वाफाल्गुनी", Planet.VENUS, "Bhaga", "Manushya Gana"),
        Nakshatra(12, "Uttara Phalguni", "उत्तराफाल्गुनी", Planet.SUN, "Aryaman", "Manushya Gana"),
        Nakshatra(13, "Hasta", "हस्त", Planet.MOON, "Savitr (Surya)", "Deva Gana"),
        Nakshatra(14, "Chitra", "चित्रा", Planet.MARS, "Vishwakarma", "Rakshasa Gana"),
        Nakshatra(15, "Swati", "स्वाति", Planet.RAHU, "Vayu Dev", "Deva Gana"),
        Nakshatra(16, "Vishakha", "विशाखा", Planet.JUPITER, "Indragni", "Rakshasa Gana"),
        Nakshatra(17, "Anuradha", "अनुराधा", Planet.SATURN, "Mitra Dev", "Deva Gana"),
        Nakshatra(18, "Jyeshtha", "ज्येष्ठा", Planet.MERCURY, "Devraj Indra", "Rakshasa Gana"),
        Nakshatra(19, "Mula", "मूल", Planet.KETU, "Nirriti", "Rakshasa Gana"),
        Nakshatra(20, "Purva Ashadha", "पूर्वाषाढ़ा", Planet.VENUS, "Apas (Jal Dev)", "Manushya Gana"),
        Nakshatra(21, "Uttara Ashadha", "उत्तराषाढ़ा", Planet.SUN, "Vishwadeva", "Manushya Gana"),
        Nakshatra(22, "Shravana", "श्रवण", Planet.MOON, "Bhagwan Vishnu", "Deva Gana"),
        Nakshatra(23, "Dhanishta", "धनिष्ठा", Planet.MARS, "Ashta Vasu", "Rakshasa Gana"),
        Nakshatra(24, "Shatabhisha", "शतभिषा", Planet.RAHU, "Varun Dev", "Rakshasa Gana"),
        Nakshatra(25, "Purva Bhadrapada", "पूर्वभाद्रपद", Planet.JUPITER, "Aja Ekapada", "Manushya Gana"),
        Nakshatra(26, "Uttara Bhadrapada", "उत्तरभाद्रपद", Planet.SATURN, "Ahirbudhnya", "Manushya Gana"),
        Nakshatra(27, "Revati", "रेवती", Planet.MERCURY, "Pushan Dev", "Deva Gana")
    )

    fun fromLongitude(longitude: Double): Pair<Nakshatra, Int> {
        val normalized = (longitude % 360.0 + 360.0) % 360.0
        val nakshatraSpan = 360.0 / 27.0 // 13.333333333333334 degrees
        val index = (normalized / nakshatraSpan).toInt()
        val degreeInNakshatra = normalized - (index * nakshatraSpan)
        val pada = (degreeInNakshatra / (nakshatraSpan / 4.0)).toInt() + 1
        return Pair(all[index % 27], pada.coerceIn(1, 4))
    }
}

data class PlanetaryPosition(
    val planet: Planet,
    val sign: ZodiacSign,
    val degreeInSign: Double,
    val totalDegree: Double,
    val nakshatra: Nakshatra,
    val pada: Int,
    val house: Int, // 1 to 12 from Lagna
    val isRetrograde: Boolean = false,
    val isCombust: Boolean = false,
    val dignity: Dignity = Dignity.NEUTRAL,
    val navamshaSign: ZodiacSign
) {
    val degreeFormatted: String
        get() {
            val deg = degreeInSign.toInt()
            val min = ((degreeInSign - deg) * 60).toInt()
            val sec = ((((degreeInSign - deg) * 60) - min) * 60).toInt()
            return "%02d° %02d' %02d\"".format(deg, min, sec)
        }
}

data class HouseData(
    val houseNumber: Int, // 1 to 12
    val sign: ZodiacSign,
    val planets: List<PlanetaryPosition>,
    val bhavaName: String,
    val sanskritName: String,
    val significance: String,
    val bodyPart: String
)

data class DashaPeriod(
    val planet: Planet,
    val startYear: Double,
    val endYear: Double,
    val startDateStr: String,
    val endDateStr: String,
    val durationYears: Double,
    val isCurrent: Boolean
)

data class VimshottariDashaResult(
    val currentMahadasha: Planet,
    val currentAntardasha: Planet,
    val mahadashaStartDate: String,
    val mahadashaEndDate: String,
    val antardashaEndDate: String,
    val progressPercent: Float,
    val allMahadashas: List<DashaPeriod>
)

data class PanchangData(
    val tithi: String,
    val tithiNumber: Int,
    val paksha: String,
    val vaar: String,
    val nakshatra: String,
    val yoga: String,
    val karana: String,
    val sunrise: String,
    val sunset: String,
    val rahuKaal: String,
    val shubhMuhurat: String
)

data class KundliChartData(
    val profile: com.example.data.model.KundliProfile,
    val ascendant: PlanetaryPosition,
    val planets: List<PlanetaryPosition>,
    val houses: List<HouseData>,
    val navamshaHouses: List<HouseData>,
    val moonSign: ZodiacSign,
    val sunSign: ZodiacSign,
    val lagnaSign: ZodiacSign,
    val dashaResult: VimshottariDashaResult,
    val panchang: PanchangData,
    val ayanamshaDegrees: Double
)
