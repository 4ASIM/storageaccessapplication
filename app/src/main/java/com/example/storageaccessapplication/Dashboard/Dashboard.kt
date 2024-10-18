package com.example.storageaccessapplication.Dashboard

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import androidx.appcompat.app.AppCompatActivity
import com.example.storageaccessapplication.R
import com.example.storageaccessapplication.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateStorageInfo()
    }

    private fun updateStorageInfo() {
        val statFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)

        val totalBlocks = statFs.blockCountLong
        val availableBlocks = statFs.availableBlocksLong
        val blockSize = statFs.blockSizeLong

        val totalStorage = totalBlocks * blockSize
        val availableStorage = availableBlocks * blockSize

        val filledStorage = (totalStorage - availableStorage) / (1024 * 1024 * 1024)
        val remainingStorage = availableStorage / (1024 * 1024 * 1024)

        val usedStoragePercentage = ((totalStorage - availableStorage) * 100 / totalStorage).toInt()

        binding.storageProgressBar.progress = usedStoragePercentage

        binding.filledStorageTextView.text = "Filled $filledStorage GB"
        binding.remainingStorageTextView.text = "Remaining $remainingStorage GB"

        binding.storagePercentageTextView.text = "Storage Used: $usedStoragePercentage%"
    }
}
