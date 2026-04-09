package com.josem.controlarr.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.josem.controlarr.data.ServerWithHost
import com.josem.controlarr.ui.components.ServerCard
import com.josem.controlarr.viewmodel.ServerViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ServerViewModel,
    onAddServer: () -> Unit,
    onEditServer: (Int) -> Unit,
    onOpenServer: (Int) -> Unit,
    onManageHosts: () -> Unit,
    onOpenHostServices: (Int) -> Unit
) {
    val serversWithHost by viewModel.serversWithHost.collectAsState()
    val hosts by viewModel.hosts.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    val filteredServers = if (searchQuery.isBlank()) {
        serversWithHost
    } else {
        serversWithHost.filter { swh ->
            swh.server.name.contains(searchQuery, ignoreCase = true) ||
                swh.server.type.displayName.contains(searchQuery, ignoreCase = true) ||
                swh.server.type.category.displayName.contains(searchQuery, ignoreCase = true) ||
                swh.host.name.contains(searchQuery, ignoreCase = true) ||
                swh.host.address.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Controlarr") },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch; if (!showSearch) searchQuery = "" }) {
                        Icon(
                            if (showSearch) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    }
                    IconButton(onClick = onManageHosts) {
                        Icon(Icons.Default.Dns, contentDescription = "Dispositivos")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddServer) {
                Icon(Icons.Default.Add, contentDescription = "Añadir servicio")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (showSearch) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar servicios...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Todos") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Por dispositivo") }
                )
            }

            when (selectedTab) {
                0 -> AllServicesGrid(
                    servers = filteredServers,
                    viewModel = viewModel,
                    onOpenServer = onOpenServer,
                    onEditServer = onEditServer
                )
                1 -> DevicesTab(
                    hosts = hosts,
                    serversWithHost = serversWithHost,
                    onOpenHostServices = onOpenHostServices
                )
            }
        }
    }
}

@Composable
private fun AllServicesGrid(
    servers: List<ServerWithHost>,
    viewModel: ServerViewModel,
    onOpenServer: (Int) -> Unit,
    onEditServer: (Int) -> Unit
) {
    if (servers.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay servicios añadidos.\nPulsa + para añadir uno.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val gridState = rememberLazyGridState()
    val list = remember(servers) { servers.toMutableStateList() }

    // Sync list when servers change from DB
    remember(servers) {
        list.clear()
        list.addAll(servers)
        true
    }

    val reorderableState = rememberReorderableLazyGridState(gridState) { from, to ->
        list.apply {
            add(to.index, removeAt(from.index))
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list, key = { it.server.id }) { swh ->
            ReorderableItem(reorderableState, key = swh.server.id) { isDragging ->
                val elevation by animateDpAsState(
                    if (isDragging) 8.dp else 0.dp, label = "dragElevation"
                )
                Box(modifier = Modifier.shadow(elevation)) {
                    ServerCard(
                        server = swh.server,
                        hostAddress = swh.host.address,
                        onClick = { onOpenServer(swh.server.id) },
                        onEdit = { onEditServer(swh.server.id) },
                        onDelete = { viewModel.deleteServer(swh.server) },
                        dragModifier = Modifier.draggableHandle(
                            onDragStopped = {
                                viewModel.reorderServers(list.toList())
                            }
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DevicesTab(
    hosts: List<com.josem.controlarr.data.Host>,
    serversWithHost: List<ServerWithHost>,
    onOpenHostServices: (Int) -> Unit
) {
    if (hosts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay dispositivos.\nGestiona dispositivos desde el icono superior.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(hosts, key = { it.id }) { host ->
            val count = serversWithHost.count { it.host.id == host.id }
            Card(
                onClick = { onOpenHostServices(host.id) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Computer,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = host.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${host.address} · $count servicio${if (count != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
