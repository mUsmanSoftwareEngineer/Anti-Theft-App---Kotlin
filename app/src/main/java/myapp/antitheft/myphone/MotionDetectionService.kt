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
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.math.sqrt


class MotionDetectionService : Service() , SensorEventListener {

    private var _notification: Notification? = null

    private var mSensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null

    private lateinit var mGravity: FloatArray
    private var mAccel = 0f
    private var mAccelCurrent = 0f
    private var mAccelLast = 0f

    private lateinit var sirenPlayer: SirenPlayer



    override fun onCreate() {
        super.onCreate()


        mAccel = 0.00f
        mAccelCurrent = SensorManager.GRAVITY_EARTH
        mAccelLast = SensorManager.GRAVITY_EARTH

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mSensorManager!!.registerListener(
            this, accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )



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
        mSensorManager!!.unregisterListener(this)
        sirenPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Return null as this is not a bound service
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {


        Log.d("SensorChanges","Sensor")
        event?.let {
             if (it.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                mGravity = event.values.clone()
                // Shake detection
                val x: Float = mGravity[0]
                val y: Float = mGravity[1]
                val z: Float = mGravity[2]
                mAccelLast = mAccelCurrent
                mAccelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                val delta: Float = mAccelCurrent - mAccelLast
                mAccel = mAccel * 0.9f + delta

                if (mAccel > 0.5) {

                    sirenPlayer.turnOn()

                }
            }
        }


    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }




}