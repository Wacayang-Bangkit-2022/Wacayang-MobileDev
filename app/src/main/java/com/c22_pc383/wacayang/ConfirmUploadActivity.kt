package com.c22_pc383.wacayang

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.c22_pc383.wacayang.databinding.ActivityConfirmUploadBinding
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import java.io.File
import java.util.concurrent.Executors


class ConfirmUploadActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityConfirmUploadBinding
    private val compressExecutor = Executors.newFixedThreadPool(1)

    private val imageCompressListener: Utils.CompressImageTask.ICompressListener =
        object : Utils.CompressImageTask.ICompressListener {
            override fun onComplete(compressedFile: File) {
                beginUpload(compressedFile)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.confirm_upload)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        setup()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, CameraActivity::class.java))
        super.onBackPressed()
    }

    override fun setup() {
        val file = intent.getSerializableExtra(CameraActivity.CAPTURED_IMG) as File
        binding.itemImage.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))

        binding.retakeBtn.setOnClickListener { onBackPressed() }
        binding.uploadBtn.setOnClickListener { onBeforeUpload(file) }
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.apply {
            retakeBtn.isEnabled = isEnabled
            uploadBtn.isEnabled = isEnabled
            progressBar.isVisible = !isEnabled
        }
    }

    private fun onBeforeUpload(file: File) {
        enableControl(false)
        compressExecutor.execute(Utils.CompressImageTask(file, imageCompressListener))
    }

    private fun beginUpload(file: File) {
        startActivity(Intent(this, DetailsActivity::class.java))
        finish()
    }
}