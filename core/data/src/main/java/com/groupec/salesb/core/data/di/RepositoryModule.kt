
package com.groupec.salesb.core.data.di

import android.content.Context
import com.groupec.salesb.core.data.repository.CategorieRepository
import com.groupec.salesb.core.data.repository.CategorieRepositoryImpl
import com.groupec.salesb.core.data.repository.ParameterRepository
import com.groupec.salesb.core.data.repository.ParameterRepositoryImpl
import com.groupec.salesb.core.data.repository.ProductRepository
import com.groupec.salesb.core.data.repository.ProductRepositoryImpl
import com.groupec.salesb.core.data.repository.SaleRepository
import com.groupec.salesb.core.data.repository.SaleRepositoryImpl
import com.groupec.salesb.core.data.repository.StatisticRepository
import com.groupec.salesb.core.data.repository.StatisticRepositoryImpl
import com.groupec.salesb.core.data.repository.common.UserLocalRepository
import com.groupec.salesb.core.data.repository.common.UserRemoteRepository
import com.groupec.salesb.core.data.repository.UserRepository
import com.groupec.salesb.core.data.repository.UserRepositoryImpl
import com.groupec.salesb.core.data.repository.common.UserSyncRepository
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.network.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext context: Context,
        apiService: ApiService,
        dataStoreManager: DataStoreManager,
        userLocalRepository: UserLocalRepository,
        userRemoteRepository: UserRemoteRepository,
        userSyncRepository: UserSyncRepository
    ) : UserRepository {
        return UserRepositoryImpl(context, apiService, dataStoreManager, userLocalRepository, userRemoteRepository, userSyncRepository)
    }

    @Provides
    @Singleton
    fun providerStatisticRepository(
        apiService: ApiService
    ) : StatisticRepository {
        return StatisticRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun providerProductRepository(
        @ApplicationContext context: Context,
        apiService: ApiService,
        dataStoreManager: DataStoreManager
    ) : ProductRepository {
        return ProductRepositoryImpl(context, apiService, dataStoreManager)
    }

    @Provides
    @Singleton
    fun providerCategorieRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager
    ) : CategorieRepository {
        return CategorieRepositoryImpl(apiService, dataStoreManager)
    }

    @Provides
    @Singleton
    fun providerSaleRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager
    ) : SaleRepository {
        return SaleRepositoryImpl(apiService, dataStoreManager)
    }
}