package com.groupec.salesb.core.data.repository.client

import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun getClients(): Flow<List<Client>>
    suspend fun getAllClients(searchQuery: String): Result<List<Client>>
    fun getPagedClients(searchQuery: String): Flow<PagingData<Client>>
    suspend fun saveClient(client: Client): Result<Unit>
    suspend fun deleteClient(clientId: Int): Result<Unit>
}
