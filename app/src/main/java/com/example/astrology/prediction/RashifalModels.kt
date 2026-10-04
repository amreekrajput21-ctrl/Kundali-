package com.example.astrology.prediction

import com.example.astrology.calc.ZodiacSign

data class DailyRashifal(
    val rashi: ZodiacSign,
    val date: String,
    val headline: String,
    val summary: String,
    val careerPrediction: String,
    val lovePrediction: String,
    val wealthPrediction: String,
    val healthPrediction: String,
    val careerScore: Int, // 1 to 5
    val loveScore: Int,
    val wealthScore: Int,
    val healthScore: Int,
    val luckyNumber: Int,
    val luckyColor: String,
    val auspiciousTime: String,
    val positiveTip: String
)

data class PersonalizedPredictions(
    val lagnaSummary: String,
    val moonMindSummary: String,
    val careerAndProfession: String,
    val relationshipsAndMarriage: String,
    val wealthAndProsperity: String,
    val spiritualPath: String,
    val recommendedGemstone: String,
    val gemstoneBenefits: String,
    val favorableMantra: String,
    val currentDashaImpact: String
)
