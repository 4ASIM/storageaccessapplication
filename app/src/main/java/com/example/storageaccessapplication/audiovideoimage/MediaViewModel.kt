package com.example.storageaccessapplication.audiovideoimage

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val _videos = MutableLiveData<List<videomodel>>()
    val videos: LiveData<List<videomodel>> get() = _videos

    fun fetchVideos(context: Context) {
        val videoList = mutableListOf<videomodel>()
        Log.d("VideoViewModel", "Fetching videos...")
        val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE)
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val sizeInKB = it.getInt(sizeColumn)
                val sizeInMB: Double = sizeInKB / 1024.0
                val formattedSizeInMB = String.format("%.0f", sizeInMB)
                val maxLength = 8
                val shortenedName = if (name.length > maxLength) {
                    name.substring(0, maxLength) + "...mp3"
                } else {
                    name
                }
                val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                videoList.add(videomodel(contentUri, shortenedName, formattedSizeInMB+ " MB"))
            }
        }
        _videos.value = videoList
    }
}
