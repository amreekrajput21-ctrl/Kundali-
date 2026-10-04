package com.example.astrology.dosha

import com.example.astrology.calc.KundliChartData
import com.example.astrology.calc.Planet
import com.example.astrology.calc.ZodiacSign
import java.util.Calendar

object DoshaAnalyzer {

    fun analyzeAllDoshas(chart: KundliChartData): AllDoshasSummary {
        val manglik = analyzeMangalDosha(chart)
        val kaalSarp = analyzeKaalSarp(chart)
        val sadeSati = analyzeSadeSati(chart.moonSign)
        val pitra = analyzePitraDosha(chart)

        // Vedic Shubh-Ashubh Harmony Score (0-100)
        var score = 90
        if (manglik.isManglik) {
            score -= if (manglik.isCancelled) 8 else if (manglik.severity == DoshaSeverity.HIGH) 25 else 15
        }
        if (kaalSarp.hasKaalSarp) {
            score -= if (kaalSarp.isPartial) 10 else 20
        }
        if (sadeSati.isUnderSadeSati) {
            score -= if (sadeSati.phase.contains("Peak") || sadeSati.phase.contains("Doosra")) 18 else 10
        } else if (sadeSati.isDhaiya) {
            score -= 8
        }
        if (pitra.hasPitraDosha) {
            score -= 12
        }

        return AllDoshasSummary(
            manglik = manglik,
            kaalSarp = kaalSarp,
            sadeSati = sadeSati,
            pitra = pitra,
            overallHarmonyScore = score.coerceIn(35, 98)
        )
    }

    private fun analyzeMangalDosha(chart: KundliChartData): ManglikDoshaResult {
        val mars = chart.planets.first { it.planet == Planet.MARS }
        val moon = chart.planets.first { it.planet == Planet.MOON }
        val jupiter = chart.planets.first { it.planet == Planet.JUPITER }

        val marsHouseFromLagna = mars.house
        val marsHouseFromMoon = ((mars.sign.signNumber - moon.sign.signNumber + 12) % 12) + 1

        val mangalHouses = listOf(1, 2, 4, 7, 8, 12)
        val isLagnaManglik = marsHouseFromLagna in mangalHouses
        val isMoonManglik = marsHouseFromMoon in mangalHouses

        val isManglik = isLagnaManglik || isMoonManglik

        val cancellations = mutableListOf<String>()
        if (mars.sign == ZodiacSign.ARIES && marsHouseFromLagna == 1) {
            cancellations.add("Mangal Dev Mesh rashi mein 1st house mein hain (Ruchaka Mahapurush Yoga dosh ko shant karta hai).")
        }
        if (mars.sign == ZodiacSign.SCORPIO && marsHouseFromLagna == 4) {
            cancellations.add("Mangal Dev apni Vrishchik rashi mein 4th house mein hain, jo parivarik sukh banaye rakhta hai.")
        }
        if (mars.sign == ZodiacSign.CAPRICORN && marsHouseFromLagna == 7) {
            cancellations.add("Mangal Dev Makar rashi mein Uccha (Exalted) hain 7th house mein, jisse Kuja Dosh bhang hota hai.")
        }
        if (mars.sign == ZodiacSign.SAGITTARIUS || mars.sign == ZodiacSign.PISCES) {
            cancellations.add("Mangal Dev Guru ki rashi (${mars.sign.sanskritName}) mein hain, jisse gusse aur aakramakta mein kami aati hai.")
        }
        // Aspect of Jupiter (Guru ki drishti)
        val jupDiff = ((mars.house - jupiter.house + 12) % 12) + 1
        if (jupDiff == 1 || jupDiff == 5 || jupDiff == 7 || jupDiff == 9) {
            cancellations.add("Devguru Brihaspati ki shubh drishti Mangal par pad rahi hai, jisse Guru-Mangal suraksha kavach banta hai.")
        }

        val isCancelled = cancellations.isNotEmpty()
        val severity = when {
            !isManglik -> DoshaSeverity.NONE
            isCancelled -> DoshaSeverity.LOW
            isLagnaManglik && isMoonManglik -> DoshaSeverity.HIGH
            else -> DoshaSeverity.MODERATE
        }

        val analysis = when {
            !isManglik -> "Aapki Kundli mein Mangal Dev shubh sthan (House $marsHouseFromLagna) mein virajman hain. Aapki kundli bilkul Manglik Dosh Mukt hai."
            isCancelled -> "Aapki Kundli mein Mangal Dev House $marsHouseFromLagna mein hain, par Vedic shastron ke niyam anusar dosh bhang ho chuka hai: ${cancellations.first()}."
            severity == DoshaSeverity.HIGH -> "Mangal Dev Lagna se House $marsHouseFromLagna aur Chandra se House $marsHouseFromMoon mein hain. Yeh teekshna Manglik sthiti banata hai, isliye rishto mein thoda dhairya aur meethi vaani zaroori hai."
            else -> "Mangal Dev House $marsHouseFromLagna (${mars.sign.sanskritName} Rashi) mein hain, jo aapko himmat, josh aur uncha aakansha deta hai, par rishte mein thoda samay aur samajhdaari chahiye."
        }

        val marriageImpact = when {
            !isManglik -> "Vaivahik jeevan sukhad, prem-poorn aur shanti se bhara rahega."
            isCancelled -> "Dosh bhang hone ki wajah se vivah par koi asar nahi padega. Saamanyatah Manglik ya Non-Manglik dono rishte anukool rahenge."
            severity == DoshaSeverity.HIGH -> "Vivah se pehle Kundli Milan (Gun Milan) karwana aur manglik jeevansathi channa ya customary shanti pooja karwana shubh rahega."
            else -> "Aapka jeevansathi aatm-vishwas se bhara, mehnati aur aage badhne wala hoga."
        }

        val remedies = listOf(
            "Har Mangalvaar ko Hanuman Chalisa ya Sunderkand ka paath karein.",
            "Mangalvaar ko laal masoor daal ya gud ka daan zarooratmand ko dein.",
            "Jyotishi ki salah lekar daayein haath ki Anamika (ring finger) mein Laal Moonga (Red Coral) pehnein.",
            "Mangalvaar ko Hanuman Ji ke aage chameli ke tel ya til ke tel ka deepak jalayein.",
            "Krodh aur gusse par kabu paane ke liye subah 10 minute Pranayama zaroor karein."
        )

        return ManglikDoshaResult(
            isManglik = isManglik,
            severity = severity,
            marsHouseFromLagna = marsHouseFromLagna,
            marsHouseFromMoon = marsHouseFromMoon,
            isCancelled = isCancelled,
            cancellations = cancellations,
            analysis = analysis,
            impactOnMarriage = marriageImpact,
            remedies = remedies
        )
    }

