package com.groupec.salesb.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.model.data.others.Subscription
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
        private val LOGO_KEY = stringPreferencesKey("logo")
        private val DEVISE_KEY = stringPreferencesKey("device")
        private val RAISON_SOCIAL_KEY = stringPreferencesKey("raisonsociale")
        private val ADRESSE_KEY = stringPreferencesKey("adresse")
        private val TELEPHONE_KEY = stringPreferencesKey("telephone")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val IFU_KEY = stringPreferencesKey("ifu")
        private val WEBSITE_KEY = stringPreferencesKey("website")
        private val ENTREPRISE_TYPE_KEY = intPreferencesKey("entreprisetype")
        private val OFFLINE_KEY = booleanPreferencesKey("offline")
        private val DEFAULT_PAYMENT_TYPE_KEY = stringPreferencesKey("defaultpaymenttype")
        private val TVA_KEY = doublePreferencesKey("tva")
        private val SHOW_TERMS_AND_CONDITIONS_KEY = booleanPreferencesKey("termsandconditions")

        private val SERVICE_VIEW_KEY = booleanPreferencesKey("serviceview")
        private val SHOW_IMAGE_ON_PRODUCT_KEY = booleanPreferencesKey("showimageonproduct")
        private val USE_INT_FOR_PRICE_AND_AMOUNT_KEY = booleanPreferencesKey("useintforpriceandamout")
        private val ACTIVE_PRINTER = booleanPreferencesKey("activeprinter")

        // Local parameters
        private val ACTIVE_PAYMENT_MODE = booleanPreferencesKey("activepaymentmode")

        // Login key
        private val USER_ID_KEY = stringPreferencesKey("userid")
        private val USER_NAME_KEY = stringPreferencesKey("nomprenom")
        private val USER_RESET_PASSWORD_KEY = stringPreferencesKey("resetpassword")
        private val USER_IS_PRO_ACTIVE_KEY = booleanPreferencesKey("isproactive")

        // Subscriptions
        private val SUBSCRIPTION_LAST_CHECK = longPreferencesKey("last_sub_check")
        private val SUBSCRIPTION_LAST_STATUS = booleanPreferencesKey("last_sub_status")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val parameterFlow: Flow<Parameter> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            Parameter(
                logo = preferences[LOGO_KEY] ?: "",
                devise = preferences[DEVISE_KEY] ?: "",
                raisonsociale = preferences[RAISON_SOCIAL_KEY] ?: "",
                adresse = preferences[ADRESSE_KEY] ?: "",
                telephone = preferences[TELEPHONE_KEY] ?: "",
                email = preferences[EMAIL_KEY] ?: "",
                ifu = preferences[IFU_KEY] ?: "",
                website = preferences[WEBSITE_KEY] ?: "",
                entreprisetype = preferences[ENTREPRISE_TYPE_KEY] ?: 0,
                offline = preferences[OFFLINE_KEY] ?: false,
                defaultpaymenttype = preferences[DEFAULT_PAYMENT_TYPE_KEY] ?: "",
                tva = preferences[TVA_KEY] ?: 0.0,
                termsandconditions = preferences[SHOW_TERMS_AND_CONDITIONS_KEY] ?: true,
                serviceview = preferences[SERVICE_VIEW_KEY] ?: false,
                showimageonproduct = preferences[SHOW_IMAGE_ON_PRODUCT_KEY] ?: false,
                useintforpriceandamout = preferences[USE_INT_FOR_PRICE_AND_AMOUNT_KEY] ?: false,
                activepaymentmode = preferences[ACTIVE_PAYMENT_MODE] ?: false,
                activeprinter = preferences[ACTIVE_PRINTER] ?: false
            )
        }

    val userFlow: Flow<UserStore> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            UserStore(
                id = preferences[USER_ID_KEY] ?: "",
                nomprenom = preferences[USER_NAME_KEY] ?: "",
                reset_password = preferences[USER_RESET_PASSWORD_KEY] ?: "",
                isProActive = preferences[USER_IS_PRO_ACTIVE_KEY] ?: false
            )
        }

    val subscriptionFlow: Flow<Subscription> = context.dataStore.data
        .map { preferences ->
            Subscription(
                preferences[SUBSCRIPTION_LAST_CHECK] ?: 0L,
                preferences[SUBSCRIPTION_LAST_STATUS] ?: true
            )
        }

    suspend fun setParameterConfig(parameter: Parameter) {
        context.dataStore.edit { datastore ->
            datastore[LOGO_KEY] = parameter.logo ?: ""
            datastore[DEVISE_KEY] = parameter.devise
            datastore[RAISON_SOCIAL_KEY] = parameter.raisonsociale
            datastore[ADRESSE_KEY] = parameter.adresse ?: ""
            datastore[TELEPHONE_KEY] = parameter.telephone ?: ""
            datastore[EMAIL_KEY] = parameter.email ?: ""
            datastore[IFU_KEY] = parameter.ifu ?: ""
            datastore[WEBSITE_KEY] = parameter.website ?: ""
            datastore[ENTREPRISE_TYPE_KEY] = parameter.entreprisetype
            datastore[OFFLINE_KEY] = parameter.offline
            datastore[SHOW_IMAGE_ON_PRODUCT_KEY] = parameter.showimageonproduct
            datastore[DEFAULT_PAYMENT_TYPE_KEY] = parameter.defaultpaymenttype
            datastore[TVA_KEY] = parameter.tva
            datastore[SERVICE_VIEW_KEY] = parameter.serviceview
            datastore[USE_INT_FOR_PRICE_AND_AMOUNT_KEY] = parameter.useintforpriceandamout
            datastore[ACTIVE_PAYMENT_MODE] = parameter.activepaymentmode
            datastore[ACTIVE_PRINTER] = parameter.activeprinter
            datastore[SHOW_TERMS_AND_CONDITIONS_KEY] = false
        }
    }

    suspend fun setUserConfig(user: UserStore) {
        context.dataStore.edit { datastore ->
            datastore[USER_ID_KEY] = user.id
            datastore[USER_NAME_KEY] = user.nomprenom
            datastore[USER_RESET_PASSWORD_KEY] = user.reset_password
            datastore[USER_IS_PRO_ACTIVE_KEY] = user.isProActive
        }
    }

    suspend fun saveUserSubscriptionStatus(isProActive: Boolean) {
        context.dataStore.edit { datastore ->
            datastore[USER_IS_PRO_ACTIVE_KEY] = isProActive
        }
    }

    suspend fun setSubscriptionConfig(subscription: Subscription) {
        context.dataStore.edit { datastore ->
            datastore[SUBSCRIPTION_LAST_CHECK] = subscription.last_sub_check
            datastore[SUBSCRIPTION_LAST_STATUS] = subscription.last_sub_status
        }
    }

    suspend fun logout()  {
        context.dataStore.edit { preferences ->
            // Remove all keys about user
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
        }
    }

    // Check if the user is logged in
    suspend fun isLoggedIn(): Boolean = context.dataStore.data.first()[USER_ID_KEY] != null

    suspend fun acceptTermsAndConditions() {
        context.dataStore.edit { datastore ->
            datastore [SHOW_TERMS_AND_CONDITIONS_KEY] = false
        }
    }

    suspend fun updateServiceView(value: Boolean) {
        context.dataStore.edit { datastore ->
            datastore[SERVICE_VIEW_KEY] = value
            if (value) {
                datastore[USE_INT_FOR_PRICE_AND_AMOUNT_KEY] = true
            } else {
                datastore[USE_INT_FOR_PRICE_AND_AMOUNT_KEY] = false
            }
        }
    }

    suspend fun updateShowImageOnProduct(value: Boolean) {
        context.dataStore.edit { datastore ->
            datastore[SHOW_IMAGE_ON_PRODUCT_KEY] = value
        }
    }

    suspend fun updateUseIntForPriceAndAmount(value: Boolean) {
        context.dataStore.edit { datastore ->
            datastore[USE_INT_FOR_PRICE_AND_AMOUNT_KEY] = value
        }
    }

    suspend fun updateActivePaymentMode(value: Boolean) {
        context.dataStore.edit { datastore ->
            datastore[ACTIVE_PAYMENT_MODE] = value
        }
    }

    suspend fun updateActivePrinter(value: Boolean) {
        context.dataStore.edit { datastore ->
            datastore[ACTIVE_PRINTER] = value
        }
    }
}
