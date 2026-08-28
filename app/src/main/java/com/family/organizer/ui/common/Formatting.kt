package com.family.organizer.ui.common

import androidx.compose.ui.graphics.Color
import com.family.organizer.ui.theme.SeriesPalette
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.round

/** "15700.0" -> "15 700 ₽" — единый формат сумм по всему приложению. */
fun formatMoney(amount: Double): String {
    val rounded = round(amount).toLong()
    val negative = rounded < 0
    val digits = kotlin.math.abs(rounded).toString()
    val grouped = digits.reversed().chunked(3).joinToString(" ").reversed()
    return (if (negative) "−" else "") + grouped + " ₽"
}

/** colorSlot (1-8) -> цвет из общей палитры (та же, что в family-app-mockup.html). */
fun colorForSlot(slot: Int): Color {
    val index = (slot - 1).coerceIn(0, SeriesPalette.size - 1)
    return SeriesPalette[index]
}

/** Мягкий фон под иконку категории/копилки — как rgba(color, 0.14) в макете. */
fun iconBackgroundForSlot(slot: Int): Color = colorForSlot(slot).copy(alpha = 0.14f)

/** Относится ли момент времени (millis) к текущему календарному месяцу устройства. */
fun isCurrentMonth(epochMillis: Long): Boolean {
    val date = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    return YearMonth.from(date) == YearMonth.now()
}

/** "сегодня" / "вчера" / "13.08" — короткая подпись даты для лент операций/задач. */
fun formatDateLabel(epochMillis: Long): String {
    val date = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    return formatDateLabel(date)
}

/** То же самое, но для LocalDate.toEpochDay() (события календаря). */
fun formatEpochDayLabel(epochDay: Long): String = formatDateLabel(LocalDate.ofEpochDay(epochDay))

private fun formatDateLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "сегодня"
        today.minusDays(1) -> "вчера"
        today.plusDays(1) -> "завтра"
        else -> "%d.%02d".format(date.dayOfMonth, date.monthValue)
    }
}

