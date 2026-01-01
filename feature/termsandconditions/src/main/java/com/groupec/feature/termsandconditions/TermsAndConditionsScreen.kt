package com.groupec.feature.termsandconditions

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
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
) {
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

    Column (modifier = Modifier.fillMaxSize()) {

        // Scrollable Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SalesBImage(modifier = Modifier.height(100.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.manage_your_sales_with_ease),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = R.drawable.termsandconditions),
                    contentDescription = "Terms And Conditions"
                )
            }
        }


        // Fixed Bottom
        Column(
            modifier =  Modifier.padding(vertical = 4.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            TermsAndCondtionsUi { isEnabled ->
                enabled = isEnabled
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                DefaultButton(
                    modifier = Modifier.fillMaxWidth(0.5f),
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
}

@Preview
@Composable
fun TermsAndConditionsScreenPreview() {
    TermsAndConditionsScreen(
        navigateToConfiguration = {}
    )
}