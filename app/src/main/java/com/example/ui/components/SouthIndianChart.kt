package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrology.calc.HouseData
import com.example.astrology.calc.PlanetaryPosition
import com.example.astrology.calc.ZodiacSign

@Composable
fun SouthIndianChart(
    houses: List<HouseData>,
    planets: List<PlanetaryPosition>,
    lagnaSign: ZodiacSign,
    selectedSign: ZodiacSign?,
    onSignSelected: (ZodiacSign) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    // Map 12 fixed positions in 4x4 grid (row, col) to ZodiacSign
    val gridMap = listOf(
        Triple(0, 0, ZodiacSign.PISCES),
        Triple(0, 1, ZodiacSign.ARIES),
        Triple(0, 2, ZodiacSign.TAURUS),
        Triple(0, 3, ZodiacSign.GEMINI),
        Triple(1, 3, ZodiacSign.CANCER),
        Triple(2, 3, ZodiacSign.LEO),
        Triple(3, 3, ZodiacSign.VIRGO),
        Triple(3, 2, ZodiacSign.LIBRA),
        Triple(3, 1, ZodiacSign.SCORPIO),
        Triple(3, 0, ZodiacSign.SAGITTARIUS),
        Triple(2, 0, ZodiacSign.CAPRICORN),
        Triple(1, 0, ZodiacSign.AQUARIUS)
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .testTag("south_indian_chart")
    ) {
        val sizePx = constraints.maxWidth.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(gridMap) {
                    detectTapGestures { offset ->
                        val cellW = sizePx / 4f
                        val cellH = sizePx / 4f
                        val col = (offset.x / cellW).toInt().coerceIn(0, 3)
                        val row = (offset.y / cellH).toInt().coerceIn(0, 3)

                        val match = gridMap.firstOrNull { it.first == row && it.second == col }
                        if (match != null) {
                            onSignSelected(match.third)
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val cellW = w / 4f
            val cellH = h / 4f

            // Outer Frame
            drawRect(
                color = Color(0xFFFFD54F),
                topLeft = Offset(0f, 0f),
                size = size,
                style = Stroke(width = 3.dp.toPx())
            )

            // Inner Center 2x2 Box
            drawRect(
                color = Color(0xFFFFD54F),
                topLeft = Offset(cellW, cellH),
                size = Size(cellW * 2f, cellH * 2f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Grid lines
            // Horizontal lines
            drawLine(Color(0xFFFFD54F), Offset(0f, cellH), Offset(cellW, cellH), 1.5.dp.toPx())
            drawLine(Color(0xFFFFD54F), Offset(cellW * 3f, cellH), Offset(w, cellH), 1.5.dp.toPx())
            drawLine(Color(0xFFFFD54F), Offset(0f, cellH * 2f), Offset(cellW, cellH * 2f), 1.5.dp.toPx())
            drawLine(Color(0xFFFFD54F), Offset(cellW * 3f, cellH * 2f), Offset(w, cellH * 2f), 1.5.dp.toPx())
            drawLine(Color(0xFFFFD54F), Offset(0f, cellH * 3f), Offset(w, cellH * 3f), 1.5.dp.toPx())
            drawLine(Color(0xFFFFD54F), Offset(0f, cellH * 1f), Offset(w, cellH * 1f), 1.5.dp.toPx())

            // Vertical lines
            drawLine(Color(0xFFFFD54F), Offset(cellW, 0f), Offset(cellW, h), 1.5.dp.toPx())
            drawLine(Color(0xFFFFD54F), Offset(cellW * 2f, 0f), Offset(cellW * 2f, cellH), 1.5.dp.toPx())
            drawLine(Color(0xFFFFD54F), Offset(cellW * 2f, cellH * 3f), Offset(cellW * 2f, h), 1.5.dp.toPx())
            drawLine(Color(0xFFFFD54F), Offset(cellW * 3f, 0f), Offset(cellW * 3f, h), 1.5.dp.toPx())

            // Draw center emblem / text
            val centerTitle = "Vedic Rashi Chakra"
            val centerMeasured = textMeasurer.measure(
                text = centerTitle,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFE082)
                )
            )
            drawText(
                textMeasurer = textMeasurer,
                text = centerTitle,
                topLeft = Offset(w / 2f - centerMeasured.size.width / 2f, h / 2f - 12.dp.toPx()),
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE082))
            )
            val lagnaLabel = "Lagna: ${lagnaSign.englishName}"
            val lagnaMeasured = textMeasurer.measure(
                text = lagnaLabel,
                style = TextStyle(fontSize = 11.sp, color = Color(0xFFFFB300))
            )
            drawText(
                textMeasurer = textMeasurer,
                text = lagnaLabel,
                topLeft = Offset(w / 2f - lagnaMeasured.size.width / 2f, h / 2f + 8.dp.toPx()),
                style = TextStyle(fontSize = 11.sp, color = Color(0xFFFFB300))
            )

            // Draw signs & occupants
            for ((row, col, sign) in gridMap) {
                val boxTopLeft = Offset(col * cellW, row * cellH)
                val isSelected = selectedSign == sign

                if (isSelected) {
                    drawRect(
                        color = Color(0x35FFC107),
                        topLeft = boxTopLeft,
                        size = Size(cellW, cellH)
                    )
                }

                // If Lagna sign, draw special mark (diagonal slash or ASC)
                val isLagna = sign == lagnaSign
                if (isLagna) {
                    drawLine(
                        color = Color(0xFFFF5252),
                        start = Offset(boxTopLeft.x, boxTopLeft.y),
                        end = Offset(boxTopLeft.x + cellW * 0.4f, boxTopLeft.y + cellH * 0.4f),
                        strokeWidth = 2.dp.toPx()
                    )
                }

                // Sign name tag in top corner
                val signTitle = "${sign.symbol} ${sign.englishName.take(3)}"
                val titleMeasured = textMeasurer.measure(
                    text = signTitle,
                    style = TextStyle(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLagna) Color(0xFFFF8A80) else Color(0xFFFFE082)
                    )
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = signTitle,
                    topLeft = Offset(boxTopLeft.x + 4.dp.toPx(), boxTopLeft.y + 3.dp.toPx()),
                    style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isLagna) Color(0xFFFF8A80) else Color(0xFFFFE082))
                )

                // Planets residing in this sign
                val signPlanets = planets.filter { it.sign == sign }
                var pY = boxTopLeft.y + 16.dp.toPx()

                if (isLagna) {
                    val ascText = "ASC"
                    val ascMeasured = textMeasurer.measure(
                        text = ascText,
                        style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = ascText,
                        topLeft = Offset(boxTopLeft.x + cellW - ascMeasured.size.width - 4.dp.toPx(), boxTopLeft.y + 3.dp.toPx()),
                        style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                    )
                }

                for (p in signPlanets.take(3)) {
                    val pText = "${p.planet.symbol}${if (p.isRetrograde) "(R)" else ""}"
                    drawText(
                        textMeasurer = textMeasurer,
                        text = pText,
                        topLeft = Offset(boxTopLeft.x + 6.dp.toPx(), pY),
                        style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = p.planet.color)
                    )
                    pY += 11.dp.toPx()
                }
                if (signPlanets.size > 3) {
                    val extra = "+${signPlanets.size - 3}"
                    drawText(
                        textMeasurer = textMeasurer,
                        text = extra,
                        topLeft = Offset(boxTopLeft.x + 6.dp.toPx(), pY),
                        style = TextStyle(fontSize = 9.sp, color = Color.White)
                    )
                }
            }
        }
    }
}
