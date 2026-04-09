package com.josem.controlarr.data

import androidx.room.Embedded
import androidx.room.Relation

data class ServerWithHost(
    @Embedded val server: Server,
    @Relation(
        parentColumn = "hostId",
        entityColumn = "id"
    )
    val host: Host
) {
    val baseUrl: String
        get() = server.baseUrl(host.address)
}
