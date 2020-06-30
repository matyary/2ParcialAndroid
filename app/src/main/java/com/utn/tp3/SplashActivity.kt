 package com.utn.tp3

import android.content.Intent
import android.media.MediaPlayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.VideoView

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long = 50
    lateinit var videoView: VideoView

    lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        videoView = findViewById(R.id.videoView)
        val videoPath = "android.resource://$packageName/raw/splash"
        videoView.setVideoPath(videoPath)
        videoView.setOnCompletionListener {
            val r = object: Runnable{
                override fun run() {
                    startActivity(Intent(this@SplashActivity,MainActivity::class.java))
                    finish()
                } 
            }
            Handler().postDelayed(r, SPLASH_TIME_OUT)
        }
        videoView.start()

        mp = MediaPlayer.create(this, R.raw.rocky)
    }

    override fun onResume() {
        super.onResume()
        mp.start()
    }

    override fun onStop() {
        super.onStop()
        mp.pause()
    }
}
