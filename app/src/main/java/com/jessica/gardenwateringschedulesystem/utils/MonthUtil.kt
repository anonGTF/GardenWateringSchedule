package com.jessica.gardenwateringschedulesystem.utils

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