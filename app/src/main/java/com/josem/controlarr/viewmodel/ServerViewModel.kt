package com.josem.controlarr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.josem.controlarr.data.Host
import com.josem.controlarr.data.HostDao
import com.josem.controlarr.data.Server
import com.josem.controlarr.data.ServerDao
import com.josem.controlarr.data.ServerWithHost
import com.josem.controlarr.data.Token
import com.josem.controlarr.data.TokenDao
import com.josem.controlarr.security.EncryptedPrefsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class ServerViewModel(
    private val serverDao: ServerDao,
    private val hostDao: HostDao,
    private val tokenDao: TokenDao,
    val encryptedPrefs: EncryptedPrefsManager
) : ViewModel() {

    val serversWithHost: StateFlow<List<ServerWithHost>> = serverDao.getAllServersWithHost()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hosts: StateFlow<List<Host>> = hostDao.getAllHosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tokens: StateFlow<List<Token>> = tokenDao.getAllTokens()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _connectionTestResult = MutableStateFlow<ConnectionTestResult?>(null)
    val connectionTestResult: StateFlow<ConnectionTestResult?> = _connectionTestResult.asStateFlow()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .hostnameVerifier { _, _ -> true }
        .build()

    fun upsertServer(server: Server) {
        viewModelScope.launch {
            serverDao.upsertServer(server)
            if (server.username.isNotBlank()) {
                encryptedPrefs.addUsername(server.username)
            }
        }
    }

    fun deleteServer(server: Server) {
        viewModelScope.launch { serverDao.deleteServer(server) }
    }

    fun upsertHost(host: Host, onResult: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = hostDao.upsertHost(host)
            onResult(id)
        }
    }

    fun deleteHost(host: Host) {
        viewModelScope.launch { hostDao.deleteHost(host) }
    }

    suspend fun getServerById(id: Int): Server? = serverDao.getServerById(id)

    suspend fun getServerWithHostById(id: Int): ServerWithHost? =
        serverDao.getServerWithHostById(id)

    suspend fun getHostById(id: Int): Host? = hostDao.getHostById(id)

    fun getServersByHost(hostId: Int) = serverDao.getServersByHost(hostId)

    suspend fun getOrCreateHostByAddress(address: String): Int {
        val existing = hosts.value.find {
            it.address.equals(address, ignoreCase = true)
        }
        if (existing != null) return existing.id
        val newId = hostDao.upsertHost(Host(name = address, address = address))
        return newId.toInt()
    }

    fun reorderServers(reorderedList: List<ServerWithHost>) {
        viewModelScope.launch {
            reorderedList.forEachIndexed { index, swh ->
                serverDao.updateSortOrder(swh.server.id, index)
            }
        }
    }

    // Token operations
    fun upsertToken(token: Token) {
        viewModelScope.launch { tokenDao.upsertToken(token) }
    }

    fun deleteToken(token: Token) {
        viewModelScope.launch { tokenDao.deleteToken(token) }
    }

    // Connection testing
    fun testConnection(address: String, port: Int, useHttps: Boolean, apiKey: String = "") {
        _connectionTestResult.value = ConnectionTestResult.Testing
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val scheme = if (useHttps) "https" else "http"
                    val urlStr = buildString {
                        append("$scheme://$address:$port")
                        if (apiKey.isNotBlank()) append("?apikey=$apiKey")
                    }
                    val request = Request.Builder().url(urlStr).head().build()
                    val response = httpClient.newCall(request).execute()
                    ConnectionTestResult.Success(response.code)
                } catch (e: Exception) {
                    ConnectionTestResult.Error(e.message ?: "Error de conexión")
                }
            }
            _connectionTestResult.value = result
        }
    }

    fun clearConnectionTest() {
        _connectionTestResult.value = null
    }

    fun getStoredUsernames(): Set<String> = encryptedPrefs.getStoredUsernames()

    companion object {
        fun factory(
            serverDao: ServerDao,
            hostDao: HostDao,
            tokenDao: TokenDao,
            encryptedPrefs: EncryptedPrefsManager
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ServerViewModel(serverDao, hostDao, tokenDao, encryptedPrefs) }
            }
    }
}

sealed class ConnectionTestResult {
    data object Testing : ConnectionTestResult()
    data class Success(val statusCode: Int) : ConnectionTestResult()
    data class Error(val message: String) : ConnectionTestResult()
}
