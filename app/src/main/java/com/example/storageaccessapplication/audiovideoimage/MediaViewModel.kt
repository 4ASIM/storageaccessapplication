package com.example.storageaccessapplication.audiovideoimage

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val _mediaItems = MutableLiveData<List<MediaModel>>()
    val mediaItems: LiveData<List<MediaModel>> get() = _mediaItems

    fun fetchMedia(context: Context, mediaType: MediaType) {
        val mediaList = mutableListOf<MediaModel>()
        val projection: Array<String>
        val uri: Uri

        when (mediaType) {
            MediaType.VIDEO -> {
                projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE)
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            MediaType.IMAGE -> {
                projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE)
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            MediaType.AUDIO -> {
                projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.SIZE)
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            MediaType.DOCUMENT -> {
                projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.SIZE)
                uri = MediaStore.Files.getContentUri("external")
            }
            MediaType.CONTACT -> {
                fetchContacts(context)
                return
            }
        }

        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(nameColumn)
                val sizeInKB = it.getInt(sizeColumn)
                val sizeInMB = sizeInKB / 1024.0
                val formattedSizeInMB = String.format("%.0f MB", sizeInMB)
                val contentUri = ContentUris.withAppendedId(uri, id)
                val maxLength = 8
                val shortenedName = if (name.length > maxLength) {
                    name.substring(0, maxLength) + ".."
                } else {
                    name
                }

                mediaList.add(MediaModel(contentUri, shortenedName, formattedSizeInMB))

            }
        }
        _mediaItems.value = mediaList
    }

    fun fetchContacts(context: Context) {
        val contactList = mutableListOf<MediaModel>()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        val cursor = context.contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val nameColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberColumn = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameColumn)
                val number = it.getString(numberColumn)
                contactList.add(MediaModel(Uri.EMPTY, name, number))
            }
        }
        _mediaItems.value = contactList
    }
}
