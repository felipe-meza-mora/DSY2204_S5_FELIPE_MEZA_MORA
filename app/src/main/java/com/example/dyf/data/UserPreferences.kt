package com.example.dyf.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(context: Context) {

    private val dataStore: DataStore<Preferences> = context.dataStore

    // Definir la clave de preferencias para almacenar la lista de usuarios en formato JSON
    private val USERS_KEY = stringPreferencesKey("users")

    //Función para guardar las preferencias del usuario.
    suspend fun saveUserPreferences(users: List<UserData>) {
        val jsonString = Gson().toJson(users)
        dataStore.edit { preferences ->
            preferences[USERS_KEY] = jsonString
        }
    }

    // Función para obtener la lista de usuarios desde las preferencias
    suspend fun getUsers(): List<UserData> {
        val preferences = dataStore.data.firstOrNull()
        val jsonString = preferences?.get(USERS_KEY) ?: "[]"
        val userListType = object : TypeToken<List<UserData>>() {}.type
        return Gson().fromJson(jsonString, userListType)
    }

    // Función eliminar la lista
    suspend fun clearUsers() {
        dataStore.edit { preferences ->
            preferences[USERS_KEY] = "[]"
        }
    }


    //Flujo para observar cambios en la lista de usuarios
    val userPreferencesFlow: Flow<List<UserData>> = dataStore.data
        .map { preferences ->
            val jsonString = preferences[USERS_KEY] ?: "[]"
            val userListType = object : TypeToken<List<UserData>>() {}.type
            Gson().fromJson(jsonString, userListType)
        }
}