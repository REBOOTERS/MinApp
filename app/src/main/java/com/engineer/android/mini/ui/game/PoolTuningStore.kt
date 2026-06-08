package com.engineer.android.mini.ui.game

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.poolTuningDataStore by preferencesDataStore(name = "pool_tuning")

class PoolTuningStore(private val context: Context) {

    data class TuningSnapshot(
        val damping: Int,
        val rail: Int,
        val pocket: Int,
        val headStringOnly: Boolean
    )

    val tuningFlow: Flow<TuningSnapshot> = context.poolTuningDataStore.data.map { prefs ->
        TuningSnapshot(
            damping = prefs[KEY_DAMPING] ?: DEFAULT_DAMPING,
            rail = prefs[KEY_RAIL] ?: DEFAULT_RAIL,
            pocket = prefs[KEY_POCKET] ?: DEFAULT_POCKET,
            headStringOnly = prefs[KEY_HEAD_STRING_ONLY] ?: DEFAULT_HEAD_STRING_ONLY
        )
    }

    suspend fun save(snapshot: TuningSnapshot) {
        context.poolTuningDataStore.edit { prefs: MutablePreferences ->
            prefs[KEY_DAMPING] = snapshot.damping
            prefs[KEY_RAIL] = snapshot.rail
            prefs[KEY_POCKET] = snapshot.pocket
            prefs[KEY_HEAD_STRING_ONLY] = snapshot.headStringOnly
        }
    }

    private companion object {
        val KEY_DAMPING: Preferences.Key<Int> = intPreferencesKey("damping")
        val KEY_RAIL: Preferences.Key<Int> = intPreferencesKey("rail")
        val KEY_POCKET: Preferences.Key<Int> = intPreferencesKey("pocket")
        val KEY_HEAD_STRING_ONLY: Preferences.Key<Boolean> = booleanPreferencesKey("head_string_only")

        const val DEFAULT_DAMPING = 42
        const val DEFAULT_RAIL = 80
        const val DEFAULT_POCKET = 50
        const val DEFAULT_HEAD_STRING_ONLY = true
    }
}