    private fun analyzeKaalSarp(chart: KundliChartData): KaalSarpResult {
        val rahu = chart.planets.first { it.planet == Planet.RAHU }
        val ketu = chart.planets.first { it.planet == Planet.KETU }
        val otherPlanets = chart.planets.filter { it.planet != Planet.RAHU && it.planet != Planet.KETU }

        val rHouse = rahu.house
        val kHouse = ketu.house

        var clockCount = 0
        var counterCount = 0

        for (p in otherPlanets) {
            val distFromRahu = ((p.house - rHouse + 12) % 12)
            if (distFromRahu in 1..5) {
                clockCount++
            } else if (distFromRahu in 7..11) {
                counterCount++
            }
        }

        val total = otherPlanets.size // 7
        val isFull = clockCount == total || counterCount == total
        val isPartial = (clockCount == total - 1 || counterCount == total - 1) && !isFull

        val hasKaalSarp = isFull || isPartial

        val kaalSarpTypes = listOf(
            Pair("Anant Kaal Sarp", "अनंत कालसर्प दोष"),       // 1st house
            Pair("Kulik Kaal Sarp", "कुलिक कालसर्प दोष"),       // 2nd house
            Pair("Vasuki Kaal Sarp", "वासुकि कालसर्प दोष"),     // 3rd house
            Pair("Shankhpal Kaal Sarp", "शंखपाल कालसर्प दोष"), // 4th house
            Pair("Padma Kaal Sarp", "पद्म कालसर्प दोष"),         // 5th house
            Pair("Mahapadma Kaal Sarp", "महापद्म कालसर्प दोष"), // 6th house
            Pair("Takshak Kaal Sarp", "तक्षक कालसर्प दोष"),     // 7th house
            Pair("Karkotak Kaal Sarp", "कर्कोटक कालसर्प दोष"), // 8th house
            Pair("Shankhachur Kaal Sarp", "शंखचूड़ कालसर्प"),  // 9th house
            Pair("Ghatak Kaal Sarp", "घातक कालसर्प दोष"),       // 10th house
            Pair("Vishdhar Kaal Sarp", "विषधर कालसर्प दोष"),   // 11th house
            Pair("Sheshnag Kaal Sarp", "शेषनाग कालसर्प दोष")    // 12th house
        )

        val selectedType = kaalSarpTypes.getOrElse(rHouse - 1) { Pair("Anant Kaal Sarp", "अनंत कालसर्प दोष") }

        val severity = when {
            !hasKaalSarp -> DoshaSeverity.NONE
            isPartial -> DoshaSeverity.LOW
            else -> DoshaSeverity.MODERATE
        }

        val description = if (hasKaalSarp) {
            "Jab sabhi grah Rahu (House $rHouse) aur Ketu (House $kHouse) ke madhya baandh jaate hain, tab yeh yoga banta hai. Aapki kundli mein '${selectedType.first}' ka prabhav hai."
        } else {
            "Sabhi grah Rahu aur Ketu ke donon taraf azaad roop se sthit hain. Aapki kundli poori tarah Kaal Sarp Dosh se mukt hai."
        }

        val effects = if (hasKaalSarp) {
            "Shuruaati umar mein thodi der ya rukawat aa sakti hai, par 28-32 varsh ki aayu ke baad vyakti zabardast safalta aur adhyatmik oonchaiyan haasil karta hai."
        } else {
            "Aapke career aur lakshyon mein swabhavik pragati aur bina kisi anjaan bandhan ke raste khulte rahenge."
        }

        val remedies = listOf(
            "Somvaar ya Pradosh Vrat ke din Bhagwan Bholenath ka Rudrabhishek karein.",
            "Roz 108 baar 'Om Namah Shivaya' ya Maha Mrityunjaya Mantra ka jaap karein.",
            "Nag Panchami ke din chandi ka chhota Nag-Nagin joda behte paani mein pravahit karein.",
            "Har Shanivaar ko be-sahara kutton aur pakshiyon ko bhojan karwayein.",
            "Ghar ke mandir mein ek chhota parad shivalinga sthapit karke jal chadhayein."
        )

        return KaalSarpResult(
            hasKaalSarp = hasKaalSarp,
            isPartial = isPartial,
            typeName = if (hasKaalSarp) selectedType.first else "Kaal Sarp Dosh Mukt",
            sanskritName = if (hasKaalSarp) selectedType.second else "दोष रहित",
            severity = severity,
            description = description,
            effects = effects,
            remedies = remedies
        )
    }

