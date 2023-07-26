package com.walletmaxpay.reader;

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.walletmaxpay.reader.MainActivity

class SmsForegroundService : Service() {
    /** if you want to filter sms provider, then please uncomment the bottom lines**/
//    val IBBL: String = "IBBL"
//    val ROCKET: String = "16216"
//    val BKASH: String = "bKash"
//    val NAGAD: String = "NAGAD"

    private lateinit var smsBroadcastReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        // Initialize the BroadcastReceiver for SMS handling
        Log.d("ForgroundService","Service Started")
        smsBroadcastReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
                    // Implement your SMS reading logic here
                    // Access SMS content from the intent extras
                    val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                    // Process the messages as needed
                    for (message in messages) {
                        Log.d("sms", message.displayMessageBody)
                        saveMessageToFirebase(message.displayMessageBody)
                    }
                }
            }
        }
        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsBroadcastReceiver, filter)
        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Ensure that the service runs in the foreground
        val notification = createNotification()
        startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, notification)

        // Return START_STICKY for pre-Android 13 versions
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            START_STICKY
        } else {
            // Return START_NOT_STICKY for Android 13 and above
            START_NOT_STICKY
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the BroadcastReceiver when the service is destroyed
        unregisterReceiver(smsBroadcastReceiver)
        stopForeground(true)
    }

    private fun saveMessageToFirebase(messageBody: String) {
        val database = FirebaseDatabase.getInstance()
        val messagesRef = database.reference.child("messageBody")

        // Generate a unique key for the message using push()
        val newMessageRef = messagesRef.push()

        val data = HashMap<String,String>()
        data["message"] = messageBody


        // Write the data to the database
        newMessageRef.setValue(data).addOnSuccessListener {
            // Data successfully saved to the database
            Log.d("Firebase", "Message saved to Firebase")
            Log.i("firebaseSMS", "$data")

        }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during data writing
                Log.e("Firebase", "Error saving message to Firebase: $exception")
        }
    }

    private fun createNotification(): Notification {
        val channelId = "sms_channel"
        val notificationId = 1 // You can use any unique ID for your notification

        // Create a notification channel (required for Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notificationIntent = Intent(this, MainActivity::class.java) // Replace YourMainActivity with your app's main activity
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Wallet Max Pay")
            .setContentText("Watching incoming SMS")
            .setSmallIcon(androidx.core.R.drawable.notification_bg) // Replace with your app's notification icon
            .setContentIntent(pendingIntent)
            .setTicker("SMS Reading Service")
            .setOngoing(true)

        // Specify the foreground service type for Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationBuilder.foregroundServiceBehavior = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
        }

        val notification = notificationBuilder.build()

        // Start the service in the foreground with the notification
        startForeground(notificationId, notification)

        Log.d("Notification", "Service Running...")
        return notification
    }


    companion object {
        private const val FOREGROUND_SERVICE_NOTIFICATION_ID = 1001
    }
}
