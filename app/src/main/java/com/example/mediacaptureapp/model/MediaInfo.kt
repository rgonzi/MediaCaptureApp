package com.example.mediacaptureapp.model

import android.graphics.Bitmap
import android.net.Uri

data class MediaInfo(
    val bitmap: Bitmap,
    val isVideo: Boolean,
    val uri: Uri
)