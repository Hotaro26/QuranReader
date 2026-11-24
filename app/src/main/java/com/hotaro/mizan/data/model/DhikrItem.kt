package com.hotaro.mizan.data.model

data class DhikrItem(
    val id: Int,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val defaultTarget: Int = 33,
    val virtue: String = ""
)

object DefaultDhikrs {
    val list = listOf(
        DhikrItem(
            id = 0,
            arabic = "سُبْحَانَ اللَّهِ",
            transliteration = "SubhanAllah",
            translation = "Glory be to Allah",
            defaultTarget = 33,
            virtue = "Whoever says this 33 times after prayer has their sins forgiven even if they were like the foam of the sea."
        ),
        DhikrItem(
            id = 1,
            arabic = "الْحَمْدُ لِلَّهِ",
            transliteration = "Alhamdulillah",
            translation = "All praise is due to Allah",
            defaultTarget = 33,
            virtue = "The best supplication is Alhamdulillah and it fills the scale of good deeds."
        ),
        DhikrItem(
            id = 2,
            arabic = "اللَّهُ أَكْبَرُ",
            transliteration = "Allahu Akbar",
            translation = "Allah is the Greatest",
            defaultTarget = 34,
            virtue = "Completes the post-prayer glorification cycle of 100."
        ),
        DhikrItem(
            id = 3,
            arabic = "أَسْتَغْفِرُ اللَّهَ",
            transliteration = "Astaghfirullah",
            translation = "I seek forgiveness from Allah",
            defaultTarget = 100,
            virtue = "The Prophet ﷺ sought Allah's forgiveness more than 70 times every day."
        ),
        DhikrItem(
            id = 4,
            arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ",
            transliteration = "La ilaha illallah",
            translation = "There is no deity worthy of worship except Allah",
            defaultTarget = 100,
            virtue = "The best phrase of remembrance and the heaviest key to Paradise."
        ),
        DhikrItem(
            id = 5,
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            transliteration = "SubhanAllahi wa bihamdihi",
            translation = "Glory and praise be to Allah",
            defaultTarget = 100,
            virtue = "Whoever recites this 100 times daily will have their sins forgiven, even if like sea foam."
        ),
        DhikrItem(
            id = 6,
            arabic = "اللَّهُمَّ صَلِّ عَلَىٰ مُحَمَّدٍ",
            transliteration = "Allahumma Salli 'ala Muhammad",
            translation = "O Allah, send blessings upon Muhammad",
            defaultTarget = 100,
            virtue = "Whoever sends blessings upon the Prophet ﷺ once, Allah blesses them ten times."
        ),
        DhikrItem(
            id = 7,
            arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            transliteration = "La hawla wa la quwwata illa billah",
            translation = "There is no might nor power except with Allah",
            defaultTarget = 33,
            virtue = "One of the treasures beneath the Throne of the Most Merciful."
        ),
        DhikrItem(
            id = 8,
            arabic = "سُبْحَانَ اللَّهِ الْعَظِيمِ",
            transliteration = "SubhanAllahil 'Adheem",
            translation = "Glory be to Allah, the Almighty",
            defaultTarget = 33,
            virtue = "Two words are beloved to the Most Merciful, light on the tongue, heavy on the Scale."
        )
    )
}
