package myapp.antitheft.myphone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class ChargerDetector(private val context: Context, private val listener: ChargerListener) {
    var isCharging: Boolean = false
    private val chargerReceiver: ChargerReceiver

    init {
        chargerReceiver = ChargerReceiver()
        isCharging = isChargerConnected()

    }

    fun startListening() {
        // Register the charger receiver
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        context.registerReceiver(chargerReceiver, filter)
    }

    fun stopListening() {
        // Unregister the charger receiver
        context.unregisterReceiver(chargerReceiver)
    }

    private fun isChargerConnected(): Boolean {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val status = batteryManager.isCharging
        val chargePlug = batteryManager.getIntProperty(BatteryManager.BATTERY_PLUGGED_AC) or
                batteryManager.getIntProperty(BatteryManager.BATTERY_PLUGGED_USB) or
                batteryManager.getIntProperty(BatteryManager.BATTERY_PLUGGED_WIRELESS)
        return status && chargePlug != 0
    }


    inner class ChargerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val action = it.action
                if (action == Intent.ACTION_POWER_CONNECTED) {
                    isCharging = true
                } else if (action == Intent.ACTION_POWER_DISCONNECTED) {
                    isCharging = false
                    listener.onChargerRemoved()
                }
            }
        }
    }

    interface ChargerListener {
        fun onChargerRemoved()
    }
}