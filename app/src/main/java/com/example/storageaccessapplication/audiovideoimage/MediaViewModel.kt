package com.example.storageaccessapplication.audiovideoimage

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val _mediaItems = MutableLiveData<List<MediaModel>>()
    val mediaItems: LiveData<List<MediaModel>> get() = _mediaItems

    fun fetchMedia(context: Context, mediaType: MediaType) {
        val mediaList = mutableListOf<MediaModel>()
        val projection = when (mediaType) {
            MediaType.VIDEO -> arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE
            )

            MediaType.AUDIO -> arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE
            )

            MediaType.IMAGE -> arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE
            )
        }

        val uri = when (mediaType) {
            MediaType.VIDEO -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            MediaType.AUDIO -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            MediaType.IMAGE -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(projection[0])
            val nameColumn = it.getColumnIndexOrThrow(projection[1])
            val sizeColumn = it.getColumnIndexOrThrow(projection[2])

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val sizeInKB = it.getInt(sizeColumn)
                val sizeInMB = sizeInKB / 1024.0
                val formattedSizeInMB = String.format("%.0f MB", sizeInMB)
                val contentUri = ContentUris.withAppendedId(uri, id)
                mediaList.add(MediaModel(contentUri, name, formattedSizeInMB))
            }
        }
        _mediaItems.value = mediaList
    }
}
