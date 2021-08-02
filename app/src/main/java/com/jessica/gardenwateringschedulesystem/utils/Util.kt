package com.jessica.gardenwateringschedulesystem.utils

import com.here.android.mpa.routing.Maneuver
import com.jessica.gardenwateringschedulesystem.R
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

fun turnToText(turn: Maneuver.Turn) = when(turn) {
    Maneuver.Turn.HEAVY_LEFT,
    Maneuver.Turn.KEEP_LEFT,
    Maneuver.Turn.LIGHT_LEFT,
    Maneuver.Turn.QUITE_LEFT -> "Belok kiri"
    Maneuver.Turn.HEAVY_RIGHT,
    Maneuver.Turn.KEEP_RIGHT,
    Maneuver.Turn.LIGHT_RIGHT,
    Maneuver.Turn.QUITE_RIGHT -> "Belok kanan"
    Maneuver.Turn.KEEP_MIDDLE,
    Maneuver.Turn.NO_TURN -> "Lurus"
    Maneuver.Turn.RETURN -> "Putar balik"
    Maneuver.Turn.ROUNDABOUT_1,
    Maneuver.Turn.ROUNDABOUT_2,
    Maneuver.Turn.ROUNDABOUT_3,
    Maneuver.Turn.ROUNDABOUT_4,
    Maneuver.Turn.ROUNDABOUT_5,
    Maneuver.Turn.ROUNDABOUT_6,
    Maneuver.Turn.ROUNDABOUT_7,
    Maneuver.Turn.ROUNDABOUT_8,
    Maneuver.Turn.ROUNDABOUT_9,
    Maneuver.Turn.ROUNDABOUT_10,
    Maneuver.Turn.ROUNDABOUT_11,
    Maneuver.Turn.ROUNDABOUT_12 -> "Lewati Putaran"
    else -> "Lurus"
}

fun turnIcon(turn: Maneuver.Turn) = when(turn) {
    Maneuver.Turn.HEAVY_LEFT,
    Maneuver.Turn.KEEP_LEFT,
    Maneuver.Turn.LIGHT_LEFT,
    Maneuver.Turn.QUITE_LEFT -> R.drawable.turn_left
    Maneuver.Turn.HEAVY_RIGHT,
    Maneuver.Turn.KEEP_RIGHT,
    Maneuver.Turn.LIGHT_RIGHT,
    Maneuver.Turn.QUITE_RIGHT -> R.drawable.turn_right
    Maneuver.Turn.KEEP_MIDDLE,
    Maneuver.Turn.NO_TURN -> R.drawable.up_straight_arrow
    Maneuver.Turn.RETURN,
    Maneuver.Turn.ROUNDABOUT_1,
    Maneuver.Turn.ROUNDABOUT_2,
    Maneuver.Turn.ROUNDABOUT_3,
    Maneuver.Turn.ROUNDABOUT_4,
    Maneuver.Turn.ROUNDABOUT_5,
    Maneuver.Turn.ROUNDABOUT_6,
    Maneuver.Turn.ROUNDABOUT_7,
    Maneuver.Turn.ROUNDABOUT_8,
    Maneuver.Turn.ROUNDABOUT_9,
    Maneuver.Turn.ROUNDABOUT_10,
    Maneuver.Turn.ROUNDABOUT_11,
    Maneuver.Turn.ROUNDABOUT_12 -> R.drawable.u_turn
    else -> R.drawable.up_straight_arrow
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}