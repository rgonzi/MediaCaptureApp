package com.example.mediacaptureapp.model

import android.net.Uri
import androidx.camera.video.Recording

data class CameraUiState(
    var mediaInfos: List<MediaInfo> = emptyList(),
    var recording: Recording? = null,
    var selectedMediaUri: Uri? = null,
    var showExpandedMedia: Boolean = false
    )