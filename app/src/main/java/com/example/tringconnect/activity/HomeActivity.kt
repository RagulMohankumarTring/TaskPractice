package com.example.tringconnect.activity

import android.app.Activity
import android.app.PictureInPictureParams
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Rational
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.example.tringconnect.R
import com.example.tringconnect.databinding.ActivityHomeBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class HomeActivity : AppCompatActivity(), Player.Listener {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var player:ExoPlayer
    private var pictureInPictureParamsBuilder: PictureInPictureParams.Builder? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            pictureInPictureParamsBuilder = PictureInPictureParams.Builder()
        }
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home)
        setupPlayer()
        addMp4Files()
        addMp3Files()
        if (savedInstanceState != null){
            savedInstanceState.getInt("mediaItem").let { restoredMedia->
                val seekTime = savedInstanceState.getLong("SeekTime")
                player.seekTo(restoredMedia,seekTime)
                player.play()
            }
        }
    }

    private fun pipMode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val aspectRatio = Rational(binding.videoView.width,binding.videoView.height)
            pictureInPictureParamsBuilder!!.setAspectRatio(aspectRatio).build()
            enterPictureInPictureMode(pictureInPictureParamsBuilder!!.build())
        }
        else{
            Toast.makeText(this,"Your device doesn't support PIP",Toast.LENGTH_LONG).show()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            pipMode()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("SeekTime",player.currentPosition)
        outState.putInt("mediaItem",player.currentMediaItemIndex)
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }
    private fun setupPlayer(){
        player = ExoPlayer.Builder(this).build()
        binding.videoView.player = player
        player.addListener(this)
    }
    private fun addMp4Files(){
        val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
        player.addMediaItem(mediaItem)
        player.prepare()
    }
    private fun addMp3Files(){
        val mediaItem = MediaItem.fromUri(getString(R.string.test_mp3))
        player.addMediaItem(mediaItem)
        player.prepare()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when(playbackState){
            Player.STATE_BUFFERING ->{
                binding.progressBar.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finishAffinity()
    }
}