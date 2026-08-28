package com.family.organizer.ui.theme

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

private val LightColors = lightColorScheme(
    primary = SeriesBlueLight,
    onPrimary = Color.White,
    background = LightPagePlane,
    surface = LightSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightGridline,
    onSurfaceVariant = LightTextSecondary,
    error = StatusCritical,
)

private val DarkColors = darkColorScheme(
    primary = SeriesBlueDark,
    onPrimary = Color.White,
    background = DarkPagePlane,
    surface = DarkSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkGridline,
    onSurfaceVariant = DarkTextSecondary,
    error = StatusCritical,
)

@Composable
fun FamilyOrganizerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Динамические (Material You) цвета выключены по умолчанию — держим
    // собственную палитру из макета единой на всех устройствах.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FamilyOrganizerTypography,
        content = content,
    )
}
