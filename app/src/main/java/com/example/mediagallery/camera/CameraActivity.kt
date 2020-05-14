package com.example.mediagallery.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.mediagallery.CustomGalleryActivity
import com.example.mediagallery.R
import com.example.mediagallery.databinding.CameraBinding
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.photoEditor.PreviewActivity
import com.example.mediagallery.viewmodel.ImageViewModel
import com.google.android.material.textfield.TextInputLayout
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
class Camera : AppCompatActivity(), LifecycleOwner {
    private lateinit var binding: CameraBinding
    private lateinit var viewFinder: TextureView
    private var lensMode = CameraX.LensFacing.BACK
    private   lateinit var captureButton: ImageButton
    private lateinit var viewModel: ImageViewModel
    private lateinit var imageCapture: ImageCapture
    private lateinit var videoCapture: VideoCapture

    @SuppressLint("ClickableViewAccessibility", "RestrictedAPI")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera)

        supportActionBar!!.hide()
        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        binding = DataBindingUtil.setContentView(this,R.layout.camera)
        viewFinder = binding.viewFinder
        captureButton = binding.captureButton

        binding.imageView.setOnClickListener {
            startActivity(Intent(this, CustomGalleryActivity::class.java))
        }

        binding.switchCamera.setOnClickListener {
            lensMode = if (lensMode == CameraX.LensFacing.FRONT) {
                CameraX.LensFacing.BACK
            } else {
                CameraX.LensFacing.FRONT
            }
            bindCameraUseCases()
        }
        // Request camera permissions
        /*if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
            binding.imageView.setOnClickListener {
                startActivity(Intent(this, CustomGalleryActivity::class.java))
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }*/

        binding.captureButton.setOnClickListener {
            photoCapture()
        }
        binding.cameraMode.setOnClickListener {
            if(camMode){
                Toast.makeText(this, "Video Mode", Toast.LENGTH_SHORT).show()
                camMode = false
                binding.cameraMode.background = getDrawable(R.drawable.ic_action_camera)
                captureButton.setOnClickListener(null)
                captureButton.setOnTouchListener { _, event ->
                    videoCapture(event)
                }
                startCamera()
            } else{
                Toast.makeText(this, "Photo Mode", Toast.LENGTH_SHORT).show()
                camMode = true
                binding.cameraMode.background = getDrawable(R.drawable.ic_action_video)
                captureButton.setOnClickListener {
                    photoCapture()
                }
                startCamera()
            }
        }

        methodWithPermissions()
        // Every time the provided texture view changes, recompute layout
        /*ViewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }*/
    }


    private val executor = Executors.newSingleThreadExecutor()

    @SuppressLint("RestrictedApi","ClickableViewAccessibility")
    private fun startCamera() {
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val rotation = viewFinder.display.rotation

        val previewConfig = PreviewConfig.Builder().apply {
            //setTargetResolution(Size(1280, 720))
            setTargetAspectRatio(screenAspectRatio)
            setLensFacing(lensMode)
            setTargetRotation(rotation)
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }
        CameraX.unbindAll()
        if(camMode){
            val imageCaptureConfig = ImageCaptureConfig.Builder()
                .apply {
                    setLensFacing(lensMode)
                    setTargetRotation(windowManager.defaultDisplay.rotation)
                    setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                    setTargetAspectRatio(screenAspectRatio)
                }
                .build()

            imageCapture = ImageCapture(imageCaptureConfig)
            CameraX.bindToLifecycle(this, preview, imageCapture)
        }else {
            val videoCaptureConfig = VideoCaptureConfig.Builder()
                .apply {
                    setTargetRotation(viewFinder.display.rotation)
                    setLensFacing(lensMode)
                    setTargetAspectRatio(screenAspectRatio)
                }.build()
            videoCapture = VideoCapture(videoCaptureConfig)
            CameraX.bindToLifecycle(this, preview, videoCapture)
        }
    }

    fun photoCapture(){

        val file = File(externalMediaDirs.first(),
            "${System.currentTimeMillis()}.jpg")

        imageCapture.takePicture(file, executor,
            object : ImageCapture.OnImageSavedListener {
                override fun onError(
                    imageCaptureError: ImageCapture.ImageCaptureError,
                    message: String,
                    exc: Throwable?
                ) {
                    val msg = "Photo capture failed: $message"
                    Log.e("CameraXApp", msg, exc)
                    viewFinder.post {
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onImageSaved(file: File) {
                    val msg = "Photo capture succeeded: ${file.absolutePath}"
                    Log.d("CameraXApp", msg)

                    //startActivity(Intent(this, PreviewActivity::class.java))
                    viewFinder.post {
                        val intent = intent()
                        intent.putExtra("uri", file.absolutePath)
                        intent.putExtra("file", file)
                        startActivity(intent)

                        //insert_to_database(file)
                        //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    @SuppressLint("RestrictedApi")
    fun videoCapture(event: MotionEvent): Boolean{
        val file = File(externalMediaDirs.first(),
            "${System.currentTimeMillis()}.mp4")

        if(event.action == MotionEvent.ACTION_DOWN) {
            videoCapture.startRecording(file, executor,
                object : VideoCapture.OnVideoSavedListener {
                    override fun onError(
                        videoCaptureError: VideoCapture.VideoCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Video capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onVideoSaved(file: File) {
                        val msg = "Video capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXApp", msg)
                        viewFinder.post {
                            //insert_to_database(file)
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } else if(event.action == MotionEvent.ACTION_UP) {
            videoCapture.stopRecording()
            Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show()
        }
        return false

    }
    fun intent(): Intent{
        val intent = Intent(this, PreviewActivity::class.java)
        return intent
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    private fun aspectRatio(width: Int, height: Int): AspectRatio {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun bindCameraUseCases() {
        CameraX.unbindAll()
        viewFinder.post { startCamera() }
    }

    fun methodWithPermissions() =
        runWithPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE) {
            viewFinder.post { startCamera() }
        }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        methodWithPermissions()
        startCamera()
    }

    companion object{
        private var camMode = true
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

}