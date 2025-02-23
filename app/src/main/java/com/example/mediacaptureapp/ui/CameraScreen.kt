package com.example.mediacaptureapp.ui

import CameraViewModel
import android.content.Context
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.mediacaptureapp.model.CameraUiState
import com.example.mediacaptureapp.model.MediaInfo
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    context: Context,
    cameraViewModel: CameraViewModel,
    cameraUiState: CameraUiState,
    cameraController: LifecycleCameraController,
    onMediaSelected: (MediaInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current



    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            GalleryBottomSheetContent(
                mediaInfos = cameraUiState.mediaInfos,
                onMediaSelected = { onMediaSelected(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        },
        modifier = Modifier
            .padding(WindowInsets.navigationBars.asPaddingValues())

    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            //AndroidView permet representar View dins de Compose
            //L'utilitzarem per mostrar la Preview
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                //Icona Galeria
                IconButton(
                    onClick = {
                        /*Obtenim fotos i obrim la galeria */
                        scope.launch {
                            cameraViewModel.loadCapturedPhotos(context)
                            scaffoldState.bottomSheetState.expand()
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Photo,
                        contentDescription = "Obrir galeria",
                        modifier = Modifier
                            .size(72.dp)
                    )
                }
                if (cameraUiState.showExpandedMedia) {
                    Dialog(onDismissRequest = { cameraViewModel.closeExpandedMedia() }) {
                        Image(
                            painter = rememberAsyncImagePainter(model = cameraUiState.selectedMediaUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                //Icona Foto
                IconButton(
                    onClick = { cameraViewModel.takePhoto(context, cameraController) },
                    modifier = Modifier
                        .size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Camera,
                        contentDescription = "Realitzar foto",
                        modifier = Modifier
                            .size(72.dp)
                    )
                }
                //Icona Video
                IconButton(
                    onClick = { cameraViewModel.recordVideo(context, cameraController) },
                    modifier = Modifier
                        .size(72.dp)
                ) {
                    if (cameraUiState.recording == null) {
                        Icon(
                            imageVector = Icons.Filled.Videocam,
                            contentDescription = "Iniciar gravació",

                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(72.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Videocam,
                            contentDescription = "Parar gravació",

                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(72.dp)
                        )
                    }

                }
            }
        }
    }
}