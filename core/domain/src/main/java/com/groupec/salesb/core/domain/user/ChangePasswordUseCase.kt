package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.UserRepository
import com.groupec.salesb.core.model.data.User
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: Int, ancPassword: String, password: String): Result<User> =
        userRepository.changePassword(userId, ancPassword, password)
}