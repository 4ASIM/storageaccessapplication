package com.example.storageaccessapplication.audiovideoimage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storageaccessapplication.R

class audiovideoimages : AppCompatActivity() {
    private lateinit var viewModel: MediaViewModel
    private lateinit var gvGallery: GridView
    private lateinit var adapter: MediaAdapter
    private var mediaType: MediaType = MediaType.VIDEO // Default to video

    private val STORAGE_PERMISSION_CODE = 1001
    private var permissionRequestCount = 0
    private val MAX_PERMISSION_REQUESTS = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audiovideoimages) // Make sure this layout file exists

        gvGallery = findViewById(R.id.gv_audiovideoimages)

        adapter = MediaAdapter(this, emptyList())
        gvGallery.adapter = adapter

        viewModel = ViewModelProvider(this).get(MediaViewModel::class.java)
        viewModel.mediaItems.observe(this) { items ->
            adapter.updateItems(items)
        }

        // Check which media type to load
        mediaType = intent.getSerializableExtra("MEDIA_TYPE") as MediaType
        checkPermissionsAndFetchMedia()
    }

    private fun checkPermissionsAndFetchMedia() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= 33) {
            when (mediaType) {
                MediaType.VIDEO -> permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
                MediaType.IMAGE -> permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                MediaType.AUDIO -> permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Adding additional permissions for documents and contacts
        permissions.add(Manifest.permission.READ_CONTACTS) // Contact permission
//        permissions.add(Manifest.permission.READ_MEDIA_DOCUMENTS) // Document permission (API 33+)

        // Request permissions if not granted
        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), STORAGE_PERMISSION_CODE)
        } else {
            fetchMedia()
        }
    }

    private fun fetchMedia() {
        viewModel.fetchMedia(this, mediaType)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                fetchMedia()
            } else {
                permissionRequestCount++

                if (permissionRequestCount < MAX_PERMISSION_REQUESTS) {
                    Toast.makeText(this, "Permission denied. Attempting again...", Toast.LENGTH_SHORT).show()
                    checkPermissionsAndFetchMedia()
                } else {
                    showPermissionSettingsDialog()
                }
            }
        }
    }

    private fun showPermissionSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
            .setMessage("You have denied permission multiple times. Would you like to open the app settings to grant the permissions?")
            .setPositiveButton("Yes") { _, _ -> openAppSettings() }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}
