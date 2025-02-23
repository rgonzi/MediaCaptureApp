package com.example.mediacaptureapp

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.mediacaptureapp.ui.Navigation


@Composable
fun PermissionHandler() {

    val context = LocalContext.current

    var allPermissionsGranted by remember { mutableStateOf(false) }

    val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ) + if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else {
        emptyList()
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            allPermissionsGranted = results.all { it.value }
            if (!allPermissionsGranted) {
                Toast.makeText(
                    context,
                    "S'han de concedir tots els permisos per utilitzar l'aplicació",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissions.toTypedArray())
    }

    if (allPermissionsGranted) {
        Navigation(context = context)
    }
}
