package com.josem.controlarr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.josem.controlarr.data.Server
import com.josem.controlarr.data.ServerDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServerViewModel(private val dao: ServerDao) : ViewModel() {

    val servers: StateFlow<List<Server>> = dao.getAllServers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun upsertServer(server: Server) {
        viewModelScope.launch { dao.upsertServer(server) }
    }

    fun deleteServer(server: Server) {
        viewModelScope.launch { dao.deleteServer(server) }
    }

    suspend fun getServerById(id: Int): Server? = dao.getServerById(id)

    companion object {
        fun factory(dao: ServerDao): ViewModelProvider.Factory = viewModelFactory {
            initializer { ServerViewModel(dao) }
        }
    }
}
