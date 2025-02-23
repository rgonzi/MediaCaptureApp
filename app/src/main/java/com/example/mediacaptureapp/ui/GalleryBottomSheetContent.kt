package com.example.mediacaptureapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.mediacaptureapp.model.MediaInfo

@Composable
fun GalleryBottomSheetContent(
    mediaInfos: List<MediaInfo>,
    onMediaSelected: (MediaInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    if (mediaInfos.isEmpty()) {
        Box(
            modifier = modifier
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hi ha fotos ni vídeos per mostrar")
        }
    } else {

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
        ) {
            items(mediaInfos) { media ->
                Box (
                    contentAlignment = Alignment.Center
                ){
                    if (mediaInfos.isNotEmpty()) {
                        Image(
                            bitmap = media.bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .size(180.dp)
                                .clickable { onMediaSelected(media) }
                        )

                        if (media.isVideo) {
                            Image(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    } else {
                        Text(text = "Realitza una foto o grava un vídeo perquè apareixi aquí")
                    }
                }
            }
        }
    }
}