package com.example.data.location

import android.util.Log
import com.example.data.model.CityLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

object GeocodingService {
    private const val TAG = "GeocodingService"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    // Preloaded geographic database covering Jammu & Kashmir districts and major regional/remote towns across India & World
    private val offlineLocations = listOf(
        // Jammu & Kashmir Towns & Districts
        CityLocation("Ramban", "Jammu and Kashmir, India", 33.2427, 75.2415, 5.5),
        CityLocation("Jammu", "Jammu and Kashmir, India", 32.7266, 74.8570, 5.5),
        CityLocation("Srinagar", "Jammu and Kashmir, India", 34.0837, 74.7973, 5.5),
        CityLocation("Udhampur", "Jammu and Kashmir, India", 32.9262, 75.1416, 5.5),
        CityLocation("Doda", "Jammu and Kashmir, India", 33.1458, 75.5463, 5.5),
        CityLocation("Kishtwar", "Jammu and Kashmir, India", 33.3129, 75.7663, 5.5),
        CityLocation("Anantnag", "Jammu and Kashmir, India", 33.7311, 75.1522, 5.5),
        CityLocation("Baramulla", "Jammu and Kashmir, India", 34.2008, 74.3475, 5.5),
        CityLocation("Kathua", "Jammu and Kashmir, India", 32.3734, 75.5186, 5.5),
        CityLocation("Rajouri", "Jammu and Kashmir, India", 33.3716, 74.3094, 5.5),
        CityLocation("Poonch", "Jammu and Kashmir, India", 33.7667, 74.1000, 5.5),
        CityLocation("Reasi", "Jammu and Kashmir, India", 33.0827, 74.8322, 5.5),
        CityLocation("Samba", "Jammu and Kashmir, India", 32.5594, 75.1189, 5.5),
        CityLocation("Pulwama", "Jammu and Kashmir, India", 33.8741, 74.8988, 5.5),
        CityLocation("Kupwara", "Jammu and Kashmir, India", 34.5262, 74.2546, 5.5),
        CityLocation("Kulgam", "Jammu and Kashmir, India", 33.6454, 75.0163, 5.5),
        CityLocation("Shopian", "Jammu and Kashmir, India", 33.7208, 74.8319, 5.5),
        CityLocation("Ganderbal", "Jammu and Kashmir, India", 34.2165, 74.7811, 5.5),
        CityLocation("Bandipora", "Jammu and Kashmir, India", 34.4172, 74.6468, 5.5),
        CityLocation("Budgam", "Jammu and Kashmir, India", 34.0150, 74.7214, 5.5),
        CityLocation("Katras (Vaishno Devi)", "Jammu and Kashmir, India", 32.9917, 74.9317, 5.5),
        CityLocation("Banihal", "Ramban, Jammu and Kashmir, India", 33.4939, 75.1983, 5.5),
        CityLocation("Batote", "Ramban, Jammu and Kashmir, India", 33.1611, 75.3194, 5.5),
        CityLocation("Gool", "Ramban, Jammu and Kashmir, India", 33.2500, 74.9667, 5.5),
        CityLocation("Leh", "Ladakh, India", 34.1526, 77.5771, 5.5),
        CityLocation("Kargil", "Ladakh, India", 34.5539, 76.1349, 5.5),

        // Himachal Pradesh & Northern Hill Towns
        CityLocation("Shimla", "Himachal Pradesh, India", 31.1048, 77.1734, 5.5),
        CityLocation("Manali", "Himachal Pradesh, India", 32.2432, 77.1892, 5.5),
        CityLocation("Dharamshala", "Himachal Pradesh, India", 32.2190, 76.3234, 5.5),
        CityLocation("Kullu", "Himachal Pradesh, India", 31.9579, 77.1095, 5.5),
        CityLocation("Dehradun", "Uttarakhand, India", 30.3165, 78.0322, 5.5),
        CityLocation("Haridwar", "Uttarakhand, India", 29.9457, 78.1642, 5.5),
        CityLocation("Rishikesh", "Uttarakhand, India", 30.0869, 78.2676, 5.5),
        CityLocation("Nainital", "Uttarakhand, India", 29.3919, 79.4542, 5.5),

        // Major Metros & Regional Capitals
        CityLocation("New Delhi", "Delhi, India", 28.6139, 77.2090, 5.5),
        CityLocation("Mumbai", "Maharashtra, India", 19.0760, 72.8777, 5.5),
        CityLocation("Bengaluru", "Karnataka, India", 12.9716, 77.5946, 5.5),
        CityLocation("Kolkata", "West Bengal, India", 22.5726, 88.3639, 5.5),
        CityLocation("Chennai", "Tamil Nadu, India", 13.0827, 80.2707, 5.5),
        CityLocation("Hyderabad", "Telangana, India", 17.3850, 78.4867, 5.5),
        CityLocation("Ahmedabad", "Gujarat, India", 23.0225, 72.5714, 5.5),
        CityLocation("Varanasi", "Uttar Pradesh, India", 25.3176, 82.9739, 5.5),
        CityLocation("Jaipur", "Rajasthan, India", 26.9124, 75.7873, 5.5),
        CityLocation("Lucknow", "Uttar Pradesh, India", 26.8467, 80.9462, 5.5),
        CityLocation("Chandigarh", "Punjab/Haryana, India", 30.7333, 76.7794, 5.5),
        CityLocation("Amritsar", "Punjab, India", 31.6340, 74.8723, 5.5),
        CityLocation("Ludhiana", "Punjab, India", 30.9010, 75.8573, 5.5),
        CityLocation("Patna", "Bihar, India", 25.5941, 85.1376, 5.5),
        CityLocation("Gaya", "Bihar, India", 24.7955, 85.0002, 5.5),
        CityLocation("Bhopal", "Madhya Pradesh, India", 23.2599, 77.4126, 5.5),
        CityLocation("Indore", "Madhya Pradesh, India", 22.7196, 75.8577, 5.5),
        CityLocation("Ujjain", "Madhya Pradesh, India", 23.1765, 75.7885, 5.5),
        CityLocation("Pune", "Maharashtra, India", 18.5204, 73.8567, 5.5),
        CityLocation("Nagpur", "Maharashtra, India", 21.1458, 79.0882, 5.5),
        CityLocation("Nashik", "Maharashtra, India", 19.9975, 73.7898, 5.5),
        CityLocation("Ayodhya", "Uttar Pradesh, India", 26.7922, 82.1998, 5.5),
        CityLocation("Mathura", "Uttar Pradesh, India", 27.4924, 77.6737, 5.5),
        CityLocation("Prayagraj", "Uttar Pradesh, India", 25.4358, 81.8463, 5.5),
        CityLocation("Guwahati", "Assam, India", 26.1445, 91.7362, 5.5),
        CityLocation("Bhubaneswar", "Odisha, India", 20.2961, 85.8245, 5.5),
        CityLocation("Puri", "Odisha, India", 19.8135, 85.8312, 5.5),

        // International Cities
        CityLocation("London", "United Kingdom", 51.5074, -0.1278, 0.0),
        CityLocation("New York", "USA", 40.7128, -74.0060, -5.0),
        CityLocation("San Francisco", "USA", 37.7749, -122.4194, -8.0),
        CityLocation("Dubai", "United Arab Emirates", 25.2048, 55.2708, 4.0),
        CityLocation("Toronto", "Canada", 43.6532, -79.3832, -5.0),
        CityLocation("Sydney", "Australia", -33.8688, 151.2093, 10.0),
        CityLocation("Singapore", "Singapore", 1.3521, 103.8198, 8.0),
        CityLocation("Kathmandu", "Nepal", 27.7172, 85.3240, 5.75)
    )

