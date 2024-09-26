
package com.groupec.salesb.core.data.di

import com.groupec.salesb.core.data.repository.ParameterRepository
import com.groupec.salesb.core.data.repository.ParameterRepositoryImpl
import com.groupec.salesb.core.data.repository.UserRepository
import com.groupec.salesb.core.data.repository.UserRepositoryImpl
import com.groupec.salesb.core.database.room.dao.UserDao
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.network.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule  {
    @Provides
    @Singleton
    fun providerParameterRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager
    ) : ParameterRepository {
        return ParameterRepositoryImpl(apiService, dataStoreManager)
    }

    @Provides
    @Singleton
    fun providerUserRepository(
        apiService: ApiService,
        userDao: UserDao,
        dataStoreManager: DataStoreManager
    ) : UserRepository {
        return UserRepositoryImpl(apiService, userDao, dataStoreManager)
    }
}