package com.example.emergencypower.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.emergencypower.MainActivity
import com.example.emergencypower.model.PhysicalSensorState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.sqrt

class SensorNotificationManager(private val context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_EMERGENCY = "emergency_power_alarms"
        const val CHANNEL_TELEMETRY = "telemetry_events"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val emergencyChannel = NotificationChannel(
                CHANNEL_EMERGENCY,
                "Аварійні сповіщення енергомережі (High Priority)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Критичні сповіщення про знеструмлення, аварії ДГ-50 та розряд АКБ"
                enableVibration(true)
            }

            val telemetryChannel = NotificationChannel(
                CHANNEL_TELEMETRY,
                "Події телеметрії та перевірок",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Періодичні звіти та стан автоматики АВР"
            }

            notificationManager.createNotificationChannel(emergencyChannel)
            notificationManager.createNotificationChannel(telemetryChannel)
        }
    }

    fun sendEmergencyNotification(title: String, message: String, notificationId: Int = 101) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_EMERGENCY)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_media_play, "ПУСК ДГ-50", pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "КВИТУВАТИ", pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun getSensorFlow(vibrationThreshold: Float = 3.5f, lightThreshold: Float = 50f): Flow<PhysicalSensorState> = callbackFlow {
        var currentX = 0f
        var currentY = 0f
        var currentZ = 9.8f
        var currentLux = 350f
        var lastVibration = 0f

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        currentX = event.values[0]
                        currentY = event.values[1]
                        currentZ = event.values[2]
                        val totalAcc = sqrt(currentX * currentX + currentY * currentY + currentZ * currentZ)
                        lastVibration = kotlin.math.abs(totalAcc - 9.8f)
                    }
                    Sensor.TYPE_LIGHT -> {
                        currentLux = event.values[0]
                    }
                }

                val isVibAlarm = lastVibration > vibrationThreshold
                val isBlackout = currentLux < lightThreshold

                trySend(
                    PhysicalSensorState(
                        accelerometerX = currentX,
                        accelerometerY = currentY,
                        accelerometerZ = currentZ,
                        vibrationMagnitude = lastVibration,
                        lightLux = currentLux,
                        batteryTempC = 32.5f,
                        isVibrationAlarm = isVibAlarm,
                        isBlackoutAlarm = isBlackout
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        accel?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        light?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
