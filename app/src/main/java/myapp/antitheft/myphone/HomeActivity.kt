package myapp.antitheft.myphone

import LockUnlockDetector
import SirenPlayer
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import myapp.antitheft.myphone.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), LockUnlockDetector.LockUnlockListener,
    ChargerDetector.ChargerListener {


    private var countDownTimer: CountDownTimer? = null
    val countdownDuration = 5000L
    val interval = 1000L

    private lateinit var sirenPlayer: SirenPlayer


    private lateinit var chargerDetector: ChargerDetector

    private lateinit var phoneLockUnlockDetector: LockUnlockDetector


    private lateinit var binding: ActivityHomeBinding
    private lateinit var builder: AlertDialog.Builder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initVars()
        initListener()

    }

    private fun initVars() {
        builder = AlertDialog.Builder(this@HomeActivity)
        builder.setCancelable(false)


        sirenPlayer = SirenPlayer.getInstance(this)


        chargerDetector = ChargerDetector(this, this)
        chargerDetector.startListening()

        phoneLockUnlockDetector = LockUnlockDetector(this, this)
        phoneLockUnlockDetector.startListening()


    }


    private fun initListener() {

        binding.pocketRemoval.setOnClickListener {

            showCustomDialog("Keep Phone In Pocket", "00:05", 0)

        }



        binding.motionDetection.setOnClickListener {


            showCustomDialog("Will Be Activated In 5 Seconds", "00:05", 1)


        }

        binding.chargerRemoval.setOnClickListener {

            if (chargerDetector.isCharging) {
                Toast.makeText(this, "Charger is not connected", Toast.LENGTH_SHORT).show()
            } else {
                showCustomDialog("Unplugged charger after 5 Seconds", "00:05", 2)
            }

        }
    }

    private fun showCustomDialog(title: String, message: String, type: Int) {

        builder.setTitle(title)

        builder.setMessage(message)


        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        countDownTimer = object : CountDownTimer(countdownDuration, interval) {
            override fun onTick(millisUntilFinished: Long) {

                val secondsRemaining = millisUntilFinished / 1000
                alertDialog.setMessage("00:$secondsRemaining")


            }

            override fun onFinish() {
                // Perform actions when the countdown is complete
                alertDialog.setMessage("00:00")
                alertDialog.dismiss()

                if (type == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(
                            Intent(
                                this@HomeActivity,
                                PocketRemovalService::class.java
                            )
                        )
                    } else {
                        startService(Intent(this@HomeActivity, PocketRemovalService::class.java))
                    }
                } else if (type == 1) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(
                            Intent(
                                this@HomeActivity,
                                MotionDetectionService::class.java
                            )
                        )
                    } else {
                        startService(Intent(this@HomeActivity, MotionDetectionService::class.java))
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(
                            Intent(
                                this@HomeActivity,
                                ChargerRemovalService::class.java
                            )
                        )
                    } else {
                        startService(Intent(this@HomeActivity, ChargerRemovalService::class.java))
                    }
                }


            }
        }

        countDownTimer?.start()


    }


    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()


    }


    override fun onPhoneLocked() {

    }

    override fun onPhoneUnlocked() {
        sirenPlayer.turnOff()
    }

    override fun onChargerRemoved() {

    }


}