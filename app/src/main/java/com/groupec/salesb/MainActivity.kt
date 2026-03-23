package com.groupec.salesb


import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.groupec.salesb.core.ConnectivityManagerUtils
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val connectivityManagerUtils by lazy { ConnectivityManagerUtils(this, lifecycleScope) }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val connectionState by connectivityManagerUtils.connectionAsStateFlow.collectAsStateWithLifecycle()
            val windowSizeClass = calculateWindowSizeClass(this)
            val isExpandedWidth by remember(windowSizeClass) {
                derivedStateOf {
                    windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
                }
            }
            SalesBAppTheme {
                /*if (!isTablet()) {
                    AppAlertInfoDialog(
                        title = stringResource(id = R.string.app_name),
                        titleColor = Primary,
                        message = stringResource(id = R.string.error_tablet_desc),
                        confirmButtonText = stringResource(id = R.string.close_app),
                        onConfirmButton = { *//* Call default onConfirmButton action *//* },
                        closing = this
                    )
                } else {
                    MainScreen(connectionState)
                }*/
                MainScreen(connectionState, isExpandedWidth, isTablet())
            }
        }
    }

    @Composable
    fun isTablet(): Boolean {
        /*
        WidthSizeClass
        Compact → largeur < 600 dp // Smartphone portrait
        Medium → largeur entre 600 dp et 840 dp // Tablette petite ou téléphone en paysage
        Expanded → largeur ≥ 840 dp // Grande tablette ou desktop
         */
        val configuration = LocalConfiguration.current
        return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.screenWidthDp > 840
        } else {
            configuration.screenWidthDp > 600
        }
    }
}

