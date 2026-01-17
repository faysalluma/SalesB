package com.groupec.salesb.core.domain.user

import com.groupec.salesb.core.data.repository.user.UserRepository
import com.groupec.salesb.core.model.data.UserStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckSubscriptionExpirationUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator suspend fun invoke(): Boolean? = userRepository.isSubscriptionExpired()
}