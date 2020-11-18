package kr.co.ho1.poopee.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.ObserverManager
import kr.co.ho1.poopee.common.util.LogManager
import kr.co.ho1.poopee.home.HomeActivity
import org.json.JSONObject
import java.net.URLDecoder

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        LogManager.e(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            LogManager.e(TAG, "Message data payload: ${remoteMessage.data}")
            sendNotification(remoteMessage.data.toString())
            scheduleJob()
        }

        remoteMessage.notification?.let {
            LogManager.e(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        LogManager.e(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    private fun handleNow() {
        LogManager.e(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        LogManager.e(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(messageBody: String) {
        val jsonObject = JSONObject(messageBody)
        val intent = Intent(ObserverManager.context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        intent.putExtra("response", jsonObject.toString())
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = "poopee_01"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setContentTitle(URLDecoder.decode(jsonObject.getString("title"), "utf-8"))
                .setStyle(NotificationCompat.BigTextStyle().bigText(URLDecoder.decode(jsonObject.getString("content"), "utf-8")))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

}