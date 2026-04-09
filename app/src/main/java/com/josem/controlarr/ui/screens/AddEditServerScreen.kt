package com.josem.controlarr.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.josem.controlarr.data.ServiceCategory
import com.josem.controlarr.data.Server
import com.josem.controlarr.data.ServerType
import com.josem.controlarr.ui.components.icon
import com.josem.controlarr.viewmodel.ServerViewModel
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ServerType.SONARR) }
    var addressText by remember { mutableStateOf("") }
    var port by remember { mutableIntStateOf(ServerType.SONARR.defaultPort) }
    var portText by remember { mutableStateOf(ServerType.SONARR.defaultPort.toString()) }
    var apiKey by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var useHttps by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var addressDropdownExpanded by remember { mutableStateOf(false) }
    var existingId by remember { mutableIntStateOf(0) }
    var existingHostId by remember { mutableIntStateOf(0) }

    // Pre-fill host address if coming from a host
    LaunchedEffect(preselectedHostId) {
        if (preselectedHostId != null && preselectedHostId > 0) {
            viewModel.getHostById(preselectedHostId)?.let { host ->
                addressText = host.address
            }
        }
    }

    LaunchedEffect(serverId) {
        if (serverId != null) {
            viewModel.getServerWithHostById(serverId)?.let { swh ->
                existingId = swh.server.id
                existingHostId = swh.server.hostId
                name = swh.server.name
                selectedType = swh.server.type
                addressText = swh.host.address
                port = swh.server.port
                portText = swh.server.port.toString()
                apiKey = swh.server.apiKey
                username = swh.server.username
                password = swh.server.password
                useHttps = swh.server.useHttps
            }
        }
    }

    // Filter suggestions
    val addressSuggestions = if (addressText.isBlank()) {
        hosts
    } else {
        hosts.filter {
            it.address.contains(addressText, ignoreCase = true) ||
                it.name.contains(addressText, ignoreCase = true)
        }
    }

    val isValid = name.isNotBlank() && addressText.isNotBlank() && portText.toIntOrNull() != null

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
            // IP / Host field with autocomplete
            ExposedDropdownMenuBox(
                expanded = addressDropdownExpanded && addressSuggestions.isNotEmpty(),
                onExpandedChange = { addressDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = addressText,
                    onValueChange = {
                        addressText = it
                        addressDropdownExpanded = true
                    },
                    label = { Text("IP / Hostname *") },
                    placeholder = { Text("192.168.1.100") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                )
                if (addressSuggestions.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = addressDropdownExpanded,
                        onDismissRequest = { addressDropdownExpanded = false }
                    ) {
                        addressSuggestions.forEach { host ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(host.address)
                                        if (host.name != host.address) {
                                            Text(
                                                host.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    addressText = host.address
                                    addressDropdownExpanded = false
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

            // Type selector grouped by category
            ExposedDropdownMenuBox(
                expanded = typeDropdownExpanded,
                onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de servicio") },
                    leadingIcon = {
                        Icon(
                            selectedType.icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = selectedType.brandColor
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = typeDropdownExpanded,
                    onDismissRequest = { typeDropdownExpanded = false }
                ) {
                    ServiceCategory.entries.forEach { category ->
                        val typesInCategory = ServerType.entries.filter { it.category == category }
                        if (typesInCategory.isNotEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        category.displayName,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {},
                                enabled = false
                            )
                            typesInCategory.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text("  ${type.displayName}") },
                                    leadingIcon = {
                                        Icon(
                                            type.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                            tint = type.brandColor
                                        )
                                    },
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
                    scope.launch {
                        val hostId = if (isEditing && addressText == hosts.find { it.id == existingHostId }?.address) {
                            existingHostId
                        } else {
                            viewModel.getOrCreateHostByAddress(addressText.trim())
                        }
                        val server = Server(
                            id = if (isEditing) existingId else 0,
                            name = name.trim(),
                            type = selectedType,
                            hostId = hostId,
                            port = port,
                            apiKey = apiKey.trim(),
                            username = username.trim(),
                            password = password,
                            useHttps = useHttps
                        )
                        viewModel.upsertServer(server)
                        onNavigateBack()
                    }
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditing) "Guardar cambios" else "Añadir servicio")
            }
        }
    }
}
