import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import android.util.Size
import com.example.mediacaptureapp.model.MediaInfo
import com.example.mediacaptureapp.model.CameraUiState
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Locale


private const val FILENAME_FORMAT = "yyyy_MM_dd-HH_mm_ss"
private const val TAG = "MediaCaptureApp"

class CameraViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()


    fun takePhoto(
        context: Context,
        cameraController: LifecycleCameraController
    ) {
        //Especifiquem el nom de les imatges que desarem usant la data i hora actuals
        val name =
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

        //Definim on guardarem la imatge creada, el nom i el tipus
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MediaCaptureApp")
            }
        }
        //Creem les opcions per guardar l'imatge
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

        //Realitzem la fotografia
        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            //Classe anonima per implementar la interfície OnImageSavedCallback
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        context,
                        "Imatge guardada correctament",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(e: ImageCaptureException) {
                    Toast.makeText(
                        context,
                        "No s'ha pogut desar la imatge",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    fun loadCapturedPhotos(context: Context) {
        val mediaInfos = mutableListOf<MediaInfo>()
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_TAKEN
        )
        val selection = "${MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("Pictures/MediaCaptureApp%")
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_TAKEN} DESC"

        val query = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val mimeTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Files.getContentUri("external"), id.toString()
                )

                val bitmap =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        context.contentResolver.loadThumbnail(
                            contentUri,
                            Size(480, 480),
                            null
                        )
                    } else {
                        if (mimeType.startsWith("video")) {
                            getMiniaturaVideo(contentUri, context)
                        } else {
                            getMiniaturaImage(contentUri, context)
                        }
                    }

                if (bitmap != null) {
                    mediaInfos.add(
                        MediaInfo(
                            bitmap,
                            mimeType.startsWith("video"),
                            contentUri
                        )
                    )
                }
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                mediaInfos = mediaInfos
            )
        }
    }

    private fun getMiniaturaImage(uri: Uri, context: Context): Bitmap? {
        return context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    }

    private fun getMiniaturaVideo(videoUri: Uri, context: Context): Bitmap? {
        val metadadesVideo = MediaMetadataRetriever()
        return try {
            metadadesVideo.setDataSource(context, videoUri)
            metadadesVideo.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            null
        } finally {
            metadadesVideo.release()
        }
    }

    fun onMediaSelected(uri: Uri, isVideo: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedMediaUri = uri,
                showExpandedMedia = !isVideo
            )
        }
    }


    fun closeExpandedMedia() {
        _uiState.update { currentState ->
            currentState.copy(
                showExpandedMedia = false
            )
        }
    }

    fun recordVideo(
        context: Context,
        cameraController: LifecycleCameraController
    ) {

        var recording = _uiState.value.recording
        if (recording != null) {
            recording.stop()
            setRecordingNull()
            return
        }
        //Especifiquem el nom de les imatges que desarem usant la data i hora actuals
        val name =
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

        //Definim on guardarem la imatge creada, el nom i el tipus
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Pictures/MediaCaptureApp")
            }
        }

        //Un cop definides les opcions, creem un MediaStoreOutputOptions
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        //Comencem a gravar
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        _uiState.update { currentState ->
            currentState.copy(
                recording = cameraController.startRecording(
                    mediaStoreOutputOptions,
                    AudioConfig.create(true),
                    ContextCompat.getMainExecutor(context)
                ) { event ->
                    when (event) {
                        is VideoRecordEvent.Finalize -> {
                            if (event.hasError()) {
                                recording?.close()
                                setRecordingNull(null)

                                Toast.makeText(
                                    context,
                                    "Error al gravar el vídeo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Video gravat correctament",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                }
            )
        }
    }

    private fun setRecordingNull(state: Recording? = null) {
        _uiState.update { currentState ->
            currentState.copy(
                recording = state
            )
        }
    }
}