    private fun analyzeSadeSati(moonSign: ZodiacSign): SadeSatiResult {
        val currentTransitSaturn = ZodiacSign.PISCES // Shani transit Meen rashi mein

        val diff = ((currentTransitSaturn.signNumber - moonSign.signNumber + 12) % 12)

        val (isUnder, phase, phaseHindi) = when (diff) {
            11 -> Triple(true, "Pehla Charan (Aarohi - Rising)", "प्रथम चरण (उदय)")
            0 -> Triple(true, "Doosra Charan (Shikhar - Peak)", "द्वितीय चरण (शिखर)")
            1 -> Triple(true, "Teesra Charan (Avarohi - Setting)", "तृतीय चरण (अस्त)")
            else -> Triple(false, "Nahi Chal Rahi", "वर्तमान में नहीं")
        }

        val isDhaiya = diff == 3 || diff == 7 // 4th or 8th house from Moon
        val dhaiyaType = when (diff) {
            3 -> "Kantaka Shani (Chauthay Ghar ki Dhaiya - 2.5 Saal)"
            7 -> "Ashtama Shani (Aathvein Ghar ki Dhaiya - 2.5 Saal)"
            else -> "None"
        }

        val analysis = when {
            isUnder && phase.contains("Pehla") ->
                "Shani Dev aapki Chandra Rashi (${moonSign.sanskritName}) se 12vein ghar mein gochar kar rahe hain. Is charan mein kharchon par dhyan dein aur naye nivesh soch-samajhkar karein."
            isUnder && phase.contains("Doosra") ->
                "Shani Dev aapki Chandra Rashi (${moonSign.sanskritName}) ke upar hi gochar kar rahe hain. Yeh pariksha aur tapasya ka samay hai. Jitni imaandari aur mehnat karenge, Shani Dev utna bada inaam denge."
            isUnder && phase.contains("Teesra") ->
                "Shani Dev aapki Chandra Rashi se 2sre ghar mein gochar kar rahe hain. Dheere-dheere manokaamnayein poori hongi aur dhan-sampatti mein thehraav aayega."
            isDhaiya ->
                "Aap par Shani ki Dhaiya ($dhaiyaType) ka asar hai. Swasthya aur rozmara ki routine mein anushasan banaye rakhein."
            else ->
                "Aap par vartaman mein Shani ki Sade Sati ya Dhaiya nahi chal rahi hai. Shani Dev aapki kundli ke anukool sthan par hain."
        }

        val remedies = listOf(
            "Har Shanivaar shaam ko Peepal ke ped ke neeche sarson ke tel ka deepak jalayein.",
            "Shanivaar ko Shani Chalisa ya 'Om Sham Shanaishcharaya Namah' ka 108 baar jaap karein.",
            "Kaale til, kaale kapde, ya lohe ke bartan zarooratmand logon ko daan karein.",
            "Apne ghar ke sevakon, safai karmchariyon aur buzurgo ka aadar karein—yeh Shani ke roop hain.",
            "Shanivaar ke din jhoot bolne, sharab aur juve se door rahein."
        )

        return SadeSatiResult(
            isUnderSadeSati = isUnder,
            phase = phase,
            phaseHindi = phaseHindi,
            isDhaiya = isDhaiya,
            dhaiyaType = dhaiyaType,
            natalMoonSign = moonSign,
            currentTransitSaturnSign = currentTransitSaturn,
            analysis = analysis,
            remedies = remedies
        )
    }

