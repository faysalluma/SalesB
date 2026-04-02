package com.groupec.salesb.core.domain.client

import com.groupec.salesb.core.data.repository.client.ClientRepository
import com.groupec.salesb.core.model.data.Client
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetClientUseCase @Inject constructor(
    private val clientRepository: ClientRepository
) {
    operator fun invoke(): Flow<List<Client>> = clientRepository.getClients()
}
