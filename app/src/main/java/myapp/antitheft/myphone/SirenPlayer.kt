import android.content.Context
import android.media.MediaPlayer
import myapp.antitheft.myphone.R

class SirenPlayer private constructor(context: Context) {
    private val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.siren)

    init {
        mediaPlayer.isLooping = true
    }

    companion object {
        private var instance: SirenPlayer? = null

        fun getInstance(context: Context): SirenPlayer {
            if (instance == null) {
                instance = SirenPlayer(context.applicationContext)
            }
            return instance as SirenPlayer
        }
    }

    fun turnOn() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun turnOff() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
    }

    fun release() {
        mediaPlayer.release()
        instance = null
    }
}
