package com.josem.controlarr.ui.screens

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.josem.controlarr.security.EncryptedPrefsManager

@Composable
fun AppLockScreen(
    encryptedPrefs: EncryptedPrefsManager,
    activity: FragmentActivity,
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    var pinInput by remember { mutableStateOf("") }
    var showPinInput by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    fun attemptBiometric() {
        val biometricManager = BiometricManager.from(context)
        val canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            showPinInput = true
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onUnlocked()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED
                ) {
                    showPinInput = true
                }
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(context, "Autenticación fallida", Toast.LENGTH_SHORT).show()
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Controlarr")
            .setSubtitle("Desbloquear aplicación")
            .setNegativeButtonText("Usar PIN")
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }

    LaunchedEffect(Unit) {
        if (encryptedPrefs.useBiometric) {
            attemptBiometric()
        } else {
            showPinInput = true
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Controlarr bloqueado",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (showPinInput) {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        pinInput = it
                        error = ""
                    },
                    label = { Text("PIN") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = error.isNotBlank(),
                    supportingText = if (error.isNotBlank()) {{ Text(error) }} else null,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (pinInput == encryptedPrefs.lockPin) {
                            onUnlocked()
                        } else {
                            error = "PIN incorrecto"
                            pinInput = ""
                        }
                    },
                    enabled = pinInput.length >= 4,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Desbloquear")
                }
            }

            if (encryptedPrefs.useBiometric && showPinInput) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { attemptBiometric() }) {
                    Text("Usar huella dactilar")
                }
            }
        }
    }
}
