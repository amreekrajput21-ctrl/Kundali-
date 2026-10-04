package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kundli_profiles")
data class KundliProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String = "Male",
    val birthDate: String, // YYYY-MM-DD
    val birthTime: String, // HH:mm
    val birthPlace: String, // City name
    val latitude: Double,
    val longitude: Double,
    val timezoneOffset: Double = 5.5, // Default Indian Standard Time (UTC+5:30)
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class CityLocation(
    val name: String,
    val stateOrCountry: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: Double
)

object StandardCities {
    val list = listOf(
        CityLocation("New Delhi", "India", 28.6139, 77.2090, 5.5),
        CityLocation("Mumbai", "Maharashtra, India", 19.0760, 72.8777, 5.5),
        CityLocation("Bengaluru", "Karnataka, India", 12.9716, 77.5946, 5.5),
        CityLocation("Kolkata", "West Bengal, India", 22.5726, 88.3639, 5.5),
        CityLocation("Chennai", "Tamil Nadu, India", 13.0827, 80.2707, 5.5),
        CityLocation("Hyderabad", "Telangana, India", 17.3850, 78.4867, 5.5),
        CityLocation("Ahmedabad", "Gujarat, India", 23.0225, 72.5714, 5.5),
        CityLocation("Varanasi", "Uttar Pradesh, India", 25.3176, 82.9739, 5.5),
        CityLocation("Jaipur", "Rajasthan, India", 26.9124, 75.7873, 5.5),
        CityLocation("Pune", "Maharashtra, India", 18.5204, 73.8567, 5.5),
        CityLocation("Lucknow", "Uttar Pradesh, India", 26.8467, 80.9462, 5.5),
        CityLocation("Chandigarh", "Punjab/Haryana, India", 30.7333, 76.7794, 5.5),
        CityLocation("Haridwar", "Uttarakhand, India", 29.9457, 78.1642, 5.5),
        CityLocation("Patna", "Bihar, India", 25.5941, 85.1376, 5.5),
        CityLocation("Bhopal", "Madhya Pradesh, India", 23.2599, 77.4126, 5.5),
        CityLocation("London", "United Kingdom", 51.5074, -0.1278, 0.0),
        CityLocation("New York", "USA", 40.7128, -74.0060, -5.0),
        CityLocation("San Francisco", "USA", 37.7749, -122.4194, -8.0),
        CityLocation("Dubai", "United Arab Emirates", 25.2048, 55.2708, 4.0),
        CityLocation("Toronto", "Canada", 43.6532, -79.3832, -5.0),
        CityLocation("Sydney", "Australia", -33.8688, 151.2093, 10.0),
        CityLocation("Singapore", "Singapore", 1.3521, 103.8198, 8.0),
        CityLocation("Kathmandu", "Nepal", 27.7172, 85.3240, 5.75)
    )

    val defaultSampleProfile = KundliProfile(
        id = 1,
        name = "Aarav Sharma",
        gender = "Male",
        birthDate = "1998-07-15",
        birthTime = "10:30",
        birthPlace = "New Delhi, India",
        latitude = 28.6139,
        longitude = 77.2090,
        timezoneOffset = 5.5,
        isDefault = true
    )
}
