package com.groupec.salesb.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserCompany
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserSession
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
        private val PARAMETER_ID_KEY = intPreferencesKey("parameterid")
        private val PARAMETER_USER_ID_KEY = intPreferencesKey("parameter_userid")
        private val PARAMETER_COMPANY_ID_KEY = intPreferencesKey("parameter_entrepriseid")
        private val CASH_ENABLED_KEY = booleanPreferencesKey("cash_enabled")
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

        private val SHOW_IMAGE_ON_PRODUCT_KEY = booleanPreferencesKey("showimageonproduct")
        private val USE_INT_FOR_PRICE_AND_AMOUNT_KEY = booleanPreferencesKey("useintforpriceandamout")
        private val ACTIVE_PRINTER = booleanPreferencesKey("activeprinter")

        // Local parameters
        private val ACTIVE_PAYMENT_MODE = booleanPreferencesKey("activepaymentmode")
        private val ACTIVE_CLIENT = booleanPreferencesKey("activeclient")

        // Login key
        private val USER_ID_KEY = stringPreferencesKey("userid")
        private val USER_NAME_KEY = stringPreferencesKey("nomprenom")
        private val USER_RESET_PASSWORD_KEY = stringPreferencesKey("resetpassword")
        private val USER_IS_PRO_ACTIVE_KEY = booleanPreferencesKey("isproactive")
        private val SESSION_USER_ID = intPreferencesKey("user_session_id")
        private val SESSION_USER_NAME = stringPreferencesKey("user_session_name")
        private val SESSION_USER_EMAIL = stringPreferencesKey("user_session_email")
        private val SESSION_USER_ADDRESS = stringPreferencesKey("user_session_address")
        private val SESSION_USER_PHONE = stringPreferencesKey("user_session_phone")
        private val SESSION_USER_ACTIVE = booleanPreferencesKey("user_session_active")
        private val SESSION_FIRST_LOGIN = booleanPreferencesKey("user_session_first_login")
        private val SESSION_SELECTED_COMPANY = intPreferencesKey("user_session_selected_company")
        private val SESSION_COMPANY_COUNT = intPreferencesKey("user_session_company_count")

        // Subscriptions
        private val SUBSCRIPTION_LAST_CHECK = longPreferencesKey("last_sub_check")
        private val SUBSCRIPTION_LAST_STATUS = booleanPreferencesKey("last_sub_status")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val userSessionFlow: Flow<UserSession> = context.dataStore.data.map session@{ preferences ->

        val id = preferences[SESSION_USER_ID] ?: return@session UserSession()
        val count = preferences[SESSION_COMPANY_COUNT] ?: return@session UserSession()
        if (count < 0) return@session UserSession()
        val companies = (0 until count).map { index ->
            UserCompany(
                memberId = preferences[longPreferencesKey(companyKey(index, "member_id"))] ?: return@session UserSession(),
                companyId = preferences[intPreferencesKey(companyKey(index, "company_id"))] ?: return@session UserSession(),
                role = preferences[stringPreferencesKey(companyKey(index, "role"))] ?: return@session UserSession(),
                companyName = preferences[stringPreferencesKey(companyKey(index, "name"))],
                image = preferences[stringPreferencesKey(companyKey(index, "image"))],
                businessType = preferences[stringPreferencesKey(companyKey(index, "business_type"))] ?: return@session UserSession(),
                permissions = preferences[stringSetPreferencesKey(companyKey(index, "permissions"))]?.toList() ?: emptyList()
            )
        }
        val account = User(
            userId = id,
            fullName = preferences[SESSION_USER_NAME] ?: return@session UserSession(),
            email = preferences[SESSION_USER_EMAIL] ?: return@session UserSession(),
            address = preferences[SESSION_USER_ADDRESS],
            phone = preferences[SESSION_USER_PHONE],
            active = preferences[SESSION_USER_ACTIVE] ?: false,
            firstLogin = preferences[SESSION_FIRST_LOGIN] ?: false,
            companies = companies
        )
        val selected = preferences[SESSION_SELECTED_COMPANY]?.takeIf { companyId ->
            companies.any { it.companyId == companyId }
        }
        UserSession(account, selected)
    }

    private fun companyKey(index: Int, field: String): String = "user_session_company_${index}_$field"

    suspend fun setUserSession(session: UserSession) {
        require(session.selectedCompanyId == null || session.selectedCompany != null)
        context.dataStore.edit { preferences ->
            if (preferences[SESSION_USER_ID] != session.account?.userId ||
                preferences[SESSION_SELECTED_COMPANY] != session.selectedCompanyId) {
                // These preferences belong to the previous account/company.
                // Keep device-wide choices (e.g. terms acceptance) untouched.
                companyContextKeys.forEach { preferences.remove(it) }
            }
            removeUserSessionKeys(preferences)
            session.account?.let { account ->
                preferences[SESSION_USER_ID] = account.userId
                preferences[SESSION_USER_NAME] = account.fullName
                preferences[SESSION_USER_EMAIL] = account.email
                account.address?.let { preferences[SESSION_USER_ADDRESS] = it }
                account.phone?.let { preferences[SESSION_USER_PHONE] = it }
                preferences[SESSION_USER_ACTIVE] = account.active
                preferences[SESSION_FIRST_LOGIN] = account.firstLogin
                session.selectedCompanyId?.let { preferences[SESSION_SELECTED_COMPANY] = it }
                preferences[SESSION_COMPANY_COUNT] = account.companies.size
                account.companies.forEachIndexed { index, company ->
                    preferences[longPreferencesKey(companyKey(index, "member_id"))] = company.memberId
                    preferences[intPreferencesKey(companyKey(index, "company_id"))] = company.companyId
                    preferences[stringPreferencesKey(companyKey(index, "role"))] = company.role
                    company.companyName?.let { preferences[stringPreferencesKey(companyKey(index, "name"))] = it }
                    company.image?.let { preferences[stringPreferencesKey(companyKey(index, "image"))] = it }
                    preferences[stringPreferencesKey(companyKey(index, "business_type"))] = company.businessType
                    preferences[stringSetPreferencesKey(companyKey(index, "permissions"))] = company.permissions.toSet()
                }
            }
        }
    }

    private fun removeUserSessionKeys(preferences: MutablePreferences) {
        preferences.asMap().keys.filter { it.name.startsWith("user_session_") }.forEach { preferences.remove(it) }
    }

    private val companyContextKeys: List<Preferences.Key<*>> = listOf(
        PARAMETER_ID_KEY, PARAMETER_USER_ID_KEY, PARAMETER_COMPANY_ID_KEY, CASH_ENABLED_KEY, LOGO_KEY, DEVISE_KEY,
        RAISON_SOCIAL_KEY, ADRESSE_KEY, TELEPHONE_KEY, EMAIL_KEY, IFU_KEY,
        WEBSITE_KEY, ENTREPRISE_TYPE_KEY, OFFLINE_KEY, DEFAULT_PAYMENT_TYPE_KEY,
        TVA_KEY, SHOW_IMAGE_ON_PRODUCT_KEY, USE_INT_FOR_PRICE_AND_AMOUNT_KEY,
        ACTIVE_PRINTER, ACTIVE_PAYMENT_MODE, ACTIVE_CLIENT,
        USER_ID_KEY, USER_NAME_KEY, USER_RESET_PASSWORD_KEY, USER_IS_PRO_ACTIVE_KEY,
        SUBSCRIPTION_LAST_CHECK, SUBSCRIPTION_LAST_STATUS
    )

    val parameterFlow: Flow<Parameter> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            Parameter(
                id = preferences[PARAMETER_ID_KEY] ?: 0,
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
                showimageonproduct = preferences[SHOW_IMAGE_ON_PRODUCT_KEY] ?: false,
                useintforpriceandamout = preferences[USE_INT_FOR_PRICE_AND_AMOUNT_KEY] ?: false,
                activepaymentmode = preferences[ACTIVE_PAYMENT_MODE] ?: false,
                activeClient = preferences[ACTIVE_CLIENT] ?: true,
                activeprinter = preferences[ACTIVE_PRINTER] ?: false,
                userid = preferences[PARAMETER_USER_ID_KEY] ?: 0,
                entrepriseid = preferences[PARAMETER_COMPANY_ID_KEY],
                cashEnabled = preferences[CASH_ENABLED_KEY] ?: false
            )
        }

    val userFlow: Flow<UserStore> = context.dataStore.data
        .map { preferences ->
            val selected = preferences[SESSION_SELECTED_COMPANY]
            val index = (0 until (preferences[SESSION_COMPANY_COUNT] ?: 0)).firstOrNull {
                preferences[intPreferencesKey(companyKey(it, "company_id"))] == selected
            }
            val permissions = index?.let {
                preferences[stringSetPreferencesKey(companyKey(it, "permissions"))]
            }.orEmpty()
            UserStore(
                id = preferences[SESSION_USER_ID]?.toString() ?: "",
                nomprenom = preferences[SESSION_USER_NAME] ?: "",
                privilege = permissions.joinToString(","),
                firstLogin = preferences[SESSION_FIRST_LOGIN] ?: false,
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
            if (parameter.entrepriseid != null) {
                require(parameter.userid == datastore[SESSION_USER_ID] &&
                    parameter.entrepriseid == datastore[SESSION_SELECTED_COMPANY])
            }
            datastore[PARAMETER_ID_KEY] = parameter.id
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
            datastore[USE_INT_FOR_PRICE_AND_AMOUNT_KEY] = parameter.useintforpriceandamout
            datastore[ACTIVE_PAYMENT_MODE] = parameter.activepaymentmode
            datastore[ACTIVE_CLIENT] = parameter.activeClient
            datastore[ACTIVE_PRINTER] = parameter.activeprinter
            datastore[SHOW_TERMS_AND_CONDITIONS_KEY] = false
            datastore[PARAMETER_USER_ID_KEY] = parameter.userid ?: 0
            parameter.entrepriseid?.let { datastore[PARAMETER_COMPANY_ID_KEY] = it }
                ?: datastore.remove(PARAMETER_COMPANY_ID_KEY)
            datastore[CASH_ENABLED_KEY] = parameter.cashEnabled
        }
    }

    suspend fun setParameterOnlyBusinessDataConfig(parameter: Parameter) {
        context.dataStore.edit { datastore ->
            require(parameter.userid == datastore[SESSION_USER_ID] &&
                parameter.entrepriseid == datastore[SESSION_SELECTED_COMPANY])
            datastore[PARAMETER_ID_KEY] = parameter.id
            datastore[LOGO_KEY] = parameter.logo ?: ""
            datastore[DEVISE_KEY] = parameter.devise
            datastore[RAISON_SOCIAL_KEY] = parameter.raisonsociale
            datastore[ADRESSE_KEY] = parameter.adresse ?: ""
            datastore[TELEPHONE_KEY] = parameter.telephone ?: ""
            datastore[EMAIL_KEY] = parameter.email ?: ""
            datastore[IFU_KEY] = parameter.ifu ?: ""
            datastore[WEBSITE_KEY] = parameter.website ?: ""
            datastore[ENTREPRISE_TYPE_KEY] = parameter.entreprisetype
            datastore[TVA_KEY] = parameter.tva
            datastore[PARAMETER_USER_ID_KEY] = parameter.userid ?: 0
            parameter.entrepriseid?.let { datastore[PARAMETER_COMPANY_ID_KEY] = it }
                ?: datastore.remove(PARAMETER_COMPANY_ID_KEY)
            datastore[CASH_ENABLED_KEY] = parameter.cashEnabled
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
            companyContextKeys.forEach { preferences.remove(it) }
            removeUserSessionKeys(preferences)
        }
    }

    // Check if the user is logged in
    suspend fun isLoggedIn(): Boolean = userSessionFlow.first().let {
        it.account?.active == true && it.selectedCompany != null
    }

    suspend fun acceptTermsAndConditions() {
        context.dataStore.edit { datastore ->
            datastore [SHOW_TERMS_AND_CONDITIONS_KEY] = false
        }
    }


}
