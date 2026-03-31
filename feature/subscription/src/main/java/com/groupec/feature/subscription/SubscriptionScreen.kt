package com.groupec.feature.subscription

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.Silver2
import com.groupec.salesb.core.designsystem.theme.Silver3
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.ui.ComposableLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: SubscriptionViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val uiState by viewModel.uiState.collectAsState()
    val parameterState by viewModel.parameterState.collectAsState()

    LaunchedEffect(uiState.pendingMessage) {
        uiState.pendingMessage?.let { message ->
            snackbarHostState.showSnackbar(
                SnackbarVisualsWithState(
                    message = message,
                    isError = uiState.pendingMessageIsError
                )
            )
            viewModel.consumeMessage()
        }
    }

    ComposableLifecycle(
        onResume = {
            // Returning from Google Play or system settings should immediately refresh catalog/purchases.
            viewModel.refresh()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SubscriptionHeader()

        Spacer(modifier = Modifier.height(24.dp))

        PlanCard(
            title = stringResource(R.string.subscription_free_plan),
            price = stringResource(R.string.subscription_free_price),
            period = stringResource(R.string.subscription_period_month_short),
            features = listOf(
                PlanFeature(stringResource(R.string.subscription_free_feature_sales), true),
                PlanFeature(stringResource(R.string.subscription_free_feature_products), true),
                PlanFeature(stringResource(R.string.subscription_free_feature_stats), true),
                PlanFeature(stringResource(R.string.subscription_free_feature_invoice), true),
                PlanFeature(stringResource(R.string.subscription_free_feature_exports), false),
                PlanFeature(stringResource(R.string.subscription_free_feature_receipt), false)
            ),
            footer = {
                DefaultButton(
                    text = stringResource(R.string.subscription_current_plan),
                    enabled = false,
                    containerColor = Color(0xFFF0F3F9),
                    textcolor = Color(0xFF4C5B75)
                ) {}
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        PlanCard(
            title = stringResource(R.string.subscription_pro_plan),
            titleColor = Primary,
            price = uiState.proPrice,
            period = uiState.proPeriodLabel.ifBlank {
                stringResource(R.string.subscription_period_month_short)
            },
            recommendedLabel = stringResource(R.string.subscription_recommended),
            highlight = true,
            features = listOf(
                PlanFeature(stringResource(R.string.subscription_pro_feature_sales), true),
                PlanFeature(stringResource(R.string.subscription_pro_feature_products), true),
                PlanFeature(stringResource(R.string.subscription_free_feature_stats), true),
                PlanFeature(stringResource(R.string.subscription_free_feature_invoice), true),
                PlanFeature(stringResource(R.string.subscription_pro_feature_exports), true),
                PlanFeature(stringResource(R.string.subscription_pro_feature_receipt), true),
            ),
            footer = {
                // Billing can only start from an Activity context because Play opens a purchase sheet.
                DefaultButton(
                    text = when {
                        uiState.isProPlanActive -> stringResource(R.string.subscription_active_plan)
                        uiState.isLoading || uiState.isRefreshing -> stringResource(com.groupec.salesb.core.ui.R.string.loading)
                        else -> stringResource(R.string.subscription_upgrade_cta)
                    },
                    enabled = activity != null &&
                            uiState.isProPlanAvailable &&
                            !uiState.isPurchaseInProgress &&
                            !uiState.isLoading &&
                            !uiState.isRefreshing &&
                            !uiState.isProPlanActive,
                    isLoading = uiState.isPurchaseInProgress,
                    onClick = {
                        activity?.let(viewModel::startPurchase)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.subscription_payment_secured),
                    style = MaterialTheme.typography.bodySmall.copy(color = Silver3),
                    textAlign = TextAlign.Center
                )
            }
        )

        uiState.errorMessage?.let { errorMessage ->
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SubscriptionHeader() {
    Surface(
        modifier = Modifier.size(54.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE8F0FF)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = AppIcons.Premium,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.subscription_title),
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Medium
        ),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(R.string.subscription_subtitle),
        style = MaterialTheme.typography.bodyMedium.copy(
            color = Silver3
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    features: List<PlanFeature>,
    modifier: Modifier = Modifier,
    titleColor: Color = Color(0xFF5C6C89),
    recommendedLabel: String? = null,
    highlight: Boolean = false,
    footer: @Composable () -> Unit
) {
    val borderColor = if (highlight) Primary else Color.Transparent
    val backgroundColor = if (highlight) White else Color(0xFFFBFCFF)

    Box(modifier = modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (highlight) 1.5.dp else 1.dp,
                    color = if (highlight) borderColor else Color(0xFFE7ECF5),
                    shape = RoundedCornerShape(22.dp)
                ),
            shape = RoundedCornerShape(22.dp),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = titleColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                val priceText = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color(0xFF1C2437),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append(price)
                    }
                    append(" ")
                    withStyle(
                        SpanStyle(
                            color = Silver3,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(period)
                    }
                }

                Text(text = priceText)

                Spacer(modifier = Modifier.height(18.dp))

                features.forEach { feature ->
                    PlanFeatureRow(feature)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                footer()
            }
        }

        if (recommendedLabel != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(topEnd = 22.dp, bottomStart = 12.dp))
                    .background(Color(0xFF16A34A))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = recommendedLabel,
                    color = White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun PlanFeatureRow(feature: PlanFeature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconTint = if (feature.enabled) Primary else Color(0xFFF08B94)
        val textColor = if (feature.enabled) Color(0xFF3A465E) else Silver3

        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (feature.enabled) Color(0xFFE9F1FF) else Color(0xFFFFEEF0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (feature.enabled) AppIcons.CheckCircle else AppIcons.Delete,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(11.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = feature.label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = textColor,
                textDecoration = if (feature.enabled) null else TextDecoration.LineThrough
            )
        )
    }
}

private data class PlanFeature(
    val label: String,
    val enabled: Boolean
)

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SubscriptionScreenPreview() {
    SalesBAppTheme {
        SubscriptionScreen(
            snackbarHostState = SnackbarHostState(),
            onBackClick = {},
            onHelpClick = {}
        )
    }
}
