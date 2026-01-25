package com.groupec.salesb.core.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

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
