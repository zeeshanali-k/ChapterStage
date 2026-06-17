package com.devscion.chapterstage.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class StageColors(
    val background: Color = Color(0xFF0A0E18),
    val backgroundHigh: Color = Color(0xFF0C111D),
    val surface: Color = Color(0xFF131A29),
    val surfaceElement: Color = Color(0x99192234),
    val surfaceHigh: Color = Color(0xFF202B41),
    val line: Color = Color(0x12FFFFFF),
    val lineHigh: Color = Color(0x22FFFFFF),
    val primary: Color = Color(0xFF7C5CFF),
    val primarySoft: Color = Color(0x297C5CFF),
    val primaryText: Color = Color(0xFFC9BBFF),
    val textPrimary: Color = Color(0xFFF1F4FA),
    val textSecondary: Color = Color(0xFF98A4BC),
    val textTertiary: Color = Color(0xFF5C6982),
    val success: Color = Color(0xFF2EE59D),
    val warning: Color = Color(0xFFF6C85F),
    val error: Color = Color(0xFFFF5C7A),
    val cyan: Color = Color(0xFF22D3EE),
    val pink: Color = Color(0xFFFF8BD1),
)

@Immutable
data class Spacing(
    val xxSmall: Dp = 2.dp,
    val xSmall: Dp = 4.dp,
    val extraSmall: Dp = xSmall,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val screenPadding: Dp = 20.dp,
    val wideScreenPadding: Dp = 32.dp,
    val maxPaneWidth: Dp = 460.dp,
    val maxContentWidth: Dp = 1040.dp,
    val wideBreakpoint: Dp = 760.dp,
)

private val LocalStageColors = staticCompositionLocalOf { StageColors() }
private val LocalSpacing = staticCompositionLocalOf { Spacing() }

val MaterialTheme.stageColors: StageColors
    @Composable
    @ReadOnlyComposable
    get() = LocalStageColors.current

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

private val ChapterStageColorScheme: ColorScheme = darkColorScheme(
    primary = StageColors().primary,
    onPrimary = Color.White,
    primaryContainer = StageColors().primarySoft,
    onPrimaryContainer = StageColors().primaryText,
    secondary = StageColors().cyan,
    onSecondary = StageColors().background,
    tertiary = StageColors().success,
    onTertiary = StageColors().background,
    background = StageColors().background,
    onBackground = StageColors().textPrimary,
    surface = StageColors().surface,
    onSurface = StageColors().textPrimary,
    surfaceVariant = StageColors().surfaceHigh,
    onSurfaceVariant = StageColors().textSecondary,
    outline = StageColors().lineHigh,
    error = StageColors().error,
    onError = Color.White,
)

private val ChapterStageTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        lineHeight = 27.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp,
    ),
)

private val ChapterStageShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(18.dp),
)

@Composable
fun ChapterStageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = StageColors()

    CompositionLocalProvider(
        LocalStageColors provides colors,
        LocalSpacing provides Spacing(),
    ) {
        MaterialTheme(
            colorScheme = ChapterStageColorScheme,
            typography = ChapterStageTypography,
            shapes = ChapterStageShapes,
            content = content,
        )
    }
}
