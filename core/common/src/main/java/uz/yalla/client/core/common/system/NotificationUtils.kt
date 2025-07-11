package uz.yalla.client.core.common.system

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationUtils {

    private const val DRIVER_ARRIVAL_CHANNEL_ID = "driver_arrival_channel"
    private const val DRIVER_ARRIVAL_NOTIFICATION_ID = 1001

    /**
     * Creates notification channel for driver arrival notifications
     */
    fun createDriverArrivalNotificationChannel(context: Context, soundResourceId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Driver Arrival"
            val descriptionText = "Notifications when driver arrives at your location"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(DRIVER_ARRIVAL_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)

                // Set custom sound for driver arrival
                val soundUri = Uri.parse("android.resource://${context.packageName}/$soundResourceId")
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows driver arrival notification with sound
     */
    fun showDriverArrivalNotification(context: Context, soundResourceId: Int) {
        // Create notification channel if it doesn't exist
        createDriverArrivalNotificationChannel(context, soundResourceId)

        // Create intent to open the app when notification is tapped
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(context, DRIVER_ARRIVAL_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // You can replace with app icon
            .setContentTitle("Driver Arrived!")
            .setContentText("Your driver has arrived at your location")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))

        // For Android versions below O, set sound manually
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/$soundResourceId")
            builder.setSound(soundUri)
        }

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            if (areNotificationsEnabled()) {
                notify(DRIVER_ARRIVAL_NOTIFICATION_ID, builder.build())
            }
        }
    }
}
