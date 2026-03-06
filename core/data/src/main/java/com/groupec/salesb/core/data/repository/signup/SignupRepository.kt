package com.groupec.salesb.core.data.repository.signup

import android.net.Uri
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.SignupConfiguration

interface SignupRepository {
    suspend fun saveSignupConfiguration(
        configuration: SignupConfiguration,
        uriLogo: Uri?
    ): Result<Unit>
}
