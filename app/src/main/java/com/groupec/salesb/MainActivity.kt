package com.groupec.salesb


import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.ui.MainScreen
import com.groupec.salesb.core.ConnectivityManagerUtils
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.theme.Red
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val connectivityManagerUtils by lazy { ConnectivityManagerUtils(this, lifecycleScope) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val connectionState by connectivityManagerUtils.connectionAsStateFlow.collectAsStateWithLifecycle()
            SalesBAppTheme {
                if (!isTablet()) {
                    AppAlertInfoDialog(
                        title = stringResource(id = R.string.app_name),
                        icon = null,
                        tintIcon = null,
                        message = stringResource(id = R.string.error_tablet_desc),
                        textButton = stringResource(id = R.string.close_app) ,
                        closing = this
                    )
                } else {
                    MainScreen(connectionState)
                }
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


