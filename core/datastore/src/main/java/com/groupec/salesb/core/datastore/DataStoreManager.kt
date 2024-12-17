package com.groupec.salesb.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext val context: Context) {
    companion object {
        // Parameters key
        private val DEVISE_KEY = stringPreferencesKey("device")
        private val RAISON_SOCIAL_KEY = stringPreferencesKey("raisonsociale")
        private val TYPE_ENTREPRISE_KEY = stringPreferencesKey("typeentreprise")
        private val OFFLINE_KEY = booleanPreferencesKey("offline")
        private val PRIMARY_COLOR_KEY = stringPreferencesKey("primarycolor")
        private val SECONDARY_COLOR_KEY = stringPreferencesKey("secondarycolor")
        private val LOAD_PRODUCT_KEY = booleanPreferencesKey("loadproducts")

        // Login key
        private val USER_ID_KEY = stringPreferencesKey("userid")
        private val USER_NAME_KEY = stringPreferencesKey("nomprenom")
        private val USER_PRIVILEGES_KEY = stringPreferencesKey("privileges")
        private val USER_FIRST_LOGIN_KEY = booleanPreferencesKey("firstlogin")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val parameterFlow: Flow<Parameter> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            Parameter(
                preferences[DEVISE_KEY] ?: "",
                preferences[RAISON_SOCIAL_KEY] ?: "",
                preferences[TYPE_ENTREPRISE_KEY] ?: "",
                preferences[OFFLINE_KEY] ?: false,
                preferences[PRIMARY_COLOR_KEY] ?: "",
                preferences[SECONDARY_COLOR_KEY] ?: "",
                preferences[LOAD_PRODUCT_KEY] ?: false
            )
        }

    val userFlow: Flow<UserStore> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            UserStore(
                preferences[USER_ID_KEY] ?: "",
                preferences[USER_NAME_KEY] ?: "",
                preferences[USER_PRIVILEGES_KEY] ?: "",
                preferences[USER_FIRST_LOGIN_KEY] ?: false

            )
        }

    suspend fun setParameterConfig(parameter: Parameter) {
        context.dataStore.edit { datastore ->
            datastore[DEVISE_KEY] = parameter.devise
            datastore[RAISON_SOCIAL_KEY] = parameter.raisonsociale
            datastore[TYPE_ENTREPRISE_KEY] = parameter.typeentreprise
            datastore[OFFLINE_KEY] = parameter.offline
            datastore[PRIMARY_COLOR_KEY] = parameter.primarycolor
            datastore[SECONDARY_COLOR_KEY] = parameter.secondarycolor
            datastore[LOAD_PRODUCT_KEY] = parameter.loadproducts
        }
    }

    suspend fun setUserConfig(user: UserStore) {
        context.dataStore.edit { datastore ->
            datastore[USER_ID_KEY] = user.id
            datastore[USER_NAME_KEY] = user.nomprenom
            datastore[USER_PRIVILEGES_KEY] = user.privilege
            datastore[USER_FIRST_LOGIN_KEY] = user.firstLogin
        }
    }

    suspend fun logout()  {
        context.dataStore.edit { preferences ->
            // Remove all keys about user
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_PRIVILEGES_KEY)
            preferences.remove(USER_FIRST_LOGIN_KEY)
        }
    }

    // Check if the user is logged in
    suspend fun isLoggedIn(): Boolean = context.dataStore.data.first()[USER_ID_KEY] != null
}