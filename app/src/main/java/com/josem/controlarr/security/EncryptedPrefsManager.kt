package com.josem.controlarr.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedPrefsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "controlarr_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var appLockEnabled: Boolean
        get() = prefs.getBoolean(KEY_APP_LOCK_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_APP_LOCK_ENABLED, value).apply()

    var useBiometric: Boolean
        get() = prefs.getBoolean(KEY_USE_BIOMETRIC, true)
        set(value) = prefs.edit().putBoolean(KEY_USE_BIOMETRIC, value).apply()

    var lockPin: String
        get() = prefs.getString(KEY_LOCK_PIN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LOCK_PIN, value).apply()

    fun getStoredUsernames(): Set<String> =
        prefs.getStringSet(KEY_STORED_USERNAMES, emptySet()) ?: emptySet()

    fun addUsername(username: String) {
        if (username.isBlank()) return
        val current = getStoredUsernames().toMutableSet()
        current.add(username.trim())
        prefs.edit().putStringSet(KEY_STORED_USERNAMES, current).apply()
    }

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_USE_BIOMETRIC = "use_biometric"
        private const val KEY_LOCK_PIN = "lock_pin"
        private const val KEY_STORED_USERNAMES = "stored_usernames"
    }
}
