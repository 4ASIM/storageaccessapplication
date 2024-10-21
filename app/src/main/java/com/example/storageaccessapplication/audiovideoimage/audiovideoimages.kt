package com.example.storageaccessapplication.audiovideoimage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storageaccessapplication.R
import com.example.storageaccessapplication.databinding.ActivityAudiovideoimagesBinding

class audiovideoimages : AppCompatActivity() {
    private lateinit var viewModel: MediaViewModel
    private lateinit var adapter: MediaAdapter
    private lateinit var binding: ActivityAudiovideoimagesBinding
    private var mediaType: MediaType = MediaType.VIDEO

    private val STORAGE_PERMISSION_CODE = 1001
    private var permissionRequestCount = 0
    private val MAX_PERMISSION_REQUESTS = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityAudiovideoimagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaType = intent.getSerializableExtra("MEDIA_TYPE") as MediaType

        adapter = MediaAdapter(this, emptyList(), mediaType)

        if (mediaType == MediaType.DOCUMENT || mediaType == MediaType.CONTACT) {
            binding.listView.visibility = View.VISIBLE
            binding.gvAudiovideoimages.visibility = View.GONE
            binding.listView.adapter = adapter
        } else {
            binding.gvAudiovideoimages.visibility = View.VISIBLE
            binding.listView.visibility = View.GONE
            binding.gvAudiovideoimages.adapter = adapter
        }

        viewModel = ViewModelProvider(this).get(MediaViewModel::class.java)
        viewModel.mediaItems.observe(this) { items ->
            adapter.updateItems(items)
        }

        checkPermissionsAndFetchMedia()
    }

    private fun checkPermissionsAndFetchMedia() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= 33) {
            when (mediaType) {
                MediaType.VIDEO -> permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
                MediaType.IMAGE -> permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                MediaType.AUDIO -> permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
                MediaType.DOCUMENT, MediaType.CONTACT -> permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (mediaType == MediaType.CONTACT) {
                permissions.add(Manifest.permission.READ_CONTACTS)
            }
        }

        if (permissions.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), STORAGE_PERMISSION_CODE)
        } else {
            fetchMedia()
        }
    }

    private fun fetchMedia() {
        if (mediaType == MediaType.CONTACT) {
            viewModel.fetchContacts(this)
        } else {
            viewModel.fetchMedia(this, mediaType)
        }
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
