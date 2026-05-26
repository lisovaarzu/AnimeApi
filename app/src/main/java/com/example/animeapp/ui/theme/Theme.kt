package com.example.animeapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppColorScheme = darkColorScheme(
    primary = AppRed,
    onPrimary = AppText,
    secondary = AppRedDark,
    onSecondary = AppText,
    background = AppBlack,
    onBackground = AppText,
    surface = AppDarkWine,
    onSurface = AppText,
    surfaceVariant = AppWine,
    onSurfaceVariant = AppTextSoft,
    error = AppError,
    onError = AppText
)

@Composable
fun AnimeAppTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = AppBlack.toArgb()
            window.navigationBarColor = AppBlack.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}