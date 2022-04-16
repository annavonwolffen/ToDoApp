package com.annevonwolffen.todoapp.work.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.annevonwolffen.domain.TasksInteractor
import com.annevonwolffen.domain.handle
import com.annevonwolffen.todoapp.R
import com.annevonwolffen.todoapp.TasksActivity
import com.annevonwolffen.todoapp.utils.toCalendar
import java.util.Calendar

class NotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val interactor: TasksInteractor
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val result = interactor.getAllTasks()
        result.handle({ tasks ->
            sendNotification(
                tasks.count {
                    it.deadline?.toCalendar()
                        ?.get(Calendar.DAY_OF_MONTH) ?: 0 == Calendar.getInstance()
                        .get(Calendar.DAY_OF_MONTH)
                }.takeIf { it > 0 } ?: return@handle
            )
        }, { Log.d(TAG, "Error occurred while making notification: $it") })
        return Result.success()
    }

    private fun sendNotification(tasksNumber: Int) {
        val intent = Intent(appContext, TasksActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

        val manager: NotificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = createChannel()
            manager.createNotificationChannel(notificationChannel)
        }

        val notification = createNotificationBuilder(pendingIntent, tasksNumber)
        manager.notify(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(): NotificationChannel {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            appContext.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = ContextCompat.getColor(appContext, R.color.colorBlue)
        notificationChannel.enableVibration(true)
        notificationChannel.vibrationPattern =
            longArrayOf(0, 100, 200, 300)
        notificationChannel.setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
            AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()
        )
        return notificationChannel
    }

    private fun createNotificationBuilder(
        pendingIntent: PendingIntent,
        tasksNumber: Int
    ): Notification {
        val notificationBuilder = NotificationCompat.Builder(
            appContext,
            NOTIFICATION_CHANNEL
        )
            .setChannelId(NOTIFICATION_CHANNEL)
            .setContentIntent(pendingIntent)
            .setContentTitle(
                appContext.getString(
                    R.string.notification_title,
                    appContext.resources.getQuantityString(
                        R.plurals.notification_task_count,
                        tasksNumber,
                        tasksNumber
                    )
                )
            )
            .setContentText(appContext.getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_check_24dp)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setVibrate(longArrayOf(0, 100, 200, 300))
            .setLights(
                ContextCompat.getColor(appContext, R.color.colorBlue),
                LED_ON_MS,
                LED_OFF_MS
            )
        return notificationBuilder.build()
    }

    companion object {
        private const val NOTIFICATION_CHANNEL = "ToDoApp_channel_1"
        private const val LED_ON_MS = 1
        private const val LED_OFF_MS = 1
        const val TAG = "NotificationWorker"
    }
}