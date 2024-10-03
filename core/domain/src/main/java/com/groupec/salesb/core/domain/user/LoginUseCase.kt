package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.UserRepository
import com.groupec.salesb.core.model.data.User
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> = userRepository.checkLogin(email, password)
}