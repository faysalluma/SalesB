package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.data.repository.user.UserRepository
import com.groupec.salesb.core.model.data.UserStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserStoreUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<UserStore> = userRepository.getUserStore()
}