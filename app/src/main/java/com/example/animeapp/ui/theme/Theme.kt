package com.example.animeapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RedAccent,
    onPrimary = LightText,
    secondary = SoftRed,
    onSecondary = DeepBlack,
    background = DeepBlack,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    surfaceVariant = CardRed,
    onSurfaceVariant = MutedText,
    error = ColorError,
    onError = LightText
)

private val LightColorScheme = lightColorScheme(
    primary = DarkRed,
    onPrimary = LightText,
    secondary = RedAccent,
    onSecondary = LightText,
    background = Color(0xFFFFF7F7),
    onBackground = Color(0xFF211111),
    surface = Color(0xFFFFFBFB),
    onSurface = Color(0xFF211111),
    surfaceVariant = Color(0xFFFFE0E0),
    onSurfaceVariant = Color(0xFF5C3333),
    error = ColorError,
    onError = LightText
)

@Composable
fun AnimeAppTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}