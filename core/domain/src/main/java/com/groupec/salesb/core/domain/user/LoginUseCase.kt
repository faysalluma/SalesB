package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.account.AccountRepository
import com.groupec.salesb.core.model.data.UserSession
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val userRepository: AccountRepository) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> = userRepository.login(email, password)
}
