package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.UserRepository
import javax.inject.Inject

class LogoutPasswordUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): Result<Unit> = userRepository.logout()
}