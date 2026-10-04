package com.example.consultation.model

import androidx.compose.ui.graphics.Color

enum class ConsultationType(val title: String, val devanagari: String) {
    CHAT("Live Chat", "चैट परामर्श"),
    VIDEO("HD Video Call", "वीडियो परामर्श")
}

enum class BookingStatus {
    UPCOMING,
    LIVE_NOW,
    COMPLETED
}

enum class PaymentMethod(val title: String) {
    UPI("UPI (GPay / PhonePe / Paytm)"),
    CARD("Credit / Debit Card"),
    NET_BANKING("Net Banking"),
    WALLET("AstroPay Wallet")
}

data class Astrologer(
    val id: String,
    val name: String,
    val title: String,
    val avatarInitial: String,
    val avatarBgColor: Color,
    val experienceYears: Int,
    val specializations: List<String>,
    val languages: List<String>,
    val rating: Double,
    val totalConsultations: Int,
    val chatPricePerMin: Int, // in INR ₹
    val videoPricePerMin: Int,
    val isOnline: Boolean,
    val nextAvailableTime: String,
    val bio: String,
    val availableSlots: List<String> = listOf("Instant (Right Now)", "11:30 AM", "02:00 PM", "05:00 PM", "08:30 PM")
)

data class ConsultationBooking(
    val bookingId: String,
    val astrologer: Astrologer,
    val consultationType: ConsultationType,
    val durationMinutes: Int,
    val totalAmount: Double,
    val slotTime: String,
    val targetNativeName: String,
    val paymentMethod: PaymentMethod,
    val paymentTransactionId: String,
    val status: BookingStatus = BookingStatus.LIVE_NOW,
    val bookedAtTimestamp: Long = System.currentTimeMillis()
)

object AstrologerDirectory {
    val astrologers = listOf(
        Astrologer(
            id = "ast_1",
            name = "Acharya Ramesh Shastri",
            title = "Vedic Jyotish & Vastu Acharya",
            avatarInitial = "RS",
            avatarBgColor = Color(0xFFD97706),
            experienceYears = 22,
            specializations = listOf("Vedic Kundli", "Career & Wealth", "Prashna Kundli", "Dasha Remedies"),
            languages = listOf("Hindi", "English", "Sanskrit"),
            rating = 4.94,
            totalConsultations = 18450,
            chatPricePerMin = 25,
            videoPricePerMin = 45,
            isOnline = true,
            nextAvailableTime = "Available Now",
            bio = "Gold Medalist from Sampurnanand Sanskrit University, Varanasi. Specializes in accurate career milestones, business breakthroughs, and classical Parashari remedies."
        ),
        Astrologer(
            id = "ast_2",
            name = "Dr. Ananya Mukherjee",
            title = "PhD in Vedic Astrology & Nakshatra Vidya",
            avatarInitial = "AM",
            avatarBgColor = Color(0xFF7C3AED),
            experienceYears = 16,
            specializations = listOf("Marriage Matching", "Love & Soulmate", "Kundli Milan", "Gemstone Therapy"),
            languages = listOf("English", "Hindi", "Bengali"),
            rating = 4.96,
            totalConsultations = 14200,
            chatPricePerMin = 30,
            videoPricePerMin = 50,
            isOnline = true,
            nextAvailableTime = "Available Now",
            bio = "Expert in relationship compatibility, Navamsha D9 analysis, Manglik dosha remedies, and psychological astrological counseling with high precision."
        ),
        Astrologer(
            id = "ast_3",
            name = "Pandit Radhakrishna Joshi",
            title = "Nadi & KP Astrology Specialist",
            avatarInitial = "RJ",
            avatarBgColor = Color(0xFF059669),
            experienceYears = 28,
            specializations = listOf("KP System", "Nadi Jyotish", "Health & Longevity", "Litigation & Debts"),
            languages = listOf("Hindi", "Gujarati", "Marathi"),
            rating = 4.91,
            totalConsultations = 26500,
            chatPricePerMin = 35,
            videoPricePerMin = 60,
            isOnline = false,
            nextAvailableTime = "Today at 03:00 PM",
            bio = "Over 28 years of traditional lineage experience. Renowned for sub-lord KP calculations, precise event timing, and practical Vedic remedial measures."
        ),
        Astrologer(
            id = "ast_4",
            name = "Jyotishi Meera Balasubramanian",
            title = "South Indian Astrological Master",
            avatarInitial = "MB",
            avatarBgColor = Color(0xFFDB2777),
            experienceYears = 19,
            specializations = listOf("Jaimini Chara Dasha", "Foreign Travel", "Government Jobs", "Sade Sati"),
            languages = listOf("Tamil", "English", "Hindi"),
            rating = 4.89,
            totalConsultations = 11800,
            chatPricePerMin = 22,
            videoPricePerMin = 38,
            isOnline = true,
            nextAvailableTime = "Available Now",
            bio = "Deep expertise in Jaimini Karakamsha and D10 Dashamsha analysis. Dedicated to guiding natives through challenging Sade Sati and Rahu Mahadashas."
        ),
        Astrologer(
            id = "ast_5",
            name = "Acharya Devavrat Sharma",
            title = "Vedic Tantra & Shanti Vidhan Expert",
            avatarInitial = "DS",
            avatarBgColor = Color(0xFFDC2626),
            experienceYears = 14,
            specializations = listOf("Kaal Sarp Shanti", "Pitra Dosha", "Spiritual Healing", "Evil Eye Protection"),
            languages = listOf("Hindi", "Punjabi", "English"),
            rating = 4.93,
            totalConsultations = 9400,
            chatPricePerMin = 20,
            videoPricePerMin = 35,
            isOnline = true,
            nextAvailableTime = "Available Now",
            bio = "Practicing priest and Vedic scholar from Haridwar. Guidance on sacred yantras, Vedic havan, and planetary propitiation rituals."
        )
    )
}
