package com.groupec.salesb.core.domain

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveUserDefaultUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<Result<Unit>> = userRepository.saveDefaultUser()
}