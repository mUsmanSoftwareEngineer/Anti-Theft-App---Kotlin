package myapp.antitheft.myphone

import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.os.Bundle
import myapp.antitheft.myphone.R
import android.content.Intent
import android.view.View
import android.widget.ImageView
import myapp.antitheft.myphone.HomeActivity
import java.lang.Exception

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val td: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(4000)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                } finally {
                    val it = Intent(this@Splash, HomeActivity::class.java)
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        }
        td.start()
    }


}