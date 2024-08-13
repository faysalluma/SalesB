package com.groupec.salesb.core.data.repository

import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) :
    UserRepository {
    override fun getUserStore(): Flow<UserStore> = dataStoreManager.userFlow

}