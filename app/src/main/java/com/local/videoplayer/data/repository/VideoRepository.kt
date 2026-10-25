package com.local.videoplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.local.videoplayer.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class VideoRepository(private val context: Context) {

    suspend fun getLocalVideos(): List<VideoItem> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<VideoItem>()
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.RESOLUTION,
            MediaStore.Video.Media.DATA
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val addedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val modifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val resolutionColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn) ?: "فيديو بدون اسم"
                val duration = cursor.getLong(durationColumn)
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getLong(addedColumn)
                val dateModified = cursor.getLong(modifiedColumn)
                val resolution = cursor.getString(resolutionColumn) ?: "HD"
                val dataPath = cursor.getString(dataColumn)
                
                val file = File(dataPath)
                val folderPath = file.parent ?: "مجلد غير معروف"
                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                if (file.exists() && file.length() > 1024) {
                    videoList.add(
                        VideoItem(
                            id = id,
                            uri = uri,
                            name = name,
                            duration = duration,
                            size = size,
                            dateAdded = dateAdded,
                            dateModified = dateModified,
                            resolution = resolution,
                            folderPath = folderPath
                        )
                    )
                }
            }
        }
        videoList
    }
}
