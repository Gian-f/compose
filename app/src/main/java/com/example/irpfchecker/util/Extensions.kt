package com.example.irpfchecker.util

import android.content.Context
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import java.util.Date

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return email.matches(emailRegex)
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun formatRelativeTime(date: Date): String {
    val currentTime = System.currentTimeMillis()
    return DateUtils.getRelativeTimeSpanString(date.time, currentTime, DateUtils.MINUTE_IN_MILLIS).toString()
}
