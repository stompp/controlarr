package com.josem.controlarr.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TokenType(val displayName: String) {
    API_KEY("API Key"),
    TELEGRAM_BOT("Telegram Bot"),
    AUTH_TOKEN("Auth Token"),
    CUSTOM("Personalizado")
}

@Entity(tableName = "tokens")
data class Token(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: TokenType,
    val value: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
