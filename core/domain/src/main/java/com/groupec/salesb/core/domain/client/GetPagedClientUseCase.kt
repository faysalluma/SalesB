package com.groupec.salesb.core.domain.client

import androidx.paging.PagingData
import com.groupec.salesb.core.data.repository.client.ClientRepository
import com.groupec.salesb.core.model.data.Client
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagedClientUseCase @Inject constructor(
    private val clientRepository: ClientRepository
) {
    operator fun invoke(searchQuery: String): Flow<PagingData<Client>> =
        clientRepository.getPagedClients(searchQuery)
}
