package com.groupec.salesb.core.domain.signup

import android.net.Uri
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.signup.SignupRepository
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.SignupConfiguration
import javax.inject.Inject

class SaveSignupConfigurationUseCase @Inject constructor(
    private val signupRepository: SignupRepository
) {
    suspend operator fun invoke(
        configuration: SignupConfiguration,
        uriLogo: Uri?
    ): Result<Parameter> = signupRepository.saveSignupConfiguration(configuration, uriLogo)
}
