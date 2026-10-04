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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.astrology.calc.HouseData

@Composable
fun NorthIndianChart(
    houses: List<HouseData>,
    selectedHouse: Int?,
    onHouseSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .testTag("north_indian_chart")
    ) {
        val sizePx = constraints.maxWidth.toFloat()

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(houses) {
                    detectTapGestures { offset ->
                        val clickedHouse = findClickedHouseNorth(offset, sizePx)
                        if (clickedHouse in 1..12) {
                            onHouseSelected(clickedHouse)
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // Outer border
            drawRect(
                color = Color(0xFFFFD54F),
                topLeft = Offset(0f, 0f),
                size = size,
                style = Stroke(width = 3.dp.toPx())
            )

            // Outer background subtle tint
            drawRect(
                color = Color(0x15FFD54F),
                topLeft = Offset(0f, 0f),
                size = size
            )

            // Diagonals (X)
            drawLine(
                color = Color(0xFFFFD54F),
                start = Offset(0f, 0f),
                end = Offset(w, h),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color(0xFFFFD54F),
                start = Offset(w, 0f),
                end = Offset(0f, h),
                strokeWidth = 2.dp.toPx()
            )

            // Inner Diamond (connecting midpoints)
            val diamondPath = Path().apply {
                moveTo(w / 2f, 0f)
                lineTo(w, h / 2f)
                lineTo(w / 2f, h)
                lineTo(0f, h / 2f)
                close()
            }
            drawPath(
                path = diamondPath,
                color = Color(0xFFFFD54F),
                style = Stroke(width = 2.dp.toPx())
            )

            // Highlight selected house if any
            if (selectedHouse != null && selectedHouse in 1..12) {
                val highlightPath = getHousePath(selectedHouse, w, h)
                drawPath(
                    path = highlightPath,
                    color = Color(0x40FFC107)
                )
            }

            // Approximate center points for 12 houses in North Indian system
            val houseCenters = listOf(
                Offset(w * 0.50f, h * 0.25f), // 1st House (Lagna diamond)
                Offset(w * 0.25f, h * 0.12f), // 2nd House
                Offset(w * 0.12f, h * 0.25f), // 3rd House
                Offset(w * 0.25f, h * 0.50f), // 4th House (Left diamond)
                Offset(w * 0.12f, h * 0.75f), // 5th House
                Offset(w * 0.25f, h * 0.88f), // 6th House
                Offset(w * 0.50f, h * 0.75f), // 7th House (Bottom diamond)
                Offset(w * 0.75f, h * 0.88f), // 8th House
                Offset(w * 0.88f, h * 0.75f), // 9th House
                Offset(w * 0.75f, h * 0.50f), // 10th House (Right diamond)
                Offset(w * 0.88f, h * 0.25f), // 11th House
                Offset(w * 0.75f, h * 0.12f)  // 12th House
            )

            // Draw content inside each of the 12 houses
            for (i in 0 until 12) {
                val houseNumber = i + 1
                val house = houses.getOrNull(i) ?: continue
                val center = houseCenters[i]

                // Sign Number (e.g. 1 for Aries, 4 for Cancer)
                val signText = "${house.sign.signNumber}"
                val signMeasured = textMeasurer.measure(
                    text = signText,
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFE082)
                    )
                )
                // Draw sign number offset slightly above center
                drawText(
                    textMeasurer = textMeasurer,
                    text = signText,
                    topLeft = Offset(center.x - signMeasured.size.width / 2f, center.y - 18.dp.toPx()),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFCA28)
                    )
                )

                // Planets in this house
                val planets = house.planets
                if (planets.isNotEmpty()) {
                    var yOffset = center.y - 2.dp.toPx()
                    for (p in planets.take(4)) {
                        val retroMarker = if (p.isRetrograde) "(R)" else ""
                        val pText = "${p.planet.symbol}$retroMarker"
                        val pMeasured = textMeasurer.measure(
                            text = pText,
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = p.planet.color
                            )
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = pText,
                            topLeft = Offset(center.x - pMeasured.size.width / 2f, yOffset),
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = p.planet.color
                            )
                        )
                        yOffset += 11.dp.toPx()
                    }
                    if (planets.size > 4) {
                        val moreText = "+${planets.size - 4}"
                        val mMeasured = textMeasurer.measure(
                            text = moreText,
                            style = TextStyle(fontSize = 9.sp, color = Color.White)
                        )
                        drawText(
                            textMeasurer = textMeasurer,
                            text = moreText,
                            topLeft = Offset(center.x - mMeasured.size.width / 2f, yOffset),
                            style = TextStyle(fontSize = 9.sp, color = Color.White)
                        )
                    }
                }
            }
        }
    }
}

