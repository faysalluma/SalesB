package com.groupec.salesb.core.domain.user

import androidx.paging.PagingData
import com.groupec.salesb.core.data.repository.user.UserRepository
import com.groupec.salesb.core.model.data.Employee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(searchQuery : String) : Flow<PagingData<Employee>> = userRepository.getPagedUsers(searchQuery)
}