    /**
     * Search coordinates for any city/town.
     * First queries OpenStreetMap Nominatim API for real-time worldwide coordinates.
     * Seamlessly merges and falls back to the rich local dataset.
     */
    suspend fun searchCoordinates(query: String): List<CityLocation> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return@withContext offlineLocations.take(15)

        val results = mutableListOf<CityLocation>()

        // 1. Check local preloaded database for exact or partial matches
        val localMatches = offlineLocations.filter {
            it.name.contains(trimmed, ignoreCase = true) ||
            it.stateOrCountry.contains(trimmed, ignoreCase = true)
        }
        results.addAll(localMatches)

        // 2. Fetch from OpenStreetMap Nominatim Geocoding API
        try {
            val encodedQuery = URLEncoder.encode(trimmed, "UTF-8")
            val url = "https://nominatim.openstreetmap.org/search?q=$encodedQuery&format=json&addressdetails=1&limit=6"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "KundliJyotishAndroidApp/1.0 (Astrology)")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string() ?: ""
                val jsonArray = JSONArray(jsonStr)

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val displayName = item.optString("display_name", "")
                    val lat = item.optDouble("lat", 0.0)
                    val lon = item.optDouble("lon", 0.0)

                    if (lat != 0.0 && lon != 0.0 && displayName.isNotBlank()) {
                        val parts = displayName.split(",")
                        val mainName = parts.firstOrNull()?.trim() ?: trimmed
                        val stateOrCountry = parts.drop(1).take(2).joinToString(", ") { it.trim() }

                        // Estimate timezone offset based on longitude if in India (~5.5) or standard lon/15
                        val tzOffset = if (lon in 68.0..97.0 && lat in 8.0..37.0) {
                            5.5 // Indian Standard Time
                        } else {
                            Math.round(lon / 15.0 * 2.0) / 2.0
                        }

                        val onlineCity = CityLocation(
                            name = mainName,
                            stateOrCountry = if (stateOrCountry.isNotBlank()) stateOrCountry else displayName,
                            latitude = Math.round(lat * 10000.0) / 10000.0,
                            longitude = Math.round(lon * 10000.0) / 10000.0,
                            timezone = tzOffset
                        )

                        // Avoid duplicate if already in local matches
                        if (results.none { Math.abs(it.latitude - onlineCity.latitude) < 0.05 && Math.abs(it.longitude - onlineCity.longitude) < 0.05 }) {
                            results.add(onlineCity)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Online geocoding lookup failed (using local database): ${e.message}")
        }

        return@withContext results
    }
}
