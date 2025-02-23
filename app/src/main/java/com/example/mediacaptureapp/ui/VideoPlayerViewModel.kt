package com.example.mediacaptureapp.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoPlayerViewModel : ViewModel() {

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState: StateFlow<ExoPlayer?> = _playerState

    fun initializePlayer(context: Context, videoUri: Uri) {
        if (_playerState.value == null) {
            viewModelScope.launch {
                val exoPlayer = ExoPlayer.Builder(context).build().also {
                    val mediaItem = MediaItem.fromUri(videoUri)
                    it.setMediaItem(mediaItem)
                    it.prepare()
                    it.playWhenReady = true
                    it.addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            Log.e("VideoPlayer", "No s'ha pogut reproduïr el video")
                        }
                    })
                }
                _playerState.value = exoPlayer
            }
        }
    }

    fun releasePlayer() {
        _playerState.value?.release()
        _playerState.value = null
    }
}