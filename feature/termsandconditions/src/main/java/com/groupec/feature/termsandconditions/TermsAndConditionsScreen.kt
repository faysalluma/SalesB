package com.groupec.feature.termsandconditions

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.SalesBImage
import com.groupec.salesb.core.ui.TermsAndCondtionsUi

@Composable
fun TermsAndConditionsScreen(
    modifier: Modifier = Modifier,
    navigateToConfiguration: () -> Unit,
    viewModel: TermsAndConditionsViewModel = hiltViewModel()
){
    val termsAndConditionsUiState by viewModel.termsAndConditionsUiState.collectAsState()
    val isLoading = termsAndConditionsUiState is TermsAndConditionsUiState.Loading
    var enabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    when (termsAndConditionsUiState) {
        is TermsAndConditionsUiState.Success -> {
            LaunchedEffect(Unit) {
                navigateToConfiguration()
            }
        }
        is TermsAndConditionsUiState.Error -> {
            LaunchedEffect(Unit) {
                Toast.makeText(
                    context,
                    (termsAndConditionsUiState as TermsAndConditionsUiState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.resetFlow()
            }
        }

        else -> {}
    }
    
    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SalesBImage(modifier = Modifier.wrapContentHeight())
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.manage_your_sales_with_ease),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Image(
                painter = painterResource(id = R.drawable.termsandconditions),
                contentScale = ContentScale.Fit,
                contentDescription = "Terms And Conditions"
            )
        }

        Column(
            modifier =  Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        ) {
            TermsAndCondtionsUi { isEnabled ->
                enabled = isEnabled
            }
            DefaultButton(
                text = stringResource(R.string.accept),
                enabled = enabled,
                isLoading = isLoading,
                onClick = {
                    viewModel.acceptTermsAndConditions()
                }
            )
        }
    }
}

@Preview
@Composable
fun TermsAndConditionsScreenPreview() {
    TermsAndConditionsScreen(
        navigateToConfiguration = {}
    )
}