package com.emreberkgoger.scoutbook.enums

enum class PlayerPosition(val positionName: String) {
    GK("Goalkeeper"),         // Kaleci
    CB("Central Back"),             // Stoper
    LB("Left Back"),                // Sol bek
    RB("Right Back"),               // Sağ bek
    CDM("Central Defensive Midfielder"),         // Orta saha
    CM("Central Midfielder"),         // Orta saha
    CAM("Central Attack Midfielder"),         // Orta saha
    FW("Forward"),               // Forvet
    LM("Left Winger"),                 // Kanat oyuncusu
    RM("Right Winger"),                 // Kanat oyuncusu
    STRIKER("Striker");               // Santrafor

    // İsteğe bağlı: Enum'dan okunabilir pozisyon adı almak için
    override fun toString(): String {
        return positionName
    }
    companion object {
        fun fromValue(value: String): PlayerPosition? {
            return values().find { it.positionName == value }
        }
    }
}