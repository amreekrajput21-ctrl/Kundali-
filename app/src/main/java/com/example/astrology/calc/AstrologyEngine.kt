package com.example.astrology.calc

import com.example.data.model.KundliProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

object AstrologyEngine {

    private fun normalizeDegrees(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0) d += 360.0
        return d
    }

    private fun degToRad(deg: Double): Double = deg * (PI / 180.0)
    private fun radToDeg(rad: Double): Double = rad * (180.0 / PI)

    // Calculate Julian Day
    fun calculateJulianDay(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int, tzOffsetHours: Double): Double {
        val decimalHour = hour + (minute / 60.0) + (second / 3600.0) - tzOffsetHours
        var y = year
        var m = month
        var d = day + (decimalHour / 24.0)

        if (m <= 2) {
            y -= 1
            m += 12
        }

        val a = y / 100
        val b = 2 - a + (a / 4)
        return (365.25 * (y + 4716)).toInt() + (30.6001 * (m + 1)).toInt() + d + b - 1524.5
    }

    // Lahiri Ayanamsha
    fun calculateLahiriAyanamsha(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        // Precession formula for Lahiri Ayanamsha (Chitrapaksha)
        return 23.857092 + (1.396971 * t) + (0.000308 * t * t)
    }

    // Local Sidereal Time
    private fun calculateLST(jd: Double, longitude: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        // Greenwich Mean Sidereal Time in degrees
        var gmst = 280.46061837 + (360.98564736629 * (jd - 2451545.0)) + (0.000387933 * t * t) - ((t * t * t) / 38710000.0)
        gmst = normalizeDegrees(gmst)
        return normalizeDegrees(gmst + longitude)
    }

    // Ascendant (Lagna) calculation
    private fun calculateAscendant(lstDeg: Double, latDeg: Double, ayanamsha: Double, t: Double): Double {
        val eps = 23.4392911 - (0.0130042 * t) // Obliquity of ecliptic
        val epsRad = degToRad(eps)
        val latRad = degToRad(latDeg)
        val lstRad = degToRad(lstDeg)

        val sinLst = sin(lstRad)
        val cosLst = cos(lstRad)
        val tanLat = tan(latRad)
        val sinEps = sin(epsRad)
        val cosEps = cos(epsRad)

        // Ascendant angle in tropical coordinates
        val y = -cosLst
        val x = sinLst * cosEps + tanLat * sinEps
        var tropicalAsc = radToDeg(atan2(y, x))
        tropicalAsc = normalizeDegrees(tropicalAsc + 90.0) // Adjust quadrant to eastern horizon

        // Convert to Vedic Sidereal (Nirayana)
        return normalizeDegrees(tropicalAsc - ayanamsha)
    }

    // Ephemeris Planetary Longitude calculations (Meeus astronomical algorithms)
    private fun calculatePlanetaryPositions(jd: Double, ayanamsha: Double, t: Double): Map<Planet, Pair<Double, Boolean>> {
        val result = mutableMapOf<Planet, Pair<Double, Boolean>>()

        // Sun
        val l0 = normalizeDegrees(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val mSun = normalizeDegrees(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val cSun = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(degToRad(mSun)) +
                (0.019993 - 0.000101 * t) * sin(degToRad(2.0 * mSun)) +
                0.000289 * sin(degToRad(3.0 * mSun))
        val trueSunTrop = normalizeDegrees(l0 + cSun)
        val sunSidereal = normalizeDegrees(trueSunTrop - ayanamsha)
        result[Planet.SUN] = Pair(sunSidereal, false)

        // Moon
        val lMoon = normalizeDegrees(218.3164477 + 481267.88123421 * t - 0.0015786 * t * t + t * t * t / 538841.0)
        val dMoon = normalizeDegrees(297.8501921 + 445267.1114034 * t - 0.0018819 * t * t + t * t * t / 545868.0)
        val mMoon = normalizeDegrees(134.9633964 + 477198.8675055 * t + 0.0087414 * t * t + t * t * t / 69699.0)
        val moonEve = 6.288774 * sin(degToRad(mMoon)) +
                1.274027 * sin(degToRad(2.0 * dMoon - mMoon)) +
                0.658314 * sin(degToRad(2.0 * dMoon)) +
                0.213618 * sin(degToRad(2.0 * mMoon)) -
                0.185116 * sin(degToRad(mSun)) -
                0.114332 * sin(degToRad(2.0 * (dMoon - mMoon)))
        val trueMoonTrop = normalizeDegrees(lMoon + moonEve)
        val moonSidereal = normalizeDegrees(trueMoonTrop - ayanamsha)
        result[Planet.MOON] = Pair(moonSidereal, false)

        // Mars
        val mMars = normalizeDegrees(19.3870 + 19140.3026 * t)
        val lMars = normalizeDegrees(355.4533 + 19140.3026 * t + 10.691 * sin(degToRad(mMars)) + 0.623 * sin(degToRad(2.0 * mMars)))
        val dMarsSun = normalizeDegrees(lMars - trueSunTrop)
        val marsGeo = normalizeDegrees(trueSunTrop + atan2(sin(degToRad(dMarsSun)), 1.524 / 1.0 - cos(degToRad(dMarsSun))).let { radToDeg(it) })
        val marsRetro = sin(degToRad(dMarsSun)) < 0 && abs(dMarsSun - 180.0) < 40.0
        result[Planet.MARS] = Pair(normalizeDegrees(marsGeo - ayanamsha), marsRetro)

        // Mercury
        val mMerc = normalizeDegrees(174.7947 + 149472.6741 * t)
        val lMercHelio = normalizeDegrees(252.2508 + 149472.6741 * t + 23.44 * sin(degToRad(mMerc)))
        val mercElong = 22.0 * sin(degToRad(lMercHelio - trueSunTrop))
        val mercTrop = normalizeDegrees(trueSunTrop + mercElong)
        val mercRetro = abs(mercElong) > 18.0 && cos(degToRad(lMercHelio - trueSunTrop)) < 0.0
        result[Planet.MERCURY] = Pair(normalizeDegrees(mercTrop - ayanamsha), mercRetro)

        // Jupiter
        val mJup = normalizeDegrees(19.8950 + 3034.9061 * t)
        val lJup = normalizeDegrees(34.4044 + 3034.9061 * t + 5.555 * sin(degToRad(mJup)))
        val dJupSun = normalizeDegrees(lJup - trueSunTrop)
        val jupGeo = normalizeDegrees(trueSunTrop + atan2(sin(degToRad(dJupSun)), 5.204 - cos(degToRad(dJupSun))).let { radToDeg(it) })
        val jupRetro = abs(dJupSun - 180.0) < 60.0
        result[Planet.JUPITER] = Pair(normalizeDegrees(jupGeo - ayanamsha), jupRetro)

        // Venus
        val mVen = normalizeDegrees(50.4161 + 58517.8154 * t)
        val lVenHelio = normalizeDegrees(181.9791 + 58517.8154 * t + 0.776 * sin(degToRad(mVen)))
        val venElong = 46.0 * sin(degToRad(lVenHelio - trueSunTrop))
        val venTrop = normalizeDegrees(trueSunTrop + venElong)
        val venRetro = abs(venElong) > 40.0 && cos(degToRad(lVenHelio - trueSunTrop)) < 0.0
        result[Planet.VENUS] = Pair(normalizeDegrees(venTrop - ayanamsha), venRetro)

        // Saturn
        val mSat = normalizeDegrees(316.9670 + 1222.1138 * t)
        val lSat = normalizeDegrees(49.9443 + 1222.1138 * t + 6.358 * sin(degToRad(mSat)))
        val dSatSun = normalizeDegrees(lSat - trueSunTrop)
        val satGeo = normalizeDegrees(trueSunTrop + atan2(sin(degToRad(dSatSun)), 9.582 - cos(degToRad(dSatSun))).let { radToDeg(it) })
        val satRetro = abs(dSatSun - 180.0) < 65.0
        result[Planet.SATURN] = Pair(normalizeDegrees(satGeo - ayanamsha), satRetro)

        // Rahu (Mean lunar ascending node - retrograde by nature)
        val rahuMean = normalizeDegrees(125.04452 - 1934.136261 * t + 0.0020708 * t * t + t * t * t / 450000.0)
        val rahuSidereal = normalizeDegrees(rahuMean - ayanamsha)
        result[Planet.RAHU] = Pair(rahuSidereal, true)

        // Ketu (Opposite to Rahu by 180 degrees)
        val ketuSidereal = normalizeDegrees(rahuSidereal + 180.0)
        result[Planet.KETU] = Pair(ketuSidereal, true)

        return result
    }

    private fun calculateDignity(planet: Planet, sign: ZodiacSign, degInSign: Double): Dignity {
        return when (planet) {
            Planet.SUN -> when (sign) {
                ZodiacSign.ARIES -> if (degInSign <= 10.0) Dignity.EXALTED else Dignity.EXALTED
                ZodiacSign.LIBRA -> Dignity.DEBILITATED
                ZodiacSign.LEO -> if (degInSign <= 20.0) Dignity.MOOLATRIKONA else Dignity.OWN_SIGN
                ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES, ZodiacSign.SCORPIO, ZodiacSign.CANCER -> Dignity.FRIEND
                ZodiacSign.GEMINI, ZodiacSign.VIRGO -> Dignity.NEUTRAL
                else -> Dignity.ENEMY
            }
            Planet.MOON -> when (sign) {
                ZodiacSign.TAURUS -> if (degInSign <= 3.0) Dignity.EXALTED else Dignity.MOOLATRIKONA
                ZodiacSign.SCORPIO -> Dignity.DEBILITATED
                ZodiacSign.CANCER -> Dignity.OWN_SIGN
                ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SAGITTARIUS -> Dignity.FRIEND
                else -> Dignity.NEUTRAL
            }
            Planet.MARS -> when (sign) {
                ZodiacSign.CAPRICORN -> Dignity.EXALTED
                ZodiacSign.CANCER -> Dignity.DEBILITATED
                ZodiacSign.ARIES -> if (degInSign <= 12.0) Dignity.MOOLATRIKONA else Dignity.OWN_SIGN
                ZodiacSign.SCORPIO -> Dignity.OWN_SIGN
                ZodiacSign.LEO, ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> Dignity.FRIEND
                ZodiacSign.TAURUS, ZodiacSign.LIBRA -> Dignity.NEUTRAL
                else -> Dignity.ENEMY
            }
            Planet.MERCURY -> when (sign) {
                ZodiacSign.VIRGO -> if (degInSign <= 15.0) Dignity.EXALTED else if (degInSign <= 20.0) Dignity.MOOLATRIKONA else Dignity.OWN_SIGN
                ZodiacSign.PISCES -> Dignity.DEBILITATED
                ZodiacSign.GEMINI -> Dignity.OWN_SIGN
                ZodiacSign.TAURUS, ZodiacSign.LIBRA, ZodiacSign.LEO -> Dignity.FRIEND
                ZodiacSign.ARIES, ZodiacSign.SCORPIO, ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS -> Dignity.NEUTRAL
                else -> Dignity.ENEMY
            }
            Planet.JUPITER -> when (sign) {
                ZodiacSign.CANCER -> Dignity.EXALTED
                ZodiacSign.CAPRICORN -> Dignity.DEBILITATED
                ZodiacSign.SAGITTARIUS -> if (degInSign <= 10.0) Dignity.MOOLATRIKONA else Dignity.OWN_SIGN
                ZodiacSign.PISCES -> Dignity.OWN_SIGN
                ZodiacSign.ARIES, ZodiacSign.LEO, ZodiacSign.SCORPIO -> Dignity.FRIEND
                ZodiacSign.AQUARIUS -> Dignity.NEUTRAL
                else -> Dignity.ENEMY
            }
            Planet.VENUS -> when (sign) {
                ZodiacSign.PISCES -> Dignity.EXALTED
                ZodiacSign.VIRGO -> Dignity.DEBILITATED
                ZodiacSign.LIBRA -> if (degInSign <= 15.0) Dignity.MOOLATRIKONA else Dignity.OWN_SIGN
                ZodiacSign.TAURUS -> Dignity.OWN_SIGN
                ZodiacSign.GEMINI, ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS -> Dignity.FRIEND
                ZodiacSign.ARIES, ZodiacSign.SCORPIO -> Dignity.NEUTRAL
                else -> Dignity.ENEMY
            }
            Planet.SATURN -> when (sign) {
                ZodiacSign.LIBRA -> Dignity.EXALTED
                ZodiacSign.ARIES -> Dignity.DEBILITATED
                ZodiacSign.AQUARIUS -> if (degInSign <= 20.0) Dignity.MOOLATRIKONA else Dignity.OWN_SIGN
                ZodiacSign.CAPRICORN -> Dignity.OWN_SIGN
                ZodiacSign.TAURUS, ZodiacSign.GEMINI, ZodiacSign.VIRGO -> Dignity.FRIEND
                ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> Dignity.NEUTRAL
                else -> Dignity.ENEMY
            }
            Planet.RAHU -> when (sign) {
                ZodiacSign.TAURUS, ZodiacSign.GEMINI -> Dignity.EXALTED
                ZodiacSign.SCORPIO, ZodiacSign.SAGITTARIUS -> Dignity.DEBILITATED
                ZodiacSign.VIRGO -> Dignity.OWN_SIGN
                else -> Dignity.NEUTRAL
            }
            Planet.KETU -> when (sign) {
                ZodiacSign.SCORPIO, ZodiacSign.SAGITTARIUS -> Dignity.EXALTED
                ZodiacSign.TAURUS, ZodiacSign.GEMINI -> Dignity.DEBILITATED
                ZodiacSign.PISCES -> Dignity.OWN_SIGN
                else -> Dignity.NEUTRAL
            }
            Planet.ASCENDANT -> Dignity.NEUTRAL
        }
    }

    private fun calculateNavamshaSign(totalDeg: Double): ZodiacSign {
        val navamshaSpan = 30.0 / 9.0 // 3° 20' = 3.33333333 degrees
        val navamshaIndex = (totalDeg / navamshaSpan).toInt()
        val signNumber = (navamshaIndex % 12) + 1
        return ZodiacSign.fromNumber(signNumber)
    }

    private fun calculateVimshottariDasha(
        moonLongitude: Double,
        birthDate: String
    ): VimshottariDashaResult {
        val (nakshatra, _) = NakshatraList.fromLongitude(moonLongitude)
        val nakshatraSpan = 360.0 / 27.0
        val degInNak = moonLongitude % nakshatraSpan
        val fractionElapsed = degInNak / nakshatraSpan

        // Dasha lord sequence order
        val dashaSequence = listOf(
            Planet.KETU, Planet.VENUS, Planet.SUN, Planet.MOON,
            Planet.MARS, Planet.RAHU, Planet.JUPITER, Planet.SATURN, Planet.MERCURY
        )

        val startLord = nakshatra.lord
        val startIndex = dashaSequence.indexOf(startLord)
        val firstLordYears = startLord.lordYears.toDouble()
        val firstLordRemainingYears = firstLordYears * (1.0 - fractionElapsed)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            cal.time = sdf.parse(birthDate) ?: Date()
        } catch (_: Exception) {
            cal.time = Date()
        }

        val allPeriods = mutableListOf<DashaPeriod>()
        var currentYear = cal.get(Calendar.YEAR) + (cal.get(Calendar.DAY_OF_YEAR) / 365.25)
        val nowCal = Calendar.getInstance()
        val nowYear = nowCal.get(Calendar.YEAR) + (nowCal.get(Calendar.DAY_OF_YEAR) / 365.25)

        var runningMahadasha = startLord
        var runningAntardasha = startLord
        var mahadashaStartStr = birthDate
        var mahadashaEndStr = birthDate
        var antardashaEndStr = birthDate
        var progressPercent = 0.5f

        for (i in 0 until 9) {
            val planet = dashaSequence[(startIndex + i) % 9]
            val duration = if (i == 0) firstLordRemainingYears else planet.lordYears.toDouble()
            val startY = currentYear
            val endY = currentYear + duration

            val pStartCal = Calendar.getInstance().apply { time = cal.time }
            pStartCal.add(Calendar.DAY_OF_YEAR, ((startY - (cal.get(Calendar.YEAR) + (cal.get(Calendar.DAY_OF_YEAR) / 365.25))) * 365.25).toInt())
            val pEndCal = Calendar.getInstance().apply { time = cal.time }
            pEndCal.add(Calendar.DAY_OF_YEAR, ((endY - (cal.get(Calendar.YEAR) + (cal.get(Calendar.DAY_OF_YEAR) / 365.25))) * 365.25).toInt())

            val startStr = sdf.format(pStartCal.time)
            val endStr = sdf.format(pEndCal.time)
            val isCurrent = nowYear in startY..endY

            allPeriods.add(
                DashaPeriod(
                    planet = planet,
                    startYear = startY,
                    endYear = endY,
                    startDateStr = startStr,
                    endDateStr = endStr,
                    durationYears = duration,
                    isCurrent = isCurrent
                )
            )

            if (isCurrent) {
                runningMahadasha = planet
                mahadashaStartStr = startStr
                mahadashaEndStr = endStr
                val elapsedInMaha = (nowYear - startY) / duration
                progressPercent = elapsedInMaha.toFloat().coerceIn(0f, 1f)

                // Sub-dasha (Antardasha) calculation
                val subSequence = (0..8).map { idx ->
                    val subPlanet = dashaSequence[(dashaSequence.indexOf(planet) + idx) % 9]
                    val subDuration = (planet.lordYears * subPlanet.lordYears) / 120.0
                    Pair(subPlanet, subDuration)
                }

                var subAccum = startY
                for ((subPlanet, subDur) in subSequence) {
                    if (nowYear in subAccum..(subAccum + subDur)) {
                        runningAntardasha = subPlanet
                        val subEndCal = Calendar.getInstance().apply { time = cal.time }
                        subEndCal.add(Calendar.DAY_OF_YEAR, (((subAccum + subDur) - (cal.get(Calendar.YEAR) + (cal.get(Calendar.DAY_OF_YEAR) / 365.25))) * 365.25).toInt())
                        antardashaEndStr = sdf.format(subEndCal.time)
                        break
                    }
                    subAccum += subDur
                }
            }

            currentYear = endY
        }

        return VimshottariDashaResult(
            currentMahadasha = runningMahadasha,
            currentAntardasha = runningAntardasha,
            mahadashaStartDate = mahadashaStartStr,
            mahadashaEndDate = mahadashaEndStr,
            antardashaEndDate = antardashaEndStr,
            progressPercent = progressPercent,
            allMahadashas = allPeriods
        )
    }

    private fun calculatePanchang(
        moonDeg: Double,
        sunDeg: Double,
        birthDate: String
    ): PanchangData {
        val diff = normalizeDegrees(moonDeg - sunDeg)
        val tithiNum = (diff / 12.0).toInt() + 1
        val isShukla = tithiNum <= 15
        val paksha = if (isShukla) "Shukla Paksha" else "Krishna Paksha"
        val tithiName = when (val t = if (isShukla) tithiNum else tithiNum - 15) {
            1 -> "Pratipada (प्रतिपदा)"
            2 -> "Dwitiya (द्वितीया)"
            3 -> "Tritiya (तृतीया)"
            4 -> "Chaturthi (चतुर्थी)"
            5 -> "Panchami (पंचमी)"
            6 -> "Shashthi (षष्ठी)"
            7 -> "Saptami (सप्तमी)"
            8 -> "Ashtami (अष्टमी)"
            9 -> "Navami (नवमी)"
            10 -> "Dashami (दशमी)"
            11 -> "Ekadashi (एकादशी)"
            12 -> "Dwadashi (द्वादशी)"
            13 -> "Trayodashi (त्रयोदशी)"
            14 -> "Chaturdashi (चतुर्दशी)"
            15 -> if (isShukla) "Purnima (पूर्णिमा)" else "Amavasya (अमावस्या)"
            else -> "Tithi $t"
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        try {
            cal.time = sdf.parse(birthDate) ?: Date()
        } catch (_: Exception) {}

        val vaar = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Ravivaar (Surya Dev ka Din)"
            Calendar.MONDAY -> "Somvaar (Bhagwan Shiv & Chandra Dev ka Din)"
            Calendar.TUESDAY -> "Mangalvaar (Bajrangbali Hanuman Ji ka Din)"
            Calendar.WEDNESDAY -> "Budhvaar (Vighnaharta Ganesh Ji ka Din)"
            Calendar.THURSDAY -> "Guruvaar (Brihaspati Dev & Vishnu Ji ka Din)"
            Calendar.FRIDAY -> "Shukravaar (Mata Mahalakshmi ka Din)"
            Calendar.SATURDAY -> "Shanivaar (Shani Dev Maharaj ka Din)"
            else -> "Somvaar"
        }

        val (nak, pada) = NakshatraList.fromLongitude(moonDeg)
        val nakshatraStr = "${nak.name} (Pada $pada)"

        val yogaNum = ((normalizeDegrees(moonDeg + sunDeg)) / (360.0 / 27.0)).toInt() + 1
        val yogaNames = listOf(
            "Vishkambha", "Priti", "Ayushman", "Saubhagya", "Sobhana", "Atiganda",
            "Sukarma", "Dhriti", "Shula", "Ganda", "Vriddhi", "Dhruva",
            "Vyaghata", "Harshana", "Vajra", "Siddhi", "Vyatipata", "Variyan",
            "Parigha", "Shiva", "Siddha", "Sadhya", "Shubha", "Shukla",
            "Brahma", "Indra", "Vaidhriti"
        )
        val yogaStr = yogaNames.getOrElse((yogaNum - 1) % 27) { "Siddha" }

        val karanaNum = ((diff / 6.0).toInt()) % 60
        val movableKaranas = listOf("Bava", "Balava", "Kaulava", "Taitila", "Gara", "Vanija", "Vishti (Bhadra)")
        val karanaStr = movableKaranas[karanaNum % movableKaranas.size]

        val rahuKaalTiming = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "07:30 AM - 09:00 AM"
            Calendar.SATURDAY -> "09:00 AM - 10:30 AM"
            Calendar.FRIDAY -> "10:30 AM - 12:00 PM"
            Calendar.WEDNESDAY -> "12:00 PM - 01:30 PM"
            Calendar.THURSDAY -> "01:30 PM - 03:00 PM"
            Calendar.TUESDAY -> "03:00 PM - 04:30 PM"
            Calendar.SUNDAY -> "04:30 PM - 06:00 PM"
            else -> "12:00 PM - 01:30 PM"
        }

        return PanchangData(
            tithi = "$tithiName ($paksha)",
            tithiNumber = tithiNum,
            paksha = paksha,
            vaar = vaar,
            nakshatra = nakshatraStr,
            yoga = yogaStr,
            karana = karanaStr,
            sunrise = "06:14 AM",
            sunset = "06:48 PM",
            rahuKaal = rahuKaalTiming,
            shubhMuhurat = "11:50 AM - 12:40 PM (Abhijit Muhurat)"
        )
    }

    // Main calculation method that generates the full Kundli Chart Data
    fun calculateKundli(profile: KundliProfile): KundliChartData {
        val dateParts = profile.birthDate.split("-").mapNotNull { it.toIntOrNull() }
        val timeParts = profile.birthTime.split(":").mapNotNull { it.toIntOrNull() }

        val year = if (dateParts.size >= 1) dateParts[0] else 1998
        val month = if (dateParts.size >= 2) dateParts[1] else 7
        val day = if (dateParts.size >= 3) dateParts[2] else 15

        val hour = if (timeParts.size >= 1) timeParts[0] else 10
        val minute = if (timeParts.size >= 2) timeParts[1] else 30

        val jd = calculateJulianDay(year, month, day, hour, minute, 0, profile.timezoneOffset)
        val t = (jd - 2451545.0) / 36525.0
        val ayanamsha = calculateLahiriAyanamsha(jd)
        val lst = calculateLST(jd, profile.longitude)

        // Ascendant (Lagna)
        val ascLongitude = calculateAscendant(lst, profile.latitude, ayanamsha, t)
        val ascSignNum = (ascLongitude / 30.0).toInt() + 1
        val ascSign = ZodiacSign.fromNumber(ascSignNum)
        val ascDegInSign = ascLongitude % 30.0
        val (ascNak, ascPada) = NakshatraList.fromLongitude(ascLongitude)
        val ascNavamsha = calculateNavamshaSign(ascLongitude)

        val ascPos = PlanetaryPosition(
            planet = Planet.ASCENDANT,
            sign = ascSign,
            degreeInSign = ascDegInSign,
            totalDegree = ascLongitude,
            nakshatra = ascNak,
            pada = ascPada,
            house = 1,
            isRetrograde = false,
            isCombust = false,
            dignity = Dignity.OWN_SIGN,
            navamshaSign = ascNavamsha
        )

        // Calculate all other 9 planets
        val rawPositions = calculatePlanetaryPositions(jd, ayanamsha, t)
        val sunPosDeg = rawPositions[Planet.SUN]?.first ?: 0.0

        val planetsList = mutableListOf<PlanetaryPosition>()
        val houseAssignments = Array(12) { mutableListOf<PlanetaryPosition>() }
        val navamshaHouseAssignments = Array(12) { mutableListOf<PlanetaryPosition>() }

        for (planet in listOf(Planet.SUN, Planet.MOON, Planet.MARS, Planet.MERCURY, Planet.JUPITER, Planet.VENUS, Planet.SATURN, Planet.RAHU, Planet.KETU)) {
            val (longDeg, isRetro) = rawPositions[planet] ?: Pair(0.0, false)
            val signNum = (longDeg / 30.0).toInt() + 1
            val sign = ZodiacSign.fromNumber(signNum)
            val degInSign = longDeg % 30.0
            val (nak, pada) = NakshatraList.fromLongitude(longDeg)
            val navSign = calculateNavamshaSign(longDeg)

            // Equal House system from Lagna (Standard Vedic Bhava Chalit)
            val houseNum = ((sign.signNumber - ascSign.signNumber + 12) % 12) + 1
            val navHouseNum = ((navSign.signNumber - ascNavamsha.signNumber + 12) % 12) + 1

            // Combust check (closeness to Sun)
            val isCombust = if (planet != Planet.SUN && planet != Planet.RAHU && planet != Planet.KETU) {
                val diffFromSun = abs(longDeg - sunPosDeg).let { if (it > 180) 360 - it else it }
                when (planet) {
                    Planet.MOON -> diffFromSun < 12.0
                    Planet.MARS -> diffFromSun < 17.0
                    Planet.MERCURY -> diffFromSun < 14.0
                    Planet.JUPITER -> diffFromSun < 11.0
                    Planet.VENUS -> diffFromSun < 10.0
                    Planet.SATURN -> diffFromSun < 15.0
                    else -> false
                }
            } else false

            val dignity = calculateDignity(planet, sign, degInSign)

            val pPos = PlanetaryPosition(
                planet = planet,
                sign = sign,
                degreeInSign = degInSign,
                totalDegree = longDeg,
                nakshatra = nak,
                pada = pada,
                house = houseNum,
                isRetrograde = isRetro,
                isCombust = isCombust,
                dignity = dignity,
                navamshaSign = navSign
            )
            planetsList.add(pPos)
            houseAssignments[houseNum - 1].add(pPos)
            navamshaHouseAssignments[navHouseNum - 1].add(pPos)
        }

        // House metadata in authentic Vedic Hinglish
        val houseSignificances = listOf(
            Triple("Tanu Bhava (1st House)", "Aapka Shareer, Vyaktitva, Atma-Vishwas aur Swasthya", "Sar, Mastishk (Head)"),
            Triple("Dhana Bhava (2nd House)", "Dhan-Daulat, Parivaar, Vaani aur Sanchit Sampatti", "Chehra, Aankhein, Gala"),
            Triple("Sahaja Bhava (3rd House)", "Sahas, Parakram, Chhote Bhai-Behen aur Chhoti Yatrayen", "Kandhe, Baazu, Haath"),
            Triple("Sukha Bhava (4th House)", "Mata ka Sukh, Ghar-Makaan, Vahan aur Man ki Shanti", "Chhati, Phepde, Hriday"),
            Triple("Putra Bhava (5th House)", "Santan, Buddhi, Vidya, Purva Janam Punya aur Creativity", "Pet, Pachan Tantra"),
            Triple("Ari Bhava (6th House)", "Shatru, Rog, Karz aur Rozmara ki Mehnat/Competition", "Aantrein, Kamar"),
            Triple("Yuvati Bhava (7th House)", "Vivah, Jeevansathi ka Swabhav aur Business Partnership", "Kamar ke Neeche ka Bhaag"),
            Triple("Randhra Bhava (8th House)", "Aayu, Gupt Dhan, Achanak Parivartan aur Adhyatma", "Guptang"),
            Triple("Dharma Bhava (9th House)", "Bhagya, Dharam, Teerth Yatra, Guru aur Pita ka Ashirwad", "Janghein (Thighs)"),
            Triple("Karma Bhava (10th House)", "Naukri, Karyakshetra, Vyapaar, Pad-Pratishtha aur Safalta", "Ghutne, Jod"),
            Triple("Labha Bhava (11th House)", "Aamadani, Munafa, Bade Bhai-Behen aur Sapno ki Purti", "Pindliyan, Takhne"),
            Triple("Vyaya Bhava (12th House)", "Kharch, Videsh Yatra, Hospital, Neend aur Moksha", "Pair ke Panje (Feet)")
        )

        val houses = (1..12).map { hNum ->
            val signForHouse = ZodiacSign.fromNumber(((ascSign.signNumber - 1 + (hNum - 1)) % 12) + 1)
            val info = houseSignificances[hNum - 1]
            HouseData(
                houseNumber = hNum,
                sign = signForHouse,
                planets = houseAssignments[hNum - 1],
                bhavaName = info.first,
                sanskritName = info.first.split(" ")[0],
                significance = info.second,
                bodyPart = info.third
            )
        }

        val navamshaHouses = (1..12).map { hNum ->
            val signForHouse = ZodiacSign.fromNumber(((ascNavamsha.signNumber - 1 + (hNum - 1)) % 12) + 1)
            val info = houseSignificances[hNum - 1]
            HouseData(
                houseNumber = hNum,
                sign = signForHouse,
                planets = navamshaHouseAssignments[hNum - 1],
                bhavaName = "Navamsha D9 - ${info.first}",
                sanskritName = info.first.split(" ")[0],
                significance = info.second,
                bodyPart = info.third
            )
        }

        val moonPos = planetsList.first { it.planet == Planet.MOON }
        val sunPos = planetsList.first { it.planet == Planet.SUN }

        val dashaResult = calculateVimshottariDasha(moonPos.totalDegree, profile.birthDate)
        val panchang = calculatePanchang(moonPos.totalDegree, sunPos.totalDegree, profile.birthDate)

        return KundliChartData(
            profile = profile,
            ascendant = ascPos,
            planets = planetsList,
            houses = houses,
            navamshaHouses = navamshaHouses,
            moonSign = moonPos.sign,
            sunSign = sunPos.sign,
            lagnaSign = ascSign,
            dashaResult = dashaResult,
            panchang = panchang,
            ayanamshaDegrees = ayanamsha
        )
    }
}
