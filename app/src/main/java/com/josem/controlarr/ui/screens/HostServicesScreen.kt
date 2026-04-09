package com.josem.controlarr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.josem.controlarr.data.Host
import com.josem.controlarr.ui.components.ServerCard
import com.josem.controlarr.viewmodel.ServerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostServicesScreen(
    viewModel: ServerViewModel,
    hostId: Int,
    onNavigateBack: () -> Unit,
    onAddServer: (Int) -> Unit,
    onEditServer: (Int) -> Unit,
    onOpenServer: (Int) -> Unit
) {
    var host by remember { mutableStateOf<Host?>(null) }
    val servers by viewModel.getServersByHost(hostId).collectAsState(initial = emptyList())

    LaunchedEffect(hostId) {
        host = viewModel.getHostById(hostId)
    }

    val currentHost = host

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(currentHost?.let { "${it.name} (${it.address})" } ?: "Servicios")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAddServer(hostId) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir servicio")
            }
        }
    ) { padding ->
        if (servers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay servicios en este dispositivo.\nPulsa + para añadir uno.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(servers, key = { it.server.id }) { serverWithHost ->
                    ServerCard(
                        server = serverWithHost.server,
                        hostAddress = serverWithHost.host.address,
                        onClick = { onOpenServer(serverWithHost.server.id) },
                        onEdit = { onEditServer(serverWithHost.server.id) },
                        onDelete = { viewModel.deleteServer(serverWithHost.server) }
                    )
                }
            }
        }
    }
}
