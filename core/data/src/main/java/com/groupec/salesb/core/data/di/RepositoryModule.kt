
package com.groupec.salesb.core.data.di

import android.content.Context
import com.groupec.salesb.core.data.repository.rayon.RayonRepository
import com.groupec.salesb.core.data.repository.rayon.RayonRepositoryImpl
import com.groupec.salesb.core.data.repository.category.CategoryRepository
import com.groupec.salesb.core.data.repository.category.CategoryRepositoryImpl
import com.groupec.salesb.core.data.repository.category.OutputRepository
import com.groupec.salesb.core.data.repository.category.OutputRepositoryImpl
import com.groupec.salesb.core.data.repository.parameter.ParameterRepository
import com.groupec.salesb.core.data.repository.parameter.ParameterRepositoryImpl
import com.groupec.salesb.core.data.repository.product.ProductRepository
import com.groupec.salesb.core.data.repository.product.ProductRepositoryImpl
import com.groupec.salesb.core.data.repository.sale.SaleRepository
import com.groupec.salesb.core.data.repository.sale.SaleRepositoryImpl
import com.groupec.salesb.core.data.repository.product.StatisticRepository
import com.groupec.salesb.core.data.repository.product.StatisticRepositoryImpl
import com.groupec.salesb.core.data.repository.common.UserLocalRepository
import com.groupec.salesb.core.data.repository.common.UserRemoteRepository
import com.groupec.salesb.core.data.repository.user.UserRepository
import com.groupec.salesb.core.data.repository.user.UserRepositoryImpl
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
    ) : CategoryRepository {
        return CategoryRepositoryImpl(apiService, dataStoreManager)
    }

    @Provides
    @Singleton
    fun providerRayonRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager
    ) : RayonRepository {
        return RayonRepositoryImpl(apiService, dataStoreManager)
    }

    @Provides
    @Singleton
    fun providerOutputRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager
    ) : OutputRepository {
        return OutputRepositoryImpl(apiService, dataStoreManager)
    }

    @Provides
    @Singleton
    fun providerSaleRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager,
        @ApplicationContext context: Context,
    ) : SaleRepository {
        return SaleRepositoryImpl(apiService, dataStoreManager, context)
    }
}