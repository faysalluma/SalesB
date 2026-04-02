package com.groupec.salesb.core.domain.client

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.client.ClientRepository
import com.groupec.salesb.core.model.data.Client
import javax.inject.Inject

class GetAllClientsUseCase @Inject constructor(
    private val clientRepository: ClientRepository
) {
    suspend operator fun invoke(searchQuery: String): Result<List<Client>> {
        return clientRepository.getAllClients(searchQuery)
    }
}
