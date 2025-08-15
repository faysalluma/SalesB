package com.groupec.salesb


import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.ui.MainScreen
import com.groupec.salesb.core.ConnectivityManagerUtils
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.theme.Primary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val connectivityManagerUtils by lazy { ConnectivityManagerUtils(this, lifecycleScope) }

    private val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request bluetooth permission
        if (bluetoothPermissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }.not()
        ) {
            ActivityCompat.requestPermissions(this, bluetoothPermissions, 0)
        }

        // Request notification permission on  Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    200
                )
            }
        }

        enableEdgeToEdge()
        setContent {
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


