package com.example.mediacaptureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import com.example.mediacaptureapp.ui.theme.MediaCaptureAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaCaptureAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PermissionHandler()
                }
            }

        }

    }
}



