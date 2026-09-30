package com.example.mazebuilderduel.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class SoundType {
    PATH_CLAIMED,
    WALL_PLACED,
    INVALID_WALL,
    TURN_SWITCH,
    WIN
}

class SoundManager {

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 44100

    private val pathSoundBytes by lazy { generatePathClaimTone() }
    private val wallSoundBytes by lazy { generateWallTone() }
    private val invalidWallBytes by lazy { generateInvalidWallTone() }
    private val turnSwitchBytes by lazy { generateTurnSwitchTone() }
    private val winSoundBytes by lazy { generateWinTone() }

    fun play(type: SoundType, enabled: Boolean) {
        if (!enabled) return

        val pcmData = when (type) {
            SoundType.PATH_CLAIMED -> pathSoundBytes
            SoundType.WALL_PLACED -> wallSoundBytes
            SoundType.INVALID_WALL -> invalidWallBytes
            SoundType.TURN_SWITCH -> turnSwitchBytes
            SoundType.WIN -> winSoundBytes
        }

        audioScope.launch {
            playPcm(pcmData)
        }
    }

    private fun playPcm(pcmData: ShortArray) {
        try {
            val bufferSize = pcmData.size * 2
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(pcmData, 0, pcmData.size)
            audioTrack.play()

            // Release after playback finishes
            val durationMs = (pcmData.size * 1000L) / sampleRate
            Thread.sleep(durationMs + 20)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Audio failure safe fallback
        }
    }

    private fun generatePathClaimTone(): ShortArray {
        val durationMs = 85
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 520.0 + progress * 140.0 // 520Hz -> 660Hz
            val envelope = (1.0 - progress) * 0.7
            val sample = sin(2.0 * PI * freq * i / sampleRate) * envelope
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateWallTone(): ShortArray {
        val durationMs = 95
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val envelope = exp(-progress * 5.0) * 0.8
            val sample = (sin(2.0 * PI * 240.0 * i / sampleRate) * 0.7 +
                    sin(2.0 * PI * 120.0 * i / sampleRate) * 0.3) * envelope
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateInvalidWallTone(): ShortArray {
        val durationMs = 140
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            // Pulsed buzz
            val pulse = if ((progress * 10).toInt() % 2 == 0) 0.8 else 0.2
            val envelope = (1.0 - progress) * pulse
            val sample = sin(2.0 * PI * 180.0 * i / sampleRate) * envelope
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateTurnSwitchTone(): ShortArray {
        val durationMs = 45
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val envelope = (1.0 - progress) * 0.4
            val sample = sin(2.0 * PI * 440.0 * i / sampleRate) * envelope
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    private fun generateWinTone(): ShortArray {
        val durationMs = 380
        val numSamples = (sampleRate * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        // C5, E5, G5, C6 arpeggio chord
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        val noteLength = numSamples / notes.size

        for (i in 0 until numSamples) {
            val noteIndex = (i / noteLength).coerceAtMost(notes.size - 1)
            val freq = notes[noteIndex]
            val progressInNote = (i % noteLength).toDouble() / noteLength
            val globalProgress = i.toDouble() / numSamples
            val envelope = (1.0 - progressInNote * 0.4) * (1.0 - globalProgress * 0.3) * 0.8
            val sample = sin(2.0 * PI * freq * i / sampleRate) * envelope
            buffer[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }
}
