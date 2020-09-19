package ge.mov.mobile.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import ge.mov.mobile.R
import ge.mov.mobile.util.Constants
import ge.mov.mobile.util.toast
import kotlinx.android.synthetic.main.activity_watch.*

class WatchActivity : AppCompatActivity() {
    private lateinit var movieSrc: String
    private lateinit var mediaController: MediaController
    private lateinit var goback: ImageView
    private lateinit var videoView: VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch)

        goback = gobackfrommovie
        videoView = movieView

        goback.visibility = View.GONE

        movieSrc = intent.extras?.getString("src", "") ?: ""

        if (movieSrc != "") {
            mediaController = MediaController(this)
            videoView.setVideoPath(movieSrc)
            mediaController.setMediaPlayer(videoView)
            videoView.setMediaController(mediaController)
            videoView.requestFocus()
            videoView.start()
        } else {
            toast("Error parsing an URL !")
        }

        goback.setOnClickListener {
            Constants.current_movie_left_at = 0
            finish()
        }
    }

    override fun onBackPressed() {
        goback.visibility = View.VISIBLE

        Handler().postDelayed({
            goback.visibility = View.GONE
        }, 3000)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val view = window.decorView
        if (hasFocus) {
            view.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
        }
    }

    override fun onPause() {
        super.onPause()

        Constants.current_movie_left_at = videoView.currentPosition
    }

    override fun onResume() {
        super.onResume()

        videoView.seekTo(Constants.current_movie_left_at)
        videoView.start()
    }
}