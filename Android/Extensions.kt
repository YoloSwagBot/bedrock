package com.appstr.ftp.util

import android.content.res.Resources

// ================================================================================================= /Pixel Density

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

// ================================================================================================= \Pixel Density

// ================================================================================================= /Any
fun Any?.isNull(): Boolean = this == null
val Any?.exists: Boolean get() = this != null
fun Any?.notNull(): Boolean = this != null
val Any?.notNull: Boolean get() = this != null
// ================================================================================================= \Any

// ================================================================================================= /Int


fun Int.isPos(): Boolean = this > 0
fun Int?.isPos(): Boolean = this?.let { it > 0 } ?: false

fun Int.isNeg(): Boolean = this < 0
fun Int?.isNeg(): Boolean = this?.let { it < 0 } ?: false

fun Int.gT(num: Int): Boolean = this > num
fun Int?.gT(num: Int): Boolean = this?.let { it > num } ?: false

fun Int.lT(num: Int): Boolean = this < num
fun Int?.lT(num: Int): Boolean = this?.let { it < num } ?: false

// ================================================================================================= \Int

// ================================================================================================= \String

fun String.isImageUrl(): Boolean = (this.endsWith(".png") || this.endsWith(".jpg"))
fun String.isGifUrl(): Boolean = this.endsWith(".gif")
fun String.isStreamableUrl(): Boolean = this.contains("https://streamable", ignoreCase = false)

// ================================================================================================= \String