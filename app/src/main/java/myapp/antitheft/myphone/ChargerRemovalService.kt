package myapp.antitheft.myphone

import LockUnlockDetector
import SirenPlayer
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlin.math.sqrt


class ChargerRemovalService : Service()  ,ChargerDetector.ChargerListener{

    private var _notification: Notification? = null



    private lateinit var chargerDetector: ChargerDetector

    private lateinit var sirenPlayer: SirenPlayer


    companion object {
        private const val SENSOR_SENSITIVITY = 4
    }

    override fun onCreate() {
        super.onCreate()


        chargerDetector = ChargerDetector(this, this)
        chargerDetector.startListening()

        sirenPlayer = SirenPlayer.getInstance(this)


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {




        if (Build.VERSION.SDK_INT >= 26) {
            val NOTIFICATION_CHANNEL_ID = "app.stole"
            val channelName = "Siren Service"
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            _notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        }

        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(10101, _notification)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sirenPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Return null as this is not a bound service
        return null
    }

    override fun onChargerRemoved() {

        sirenPlayer.turnOn()
    }


}