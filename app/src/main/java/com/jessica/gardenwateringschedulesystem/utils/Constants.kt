package com.jessica.gardenwateringschedulesystem.utils

import java.util.concurrent.Executors

const val SCHEDULES = "schedules"
const val USERS = "users"
const val ABOUT = "about"
private val SINGLE_EXECUTOR = Executors.newSingleThreadExecutor()

fun executeThread(f: () -> Unit) {
    SINGLE_EXECUTOR.execute(f)
}