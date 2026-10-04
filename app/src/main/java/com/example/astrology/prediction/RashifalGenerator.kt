package com.example.astrology.prediction

import com.example.astrology.calc.KundliChartData
import com.example.astrology.calc.Planet
import com.example.astrology.calc.ZodiacSign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RashifalGenerator {

    fun getTodayDateFormatted(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        return sdf.format(Date())
    }

    fun getAllDailyRashifals(date: String = getTodayDateFormatted()): List<DailyRashifal> {
        return ZodiacSign.entries.map { getDailyRashifal(it, date) }
    }

    fun getDailyRashifal(sign: ZodiacSign, date: String = getTodayDateFormatted()): DailyRashifal {
        return when (sign) {
            ZodiacSign.ARIES -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Naya Josh aur Leadership ka Mauka",
                summary = "Mangal Dev ki kripa se aaj aapka aatm-vishwas buland rahega. Ruke hue kaam tezi se poore honge. Karyakshetra mein log aapki baat dhyan se sunenge.",
                careerPrediction = "Naye project shuru karne aur boss ya senior se baat karne ke liye shreshth din hai. Aapki mehnat rang layegi.",
                lovePrediction = "Jeevansathi ya partner ke saath meetha samay beetega. Jo log single hain, unhe kisi khaas vyakti ka sandesh mil sakta hai.",
                wealthPrediction = "Purane nivesh ya business se achanak labh milne ke aasaar hain. Fizoolkharchi se bachein.",
                healthPrediction = "Shareer mein taazgi aur urja rahegi. Subah halka vyayam aur surya namaskar zaroor karein.",
                careerScore = 5,
                loveScore = 4,
                wealthScore = 4,
                healthScore = 5,
                luckyNumber = 9,
                luckyColor = "Laal (Red) / Kesariya (Saffron)",
                auspiciousTime = "09:15 AM - 11:30 AM",
                positiveTip = "Gusse se bachein aur sabhi ke sath vinamrata se pesh aayein."
            )
            ZodiacSign.TAURUS -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Dhan Labh aur Parivarik Khushiyan",
                summary = "Shukra Dev aapki rashi par meherban hain. Ghar-parivaar mein shanti aur prem bana rahega. Naye aarthik sadhan khulne ke yoga hain.",
                careerPrediction = "Naukri aur vyapaar mein thehraav aayega. Partners aur seniors ka poora sahyog milega.",
                lovePrediction = "Prem jeevan mein madhurta aayegi. Ek chhota sa surprise aapke rishte ko aur mazboot karega.",
                wealthPrediction = "Sampatti, sona ya naye samaan ki kharidari ke liye shubh din hai. Bank balance mein badhotari hogi.",
                healthPrediction = "Swasthya accha rahega. Thandi cheezon se parhez karein aur paani khoob piyein.",
                careerScore = 4,
                loveScore = 5,
                wealthScore = 5,
                healthScore = 4,
                luckyNumber = 6,
                luckyColor = "Safed (White) / Halka Hara (Green)",
                auspiciousTime = "02:00 PM - 03:45 PM",
                positiveTip = "Puraani baaton ko lekar chinta na karein, vartaman ka anand lein."
            )
            ZodiacSign.GEMINI -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Tez Buddhi aur Badi Kamyabi",
                summary = "Budh Dev ki kripa se aapki soch aur baat-cheet karne ki kala lajawab rahegi. Naye logon se sampark faaydemand saabit hoga.",
                careerPrediction = "Interview, meeting ya online business mein zabardast safalta milegi. Aapke vichaaron ki taareef hogi.",
                lovePrediction = "Dil kholkar baatein karein. Galatfehmiyan door hongi aur aapsi bharosa badhega.",
                wealthPrediction = "Ek se zyada jagah se aamadani ke raste banenge. Udhaar lene ya dene se bachein.",
                healthPrediction = "Dimaag par kaam ka bojh na lein. Thodi der dhyan (meditation) lagana laabhkari hoga.",
                careerScore = 5,
                loveScore = 4,
                wealthScore = 4,
                healthScore = 4,
                luckyNumber = 5,
                luckyColor = "Dhani Hara (Light Green) / Aasmani",
                auspiciousTime = "10:30 AM - 12:15 PM",
                positiveTip = "Ek samay par ek hi kaam par focus karein, dhyan bhatakne na dein."
            )
            ZodiacSign.CANCER -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Man ki Shanti aur Rishton mein Sanjeevani",
                summary = "Chandra Dev aapke hriday ko shanti aur gahri samajh de rahe hain. Parivaar ke sath samay beetega aur aatmik santushti milegi.",
                careerPrediction = "Workplace par aapka sahanubhuti-poorn vyavhar sabhi ka dil jeetega. Zid se bachein aur mil-jhulkar kaam karein.",
                lovePrediction = "Partner ke prati aapka samarpit bhav rishte mein nayi jaan daal dega. Bhavuk baatein dil ko chhu lengi.",
                wealthPrediction = "Ghar ki zarooraton par kharch ho sakta hai, par aamadani ke srot bane rahenge.",
                healthPrediction = "Pet ka dhyan rakhein. Tel-masale wale bhojan se bachein aur taza phal khayein.",
                careerScore = 4,
                loveScore = 5,
                wealthScore = 4,
                healthScore = 4,
                luckyNumber = 2,
                luckyColor = "Moti Safed (Pearl White) / Chandi",
                auspiciousTime = "07:30 AM - 09:00 AM",
                positiveTip = "Apni antaratma (gut feeling) ki aawaz sunein, faisla sahi niklega."
            )
            ZodiacSign.LEO -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Rajkiya Tej aur Bada Maan-Sammaan",
                summary = "Surya Dev ki roshni aapke vyaktitva ko nikharegi. Samaj aur karyakshetra mein aapka rutba badhega. Kathin kaam asani se ho jayenge.",
                careerPrediction = "Adhikariyon aur sarkari kshetra se sahayata milegi. Promotion ya naye rights milne ke yoga hain.",
                lovePrediction = "Jeevansathi aapki uplabdhiyon par garv karega. Prem mein aadar aur vishwas badhega.",
                wealthPrediction = "Aarthik sthiti majboot rahegi. Vyapaar mein naye contract sign ho sakte hain.",
                healthPrediction = "Aapka stamina shandaar rahega. Read ki haddi (spine) aur posture seedha rakhein.",
                careerScore = 5,
                loveScore = 4,
                wealthScore = 5,
                healthScore = 5,
                luckyNumber = 1,
                luckyColor = "Sunehra (Golden) / Naarangi (Orange)",
                auspiciousTime = "11:45 AM - 01:30 PM",
                positiveTip = "Ghamand aur ahankaar se door rahein, sabhi ko sath lekar chalein."
            )
            ZodiacSign.VIRGO -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Karyasiddhi aur Chhoti Samasyaon ka Hal",
                summary = "Budh Dev aapki barikiyon ko parakhne ki shamata ko badha rahe hain. Ulajhe hue maamle sulajh jayenge aur sukoon milega.",
                careerPrediction = "Accounts, audit, technical kaam ya research mein safalta milegi. Galatiyan pakadne mein aap maahir rahenge.",
                lovePrediction = "Chhoti-chhoti baaton par nuktacheeni na karein. Partner ki khubiyon ki taareef karein.",
                wealthPrediction = "Budget banakar chalein. Fizool ke kharche rukenge aur bachat mein vriddhi hogi.",
                healthPrediction = "Pachan tantra theek rahega. Subah gunguna paani peena laabhdayak rahega.",
                careerScore = 5,
                loveScore = 3,
                wealthScore = 4,
                healthScore = 4,
                luckyNumber = 5,
                luckyColor = "Mehendi Hara / Khaki",
                auspiciousTime = "03:15 PM - 05:00 PM",
                positiveTip = "Har cheez ko perfect banane ke chakkar mein khud ko stress na dein."
            )
            ZodiacSign.LIBRA -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Sammanjasya, Prem aur Kala ka Utsav",
                summary = "Shukra Dev aapke aakarshan aur kootneeti ko char chand laga rahe hain. Vivadon ko aapsi baatcheet se suljhane ke liye shreshth din hai.",
                careerPrediction = "Partnership aur media, design ya public dealing se jude logon ke liye zabardast din hai.",
                lovePrediction = "Prem sambandhon mein romance ka rang chhadega. Sham ko sath ghoomne ya dinner ka plan banega.",
                wealthPrediction = "Aamadani theek rahegi. Khubsurti aur shauk ki cheezon par santulit kharch karein.",
                healthPrediction = "Pani ki kami na hone dein. Gurde (kidney) ki sehat ke liye dhyan rakhein.",
                careerScore = 4,
                loveScore = 5,
                wealthScore = 4,
                healthScore = 4,
                luckyNumber = 7,
                luckyColor = "Gulaabi (Pink) / Neela Firoza",
                auspiciousTime = "12:30 PM - 02:15 PM",
                positiveTip = "Faisla lene mein der na karein; aatm-vishwas ke sath aage badhein."
            )
            ZodiacSign.SCORPIO -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Gahra Aatm-Bal aur Rahasyamayi Labh",
                summary = "Mangal aur Ketu aapko adbhut aantarik shakti de rahe hain. Kisi bhi chunauti ka samna aap nidar hokar karenge aur vijay payenge.",
                careerPrediction = "Gahri research, vigilance ya tough targets ko poora karne mein aap aage rahenge. Gupt shatru parajit honge.",
                lovePrediction = "Jazbaaton mein gahraai aayegi. Ek-doosre ke prati wafadaari aur sachchai rishte ko atoot banayegi.",
                wealthPrediction = "Puraane ruke hue dhan ki wapsi ke aasaar hain. Joint property se faayda ho sakta hai.",
                healthPrediction = "Mansik shanti ke liye thoda samay ekant mein bitayein aur Om ka ucharan karein.",
                careerScore = 4,
                loveScore = 4,
                wealthScore = 5,
                healthScore = 4,
                luckyNumber = 8,
                luckyColor = "Gehra Laal (Maroon) / Bhura",
                auspiciousTime = "01:00 PM - 02:45 PM",
                positiveTip = "Purani ranjishon ko bhoolakar aage badhein, dil halka rahega."
            )
            ZodiacSign.SAGITTARIUS -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Bhagya Uday aur Adhyatmik Khushi",
                summary = "Devguru Brihaspati aapko gyan, umeed aur naye avsar pradan kar rahe hain. Teerth darshan ya dharmiq karyon mein ruchi badhegi.",
                careerPrediction = "Education, coaching, law ya videshi vyapaar se jude logon ke liye laabhdayak samay hai. Nayi seekh milegi.",
                lovePrediction = "Aapka hasmukh andaz partner ka man moh lega. Jeevansathi ke sath dharmiq ya lamba safar sukhad rahega.",
                wealthPrediction = "Aarthik labh ke yoga hain. Kisi bujurg ya guru ke ashirwad se raste khulenge.",
                healthPrediction = "Swasthya uttam rahega. Halka vyayam aur pairon ki exercise zaroor karein.",
                careerScore = 5,
                loveScore = 4,
                wealthScore = 4,
                healthScore = 5,
                luckyNumber = 3,
                luckyColor = "Peela (Yellow) / Kesari",
                auspiciousTime = "08:30 AM - 10:15 AM",
                positiveTip = "Apne sapno ko chhota na karein; bade lakshya banayein aur mehnat karein."
            )
            ZodiacSign.CAPRICORN -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Thos Natije aur Mehnat ka Inaam",
                summary = "Shani Dev Maharaj aapki lagan aur anushasan se prasanna hain. Lambe samay se chal rahi mehnat ka meetha phal milne wala hai.",
                careerPrediction = "Karyakshetra mein pad aur zimmedari badh sakti hai. Sahkarmi aapki kabil-e-tareef mehnat ko manenge.",
                lovePrediction = "Rishto mein thehraav aur wafadaari sabse aage rahegi. Ek doosre ke career mein madad karenge.",
                wealthPrediction = "Dhan bachane mein safal rahenge. Long term investment ya fixed deposit ke liye shubh din hai.",
                healthPrediction = "Jodon aur ghutno ki dekhbhal karein. Thand se bachein aur stretch karein.",
                careerScore = 5,
                loveScore = 4,
                wealthScore = 5,
                healthScore = 4,
                luckyNumber = 8,
                luckyColor = "Navy Blue / Kaala (Black)",
                auspiciousTime = "04:00 PM - 05:45 PM",
                positiveTip = "Kaam ke sath thoda aaram bhi karein; overwork se thakaan ho sakti hai."
            )
            ZodiacSign.AQUARIUS -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Naye Idea aur Doston ka Sahyog",
                summary = "Shani Dev aapko door-drishti aur azaad vichaar de rahe hain. Samajik karyon aur group projects mein aapka naam aage aayega.",
                careerPrediction = "Tech, innovation, NGO ya startup ke liye shandar din hai. Out of the box thinking se faayda hoga.",
                lovePrediction = "Dostana prem sambandh banenge. Ek doosre ke vichaaron ka aadar karein aur azaadi dein.",
                wealthPrediction = "Achanak se aarthik madad mil sakti hai. Online kharidari mein thoda dhyan rakhein.",
                healthPrediction = "Rakt sanchar (circulation) theek rakhein. Sham ko halki tehalna behad faydemand rahega.",
                careerScore = 4,
                loveScore = 4,
                wealthScore = 4,
                healthScore = 4,
                luckyNumber = 4,
                luckyColor = "Electric Blue / Aasmani",
                auspiciousTime = "01:30 PM - 03:15 PM",
                positiveTip = "Apni nayi soch par vishwas rakhein, logon ki baaton se dhyan na bhatkayein."
            )
            ZodiacSign.PISCES -> DailyRashifal(
                rashi = sign,
                date = date,
                headline = "Dayalu Bhav, Sukoon aur Kalaatmak Labh",
                summary = "Guru Dev aapke hriday ko karuna aur kripa se bhar rahe hain. Kala, sangeet, kavita ya dharmiq vichaaron mein man lagega.",
                careerPrediction = "Hospital, hospitalities, creative arts ya counseling se jude logon ko samman milega. Bholenath ka ashirwad rahega.",
                lovePrediction = "Bina sharton ka prem aur sammohan bana rahega. Partner aapki bhavnaon ki qadr karega.",
                wealthPrediction = "Daan-punya aur shubh karyon par kharch hoga. Bhagya se dhan ka aagman hota rahega.",
                healthPrediction = "Neend poori lein aur shaam ko thoda samay sukoon se bitayein.",
                careerScore = 4,
                loveScore = 5,
                wealthScore = 4,
                healthScore = 4,
                luckyNumber = 3,
                luckyColor = "Haldi Peela / Pista Green",
                auspiciousTime = "06:00 AM - 07:45 AM",
                positiveTip = "Daan karne se aane wale dukh kat jaate hain; kisi zarooratmand ki madad karein."
            )
        }
    }

    fun generatePersonalizedPredictions(chart: KundliChartData): PersonalizedPredictions {
        val lagna = chart.lagnaSign
        val moon = chart.moonSign
        val sun = chart.sunSign
        val tenthHouse = chart.houses.first { it.houseNumber == 10 }
        val seventhHouse = chart.houses.first { it.houseNumber == 7 }
        val dasha = chart.dashaResult

        val lagnaText = "Aapka Lagna ${lagna.sanskritName} (${lagna.englishName}) hai, jiske swami grah ${lagna.ruler.sanskritName} hain. Aapka tatva ${lagna.element} hai. Is lagna ke vyakti nidar, mehnati aur aage badhne ka shauk rakhne wale hote hain. Aapka aatm-bal aur swasthya aapke jeevan ki sabse badi taqat hai."

        val moonText = "Aapki Janam Rashi ${moon.sanskritName} (${moon.englishName}) hai aur rashi swami ${moon.ruler.sanskritName} hain. Yeh aapke man, bhavnaon aur cognitive soch ko nirdharit karta hai. Aap andar se kafi ${if (moon.element.contains("Agni")) "joshile, sahasik aur swatantra" else if (moon.element.contains("Prithvi")) "vyavaharik, thehre hue aur parinam dene wale" else if (moon.element.contains("Vayu")) "buddhiman, baat-cheet ke shaukeen aur naye vichaaron wale" else "bhavuk, dayalu aur parivar se lagav rakhne wale"} vyakti hain."

        val careerText = "Aapka 10th House (Karma Bhava) ${tenthHouse.sign.sanskritName} rashi mein hai, jiske swami ${tenthHouse.sign.ruler.sanskritName} hain. ${
            if (tenthHouse.planets.isNotEmpty()) "Is ghar mein ${tenthHouse.planets.joinToString { it.planet.sanskritName }} virajman hain, jo aapko leadership, pad-pratishtha aur safalta dilaane mein madad karte hain."
            else "Yeh sthiti aapko management, vyapaar, sarkari seva ya tech & advisory kshetra mein uncha mukaam dila sakti hai."
        }"

        val marriageText = "Aapka 7th House (Jeevansathi Bhava) ${seventhHouse.sign.sanskritName} rashi mein padta hai jiske swami ${seventhHouse.sign.ruler.sanskritName} hain. Isse sanket milta hai ki aapka jeevansathi ${seventhHouse.sign.sanskritName} swabhav ka hoga—${
            when (seventhHouse.sign) {
                ZodiacSign.ARIES, ZodiacSign.SCORPIO -> "joshila, himmati, raksha karne wala aur aatm-nirbhar."
                ZodiacSign.TAURUS, ZodiacSign.LIBRA -> "sundar, sanskari, shantipriya aur prem-bhav se bhara."
                ZodiacSign.GEMINI, ZodiacSign.VIRGO -> "buddhiman, hoshiyaar, meetha bolne wala aur vyavaharik."
                ZodiacSign.CANCER, ZodiacSign.LEO -> "parivaar ka dhyan rakhne wala, aadarshvadi aur rajasi andaz wala."
                ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> "dharmiq, gyaani, dayalu aur samajhdar."
                ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS -> "anushasit, mehnati, wafadar aur zimmedari uthane wala."
            }
        }"

        val wealthText = "Aapki Kundli mein 2nd (Dhan Bhava) aur 11th (Labha Bhava) ke anukool yoga hain. Mehnat ke dam par aamadani lagatar badhti rahegi. Surya Dev ki sthiti aapko aarthik taqat deti hai."

        val spiritualText = "9th (Dharma Bhava) aur 12th House aapko dharmiq karyon, daan-punya aur purvajo ke ashirwad se jodte hain, jisse jeevan mein shanti bani rahti hai."

        val (gemstone, benefits) = when (lagna) {
            ZodiacSign.ARIES, ZodiacSign.SCORPIO -> Pair("Laal Moonga (Red Coral)", "Sharirik taqat, sahas aur shatruon par vijay dilata hai.")
            ZodiacSign.TAURUS, ZodiacSign.LIBRA -> Pair("Heera (Diamond) ya Safed Pukhraj", "Saundarya, aakarshan, vaivahik sukh aur dhan-sampatti badhata hai.")
            ZodiacSign.GEMINI, ZodiacSign.VIRGO -> Pair("Panna (Emerald)", "Buddhi, business negotiations, meethi vaani aur yaaddasht ko tez karta hai.")
            ZodiacSign.CANCER -> Pair("Asli Moti (Natural Pearl)", "Man ki ghabrahat door karta hai, shanti deta hai aur swasthya behtar banata hai.")
            ZodiacSign.LEO -> Pair("Manikya (Ruby)", "Rajkiya samman, aatm-vishwas aur pita ka ashirwad dilata hai.")
            ZodiacSign.SAGITTARIUS, ZodiacSign.PISCES -> Pair("Peela Pukhraj (Yellow Sapphire)", "Bhagya uday, gyan, adhyatmik unnati aur shubh vivaah ke yoga banata hai.")
            ZodiacSign.CAPRICORN, ZodiacSign.AQUARIUS -> Pair("Neelam (Blue Sapphire) ya Katela (Amethyst)", "Karyakshetra mein thehraav, badi safalta aur Shani Dev ka ashirwad deta hai.")
        }

        val mantra = when (lagna.ruler) {
            Planet.SUN -> "Om Hreem Hroum Sah Suryaya Namaha (ॐ सूर्याय नमः)"
            Planet.MOON -> "Om Shram Shreem Shroum Sah Chandraya Namaha (ॐ सोमाय नमः)"
            Planet.MARS -> "Om Kram Kreem Kroum Sah Bhaumaya Namaha (ॐ भौमाय नमः)"
            Planet.MERCURY -> "Om Bram Breem Broum Sah Budhaya Namaha (ॐ बुधाय नमः)"
            Planet.JUPITER -> "Om Gram Greem Groum Sah Guruve Namaha (ॐ बृहस्पतये नमः)"
            Planet.VENUS -> "Om Dram Dreem Droum Sah Shukraya Namaha (ॐ शुक्राय नमः)"
            Planet.SATURN -> "Om Pram Preem Proum Sah Shanaye Namaha (ॐ शनये नमः)"
            else -> "Om Namah Shivaya (ॐ नमः शिवाय)"
        }

        val dashaImpact = "Vartaman mein aapki Kundli mein ${dasha.currentMahadasha.sanskritName} ki Mahadasha aur ${dasha.currentAntardasha.sanskritName} ki Antardasha chal rahi hai (jo ${dasha.antardashaEndDate} tak prabhavi hai). Yeh samay aage badhne, nayi cheezein seekhne aur aatm-chintan ke liye sabse anukool hai."

        return PersonalizedPredictions(
            lagnaSummary = lagnaText,
            moonMindSummary = moonText,
            careerAndProfession = careerText,
            relationshipsAndMarriage = marriageText,
            wealthAndProsperity = wealthText,
            spiritualPath = spiritualText,
            recommendedGemstone = gemstone,
            gemstoneBenefits = benefits,
            favorableMantra = mantra,
            currentDashaImpact = dashaImpact
        )
    }
}
