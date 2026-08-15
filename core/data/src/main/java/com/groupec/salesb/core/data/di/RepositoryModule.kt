
package com.groupec.salesb.core.data.di

import android.content.Context
import com.groupec.salesb.core.data.repository.activity.RecentActivityRepository
import com.groupec.salesb.core.data.repository.activity.RecentActivityRepositoryImpl
import com.groupec.salesb.core.data.repository.client.ClientRepository
import com.groupec.salesb.core.data.repository.client.ClientRepositoryImpl
import com.groupec.salesb.core.data.repository.rayon.RayonRepository
import com.groupec.salesb.core.data.repository.rayon.RayonRepositoryImpl
import com.groupec.salesb.core.data.repository.category.CategoryRepository
import com.groupec.salesb.core.data.repository.category.CategoryRepositoryImpl
import com.groupec.salesb.core.data.repository.category.OutputRepository
import com.groupec.salesb.core.data.repository.category.OutputRepositoryImpl
import com.groupec.salesb.core.data.repository.parameter.ParameterRepository
import com.groupec.salesb.core.data.repository.parameter.ParameterRepositoryImpl
import com.groupec.salesb.core.data.repository.handleservice.HandleServiceRepository
import com.groupec.salesb.core.data.repository.handleservice.HandleServiceRepositoryImpl
import com.groupec.salesb.core.data.repository.product.ProductRepository
import com.groupec.salesb.core.data.repository.product.ProductRepositoryImpl
import com.groupec.salesb.core.data.repository.sale.SaleRepository
import com.groupec.salesb.core.data.repository.sale.SaleRepositoryImpl
import com.groupec.salesb.core.data.repository.signup.SignupRepository
import com.groupec.salesb.core.data.repository.signup.SignupRepositoryImpl
import com.groupec.salesb.core.data.repository.product.StatisticRepository
import com.groupec.salesb.core.data.repository.product.StatisticRepositoryImpl
import com.groupec.salesb.core.data.repository.common.UserLocalRepository
import com.groupec.salesb.core.data.repository.common.UserRemoteRepository
import com.groupec.salesb.core.data.repository.user.UserRepository
import com.groupec.salesb.core.data.repository.user.UserRepositoryImpl
import com.groupec.salesb.core.data.repository.common.UserSyncRepository
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.googlebilling.GoogleBillingProvider
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
    fun provideRecentActivityRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager,
    ): RecentActivityRepository {
        return RecentActivityRepositoryImpl(apiService, dataStoreManager)
    }

    @Provides
    @Singleton
    fun providerParameterRepository(
        @ApplicationContext context: Context,
        dataStoreManager: DataStoreManager,
        apiService: ApiService
    ) : ParameterRepository {
        return ParameterRepositoryImpl(dataStoreManager, apiService, context)
    }

    @Provides
    @Singleton
    fun providerHandleServiceRepository(
        dataStoreManager: DataStoreManager
    ) : HandleServiceRepository {
        return HandleServiceRepositoryImpl(dataStoreManager)
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
        return UserRepositoryImpl(
            context,
            apiService,
            dataStoreManager,
            userLocalRepository,
            userRemoteRepository,
            userSyncRepository
        )
    }

    @Provides
    @Singleton
    fun providerStatisticRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager
    ) : StatisticRepository {
        return StatisticRepositoryImpl(apiService, dataStoreManager)
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
    fun providerClientRepository(
        apiService: ApiService,
        dataStoreManager: DataStoreManager
    ) : ClientRepository {
        return ClientRepositoryImpl(apiService, dataStoreManager)
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

    @Provides
    @Singleton
    fun provideSignupRepository(
        @ApplicationContext context: Context,
        apiService: ApiService
    ) : SignupRepository {
        return SignupRepositoryImpl(context, apiService)
    }
}
