package com.groupec.salesb.core.domain.client

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.client.ClientRepository
import javax.inject.Inject

class DeleteClientUseCase @Inject constructor(
    private val clientRepository: ClientRepository
) {
    suspend operator fun invoke(clientId: Int): Result<Unit> = clientRepository.deleteClient(clientId)
}
