package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF93C5FD),
    onPrimary = Color(0xFF1E3A8A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF5EEAD4),
    onSecondary = Color(0xFF042F2E),
    secondaryContainer = Color(0xFF134E4A),
    onSecondaryContainer = Color(0xFFCCFBF1),
    tertiary = Color(0xFDFCD34D),
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = Color(0xFFFEF3C7),
    background = AcademicDarkBg,
    onBackground = Color(0xFFF8FAFC),
    surface = AcademicDarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = AcademicDarkCard,
    onSurfaceVariant = Color(0xFFCBD5E1)
)

private val LightColorScheme = lightColorScheme(
    primary = AcademicNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = AcademicNavyContainer,
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = AcademicTealSecondary,
    onSecondary = Color.White,
    secondaryContainer = AcademicTealContainer,
    onSecondaryContainer = Color(0xFF042F2E),
    tertiary = AcademicGoldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = AcademicGoldContainer,
    onTertiaryContainer = Color(0xFF78350F),
    background = AcademicLightBg,
    onBackground = Color(0xFF0F172A),
    surface = AcademicLightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = AcademicLightCard,
    onSurfaceVariant = Color(0xFF334155)
)

@Composable
fun AcademicResearchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false for consistent academic branding
    content: @Composable () -> Unit,
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
