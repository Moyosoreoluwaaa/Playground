package com.playground.loose

import androidx.media3.common.Player

/**
 * Extension functions for RepeatMode conversions
 */

/**
 * Convert RepeatMode enum to Player.RepeatMode constant
 */
//fun RepeatMode.toPlayerRepeatMode(): Int {
//    return when (this) {
//        RepeatMode.OFF -> Player.REPEAT_MODE_OFF
//        RepeatMode.ONE -> Player.REPEAT_MODE_ONE
//        RepeatMode.ALL -> Player.REPEAT_MODE_ALL
//    }
//}

/**
 * Convert Player.RepeatMode constant to RepeatMode enum
 */
fun Int.toRepeatMode(): RepeatMode {
    return when (this) {
        Player.REPEAT_MODE_OFF -> RepeatMode.OFF
        Player.REPEAT_MODE_ONE -> RepeatMode.ONE
        Player.REPEAT_MODE_ALL -> RepeatMode.ALL
        else -> RepeatMode.OFF
    }
}