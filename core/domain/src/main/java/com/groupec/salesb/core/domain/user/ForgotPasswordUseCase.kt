package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.UserRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String): Result<Unit> = userRepository.forgotPassword(email)
}