package com.example.pruebas

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters

class WorkerNotification(context: Context, params: WorkerParameters) : Worker(context,
    params
) {
    override fun doWork(): Result {
        return try {
            val notificationID = 1
            val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEfect =
                    VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEfect)
            } else {
                vibrator.vibrate(5000)
            }

            val resultIntent = Intent(applicationContext, TimersActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                notificationID,
                resultIntent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationBuilder =
                NotificationCompat.Builder(applicationContext, "my_channel_id")
                    .setSmallIcon(R.drawable.prueba_icon)
                    .setContentTitle("Tiempo terminado")
                    .setContentText("Tu platillo esta listo ")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)

            val notificationManager = NotificationManagerCompat.from(applicationContext)
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(notificationID, notificationBuilder.build())
            }
            Result.success()
        }catch (throwable: Throwable) {
            Result.failure()
        }
    }
}