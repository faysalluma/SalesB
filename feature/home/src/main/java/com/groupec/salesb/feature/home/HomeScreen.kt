package com.groupec.salesb.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.groupec.salesb.core.Period
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.RecentActivity
import com.groupec.salesb.core.model.data.RecentActivityType
import com.groupec.salesb.core.ui.ComposableLifecycle

private const val PHONE_RECENT_ACTIVITIES_LIMIT = 2
private const val TABLET_RECENT_ACTIVITIES_LIMIT = 5

@Composable
fun HomeScreen(
    isExpandedWidth: Boolean,
    isTablet: Boolean,
    navigateToSaleList: () -> Unit,
    navigateToProduct: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { }
    val userStore by viewModel.userStore.collectAsStateWithLifecycle()
    val parameter by viewModel.parameter.collectAsStateWithLifecycle()
    val totalSales by viewModel.totalSales.collectAsStateWithLifecycle()
    val totalAmountSales by viewModel.totalAmountSales.collectAsStateWithLifecycle()
    val totalAlertSeuilProducts by viewModel.totalAlertSeuilProducts.collectAsStateWithLifecycle()
    val recentActivities by viewModel.recentActivitiesUiState.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val recentActivitiesLimit = if (isTablet) {
        TABLET_RECENT_ACTIVITIES_LIMIT
    } else {
        PHONE_RECENT_ACTIVITIES_LIMIT
    }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ComposableLifecycle(
        onResume = { viewModel.refreshDashboard(recentActivitiesLimit) },
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        val spacer = if (isExpandedWidth) 40.dp else 24.dp

        if (totalAlertSeuilProducts > 0) {
            LowInventoryBanner(
                count = totalAlertSeuilProducts,
                onSeeStock = navigateToProduct,
            )
        }

        DashboardHeader(
            userName = userStore.nomprenom.substringBefore(" ").trim(),
            selectedPeriod = selectedPeriod,
            onPeriodChange = { period ->
                viewModel.onPeriodChange(period)
                viewModel.refreshDashboard(recentActivitiesLimit)
            },
        )

        Spacer(Modifier.height(spacer))

        if (isExpandedWidth) {
            Box(
                modifier = Modifier.fillMaxSize(0.85f).align(Alignment.CenterHorizontally)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(spacer),
                    verticalAlignment = Alignment.Top,
                ) {
                    RevenueCard(
                        totalSales = totalSales,
                        totalAmountSales = totalAmountSales,
                        devise = parameter.devise,
                        onClick = navigateToSaleList,
                        modifier = Modifier
                            .weight(0.4f).fillMaxHeight(),
                    )
                    ActivitiesCard(
                        activitiesUiState = recentActivities,
                        devise = parameter.devise,
                        showSeeAll = true,
                        onSeeAll = navigateToSaleList,
                        modifier = Modifier.weight(0.6f).fillMaxHeight(),
                    )
                }
            }
        } else {
            RevenueCard(
                totalSales = totalSales,
                totalAmountSales = totalAmountSales,
                devise = parameter.devise,
                onClick = navigateToSaleList,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(spacer.plus(8.dp)))
            ActivitiesCard(
                activitiesUiState = recentActivities,
                devise = parameter.devise,
                showSeeAll = false,
                onSeeAll = navigateToSaleList,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun LowInventoryBanner(
    count: Int,
    onSeeStock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    imageVector = AppIcons.Warning,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp),
                )
                Text(
                    text = pluralStringResource(R.plurals.low_inventory_alert, count, count),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            TextButton(
                onClick = onSeeStock,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(
                    text = stringResource(R.string.see_stock),
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    userName: String,
    selectedPeriod: Period,
    onPeriodChange: (Period) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (userName.isBlank()) {
                stringResource(R.string.greeting)
            } else {
                stringResource(R.string.greeting_with_name, userName)
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f).padding(top = 4.dp),
        )
        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodChange = onPeriodChange,
        )
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: Period,
    onPeriodChange: (Period) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            contentPadding = ButtonDefaults.ContentPadding,
        ) {
            Text(
                text = selectedPeriod.getTitle(context),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Period.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.getTitle(context)) },
                    onClick = {
                        expanded = false
                        onPeriodChange(period)
                    },
                )
            }
        }
    }
}

