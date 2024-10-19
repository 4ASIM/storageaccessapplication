package com.example.storageaccessapplication.audiovideoimage

import android.net.Uri

data class MediaModel (
    val mediaUri: Uri,
    val mediaName: String,
    val mediaSize: String
)
