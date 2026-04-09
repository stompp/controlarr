package com.josem.controlarr.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.josem.controlarr.data.Server
import com.josem.controlarr.data.ServerType
import com.josem.controlarr.viewmodel.ServerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditServerScreen(
    viewModel: ServerViewModel,
    serverId: Int?,
    preselectedHostId: Int?,
    onNavigateBack: () -> Unit
) {
    val isEditing = serverId != null
    val hosts by viewModel.hosts.collectAsState()

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ServerType.SONARR) }
    var selectedHostId by remember { mutableIntStateOf(preselectedHostId ?: 0) }
    var port by remember { mutableIntStateOf(ServerType.SONARR.defaultPort) }
    var portText by remember { mutableStateOf(ServerType.SONARR.defaultPort.toString()) }
    var apiKey by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var useHttps by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var hostDropdownExpanded by remember { mutableStateOf(false) }
    var existingId by remember { mutableIntStateOf(0) }

    LaunchedEffect(serverId) {
        if (serverId != null) {
            viewModel.getServerById(serverId)?.let { server ->
                existingId = server.id
                name = server.name
                selectedType = server.type
                selectedHostId = server.hostId
                port = server.port
                portText = server.port.toString()
                apiKey = server.apiKey
                username = server.username
                password = server.password
                useHttps = server.useHttps
            }
        }
    }

    val selectedHost = hosts.find { it.id == selectedHostId }
    val isValid = name.isNotBlank() && selectedHostId > 0 && portText.toIntOrNull() != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar servicio" else "Añadir servicio") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Host selector
            ExposedDropdownMenuBox(
                expanded = hostDropdownExpanded,
                onExpandedChange = { hostDropdownExpanded = !hostDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedHost?.let { "${it.name} (${it.address})" } ?: "Seleccionar dispositivo...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Dispositivo *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hostDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = hostDropdownExpanded,
                    onDismissRequest = { hostDropdownExpanded = false }
                ) {
                    if (hosts.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay dispositivos. Crea uno primero.") },
                            onClick = { hostDropdownExpanded = false },
                            enabled = false
                        )
                    } else {
                        hosts.forEach { host ->
                            DropdownMenuItem(
                                text = { Text("${host.name} (${host.address})") },
                                onClick = {
                                    selectedHostId = host.id
                                    hostDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre *") },
                placeholder = { Text("Mi Sonarr") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = typeDropdownExpanded,
                onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de servicio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = typeDropdownExpanded,
                    onDismissRequest = { typeDropdownExpanded = false }
                ) {
                    ServerType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                selectedType = type
                                if (!isEditing) {
                                    port = type.defaultPort
                                    portText = type.defaultPort.toString()
                                }
                                typeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = portText,
                onValueChange = { newValue ->
                    portText = newValue
                    newValue.toIntOrNull()?.let { port = it }
                },
                label = { Text("Puerto") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Usar HTTPS")
                Switch(
                    checked = useHttps,
                    onCheckedChange = { useHttps = it }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "Credenciales",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key (para servicios *arr)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val server = Server(
                        id = if (isEditing) existingId else 0,
                        name = name.trim(),
                        type = selectedType,
                        hostId = selectedHostId,
                        port = port,
                        apiKey = apiKey.trim(),
                        username = username.trim(),
                        password = password,
                        useHttps = useHttps
                    )
                    viewModel.upsertServer(server)
                    onNavigateBack()
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Guardar cambios" else "Añadir servicio")
            }
        }
    }
}
