package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.astrology.calc.KundliChartData
import com.example.astrology.calc.Planet
import com.example.astrology.dosha.AllDoshasSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAstrologerService {
    private const val TAG = "GeminiAstrologer"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun askAstrologer(
        userQuery: String,
        chart: KundliChartData,
        doshas: AllDoshasSummary
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If API key is available, attempt real Gemini API call
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = buildSystemContext(chart, doshas)
                val fullPrompt = "$systemPrompt\n\nUser Question: $userQuery\n\nIMPORTANT: Answer completely in clear, natural Hinglish language (Romanized Hindi) according to ancient Vedic scriptures (Brihat Parashara Hora Shastra, Phaladeepika). Give practical Vedic upaays (remedies)."

                val jsonBody = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                put(JSONObject().put("text", fullPrompt))
                            }
                            put("parts", partsArray)
                        }
                        put(contentObj)
                    }
                    put("contents", contentsArray)

                    val genConfig = JSONObject().apply {
                        put("temperature", 0.7)
                        put("topP", 0.95)
                        put("maxOutputTokens", 1200)
                    }
                    put("generationConfig", genConfig)
                }

                val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("$BASE_URL?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    val root = JSONObject(responseStr)
                    val text = root.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text")

                    if (!text.isNullOrBlank()) {
                        return@withContext text
                    }
                } else {
                    Log.w(TAG, "Gemini API responded with status ${response.code}: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini call failed, switching to expert Vedic astrology engine: ${e.message}")
            }
        }

        // Expert Vedic Astrological Rule-Based Generator Fallback in Hinglish
        return@withContext generateExpertVedicResponse(userQuery, chart, doshas)
    }

    private fun buildSystemContext(chart: KundliChartData, doshas: AllDoshasSummary): String {
        return """
            You are 'Panditji', a wise, compassionate, and scholarly Vedic Astrologer (Jyotish Acharya) with deep knowledge of Brihat Parashara Hora Shastra, Jaimini Sutras, and ancient Hindu granthas.
            You MUST always reply completely in natural, polite Hinglish (Hindi written in English alphabet, e.g. 'Aapki kundli mein...', 'Shani dev ka ashirwad...', 'Upaay ke liye...').
            
            Here is the native's verified birth chart (Kundli) data:
            - Native Name: ${chart.profile.name}
            - Birth Date & Time: ${chart.profile.birthDate} at ${chart.profile.birthTime} (${chart.profile.birthPlace})
            - Ascendant (Lagna): ${chart.lagnaSign.sanskritName} (${chart.lagnaSign.englishName}) ruled by ${chart.lagnaSign.ruler.sanskritName}
            - Moon Sign (Rashi): ${chart.moonSign.sanskritName} (${chart.moonSign.englishName}), Nakshatra: ${chart.ascendant.nakshatra.name}
            - Sun Sign: ${chart.sunSign.sanskritName}
            - Current Vimshottari Mahadasha: ${chart.dashaResult.currentMahadasha.sanskritName}
            - Current Antardasha: ${chart.dashaResult.currentAntardasha.sanskritName} (until ${chart.dashaResult.antardashaEndDate})
            - Mangal Dosha: ${if (doshas.manglik.isManglik) "${doshas.manglik.severity.title} (Bhang: ${doshas.manglik.isCancelled})" else "Mukt"}
            - Kaal Sarp Dosha: ${if (doshas.kaalSarp.hasKaalSarp) doshas.kaalSarp.typeName else "Mukt"}
            - Shani Sade Sati: ${if (doshas.sadeSati.isUnderSadeSati) doshas.sadeSati.phase else if (doshas.sadeSati.isDhaiya) doshas.sadeSati.dhaiyaType else "Nahi Chal Rahi"}
        """.trimIndent()
    }

    fun generateExpertVedicResponse(
        query: String,
        chart: KundliChartData,
        doshas: AllDoshasSummary
    ): String {
        val q = query.lowercase()
        val lagna = chart.lagnaSign
        val moon = chart.moonSign
        val dasha = chart.dashaResult
        val tenth = chart.houses.first { it.houseNumber == 10 }
        val seventh = chart.houses.first { it.houseNumber == 7 }

        return when {
            q.contains("career") || q.contains("job") || q.contains("naukri") || q.contains("vyapaar") || q.contains("promotion") || q.contains("business") -> {
                """
                |**Vedic Career aur Karyakshetra Vishleshan (Karma Bhava)**
                |
                |Pranam ${chart.profile.name} ji. Aapki Janma Kundli mein 10th House (Karma Bhava) **${tenth.sign.sanskritName} (${tenth.sign.englishName})** rashi mein hai, jiske swami grah **${tenth.sign.ruler.sanskritName} Dev** hain.
                |
                |• **Karyakshetra ki Taqat:** ${lagna.sanskritName} Lagna aur ${moon.sanskritName} Chandra rashi ke anusaar, aapke andar ${if (tenth.sign.ruler in listOf(Planet.SUN, Planet.MARS)) "leadership, administrative control, engineering ya unche pad par kaam karne" else if (tenth.sign.ruler in listOf(Planet.MERCURY, Planet.JUPITER)) "planning, consulting, finance, data ya shiksha kshetra mein" else "creativity, negotiation aur networking mein"} adbhut kshamata hai.
                |• **Dasha ka Samay:** Vartaman mein aapki Kundli mein **${dasha.currentMahadasha.sanskritName} ki Mahadasha** aur **${dasha.currentAntardasha.sanskritName} ki Antardasha** chal rahi hai. Agle kuch mahino mein (${dasha.antardashaEndDate} tak) career mein naya mod aur badhotari ke yoga ban rahe hain.
                |
                |• **Career ke liye Shubh Vedic Upaay:**
                |1. Roz subah taambe ke lote se Surya Dev ko jal aur thoda laal chandan arpit karein.
                |2. Gayatri Mantra ka 21 ya 108 baar jaap karein.
                |3. Apne kaam ki table par thoda peela chandan ya chhota brass elephant rakhein.
                """.trimMargin()
            }
            q.contains("marriage") || q.contains("shadi") || q.contains("spouse") || q.contains("patni") || q.contains("pati") || q.contains("love") || q.contains("relationship") -> {
                val manglikStatus = if (doshas.manglik.isManglik) {
                    if (doshas.manglik.isCancelled) "Aapki Kundli mein Manglik prabhav hai par shastron ke anusaar yeh dosh bhang ho chuka hai."
                    else "Mangal Dev House ${doshas.manglik.marsHouseFromLagna} mein hain, jisse rishton mein thoda dhairya aur samajhdaari zaroori hai."
                } else "Aapki Kundli poori tarah se Mangal Dosh se mukt hai."

                """
                |**Vaivahik Jeevan aur Rishto ka Jyotish Vishleshan (7th House)**
                |
                |Pranam ${chart.profile.name} ji. Aapki Kundli ka 7th House (Jeevansathi Bhava) **${seventh.sign.sanskritName} (${seventh.sign.englishName})** rashi mein sthit hai, jiske swami **${seventh.sign.ruler.sanskritName} Dev** hain.
                |
                |• **Jeevansathi ka Swabhav:** Aapka jeevansathi ${seventh.sign.sanskritName} gunon se yukt hoga—samajhdar, aadarshvadi, parivarik aur aapka sath nibhane wala.
                |• **Mangal Dosh Sthiti:** $manglikStatus
                |• **Rishte mein Shanti:** Aapki ${moon.sanskritName} rashi hai, isliye aapsi baatcheet mein krodh se bachein aur vishwas banaye rakhein.
                |
                |• **Vaivahik Sukh ke liye Shubh Vedic Upaay:**
                |1. Somvaar ya Shukravaar ko Shiv-Parvati ji ke mandir mein kacha doodh aur jal arpit karein.
                |2. Chandi ka chhota chhalla ya kangan dharan karein jisse man shant rahe.
                |3. Shukravaar ko kheer banakar Mata Lakshmi ko bhog lagayein.
                """.trimMargin()
            }
            q.contains("gemstone") || q.contains("stone") || q.contains("ratna") || q.contains("anguthi") -> {
                val (stone, finger, day) = when (lagna.ruler) {
                    Planet.MARS -> Triple("Laal Moonga (Red Coral)", "Anamika (Ring finger)", "Mangalvaar")
                    Planet.VENUS -> Triple("Heera ya Safed Pukhraj / Zircon", "Madhyama ya Anamika", "Shukravaar")
                    Planet.MERCURY -> Triple("Zambian Panna (Emerald)", "Kanishtha (Little finger)", "Budhvaar")
                    Planet.MOON -> Triple("Asli Moti (South Sea Pearl)", "Kanishtha (Little finger)", "Somvaar")
                    Planet.SUN -> Triple("Manikya (Burmese Ruby)", "Anamika (Ring finger)", "Ravivaar")
                    Planet.JUPITER -> Triple("Peela Pukhraj (Ceylon Yellow Sapphire)", "Tarjani (Index finger)", "Guruvaar")
                    Planet.SATURN -> Triple("Neelam (Blue Sapphire) ya Katela (Amethyst)", "Madhyama (Middle finger)", "Shanivaar")
                    else -> Triple("Asli Moti", "Kanishtha", "Somvaar")
                }

                """
                |**Vedic Ratna (Gemstone) Paramarsh**
                |
                |Aapke **${lagna.sanskritName} (${lagna.englishName}) Lagna** ke anusaar, aapke Lagnesh **${lagna.ruler.sanskritName} Dev** hain.
                |
                |• **Aapka Mukhya Jeevan Ratna:** **$stone**
                |• **Dharan karne ki Dhatu (Metal):** ${if (lagna.ruler in listOf(Planet.SUN, Planet.MARS, Planet.JUPITER)) "Sona (Gold) ya Taamba (Copper)" else "Chandi (Silver) ya Panchdhatu"}
                |• **Kaunsi Ungli mein pehnein:** Daayein haath ki $finger mein
                |• **Dharan karne ka Shubh Din:** Shukla Paksha ka $day subah
                |
                |• **Ratna Dharan ke Fayde:**
                |Yeh ratna aapke aura aur aatm-bal ko majboot karega, bimariyon se bachayega aur grahon ke ashubh prabhav ko shant karke bhagya uday karega.
                """.trimMargin()
            }
            q.contains("dasha") || q.contains("mahadasha") || q.contains("antardasha") || q.contains("samay") -> {
                """
                |**Vimshottari Dasha Vishleshan**
                |
                |• **Chalti hui Mahadasha:** **${dasha.currentMahadasha.sanskritName} Mahadasha** (${dasha.mahadashaStartDate} se ${dasha.mahadashaEndDate} tak)
                |• **Chalti hui Antardasha:** **${dasha.currentAntardasha.sanskritName} Antardasha** (${dasha.antardashaEndDate} tak)
                |• **Cycle Progress:** Lagbhag ${(dasha.progressPercent * 100).toInt()}% samay poora ho chuka hai.
                |
                |• **Grah ka Asar:**
                |${dasha.currentMahadasha.sanskritName}-${dasha.currentAntardasha.sanskritName} kaal mein un gharon ki urja sakriya hoti hai jahan yeh grah virajman hain. Yeh samay aage badhne, mehnat karne aur naye raste talashne ke liye shreshth hai.
                |
                |• **Shubh Mantra:**
                |Roz subah 108 baar '${dasha.currentMahadasha.sanskritName} Dev ke Beej Mantra' ka jaap karein.
                """.trimMargin()
            }
            q.contains("sade sati") || q.contains("shani") || q.contains("dhaiya") -> {
                """
                |**Shani Sade Sati aur Shani Dev ka Gochar**
                |
                |• **Sade Sati Sthiti:** ${if (doshas.sadeSati.isUnderSadeSati) "Chal Rahi Hai - ${doshas.sadeSati.phase}" else if (doshas.sadeSati.isDhaiya) "Dhaiya Chal Rahi Hai - ${doshas.sadeSati.dhaiyaType}" else "Aap Sade Sati aur Dhaiya se poori tarah mukt hain"}
                |• **Aapki Chandra Rashi:** ${doshas.sadeSati.natalMoonSign.sanskritName}
                |• **Shani Dev ka Gochar:** ${doshas.sadeSati.currentTransitSaturnSign.sanskritName} Rashi mein
                |
                |• **Panditji ka Paramarsh:**
                |Shani Dev Nyayadheesh hain. Jo vyakti imaandari, mehnat, sachchai aur gareebon ki madad karta hai, Shani Dev uspar sabse zyada prasanna hote hain.
                |
                |• **Prabhavshali Vedic Upaay:**
                |1. Shanivaar shaam ko Peepal ke ped ke neeche sarson ke tel ka diya jalayein.
                |2. Dashrath Krit Shani Stotra ya Hanuman Chalisa ka roz paath karein.
                |3. Kaale til aur kaale kapde ka daan karein.
                """.trimMargin()
            }
            else -> {
                """
                |**${chart.profile.name} ji ke liye Sampurna Vedic Margdarshan**
                |
                |Aapki Kundli mein adbhut kshamata aur prathibha ke sanket hain.
                |
                |• **Lagna:** ${lagna.sanskritName} (Swami: ${lagna.ruler.sanskritName}) - Nidar aur aage badhne ka shauk.
                |• **Chandra Rashi:** ${moon.sanskritName} - Gahri soch aur kalpanashakti.
                |• **Chalti Dasha:** ${dasha.currentMahadasha.sanskritName} Mahadasha aur ${dasha.currentAntardasha.sanskritName} Antardasha.
                |• **Vedic Harmony Score:** ${doshas.overallHarmonyScore}/100 (Shubh).
                |
                |Aap **Career**, **Naukri/Business**, **Shadi/Vivah**, **Bhagya Ratna**, ya **Dasha ke Upaay** ke baare mein koi bhi sawal poochh sakte hain!
                """.trimMargin()
            }
        }
    }
}
