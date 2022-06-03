package com.c22_pc383.wacayang

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.postDelayed
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.c22_pc383.wacayang.data.AppPreference
import com.c22_pc383.wacayang.data.WayangPredictResponse
import com.c22_pc383.wacayang.databinding.ActivityConfirmUploadBinding
import com.c22_pc383.wacayang.factory.WayangViewModelFactory
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.repository.WayangRepository
import com.c22_pc383.wacayang.view_model.WayangViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.Executors

class ConfirmUploadActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityConfirmUploadBinding
    private lateinit var viewModel: WayangViewModel

    private var predictedResponse: WayangPredictResponse? = null

    private val compressExecutor = Executors.newFixedThreadPool(1)
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.apply {
            title = getString(R.string.confirm_upload)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        viewModel = ViewModelProvider(
            this, WayangViewModelFactory(WayangRepository.getAIRepository())
        )[WayangViewModel::class.java]

        setup()
        observerCall()
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

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.clearTempFiles(this)
        handler.removeCallbacksAndMessages(null)
    }

    override fun setup() {
        val file = intent.getSerializableExtra(CameraActivity.CAPTURED_IMG) as File
        binding.itemImage.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))

        binding.retakeBtn.setOnClickListener { onBackPressed() }
        binding.uploadBtn.setOnClickListener { onBeforeUpload(file) }
    }

    override fun observerCall() {
        viewModel.apply {
            predictedWayang.observe(this@ConfirmUploadActivity) { predictedResponse = it }
            isPredictWayangError.observe(this@ConfirmUploadActivity) {
                onEndUpload()
                if (it) {
                    enableControl(true)
                    Utils.toastNetworkError(this@ConfirmUploadActivity)
                }
            }
        }
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.apply {
            controlPanel.isVisible = isEnabled
            progressBar.isVisible = !isEnabled
        }
    }

    private val imageCompressListener: Utils.CompressImageTask.ICompressListener =
        object : Utils.CompressImageTask.ICompressListener {
            override fun onComplete(compressedFile: File) {
                beginUpload(compressedFile)
            }
        }

    private fun onBeforeUpload(file: File) {
        enableControl(false)
        compressExecutor.execute(Utils.CompressImageTask(file, imageCompressListener))
    }

    private fun beginUpload(file: File) {
        enableControl(false)

        val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            file.name,
            imageFile
        )

        predictedResponse = null
        viewModel.predictWayang(AppPreference(this).getToken(), imageMultipart)
    }

    private fun onEndUpload() {
        if (predictedResponse == null) {
            enableControl(true)
            return
        }

        val predictedLabel = predictedResponse?.predictionLabel
        Utils.setHtmlText(binding.statusText, resources.getString(R.string.predicted_as, "<b>$predictedLabel</b>"))

        handler.postDelayed(SEARCH_WAIT_DELAY) {
            Utils.setHtmlText(binding.statusText, resources.getString(R.string.searching_for, "<b>$predictedLabel</b>"))

            handler.postDelayed(SEARCH_WAIT_DELAY) {
                // Open Search Activity with predicted label as keyword to search Wayang information
                startActivity(Intent(this, SearchActivity::class.java).apply {
                    putExtra(SearchActivity.VIEW_ALL_EXTRA, false)
                    putExtra(SearchActivity.INITIAL_SEARCH_KEYWORD, predictedLabel)
                })
                finish()
            }
        }
    }

    companion object {
        private const val SEARCH_WAIT_DELAY = 1000L
    }
}