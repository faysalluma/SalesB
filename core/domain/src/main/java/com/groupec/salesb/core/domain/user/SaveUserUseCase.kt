package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.user.UserRepository
import com.groupec.salesb.core.model.data.EmployeeInput
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: EmployeeInput) : Result<Unit> = userRepository.saveUser(user)
}
