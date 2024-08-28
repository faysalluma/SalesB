package com.groupec.feature.login

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SalesBImage
import com.groupec.salesb.core.designsystem.component.TitleHeader
import com.groupec.salesb.core.designsystem.icon.AppIcons.Person
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.LoginForm


@Composable
fun LoginScreen(
    raisonSociale: String,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),

) {
    ComposableLifecycle(
        // onResume = { getOrders() }
    )

    Row(
        modifier = modifier.fillMaxSize()
    ) {
        SalesBImage(Modifier.weight(1f))
        Column(Modifier.weight(1f)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize(),
            ){
                Column(modifier = Modifier.padding(24.dp),) {
                    TitleHeader(
                        title = stringResource(id = R.string.title_login, raisonSociale),
                        detail = stringResource(id = R.string.detail_login)
                    )

                    Spacer(modifier = Modifier.padding(vertical = 22.dp))

                    LoginForm(onSubmitForm = {

                    })
                }
            }
        }

    }
}

