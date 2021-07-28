package com.jessica.gardenwateringschedulesystem.utils

import java.text.SimpleDateFormat
import java.util.*

fun monthNumberToString(month: Int) = when(month) {
    Calendar.JANUARY -> "Januari"
    Calendar.FEBRUARY -> "Februari"
    Calendar.MARCH -> "Maret"
    Calendar.APRIL -> "April"
    Calendar.MAY -> "Mei"
    Calendar.JUNE -> "Juni"
    Calendar.JULY -> "Juli"
    Calendar.AUGUST -> "Agustus"
    Calendar.SEPTEMBER -> "September"
    Calendar.OCTOBER -> "Oktober"
    Calendar.NOVEMBER -> "November"
    Calendar.DECEMBER -> "Desember"
    else -> "Januari"
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}