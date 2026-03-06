package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.user.UserRepository
import javax.inject.Inject

class SaveUserStoreByIdUseCase @Inject constructor(private val userRepository: UserRepository){
    suspend operator fun invoke(userId: Int): Result<Unit> = userRepository.saveUserStoreById(userId)
}
