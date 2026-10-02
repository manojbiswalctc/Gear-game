package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class MechanicalAudioEngine {

    private val sampleRate = 22050
    private var isMuted = false
    private val scope = CoroutineScope(Dispatchers.Default)
    private var machineHumJob: Job? = null
    private var isMachineRunning = false
    private var currentHumFrequency = 80f

    fun setMuted(muted: Boolean) {
        isMuted = muted
        if (muted) stopMachineHum()
    }

    /**
     * Crisp metallic click when a gear snaps onto a mounting peg or meshes.
     */
    fun playGearSnap() {
        if (isMuted) return
        scope.launch {
            val durationMs = 60
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val baseFreq = 1800f
            val clickFreq = 3200f

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                val envelope = kotlin.math.exp(-t * 70f)
                val sample = (sin(2 * PI * baseFreq * t) * 0.6f + sin(2 * PI * clickFreq * t) * 0.4f) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.5f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Deep heavy clank when a large torque gear is seated.
     */
    fun playHeavyClank() {
        if (isMuted) return
        scope.launch {
            val durationMs = 120
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val baseFreq = 340f

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                val envelope = kotlin.math.exp(-t * 30f)
                val noise = (Random.nextFloat() * 2f - 1f) * 0.2f
                val sample = (sin(2 * PI * baseFreq * t) + noise) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.6f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Grinding friction sound when gears collide or overload occurs.
     */
    fun playGrindingJam() {
        if (isMuted) return
        scope.launch {
            val durationMs = 350
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                val envelope = (1f - (i.toFloat() / numSamples))
                val roughNoise = (Random.nextFloat() * 2f - 1f)
                val tone = sin(2 * PI * 180f * t)
                val sample = (roughNoise * 0.7f + tone * 0.3f) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.7f).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Steam pressure exhaust sound.
     */
    fun playSteamRelease() {
        if (isMuted) return
        scope.launch {
            val durationMs = 280
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / numSamples
                val envelope = sin(PI.toFloat() * t) * (1f - t * 0.5f)
                val whiteNoise = Random.nextFloat() * 2f - 1f
                buffer[i] = (whiteNoise * envelope * Short.MAX_VALUE * 0.35f).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Harmonious mechanical brass bell chime upon level completion.
     */
    fun playVictoryChime() {
        if (isMuted) return
        scope.launch {
            val durationMs = 800
            val numSamples = (sampleRate * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val frequencies = listOf(523.25f, 659.25f, 783.99f, 1046.50f) // C5, E5, G5, C6 chord

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                val envelope = kotlin.math.exp(-t * 3.5f)
                var sum = 0f
                for (f in frequencies) {
                    sum += sin(2 * PI.toFloat() * f * t)
                }
                val sample = (sum / frequencies.size) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.65f).toInt().toShort()
            }
            playPcmBuffer(buffer)
        }
    }

    /**
     * Start continuous background mechanical hum that modulates with machine RPM.
     */
    fun startMachineHum(initialRpm: Float = 60f) {
        if (isMuted || isMachineRunning) return
        isMachineRunning = true
        currentHumFrequency = (initialRpm * 1.5f).coerceIn(40f, 220f)

        machineHumJob = scope.launch {
            try {
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val bufferSize = minBufferSize.coerceAtLeast(2048)
                val track = AudioTrack.Builder()
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
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()
                val chunk = ShortArray(1024)
                var phase = 0f

                while (isActive && isMachineRunning) {
                    val step = (2 * PI.toFloat() * currentHumFrequency) / sampleRate
                    for (i in chunk.indices) {
                        phase = (phase + step) % (2 * PI.toFloat())
                        val fundamental = sin(phase)
                        val harmonic = sin(phase * 2f) * 0.35f
                        val microNoise = (Random.nextFloat() * 2f - 1f) * 0.08f
                        chunk[i] = ((fundamental + harmonic + microNoise) * Short.MAX_VALUE * 0.18f).toInt().toShort()
                    }
                    track.write(chunk, 0, chunk.size)
                }
                track.stop()
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    fun updateMachineHumRpm(rpm: Float) {
        currentHumFrequency = (rpm * 1.5f).coerceIn(40f, 240f)
    }

    fun stopMachineHum() {
        isMachineRunning = false
        machineHumJob?.cancel()
        machineHumJob = null
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
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
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            track.setNotificationMarkerPosition(buffer.size)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) {
                    t?.release()
                }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
        } catch (_: Exception) {
        }
    }
}
