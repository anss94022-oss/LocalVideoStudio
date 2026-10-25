package com.local.videoplayer.data.model

import android.net.Uri

data class VideoItem(
    val id: Long,
    val uri: Uri,
    val name: String,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val resolution: String,
    val folderPath: String,
    val lastPosition: Long = 0L,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val playCount: Int = 0
)
