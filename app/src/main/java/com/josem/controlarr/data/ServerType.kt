package com.josem.controlarr.data

import androidx.compose.ui.graphics.Color

enum class ServerType(
    val displayName: String,
    val defaultPort: Int,
    val brandColor: Color,
    val category: ServiceCategory
) {
    // *arr services
    SONARR("Sonarr", 8989, Color(0xFF3FC2EF), ServiceCategory.ARR),
    RADARR("Radarr", 7878, Color(0xFFFFC230), ServiceCategory.ARR),
    PROWLARR("Prowlarr", 9696, Color(0xFFA855F7), ServiceCategory.ARR),
    LIDARR("Lidarr", 8686, Color(0xFF00C853), ServiceCategory.ARR),
    READARR("Readarr", 8787, Color(0xFF8B0000), ServiceCategory.ARR),
    BAZARR("Bazarr", 6767, Color(0xFF2BABE2), ServiceCategory.ARR),
    OVERSEERR("Overseerr", 5055, Color(0xFF7B2FBE), ServiceCategory.ARR),

    // Media
    PLEX("Plex", 32400, Color(0xFFE5A00D), ServiceCategory.MEDIA),
    JELLYFIN("Jellyfin", 8096, Color(0xFF00A4DC), ServiceCategory.MEDIA),
    EMBY("Emby", 8096, Color(0xFF52B54B), ServiceCategory.MEDIA),
    TAUTULLI("Tautulli", 8181, Color(0xFFE5A00D), ServiceCategory.MEDIA),

    // Downloads
    QBITTORRENT("qBittorrent", 8080, Color(0xFF2F67BA), ServiceCategory.DOWNLOADS),
    TRANSMISSION("Transmission", 9091, Color(0xFFB71C1C), ServiceCategory.DOWNLOADS),
    DELUGE("Deluge", 8112, Color(0xFF2196F3), ServiceCategory.DOWNLOADS),
    SABNZBD("SABnzbd", 8080, Color(0xFFFFC107), ServiceCategory.DOWNLOADS),
    NZBGET("NZBGet", 6789, Color(0xFF4CAF50), ServiceCategory.DOWNLOADS),

    // Management
    PORTAINER("Portainer", 9443, Color(0xFF13BEF9), ServiceCategory.MANAGEMENT),
    NGINX_PROXY_MANAGER("Nginx Proxy Manager", 81, Color(0xFFF15833), ServiceCategory.MANAGEMENT),
    PIHOLE("Pi-hole", 80, Color(0xFF96060C), ServiceCategory.MANAGEMENT),
    ADGUARD_HOME("AdGuard Home", 3000, Color(0xFF68BC71), ServiceCategory.MANAGEMENT),
    GRAFANA("Grafana", 3000, Color(0xFFF46800), ServiceCategory.MANAGEMENT),
    NEXTCLOUD("Nextcloud", 443, Color(0xFF0082C9), ServiceCategory.MANAGEMENT),
    UNRAID("Unraid", 80, Color(0xFFF15A2C), ServiceCategory.MANAGEMENT),

    // Home automation
    HOMEASSISTANT("Home Assistant", 8123, Color(0xFF41BDF5), ServiceCategory.HOME),

    // Other
    OTHER("Otro", 80, Color(0xFF9E9E9E), ServiceCategory.OTHER)
}
