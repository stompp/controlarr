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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServerViewModel(
    private val serverDao: ServerDao,
    private val hostDao: HostDao
) : ViewModel() {

    val serversWithHost: StateFlow<List<ServerWithHost>> = serverDao.getAllServersWithHost()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hosts: StateFlow<List<Host>> = hostDao.getAllHosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun upsertServer(server: Server) {
        viewModelScope.launch { serverDao.upsertServer(server) }
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

    /**
     * Find or create a host by address. Returns the hostId.
     */
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

    companion object {
        fun factory(serverDao: ServerDao, hostDao: HostDao): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { ServerViewModel(serverDao, hostDao) }
            }
    }
}
