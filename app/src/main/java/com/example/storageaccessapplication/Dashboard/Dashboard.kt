package com.example.storageaccessapplication.Dashboard
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.storageaccessapplication.R
import com.example.storageaccessapplication.audiovideoimage.MediaType
import com.example.storageaccessapplication.audiovideoimage.audiovideoimages
import com.example.storageaccessapplication.databinding.ActivityDashboardBinding


class Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateStorageInfo()
        val videoCard = findViewById<CardView>(R.id.cv_videos)
        videoCard.setOnClickListener {
            val intent = Intent(this, audiovideoimages::class.java).apply {
                putExtra("MEDIA_TYPE", MediaType.VIDEO)
            }
            startActivity(intent)
        }

        val imageCard = findViewById<RelativeLayout>(R.id.rl_images)
        imageCard.setOnClickListener {
            val intent = Intent(this, audiovideoimages::class.java).apply {
                putExtra("MEDIA_TYPE", MediaType.IMAGE)
            }
            startActivity(intent)
        }


        val audioCard = findViewById<RelativeLayout>(R.id.rv_audio)
        audioCard.setOnClickListener {
            val intent = Intent(this, audiovideoimages::class.java).apply {
                putExtra("MEDIA_TYPE", MediaType.AUDIO)
            }
            startActivity(intent)
        }
        val contactCard = findViewById<RelativeLayout>(R.id.rl_contact)
        audioCard.setOnClickListener {
            val intent = Intent(this, audiovideoimages::class.java).apply {
                putExtra("MEDIA_TYPE", MediaType.AUDIO)
            }
            startActivity(intent)
        }
//
//        val imageCard = findViewById<CardView>(R.id.contacttt)
//        imageCard.setOnClickListener {
//            val intent = Intent(this, video::class.java).apply {
//
//          }
//          startActivity(intent)
//        }
//
//        val audioCard = findViewById<CardView>(R.id.tv_Audio)
//        audioCard.setOnClickListener {
//            val intent = Intent(this, audiovideoimages::class.java).apply {
//                putExtra("MEDIA_TYPE", MediaType.AUDIO)
//            }
//            startActivity(intent)
//        }
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
