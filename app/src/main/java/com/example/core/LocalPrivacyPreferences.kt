package com.example.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.privacyDataStore: DataStore<Preferences> by preferencesDataStore(name = "naribondhu_privacy_prefs")

class LocalPrivacyPreferences(private val context: Context) {
    private val keyLanguage = stringPreferencesKey("app_language")
    private val keyPinCode = stringPreferencesKey("discreet_pin")
    private val keyPinEnabled = booleanPreferencesKey("discreet_pin_enabled")
    private val keyLocalDiscreetMode = booleanPreferencesKey("local_discreet_mode")

    val languageFlow: Flow<AppLanguage> = context.privacyDataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[keyLanguage])
    }

    val pinEnabledFlow: Flow<Boolean> = context.privacyDataStore.data.map { prefs ->
        prefs[keyPinEnabled] == true && !prefs[keyPinCode].isNullOrEmpty()
    }

    val pinCodeFlow: Flow<String> = context.privacyDataStore.data.map { prefs ->
        prefs[keyPinCode].orEmpty()
    }

    val localDiscreetModeFlow: Flow<Boolean> = context.privacyDataStore.data.map { prefs ->
        prefs[keyLocalDiscreetMode] == true
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.privacyDataStore.edit { prefs ->
            prefs[keyLanguage] = language.code
        }
    }

    suspend fun setDiscreetPin(pin: String?) {
        context.privacyDataStore.edit { prefs ->
            if (pin.isNullOrBlank()) {
                prefs.remove(keyPinCode)
                prefs[keyPinEnabled] = false
            } else {
                prefs[keyPinCode] = pin.take(4)
                prefs[keyPinEnabled] = true
            }
        }
    }

    suspend fun setLocalDiscreetMode(enabled: Boolean) {
        context.privacyDataStore.edit { prefs ->
            prefs[keyLocalDiscreetMode] = enabled
        }
    }

    suspend fun clearAll() {
        context.privacyDataStore.edit { it.clear() }
    }
}
