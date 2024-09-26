package com.groupec.salesb.core.domain

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.UserRepository
import com.groupec.salesb.core.model.data.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDefaultUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<Result<User>> = userRepository.getDefaultUser()
}