    private fun analyzePitraDosha(chart: KundliChartData): PitraDoshaResult {
        val sun = chart.planets.first { it.planet == Planet.SUN }
        val rahu = chart.planets.first { it.planet == Planet.RAHU }
        val ketu = chart.planets.first { it.planet == Planet.KETU }
        val saturn = chart.planets.first { it.planet == Planet.SATURN }
        val ninthHouse = chart.houses.first { it.houseNumber == 9 }

        val indicators = mutableListOf<String>()

        if (sun.house == rahu.house) {
            indicators.add("Surya aur Rahu ki yuti (Grahan Yoga) House ${sun.house} mein hai.")
        }
        if (sun.house == ketu.house) {
            indicators.add("Surya aur Ketu ki yuti House ${sun.house} mein purvaj rin ko darshati hai.")
        }
        if (sun.house == saturn.house) {
            indicators.add("Surya aur Shani ki yuti House ${sun.house} mein pita-purvajo ke dharmiq rin ko darshati hai.")
        }
        if (ninthHouse.planets.any { it.planet == Planet.RAHU || it.planet == Planet.KETU }) {
            indicators.add("Dharma aur Pitri Bhava (9th House) mein chhaya grah (Rahu/Ketu) sthit hain.")
        }

        val hasDosha = indicators.isNotEmpty()
        val severity = when {
            indicators.size >= 2 -> DoshaSeverity.MODERATE
            indicators.size == 1 -> DoshaSeverity.LOW
            else -> DoshaSeverity.NONE
        }

        val analysis = if (hasDosha) {
            "Kundli mein Pitra Rin ke sanket hain. Iska matlab hai ki purvajo ke ashirwad ke liye kuch vishesh daan-punya aur shanti upasana karna laabhdayak rahega."
        } else {
            "Aapka 9th House aur Surya Dev poori tarah nirmal hain. Aap par pitron aur kuldevta ka purna ashirwad bana hua hai."
        }

        val remedies = listOf(
            "Amavasya ya Pitru Paksha ke dauran purvajo ke naam par Tarpan aur Brahmin Bhojan karwayein.",
            "Subah naha-dhokar Peepal ke ped mein thoda kacha doodh, kaale til aur jal arpit karein.",
            "Amavasya ko gai (cow), kauve (crow) aur kutton ko bhojan karwayein.",
            "Roz subah poorav disha ki taraf muh karke 21 baar Gayatri Mantra ka jaap karein.",
            "Mata-pita aur ghar ke buzurgon ka charan sparsh karke ashirwad zaroor lein."
        )

        return PitraDoshaResult(
            hasPitraDosha = hasDosha,
            severity = severity,
            indicators = indicators,
            analysis = analysis,
            remedies = remedies
        )
    }
}
