package gt.uvg.lab09.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import gt.uvg.lab09.repository.PreferencesRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LocalSettings(context: Context) : PreferencesRepository {
    private val dataStore = context.applicationContext.settingsDataStore
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val reservationsReadyKey = booleanPreferencesKey("reservations_ready_v1")

    override fun observeDarkTheme(): Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[darkThemeKey] ?: false }

    override suspend fun setDarkTheme(enabled: Boolean): Result<Unit> = repositoryResult {
        dataStore.edit { it[darkThemeKey] = enabled }
        Unit
    }

    suspend fun areReservationsReady(): Boolean =
        dataStore.data.first()[reservationsReadyKey] ?: false

    suspend fun markReservationsReady() {
        dataStore.edit { it[reservationsReadyKey] = true }
    }
}
