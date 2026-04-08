package com.josem.controlarr.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class Server(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: ServerType,
    val host: String,
    val port: Int,
    val apiKey: String = "",
    val useHttps: Boolean = false
) {
    val baseUrl: String
        get() {
            val scheme = if (useHttps) "https" else "http"
            return "$scheme://$host:$port"
        }
}
