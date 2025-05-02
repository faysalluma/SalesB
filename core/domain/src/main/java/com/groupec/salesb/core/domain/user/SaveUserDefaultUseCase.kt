package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveUserDefaultUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<Result<Unit>> = userRepository.saveDefaultUser()
}