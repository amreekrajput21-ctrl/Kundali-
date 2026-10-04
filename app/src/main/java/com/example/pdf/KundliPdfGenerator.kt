package com.example.pdf

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import com.example.astrology.calc.KundliChartData
import com.example.astrology.calc.Planet
import com.example.astrology.calc.ZodiacSign
import com.example.astrology.dosha.AllDoshasSummary
import com.example.astrology.prediction.PersonalizedPredictions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object KundliPdfGenerator {
    private const val TAG = "KundliPdfGenerator"

    // Standard A4 dimensions in PostScript points (72 points per inch)
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    suspend fun generateAndSaveKundliPdf(
        context: Context,
        chart: KundliChartData,
        doshas: AllDoshasSummary,
        predictions: PersonalizedPredictions?
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val pdfDoc = PdfDocument()

            // Page 1: Janam Vivaran, Panchang & Kundli Chakras
            renderPageOne(pdfDoc, chart)

            // Page 2: Graha Sphuta Table, Vimshottari Dasha & Dosh Vichaar
            renderPageTwo(pdfDoc, chart, doshas, predictions)

            // File naming based on native's name
            val safeName = chart.profile.name.replace("\\s+".toRegex(), "_")
            val fileName = "Kundli_${safeName}_${System.currentTimeMillis() % 10000}.pdf"

            // Save to app external files dir
            val targetDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
            val pdfFile = File(targetDir, fileName)
            val outputStream = FileOutputStream(pdfFile)
            pdfDoc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDoc.close()

            // Also copy to Android Public MediaStore Downloads folder if Android 10+
            saveToPublicDownloads(context, pdfFile, fileName)

            Log.i(TAG, "Kundli PDF generated successfully at ${pdfFile.absolutePath}")
            Result.success(pdfFile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate Kundli PDF", e)
            Result.failure(e)
        }
    }

    private fun renderPageOne(pdfDoc: PdfDocument, chart: KundliChartData) {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDoc.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Background
        paint.color = Color.rgb(253, 252, 248)
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), paint)

        // Ornate Border
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        paint.color = Color.rgb(180, 140, 40) // Sacred Gold
        canvas.drawRect(20f, 20f, (PAGE_WIDTH - 20).toFloat(), (PAGE_HEIGHT - 20).toFloat(), paint)

        paint.strokeWidth = 0.8f
        paint.color = Color.rgb(215, 185, 110)
        canvas.drawRect(24f, 24f, (PAGE_WIDTH - 24).toFloat(), (PAGE_HEIGHT - 24).toFloat(), paint)

        paint.style = Paint.Style.FILL

        // Header Title Banner
        paint.color = Color.rgb(20, 25, 45) // Deep Cosmic Navy
        canvas.drawRoundRect(35f, 35f, (PAGE_WIDTH - 35).toFloat(), 95f, 10f, 10f, paint)

        paint.color = Color.rgb(255, 215, 0) // Gold
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("ॐ KUNDLI JYOTISH — VEDIC JANMA PATRIKA ॐ", (PAGE_WIDTH / 2).toFloat(), 65f, paint)

        paint.color = Color.rgb(230, 230, 230)
        paint.textSize = 10.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Brihat Parashara Hora Shastra & Lahiri Chitrapaksha Ayanamsha Pramaanik Report", (PAGE_WIDTH / 2).toFloat(), 83f, paint)

        // Native Details Box
        paint.color = Color.rgb(245, 240, 225)
        canvas.drawRoundRect(35f, 105f, (PAGE_WIDTH - 35).toFloat(), 185f, 8f, 8f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.rgb(140, 90, 20)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("JANAM VIVARAN (BIRTH DETAILS)", 48f, 124f, paint)

        paint.color = Color.rgb(30, 30, 30)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val profile = chart.profile
        canvas.drawText("• Pura Naam (Name): ${profile.name} (${profile.gender})", 48f, 142f, paint)
        canvas.drawText("• Janam Tithi (Date): ${profile.birthDate}", 48f, 158f, paint)
        canvas.drawText("• Janam Samay (Time): ${profile.birthTime} (IST/UTC+${profile.timezoneOffset})", 48f, 174f, paint)

        canvas.drawText("• Janam Sthan (Place): ${profile.birthPlace}", 310f, 142f, paint)
        canvas.drawText("• Coordinates: ${profile.latitude}° N, ${profile.longitude}° E", 310f, 158f, paint)
        canvas.drawText("• Lahiri Ayanamsha: ${"%.2f".format(chart.ayanamshaDegrees)}° (Nirayana Sidereal)", 310f, 174f, paint)

        // Vedic Fundamentals & Panchang Box
        paint.color = Color.rgb(238, 243, 250)
        canvas.drawRoundRect(35f, 195f, (PAGE_WIDTH - 35).toFloat(), 265f, 8f, 8f, paint)

        paint.color = Color.rgb(25, 60, 130)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("VEDIC PANCHANG & ASTROLOGICAL FUNDAMENTALS", 48f, 213f, paint)

        paint.color = Color.rgb(30, 30, 30)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val p = chart.panchang
        canvas.drawText("• Lagna: ${chart.lagnaSign.sanskritName} (${chart.lagnaSign.englishName}) - Swami: ${chart.lagnaSign.ruler.sanskritName}", 48f, 230f, paint)
        canvas.drawText("• Chandra Rashi: ${chart.moonSign.sanskritName} (${chart.moonSign.englishName})", 48f, 245f, paint)
        canvas.drawText("• Nakshatra: ${chart.ascendant.nakshatra.name} (Charan ${chart.ascendant.pada})", 48f, 259f, paint)

        canvas.drawText("• Tithi: ${p.tithi} | Vaar: ${p.vaar}", 310f, 230f, paint)
        canvas.drawText("• Yoga: ${p.yoga} | Karana: ${p.karana}", 310f, 245f, paint)
        canvas.drawText("• Abhijit Muhurat: ${p.shubhMuhurat} | Rahu Kaal: ${p.rahuKaal}", 310f, 259f, paint)

        // Section Title: D1 Janma Lagna Chakra & D9 Navamsha Chakra
        paint.color = Color.rgb(180, 140, 40)
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("D1 JANMA LAGNA CHAKRA (MAIN CHART)", 165f, 292f, paint)
        canvas.drawText("D9 NAVAMSHA CHAKRA (BHAGYA & SPOUSE)", 430f, 292f, paint)

        // Draw D1 North Indian Kundli Chart (Left)
        drawNorthIndianChartPdf(
            canvas = canvas,
            centerX = 165f,
            centerY = 415f,
            size = 220f,
            houses = chart.houses,
            paint = paint
        )

        // Draw D9 Navamsha Kundli Chart (Right)
        drawNorthIndianChartPdf(
            canvas = canvas,
            centerX = 430f,
            centerY = 415f,
            size = 220f,
            houses = chart.navamshaHouses,
            paint = paint
        )

        // 12 Houses Quick Summary Table at bottom of Page 1
        paint.color = Color.rgb(20, 25, 45)
        canvas.drawRoundRect(35f, 545f, (PAGE_WIDTH - 35).toFloat(), 805f, 8f, 8f, paint)

        paint.color = Color.rgb(255, 215, 0)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("BHAVA VIVARAN (12 HOUSES SUMMARY - LAGNA KUNDLI)", 48f, 567f, paint)

        paint.textSize = 8.5f
        paint.color = Color.rgb(220, 220, 220)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // Two columns of 6 houses each
        for (i in 0 until 6) {
            val hLeft = chart.houses[i]
            val hRight = chart.houses[i + 6]
            val yPos = 588f + (i * 35f)

            // Left column (H1 to H6)
            paint.color = Color.rgb(255, 215, 0)
            canvas.drawText("Ghar ${hLeft.houseNumber} (${hLeft.bhavaName}):", 48f, yPos, paint)
            paint.color = Color.WHITE
            canvas.drawText("${hLeft.sign.symbol} ${hLeft.sign.sanskritName} (Swami: ${hLeft.sign.ruler.sanskritName})", 155f, yPos, paint)
            val leftPlanets = if (hLeft.planets.isEmpty()) "Grah: Shunya" else "Grah: " + hLeft.planets.joinToString { it.planet.sanskritName }
            paint.color = Color.rgb(180, 220, 255)
            canvas.drawText(leftPlanets, 48f, yPos + 13f, paint)

            // Right column (H7 to H12)
            paint.color = Color.rgb(255, 215, 0)
            canvas.drawText("Ghar ${hRight.houseNumber} (${hRight.bhavaName}):", 310f, yPos, paint)
            paint.color = Color.WHITE
            canvas.drawText("${hRight.sign.symbol} ${hRight.sign.sanskritName} (Swami: ${hRight.sign.ruler.sanskritName})", 420f, yPos, paint)
            val rightPlanets = if (hRight.planets.isEmpty()) "Grah: Shunya" else "Grah: " + hRight.planets.joinToString { it.planet.sanskritName }
            paint.color = Color.rgb(180, 220, 255)
            canvas.drawText(rightPlanets, 310f, yPos + 13f, paint)
        }

        // Footer Page 1
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.GRAY
        paint.textSize = 8.5f
        canvas.drawText("Prishth 1 / 2 • Kundli Jyotish App", (PAGE_WIDTH / 2).toFloat(), 824f, paint)

        pdfDoc.finishPage(page)
    }

    private fun renderPageTwo(
        pdfDoc: PdfDocument,
        chart: KundliChartData,
        doshas: AllDoshasSummary,
        predictions: PersonalizedPredictions?
    ) {
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 2).create()
        val page = pdfDoc.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply { isAntiAlias = true }

        // Background
        paint.color = Color.rgb(253, 252, 248)
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), paint)

        // Borders
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        paint.color = Color.rgb(180, 140, 40)
        canvas.drawRect(20f, 20f, (PAGE_WIDTH - 20).toFloat(), (PAGE_HEIGHT - 20).toFloat(), paint)
        paint.strokeWidth = 0.8f
        paint.color = Color.rgb(215, 185, 110)
        canvas.drawRect(24f, 24f, (PAGE_WIDTH - 24).toFloat(), (PAGE_HEIGHT - 24).toFloat(), paint)

        paint.style = Paint.Style.FILL

        // Header Table: Graha Sphuta (Planetary Positions)
        paint.color = Color.rgb(20, 25, 45)
        canvas.drawRoundRect(35f, 35f, (PAGE_WIDTH - 35).toFloat(), 70f, 6f, 6f, paint)

        paint.color = Color.rgb(255, 215, 0)
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("GRAHA SPHUTA (EXACT PLANETARY POSITIONS TABLE)", 48f, 57f, paint)

        // Table Header
        paint.color = Color.rgb(240, 235, 220)
        canvas.drawRect(35f, 75f, (PAGE_WIDTH - 35).toFloat(), 95f, paint)

        paint.color = Color.rgb(50, 50, 50)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Grah (Planet)", 42f, 89f, paint)
        canvas.drawText("Rashi (Zodiac)", 115f, 89f, paint)
        canvas.drawText("Ansh (Degree)", 210f, 89f, paint)
        canvas.drawText("Nakshatra & Pada", 295f, 89f, paint)
        canvas.drawText("Ghar (H)", 405f, 89f, paint)
        canvas.drawText("Avastha / Status", 455f, 89f, paint)

        // Table Rows (Ascendant + 9 Planets)
        var rowY = 111f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 8.5f

        // Ascendant row
        canvas.drawText("Lagna (Ascendant)", 42f, rowY, paint)
        canvas.drawText("${chart.lagnaSign.sanskritName} (${chart.lagnaSign.symbol})", 115f, rowY, paint)
        canvas.drawText(chart.ascendant.degreeFormatted, 210f, rowY, paint)
        canvas.drawText("${chart.ascendant.nakshatra.name} - ${chart.ascendant.pada}", 295f, rowY, paint)
        canvas.drawText("1st", 405f, rowY, paint)
        canvas.drawText("Uday Lagna", 455f, rowY, paint)

        // Line divider
        paint.color = Color.rgb(230, 230, 230)
        canvas.drawLine(35f, rowY + 5f, (PAGE_WIDTH - 35).toFloat(), rowY + 5f, paint)

        for (p in chart.planets) {
            rowY += 17f
            paint.color = Color.rgb(20, 20, 20)
            val name = "${p.planet.sanskritName}${if (p.isRetrograde) " (V)" else ""}"
            canvas.drawText(name, 42f, rowY, paint)
            canvas.drawText("${p.sign.sanskritName} (${p.sign.symbol})", 115f, rowY, paint)
            canvas.drawText(p.degreeFormatted, 210f, rowY, paint)
            canvas.drawText("${p.nakshatra.name} - ${p.pada}", 295f, rowY, paint)
            canvas.drawText("${p.house}th", 405f, rowY, paint)
            canvas.drawText(p.dignity.title, 455f, rowY, paint)

            paint.color = Color.rgb(235, 235, 235)
            canvas.drawLine(35f, rowY + 4f, (PAGE_WIDTH - 35).toFloat(), rowY + 4f, paint)
        }

        // Section: Vimshottari Dasha
        rowY += 22f
        paint.color = Color.rgb(20, 25, 45)
        canvas.drawRoundRect(35f, rowY, (PAGE_WIDTH - 35).toFloat(), rowY + 70f, 6f, 6f, paint)

        paint.color = Color.rgb(255, 215, 0)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("VIMSHOTTARI DASHA PRANALI (120-VARSHIYA CHAKRA)", 48f, rowY + 18f, paint)

        val dasha = chart.dashaResult
        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("• Chalti Mahadasha: ${dasha.currentMahadasha.sanskritName} Dev (${dasha.mahadashaStartDate} se ${dasha.mahadashaEndDate} tak)", 48f, rowY + 36f, paint)
        canvas.drawText("• Antardasha: ${dasha.currentAntardasha.sanskritName} Dev (${dasha.antardashaEndDate} tak sakriya hai)", 48f, rowY + 52f, paint)
        canvas.drawText("• Dasha Cycle Progress: ${(dasha.progressPercent * 100).toInt()}% poora ho chuka hai", 340f, rowY + 52f, paint)

        // Section: Vedic Dosh Vichaar
        rowY += 82f
        paint.color = Color.rgb(248, 242, 235)
        canvas.drawRoundRect(35f, rowY, (PAGE_WIDTH - 35).toFloat(), rowY + 175f, 6f, 6f, paint)

        paint.color = Color.rgb(180, 50, 40)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("VEDIC DOSH VICHAAR & SHANTI UPAAY (HARMONY SCORE: ${doshas.overallHarmonyScore}/100)", 48f, rowY + 20f, paint)

        paint.color = Color.rgb(30, 30, 30)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        // 1. Manglik Dosha
        val mangal = doshas.manglik
        val mangalStatus = if (!mangal.isManglik) "Dosh Mukt (दोष रहित)" else if (mangal.isCancelled) "Dosh Bhang / Anshik (Cancellations Shastrokt)" else "Sakriya (${mangal.severity.title})"
        canvas.drawText("1. Mangal (Kuja) Dosh: $mangalStatus", 48f, rowY + 38f, paint)
        canvas.drawText("   Vivaran: ${mangal.analysis.take(95)}...", 48f, rowY + 52f, paint)

        // 2. Kaal Sarp Dosha
        val ks = doshas.kaalSarp
        val ksStatus = if (!ks.hasKaalSarp) "Dosh Mukt" else if (ks.isPartial) "Anshik (${ks.typeName})" else "Purna (${ks.typeName})"
        canvas.drawText("2. Kaal Sarp Dosh: $ksStatus", 48f, rowY + 70f, paint)
        canvas.drawText("   Vivaran: ${ks.description.take(95)}...", 48f, rowY + 84f, paint)

        // 3. Shani Sade Sati
        val ss = doshas.sadeSati
        val ssStatus = if (ss.isUnderSadeSati) ss.phase else if (ss.isDhaiya) "Dhaiya Chalu Hai (${ss.dhaiyaType})" else "Vartaman mein nahi chal rahi"
        canvas.drawText("3. Shani Sade Sati / Dhaiya: $ssStatus", 48f, rowY + 102f, paint)
        canvas.drawText("   Vivaran: ${ss.analysis.take(95)}...", 48f, rowY + 116f, paint)

        // 4. Pitra Dosha
        val pitra = doshas.pitra
        val pitraStatus = if (!pitra.hasPitraDosha) "Pitron ka Ashirwad (Nirmal 9th House)" else "Pitra Rin Sanket (${pitra.severity.title})"
        canvas.drawText("4. Pitra Dosh: $pitraStatus", 48f, rowY + 134f, paint)
        canvas.drawText("   Vivaran: ${pitra.analysis.take(95)}...", 48f, rowY + 148f, paint)
        canvas.drawText("   Mukhya Upaay: Somvaar Shiv-jalabhishek, Gayatri Mantra jaap aur Peepal pujan karein.", 48f, rowY + 162f, paint)

        // Section: Suraksha Kavach (Gemstone & Mantra)
        rowY += 185f
        paint.color = Color.rgb(235, 245, 235)
        canvas.drawRoundRect(35f, rowY, (PAGE_WIDTH - 35).toFloat(), rowY + 80f, 6f, 6f, paint)

        paint.color = Color.rgb(20, 110, 50)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("VEDIC SURAKSHA KAVACH (SHUBH BHAGYA RATNA & MANTRA)", 48f, rowY + 20f, paint)

        paint.color = Color.rgb(30, 30, 30)
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        if (predictions != null) {
            canvas.drawText("• Shubh Jeevan Ratna (Gemstone): ${predictions.recommendedGemstone}", 48f, rowY + 38f, paint)
            canvas.drawText("• Dharan Labh: ${predictions.gemstoneBenefits}", 48f, rowY + 53f, paint)
            canvas.drawText("• Nitya Jaap Mantra: ${predictions.favorableMantra}", 48f, rowY + 68f, paint)
        } else {
            canvas.drawText("• Nitya Jaap Mantra: Om Namah Shivaya (ॐ नमः शिवाय)", 48f, rowY + 38f, paint)
        }

        // Footer Page 2
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.GRAY
        paint.textSize = 8.5f
        canvas.drawText("Prishth 2 / 2 • Kundli Jyotish App (Pramanik Vedic Report)", (PAGE_WIDTH / 2).toFloat(), 824f, paint)

        pdfDoc.finishPage(page)
    }

    private fun drawNorthIndianChartPdf(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        size: Float,
        houses: List<com.example.astrology.calc.HouseData>,
        paint: Paint
    ) {
        val half = size / 2f
        val left = centerX - half
        val top = centerY - half
        val right = centerX + half
        val bottom = centerY + half

        // Fill background
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(250, 248, 240)
        canvas.drawRect(left, top, right, bottom, paint)

        // Outer square
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = Color.rgb(180, 140, 40)
        canvas.drawRect(left, top, right, bottom, paint)

        // Diagonal lines
        canvas.drawLine(left, top, right, bottom, paint)
        canvas.drawLine(left, bottom, right, top, paint)

        // Central diamond (rhombus connecting midpoints)
        val path = Path().apply {
            moveTo(centerX, top)
            lineTo(right, centerY)
            lineTo(centerX, bottom)
            lineTo(left, centerY)
            close()
        }
        canvas.drawPath(path, paint)

        // Draw house signs and planets
        paint.style = Paint.Style.FILL

        // Center coordinates of each of the 12 houses in North Indian layout
        val houseCoords = listOf(
            Pair(centerX, centerY - half * 0.45f),      // H1 (Top Diamond)
            Pair(centerX - half * 0.45f, top + half * 0.25f), // H2
            Pair(left + half * 0.25f, top + half * 0.45f),    // H3
            Pair(centerX - half * 0.45f, centerY),           // H4 (Left Diamond)
            Pair(left + half * 0.25f, bottom - half * 0.45f), // H5
            Pair(centerX - half * 0.45f, bottom - half * 0.25f), // H6
            Pair(centerX, centerY + half * 0.45f),      // H7 (Bottom Diamond)
            Pair(centerX + half * 0.45f, bottom - half * 0.25f), // H8
            Pair(right - half * 0.25f, bottom - half * 0.45f),// H9
            Pair(centerX + half * 0.45f, centerY),           // H10 (Right Diamond)
            Pair(right - half * 0.25f, top + half * 0.45f),   // H11
            Pair(centerX + half * 0.45f, top + half * 0.25f)  // H12
        )

        for (i in 0 until 12) {
            val (hx, hy) = houseCoords[i]
            val houseData = houses.getOrElse(i) { null } ?: continue

            // Sign number
            paint.color = Color.rgb(180, 120, 20)
            paint.textSize = 8.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("${houseData.sign.signNumber}", hx, hy - 4f, paint)

            // Planets abbreviation list
            if (houseData.planets.isNotEmpty()) {
                paint.color = Color.rgb(20, 20, 20)
                paint.textSize = 6.5f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                val pStr = houseData.planets.joinToString(" ") { p ->
                    val abbr = when (p.planet) {
                        Planet.SUN -> "Su"
                        Planet.MOON -> "Mo"
                        Planet.MARS -> "Ma"
                        Planet.MERCURY -> "Me"
                        Planet.JUPITER -> "Ju"
                        Planet.VENUS -> "Ve"
                        Planet.SATURN -> "Sa"
                        Planet.RAHU -> "Ra"
                        Planet.KETU -> "Ke"
                        Planet.ASCENDANT -> "Lg"
                    }
                    if (p.isRetrograde) "$abbr(R)" else abbr
                }
                canvas.drawText(pStr, hx, hy + 8f, paint)
            }
        }
    }

    private fun saveToPublicDownloads(context: Context, sourceFile: File, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        sourceFile.inputStream().use { input ->
                            input.copyTo(out)
                        }
                    }
                    Log.i(TAG, "Saved copy to Public MediaStore Downloads: $uri")
                }
            } catch (e: Exception) {
                Log.w(TAG, "MediaStore copy skipped: ${e.message}")
            }
        }
    }

    fun openOrSharePdf(context: Context, pdfFile: File) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val fileUri: Uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Test if any PDF viewer is available, else launch share intent
            val chooser = Intent.createChooser(viewIntent, "Kundli PDF Kholein ya Share Karein").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch PDF viewer, falling back to share", e)
            sharePdf(context, pdfFile)
        }
    }

    fun sharePdf(context: Context, pdfFile: File) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val fileUri: Uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "Vedic Kundli Report - ${pdfFile.name}")
                putExtra(Intent.EXTRA_TEXT, "Namaste, yeh rahi aapki shuddh Vedic Kundli Report (Kundli Jyotish App dwara pradaan ki gayi).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Kundli PDF Share Karein").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "Share PDF failed", e)
        }
    }
}
