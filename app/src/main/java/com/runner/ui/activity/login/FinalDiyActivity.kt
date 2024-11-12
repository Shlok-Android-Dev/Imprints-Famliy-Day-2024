package com.runner.ui.activity.login

import android.content.Intent
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.runner.R
import com.runner.ui.activity.BaseActivity

class FinalDiyActivity : BaseActivity() {

    var VideoView: VideoView? = null
    var btnHome: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final_diy)

        VideoView = findViewById(R.id.VideoView)
        btnHome = findViewById(R.id.btnHome)
        val url = "android.resource://" + applicationContext.packageName
            .toString() + "/" + R.raw.steps

        val video = Uri.parse(url)
        VideoView!!.setVideoURI(video)
        VideoView!!.setOnCompletionListener(OnCompletionListener { VideoView!!.start() })

        VideoView!!.start()


        btnHome!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@FinalDiyActivity, DiyHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        })
    }

    override fun selectFileButtonOnClick() {

    }

    override fun printButtonOnClick() {

    }

    override fun onBackPressed() {

    }
}