package com.groupec.salesb.core.firebaseremoteconfig

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class FirebaseRemoteConfigProvider @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) {
    private fun getFlagValue(key: String): Any {
        val defaultValue = defaultValueMap[key]
        return when (defaultValue) {
            is String -> firebaseRemoteConfig.getString(key)
            is Boolean -> firebaseRemoteConfig.getBoolean(key)
            is Long -> firebaseRemoteConfig.getLong(key)
            else -> ""
        }
    }
    fun getStringFlagValue(map: HashMap<String, Any>, key: String) = map[key] as? String ?: ""
    fun getBooleanFlagValue(map: HashMap<String, Any>, key: String) = map[key] as? Boolean ?: false
    fun getLongFlagValue(map: HashMap<String, Any>, key: String) = map[key] as? Long ?: 0L

    fun configKeys(keyList: List<String>) : SharedFlow<HashMap<String, Any>> = observeFeatureFlagList(keyList)
        .map { updatedKeys ->
            val hashMap = HashMap<String, Any>()
            updatedKeys.map { key ->
                hashMap[key] = getFlagValue(key = key)
            }
            hashMap
        }
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(),
            replay = 1, // Recoit la derniere valeur (si 2 recoit les dernieres valeurs, si 0 ne recoit aucune valeur si le flow est terminé)
        )

    private fun observeFeatureFlagList(keyList: List<String>): Flow<List<String>> = callbackFlow {
        firebaseRemoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {

            override fun onUpdate(configUpdate: ConfigUpdate) {
                firebaseRemoteConfig.activate().addOnCompleteListener {
                    trySend(
                        configUpdate
                            .updatedKeys
                            .toList()
                            .filter { it in keyList }
                    )
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                close(error.cause)
            }
        })
        awaitClose()
    }.onStart {
        emit(keyList)
    }
}

enum class HomeScreenFeatureFlag(
    val keyName: String
) {
    FORCE_UPDATE(
        keyName = "force_update"
    ),
    MESSAGE(
        keyName = "message"
    ),
    OTHER(
        keyName = "other"
    ),
}

val defaultValueMap = mapOf(
    HomeScreenFeatureFlag.FORCE_UPDATE.keyName to false,
    HomeScreenFeatureFlag.MESSAGE.keyName to "",
    HomeScreenFeatureFlag.OTHER.keyName to "",
)