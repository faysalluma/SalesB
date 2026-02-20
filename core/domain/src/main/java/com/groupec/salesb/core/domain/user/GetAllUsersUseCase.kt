package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.user.UserRepository
import com.groupec.salesb.core.model.data.User
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(searchQuery: String): Result<List<User>> {
        return userRepository.getAllUsers(searchQuery)
    }
}
