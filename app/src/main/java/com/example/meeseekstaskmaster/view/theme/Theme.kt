package com.example.meeseekstaskmaster.view.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PortalGreen,
    onPrimary = SpaceDark,
    primaryContainer = PortalGreenDark,
    onPrimaryContainer = PortalGreenBright,
    secondary = MeeseeksCyan,
    onSecondary = SpaceDark,
    secondaryContainer = CardBackground,
    onSecondaryContainer = MeeseeksBlue,
    tertiary = MeeseeksHairOrange,
    background = SpaceDark,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextMuted,
    error = StressCrisisColor,
    onError = TextWhite
)

@Composable
fun MeeseeksTaskMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // La aplicación mantiene un estilo oscuro sci-fi espacial de Rick y Morty
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SpaceDark.toArgb()
                window.navigationBarColor = SpaceDark.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
