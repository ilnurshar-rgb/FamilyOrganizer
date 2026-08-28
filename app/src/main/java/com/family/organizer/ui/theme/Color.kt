package com.family.organizer.ui.theme

import androidx.compose.ui.graphics.Color

// Палитра взята из HTML-макета приложения — единый визуальный язык
// между дизайном и реальным кодом.

// Light
val LightSurface = Color(0xFFFCFCFB)
val LightPagePlane = Color(0xFFF9F9F7)
val LightTextPrimary = Color(0xFF0B0B0B)
val LightTextSecondary = Color(0xFF52514E)
val LightGridline = Color(0xFFE1E0D9)

// Dark
val DarkSurface = Color(0xFF1A1A19)
val DarkPagePlane = Color(0xFF0D0D0D)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFFC3C2B7)
val DarkGridline = Color(0xFF2C2C2A)

// Категориальная палитра (серии 1-8), одинаковый порядок в обеих темах
val SeriesBlueLight = Color(0xFF2A78D6)
val SeriesBlueDark = Color(0xFF3987E5)
val SeriesOrangeLight = Color(0xFFEB6834)
val SeriesAquaLight = Color(0xFF1BAF7A)
val SeriesYellowLight = Color(0xFFEDA100)
val SeriesMagentaLight = Color(0xFFE87BA4)
val SeriesGreenLight = Color(0xFF008300)
val SeriesVioletLight = Color(0xFF4A3AA7)
val SeriesRedLight = Color(0xFFE34948)

val StatusGood = Color(0xFF0CA30C)
val StatusCritical = Color(0xFFD03B3B)

// Индексированная палитра 1-8 — соответствует colorSlot в данных
// (категории, копилки, участники семьи). Единый порядок с CSS-переменными
// --series-1..8 из family-app-mockup.html.
val SeriesPalette = listOf(
    SeriesBlueLight,
    SeriesOrangeLight,
    SeriesAquaLight,
    SeriesYellowLight,
    SeriesMagentaLight,
    SeriesGreenLight,
    SeriesVioletLight,
    SeriesRedLight,
)