@Composable
private fun RevenueCard(
    totalSales: Int,
    totalAmountSales: Double,
    devise: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = Color.Unspecified,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.revenue),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(
                        R.string.amount_with_currency,
                        totalAmountSales.formatAmount(),
                        devise,
                    ),
                    style = MaterialTheme.typography.headlineLarge,
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.62f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = AppIcons.MySales,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            text = pluralStringResource(R.plurals.sales_count, totalSales, totalSales),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivitiesCard(
    activitiesUiState: UIState<List<RecentActivity>>,
    devise: String,
    showSeeAll: Boolean,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerModifier = if (showSeeAll) {
        modifier
            //.clip(RoundedCornerShape(20.dp))
            //.background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .padding(16.dp)
    } else {
        modifier
    }

    Column(
        modifier = containerModifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = AppIcons.ListAlt,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp).padding(top = 4.dp)
                )
                Text(
                    text = stringResource(R.string.recent_activities),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            if (showSeeAll) {
                TextButton(onClick = onSeeAll) {
                    Text(stringResource(R.string.see_all))
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = if (showSeeAll) Alignment.Center else Alignment.TopStart,
        ) {
            when (activitiesUiState) {
                UIState.Loading -> AppLoadingScreen()

                is UIState.Error -> Text(
                    text = stringResource(R.string.activities_unavailable),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )

                is UIState.Success -> {
                    if (activitiesUiState.data.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_recent_activity),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(if (showSeeAll) 0.dp else 12.dp),
                        ) {
                            activitiesUiState.data.forEachIndexed { index, activity ->
                                ActivityRow(
                                    activity = activity,
                                    devise = devise,
                                    outlined = !showSeeAll,
                                )
                                if (showSeeAll && index < activitiesUiState.data.lastIndex) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(
    activity: RecentActivity,
    devise: String,
    outlined: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        //color = MaterialTheme.colorScheme.surfaceContainerLowest,
        //color = if (outlined) MaterialTheme.colorScheme.surface else Color.Transparent,
        border = if (outlined) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (activity.type) {
                            RecentActivityType.Sale -> AppIcons.ShoppingBag
                            RecentActivityType.Output -> AppIcons.Output
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activityTitle(activity),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = relativeActivityTime(activity),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = activityAmount(activity, devise),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = when (activity.type) {
                    RecentActivityType.Sale -> MaterialTheme.colorScheme.primary
                    RecentActivityType.Output -> MaterialTheme.colorScheme.error
                },
            )
        }
    }
}

@Composable
private fun activityTitle(activity: RecentActivity): String = when (activity.type) {
    RecentActivityType.Sale -> activity.id?.let { stringResource(R.string.sale_activity, it) }
        ?: stringResource(R.string.sale_activity_without_id)

    RecentActivityType.Output -> activity.description.takeUnless { it.isNullOrBlank() }
        ?: activity.id?.let { stringResource(R.string.output_activity, it) }
        ?: stringResource(R.string.output_activity_without_id)
}

@Composable
private fun relativeActivityTime(activity: RecentActivity): String {
    val date = activity.occurredAt ?: return stringResource(R.string.activity_date_unknown)
    return DateUtils.getRelativeTimeSpanString(
        date.time,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE,
    ).toString()
}

@Composable
private fun activityAmount(activity: RecentActivity, devise: String): String {
    val amount = activity.amount.formatAmount()
    return when (activity.type) {
        RecentActivityType.Sale -> stringResource(R.string.positive_amount, amount, devise)
        RecentActivityType.Output -> stringResource(R.string.negative_amount, amount, devise)
    }
}
