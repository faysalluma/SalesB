package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.account.AccountRepository
import com.groupec.salesb.core.model.data.User
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(private val userRepository: AccountRepository) {
    suspend operator fun invoke(ancPassword: String, password: String): Result<User> =
        userRepository.changePassword(ancPassword, password)
}
