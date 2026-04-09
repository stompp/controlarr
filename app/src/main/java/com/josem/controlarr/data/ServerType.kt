package com.josem.controlarr.data

import androidx.compose.ui.graphics.Color

enum class ServerType(
    val displayName: String,
    val defaultPort: Int,
    val brandColor: Color,
    val letter: String
) {
    SONARR("Sonarr", 8989, Color(0xFF3FC2EF), "S"),
    RADARR("Radarr", 7878, Color(0xFFFFC230), "R"),
    PROWLARR("Prowlarr", 9696, Color(0xFFA855F7), "P"),
    LIDARR("Lidarr", 8686, Color(0xFF00C853), "L"),
    READARR("Readarr", 8787, Color(0xFF8B0000), "Re"),
    BAZARR("Bazarr", 6767, Color(0xFF2BABE2), "B"),
    OVERSEERR("Overseerr", 5055, Color(0xFF7B2FBE), "O"),
    TAUTULLI("Tautulli", 8181, Color(0xFFE5A00D), "T"),
    QBITTORRENT("qBittorrent", 8080, Color(0xFF2F67BA), "qB"),
    JELLYFIN("Jellyfin", 8096, Color(0xFF00A4DC), "J"),
    HOMEASSISTANT("Home Assistant", 8123, Color(0xFF41BDF5), "HA"),
    OTHER("Otro", 80, Color(0xFF9E9E9E), "?")
}
