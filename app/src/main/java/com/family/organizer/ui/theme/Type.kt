package com.family.organizer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Системный шрифт по умолчанию (как в HTML-макете: system-ui) — Compose
// использует системный sans без дополнительных подключений.
//
// Вся шкала задана явно (а не только часть ролей) и заметно компактнее
// стандартной Material3 — так ни один экран не «плывёт» из-за длинных
// русских слов и подписей, а не только нижнее меню.
val FamilyOrganizerTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 34.sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 19.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 11.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 10.sp),
)
