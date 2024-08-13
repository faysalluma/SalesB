package com.groupec.salesb.core.data.repository

import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore
import kotlinx.coroutines.flow.Flow

interface UserRepository{
    fun getUserStore() : Flow<UserStore>
}