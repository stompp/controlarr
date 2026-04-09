package com.josem.controlarr.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GetApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import com.josem.controlarr.data.ServerType

val ServerType.icon: ImageVector
    get() = when (this) {
        // *arr
        ServerType.SONARR -> Icons.Default.Tv
        ServerType.RADARR -> Icons.Default.Movie
        ServerType.PROWLARR -> Icons.Default.Search
        ServerType.LIDARR -> Icons.Default.MusicNote
        ServerType.READARR -> Icons.Default.Book
        ServerType.BAZARR -> Icons.Default.Subtitles
        ServerType.OVERSEERR -> Icons.Default.LiveTv
        // Media
        ServerType.PLEX -> Icons.Default.PlayCircle
        ServerType.JELLYFIN -> Icons.Default.WaterDrop
        ServerType.EMBY -> Icons.Default.VideoLibrary
        ServerType.TAUTULLI -> Icons.AutoMirrored.Filled.ShowChart
        // Downloads
        ServerType.QBITTORRENT -> Icons.Default.Download
        ServerType.TRANSMISSION -> Icons.Default.SwapHoriz
        ServerType.DELUGE -> Icons.Default.CloudDownload
        ServerType.SABNZBD -> Icons.Default.Newspaper
        ServerType.NZBGET -> Icons.Default.GetApp
        // Management
        ServerType.PORTAINER -> Icons.Default.Inventory2
        ServerType.NGINX_PROXY_MANAGER -> Icons.Default.Security
        ServerType.PIHOLE -> Icons.Default.Shield
        ServerType.ADGUARD_HOME -> Icons.Default.VerifiedUser
        ServerType.GRAFANA -> Icons.AutoMirrored.Filled.ShowChart
        ServerType.NEXTCLOUD -> Icons.Default.Cloud
        ServerType.UNRAID -> Icons.Default.Storage
        // Home
        ServerType.HOMEASSISTANT -> Icons.Default.Home
        // Other
        ServerType.OTHER -> Icons.Default.Language
    }
