package com.groupec.salesb.core.data.repository

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import kotlinx.coroutines.flow.Flow

interface UserRepository{
    fun saveDefaultUser() : Flow<Result<Unit>>
    fun getDefaultUser() : Flow<Result<User>>
    fun getUserStore() : Flow<UserStore>
}