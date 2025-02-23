package com.example.mediacaptureapp.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayerScreen(
    videoUri: Uri,
    videoPlayerViewModel: VideoPlayerViewModel
) {

    val context = LocalContext.current
    val player by videoPlayerViewModel.playerState.collectAsState()

    Scaffold(
    ) {innerPadding ->
        LaunchedEffect(videoUri) {
            videoPlayerViewModel.initializePlayer(context, videoUri)
        }

        DisposableEffect(Unit) {
            onDispose {
                videoPlayerViewModel.releasePlayer()
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                }
            },
            update = { playerView ->
                playerView.player = player
            }
        )
    }
}