private fun getHousePath(house: Int, w: Float, h: Float): Path {
    return Path().apply {
        when (house) {
            1 -> { // Top Diamond
                moveTo(w / 2f, 0f)
                lineTo(w * 0.75f, h * 0.25f)
                lineTo(w / 2f, h / 2f)
                lineTo(w * 0.25f, h * 0.25f)
                close()
            }
            2 -> { // Upper Left Triangle
                moveTo(0f, 0f)
                lineTo(w / 2f, 0f)
                lineTo(w * 0.25f, h * 0.25f)
                close()
            }
            3 -> { // Left Top Triangle
                moveTo(0f, 0f)
                lineTo(w * 0.25f, h * 0.25f)
                lineTo(0f, h / 2f)
                close()
            }
            4 -> { // Left Diamond
                moveTo(0f, h / 2f)
                lineTo(w * 0.25f, h * 0.25f)
                lineTo(w / 2f, h / 2f)
                lineTo(w * 0.25f, h * 0.75f)
                close()
            }
            5 -> { // Left Bottom Triangle
                moveTo(0f, h / 2f)
                lineTo(w * 0.25f, h * 0.75f)
                lineTo(0f, h)
                close()
            }
            6 -> { // Bottom Left Triangle
                moveTo(0f, h)
                lineTo(w * 0.25f, h * 0.75f)
                lineTo(w / 2f, h)
                close()
            }
            7 -> { // Bottom Diamond
                moveTo(w / 2f, h / 2f)
                lineTo(w * 0.75f, h * 0.75f)
                lineTo(w / 2f, h)
                lineTo(w * 0.25f, h * 0.75f)
                close()
            }
            8 -> { // Bottom Right Triangle
                moveTo(w / 2f, h)
                lineTo(w * 0.75f, h * 0.75f)
                lineTo(w, h)
                close()
            }
            9 -> { // Right Bottom Triangle
                moveTo(w, h / 2f)
                lineTo(w, h)
                lineTo(w * 0.75f, h * 0.75f)
                close()
            }
            10 -> { // Right Diamond
                moveTo(w / 2f, h / 2f)
                lineTo(w * 0.75f, h * 0.25f)
                lineTo(w, h / 2f)
                lineTo(w * 0.75f, h * 0.75f)
                close()
            }
            11 -> { // Right Top Triangle
                moveTo(w * 0.75f, h * 0.25f)
                lineTo(w, 0f)
                lineTo(w, h / 2f)
                close()
            }
            12 -> { // Upper Right Triangle
                moveTo(w / 2f, 0f)
                lineTo(w, 0f)
                lineTo(w * 0.75f, h * 0.25f)
                close()
            }
        }
    }
}

private fun findClickedHouseNorth(offset: Offset, size: Float): Int {
    val x = offset.x / size
    val y = offset.y / size

    // Normalize coordinates 0..1
    return when {
        // Central 4 diamonds (Kendras)
        x in 0.25f..0.75f && y in 0.0f..0.5f && (y <= x) && (y <= 1f - x) && (y >= 0.5f - x) && (y >= x - 0.5f) -> 1
        x in 0.0f..0.5f && y in 0.25f..0.75f && (x <= y) && (x <= 1f - y) && (x >= 0.5f - y) && (x >= y - 0.5f) -> 4
        x in 0.25f..0.75f && y in 0.5f..1.0f && (y >= x) && (y >= 1f - x) && (y <= 1.5f - x) && (y <= x + 0.5f) -> 7
        x in 0.5f..1.0f && y in 0.25f..0.75f && (x >= y) && (x >= 1f - y) && (x <= 1.5f - y) && (x <= y + 0.5f) -> 10

        // Corner/edge triangles
        x < 0.5f && y < 0.5f -> if (y < x) 2 else 3
        x < 0.5f && y >= 0.5f -> if (1f - y > x) 5 else 6
        x >= 0.5f && y >= 0.5f -> if (y > x) 8 else 9
        else -> if (1f - x > y) 12 else 11
    }
}
