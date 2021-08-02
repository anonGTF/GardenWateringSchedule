package com.jessica.gardenwateringschedulesystem.utils

import com.here.android.mpa.common.GeoCoordinate
import java.util.concurrent.Executors

const val SCHEDULES = "schedules"
const val USERS = "users"
const val ABOUT = "about"
const val ROUTES = "routes"
const val ROUTE_WAYPOINTS = "route_waypoints"
const val HOME_LATITUDE = -7.2783266
const val HOME_LONGITUDE = 112.7604853
private val SINGLE_EXECUTOR = Executors.newSingleThreadExecutor()

fun executeThread(f: () -> Unit) {
    SINGLE_EXECUTOR.execute(f)
}