package com.local.videoplayer.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.local.videoplayer.data.model.VideoItem
import com.local.videoplayer.data.repository.VideoRepository
import com.local.videoplayer.ui.library.LibraryScreen
import com.local.videoplayer.ui.player.VideoPlayerActivity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var videoRepository: VideoRepository

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadVideos()
        }
    }

    var videoListState = mutableStateOf<List<VideoItem>>(emptyList())
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoRepository = VideoRepository(this)

        checkAndRequestPermission()

        setContent {
            val videos by videoListState
            LibraryScreen(
                videos = videos,
                onVideoClick = { video ->
                    val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                        putExtra("VIDEO_URI", video.uri.toString())
                    }
                    startActivity(intent)
                },
                onFolderSelectClick = {
                    loadVideos()
                },
                onSearchQueryChanged = { query ->
                    lifecycleScope.launch {
                        val all = videoRepository.getLocalVideos()
                        if (query.isBlank()) {
                            videoListState.value = all
                        } else {
                            videoListState.value = all.filter { it.name.contains(query, ignoreCase = true) }
                        }
                    }
                }
            )
        }
    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                loadVideos()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun loadVideos() {
        lifecycleScope.launch {
            videoListState.value = videoRepository.getLocalVideos()
        }
    }
}
