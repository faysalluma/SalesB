package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.user.UserRepository
import javax.inject.Inject

class CheckUserEmailExistsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> = userRepository.checkEmailExists(email)
}
