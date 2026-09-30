package com.example.mazebuilderduel.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

enum class HapticFeedbackType {
    PATH_CLAIM,
    WALL_PLACE,
    INVALID_WARNING,
    WIN
}

class HapticManager(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun performHaptic(type: HapticFeedbackType, enabled: Boolean, view: View? = null) {
        if (!enabled) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator?.hasVibrator() == true) {
                when (type) {
                    HapticFeedbackType.PATH_CLAIM -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                        } else {
                            vibrator?.vibrate(VibrationEffect.createOneShot(12, 50))
                        }
                    }
                    HapticFeedbackType.WALL_PLACE -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                        } else {
                            vibrator?.vibrate(VibrationEffect.createOneShot(24, 100))
                        }
                    }
                    HapticFeedbackType.INVALID_WARNING -> {
                        val timings = longArrayOf(0, 30, 40, 40)
                        val amplitudes = intArrayOf(0, 180, 0, 180)
                        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    HapticFeedbackType.WIN -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
                        } else {
                            val timings = longArrayOf(0, 50, 40, 80)
                            val amplitudes = intArrayOf(0, 150, 0, 255)
                            vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                        }
                    }
                }
            } else if (view != null) {
                when (type) {
                    HapticFeedbackType.PATH_CLAIM -> view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    HapticFeedbackType.WALL_PLACE -> view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    HapticFeedbackType.INVALID_WARNING -> view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    HapticFeedbackType.WIN -> view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }
            }
        } catch (_: Exception) {
            // Ignore haptic failures
        }
    }
}
