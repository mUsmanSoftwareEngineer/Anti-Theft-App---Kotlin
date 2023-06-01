import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.app.KeyguardManager

class LockUnlockDetector(private val context: Context, private val listener: LockUnlockListener) {
    private val lockUnlockReceiver: LockUnlockReceiver

    init {
        lockUnlockReceiver = LockUnlockReceiver()
    }

    fun startListening() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        context.registerReceiver(lockUnlockReceiver, filter)
    }

    fun stopListening() {
        context.unregisterReceiver(lockUnlockReceiver)
    }

    inner class LockUnlockReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val action = it.action

                when (action) {
                    Intent.ACTION_SCREEN_ON -> {
                        if (!isDeviceLocked()) {
                            listener.onPhoneUnlocked()
                        }
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        if (!isDeviceLocked()) {
                            listener.onPhoneLocked()
                        }
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        listener.onPhoneUnlocked()
                    }
                }
            }
        }
    }

    private fun isDeviceLocked(): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardLocked
    }

    interface LockUnlockListener {
        fun onPhoneLocked()
        fun onPhoneUnlocked()
    }
}
