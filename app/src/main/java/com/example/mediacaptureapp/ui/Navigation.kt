package com.example.mediacaptureapp.ui

import CameraViewModel
import android.content.Context
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class MediaCaptureAppScreens {
    Camera,
    VideoPlayer
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    context: Context
) {
    val cameraViewModel = viewModel<CameraViewModel>()
    val cameraUiState by cameraViewModel.uiState.collectAsState()

    val videoPlayerViewModel = viewModel<VideoPlayerViewModel>()

    //Inicialitzem el CameraController
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }

    NavHost(
        navController = navController,
        startDestination = MediaCaptureAppScreens.Camera.name,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(MediaCaptureAppScreens.Camera.name) {
            CameraScreen(
                context,
                cameraViewModel,
                cameraUiState,
                cameraController,
                onMediaSelected = {
                    cameraViewModel.onMediaSelected(it.uri, it.isVideo)
                    if(it.isVideo) {
                        navController.navigate(MediaCaptureAppScreens.VideoPlayer.name)
                    }
                }
            )
        }

        composable(MediaCaptureAppScreens.VideoPlayer.name) {
            VideoPlayerScreen(cameraUiState.selectedMediaUri!!, videoPlayerViewModel)
        }
    }
}