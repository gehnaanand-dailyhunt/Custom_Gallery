package com.example.mediagallery.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.mediagallery.CustomGalleryActivity
import com.example.mediagallery.R
import com.example.mediagallery.databinding.CameraBinding
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.viewmodel.ImageViewModel
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.util.concurrent.Executors

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
class Camera : AppCompatActivity(), LifecycleOwner {
    private lateinit var binding: CameraBinding
    private lateinit var viewFinder: TextureView
    private   lateinit var captureButton: ImageButton
    private lateinit var viewModel: ImageViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera)

        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        binding = DataBindingUtil.setContentView(this,R.layout.camera)
        viewFinder = binding.viewFinder
        captureButton = binding.captureButton

        // Request camera permissions
        if (allPermissionsGranted()) {
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
        }


        // Every time the provided texture view changes, recompute layout
        /*ViewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }*/
    }


    private val executor = Executors.newSingleThreadExecutor()

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            //setTargetResolution(Size(1280, 720))
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

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
            }
            .build()


        val imageCapture = ImageCapture(imageCaptureConfig)
        binding.captureButton.setOnClickListener {

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
                        viewFinder.post {
                            insert_to_database(file)
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    fun insert_to_database(image_file : File){
        val custLayout = LayoutInflater.from(this).inflate(R.layout.text_input_dialog,null)
        val image_title: TextInputLayout = custLayout.findViewById(R.id.title)
        val image_tag : TextInputLayout = custLayout.findViewById(R.id.tag)
        val builder = AlertDialog.Builder(this).setView(custLayout)
            .setPositiveButton("Save"){dialog, _ ->
                val title = image_title.editText!!.text.toString()
                val tag = image_tag.editText!!.text.toString()
                val uri = image_file.absolutePath
                val date = image_file.lastModified()
                val galleryPicture = GalleryPicture(0, title, tag, date, uri)
                viewModel.insert(galleryPicture)
                dialog.dismiss()
                Toast.makeText(this, title, Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                image_file.delete()
                dialog.cancel()
            }
        builder.show()
        return
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

}