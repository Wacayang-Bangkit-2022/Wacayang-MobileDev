package com.c22_pc383.wacayang

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.*
import androidx.camera.core.AspectRatio.RATIO_4_3
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.c22_pc383.wacayang.data.AppPreference
import com.c22_pc383.wacayang.databinding.ActivityCameraBinding
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import java.io.File

class CameraActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityCameraBinding
    private var camSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imgCapture: ImageCapture? = null
    private var isFlashOn = false

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val chosenImage: Uri = result.data?.data as Uri
            onCaptureImageSuccess(Utils.convertUriToFile(chosenImage, this), false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        openCamera()
        hideDefaultSystemUI()
    }

    override fun setup() {
        isFlashOn = AppPreference(this).getCameraFlashPref()
        setupFlashImage()

        binding.apply {
            captureCam.setOnClickListener { captureImage() }
            switchCam.setOnClickListener {
                camSelector = if (camSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
                openCamera()
            }
            galleryButton.setOnClickListener { openGallery() }
            flashBtn.setOnClickListener { toggleFlash() }
        }
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.apply {
            captureCam.isEnabled = isEnabled
            switchCam.isEnabled = isEnabled
            galleryButton.isEnabled = isEnabled
            progressBar.visibility = if (isEnabled) View.GONE else View.VISIBLE
        }
    }

    private fun toggleFlash() {
        isFlashOn = !isFlashOn
        AppPreference(this).setCameraFlashPref(isFlashOn)
        setupFlashImage()
    }

    private fun setupFlashImage() {
        binding.flashBtn.setImageResource(
            if (isFlashOn) R.drawable.ic_baseline_flash_on_24
            else R.drawable.ic_baseline_flash_off_24)
    }

    private fun openCamera() {
        val futureProvider = ProcessCameraProvider.getInstance(this@CameraActivity)
        futureProvider.addListener({
            imgCapture = ImageCapture.Builder().build()

            val provider = futureProvider.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(RATIO_4_3)
                .build().apply {
                setSurfaceProvider(binding.camViewFinder.surfaceProvider)
            }

            try {
                provider.apply {
                    unbindAll()
                    bindToLifecycle(this@CameraActivity, camSelector, preview, imgCapture) }
            } catch (exc: Exception) { onCameraError() }
        }, ContextCompat.getMainExecutor(this@CameraActivity))
    }

    private fun openGallery() {
        launcherIntentGallery.launch(
            Intent.createChooser(
                Intent().apply {
                    action = Intent.ACTION_GET_CONTENT
                    type = "image/*"
                }, resources.getString(R.string.choose_image)
            )
        )
    }

    private fun captureImage() {
        enableControl(false)

        val toCapture = imgCapture ?: return
        val file = Utils.makeTempFile(application)
        val outputOption = ImageCapture.OutputFileOptions.Builder(file).build()

        toCapture.flashMode = if (isFlashOn) FLASH_MODE_ON else FLASH_MODE_OFF
        toCapture.takePicture(outputOption,
            ContextCompat.getMainExecutor(this@CameraActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) { onCameraError() }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onCaptureImageSuccess(file, true)
                }
            }
        )
    }

    private fun hideDefaultSystemUI() {
        supportActionBar?.hide()

        @Suppress("DEPRECATION")
        window.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                insetsController?.hide(WindowInsets.Type.statusBars())
            else
                setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    private fun onCaptureImageSuccess(file: File, isCamera: Boolean) {
        var bitmap = BitmapFactory.decodeFile(file.path)
        if (isCamera) {
            val isBackCamera = camSelector == CameraSelector.DEFAULT_BACK_CAMERA
            bitmap = Utils.stabilizeRotateBitmap(bitmap, isBackCamera)
        }
        val imageFile = Utils.convertBitmapToFile(bitmap, file)

        startActivity(Intent(this, ConfirmUploadActivity::class.java).apply {
            putExtra(CAPTURED_IMG, imageFile)
        })
        finish()
    }

    private fun onCameraError() {
        enableControl(true)
        Toast.makeText(
            this@CameraActivity, R.string.camera_failed,
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val CAPTURED_IMG = "captured_image"
    }
}