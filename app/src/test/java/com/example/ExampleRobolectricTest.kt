package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.astrology.calc.AstrologyEngine
import com.example.astrology.calc.Planet
import com.example.astrology.calc.ZodiacSign
import com.example.astrology.dosha.DoshaAnalyzer
import com.example.consultation.model.AstrologerDirectory
import com.example.data.model.StandardCities
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kundli Jyotish", appName)
    }

    @Test
    fun `verify astrology calculation engine`() {
        val profile = StandardCities.defaultSampleProfile
        val chart = AstrologyEngine.calculateKundli(profile)

        assertNotNull(chart)
        assertEquals(12, chart.houses.size)
        assertEquals(9, chart.planets.size)
        assertNotNull(chart.moonSign)
        assertNotNull(chart.lagnaSign)
        assertTrue(chart.ayanamshaDegrees > 23.0)

        val doshas = DoshaAnalyzer.analyzeAllDoshas(chart)
        assertNotNull(doshas)
        assertTrue(doshas.overallHarmonyScore in 0..100)
    }

    @Test
    fun `verify astrologers directory has verified profiles`() {
        val astrologers = AstrologerDirectory.astrologers
        assertTrue(astrologers.isNotEmpty())
        assertTrue(astrologers.any { it.isOnline })
        assertTrue(astrologers.all { it.chatPricePerMin > 0 && it.videoPricePerMin > 0 })
    }
}
