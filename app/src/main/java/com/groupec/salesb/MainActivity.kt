package com.groupec.salesb


import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.ui.MainScreen
import com.groupec.salesb.core.ConnectivityManagerUtils
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.theme.Primary
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.groupec.salesb.navigation.NavigationItem

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var navController: NavHostController ? = null
    private val connectivityManagerUtils by lazy { ConnectivityManagerUtils(this, lifecycleScope) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            val connectionState by connectivityManagerUtils.connectionAsStateFlow.collectAsStateWithLifecycle()
            SalesBAppTheme {
                if (!isTablet()) {
                    AppAlertInfoDialog(
                        title = stringResource(id = R.string.app_name),
                        titleColor = Primary,
                        message = stringResource(id = R.string.error_tablet_desc),
                        confirmButtonText = stringResource(id = R.string.close_app),
                        onConfirmButton = { /* Call default onConfirmButton action */ },
                        closing = this
                    )
                } else {
                    MainScreen(navController!!, connectionState)
                }
            }
        }
        // Add launch mode singleTop on manifest to handle new intent
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData: Uri? = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.also { email ->
                // navController?.navigate(NavigationItem.ChangePassword.route.plus("?firstLogin=true&email={email}"))
            }
        }
    }

    @Composable
    fun isTablet(): Boolean {
        val configuration = LocalConfiguration.current
        return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.screenWidthDp > 840
        } else {
            configuration.screenWidthDp > 600
        }
    }

}


