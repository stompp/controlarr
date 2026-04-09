package com.josem.controlarr.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "servers",
    foreignKeys = [
        ForeignKey(
            entity = Host::class,
            parentColumns = ["id"],
            childColumns = ["hostId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hostId")]
)
data class Server(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: ServerType,
    val hostId: Int,
    val port: Int,
    val apiKey: String = "",
    val username: String = "",
    val password: String = "",
    val useHttps: Boolean = false
) {
    fun baseUrl(hostAddress: String): String {
        val scheme = if (useHttps) "https" else "http"
        return "$scheme://$hostAddress:$port"
    }
}
