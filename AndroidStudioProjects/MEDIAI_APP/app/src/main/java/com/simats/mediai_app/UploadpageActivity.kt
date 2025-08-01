package com.simats.mediai_app

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.responses.ImageRequest
import com.simats.mediai_app.responses.ImageResponse
import com.simats.mediai_app.retrofit.retrofit2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class UploadpageActivity : AppCompatActivity() {
    // Declare cameraImageUri at the top of the class
    private var cameraImageUri: Uri? = null
    private lateinit var imagePreview: ImageView
    private var imageBase64: String? = null

    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Commented out as per your last change
        setContentView(R.layout.uploadpage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(com.simats.mediai_app.R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagePreview = findViewById(R.id.main)
        val btnOpenCamera = findViewById<Button>(R.id.btn_capture_camera)
        val btnChooseFile = findViewById<Button>(R.id.btn_upload_image)
        val btnSubmitImage = findViewById<Button>(R.id.btn_submit_image)

        // Register permission launchers
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera() else Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
        galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openGallery() else Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }

        // Register image pickers
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, cameraImageUri)
                imagePreview.setImageBitmap(bitmap)
                imageBase64 = bitmapToBase64(bitmap)
            }
        }
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                imagePreview.setImageBitmap(bitmap)
                imageBase64 = bitmapToBase64(bitmap)
            }
        }

        // Camera button
        btnOpenCamera.setOnClickListener { requestCameraPermission() }
        // File upload button
        btnChooseFile.setOnClickListener { requestGalleryPermission() }

        btnSubmitImage.setOnClickListener {
            if (imageBase64 == null) {
                Toast.makeText(this, "Please upload image", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage(imageBase64!!)
            }
        }

        // Back arrow navigation to dashboard
        findViewById<android.view.View>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, dashboardupdateded::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun requestGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        galleryPermissionLauncher.launch(permission)
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        cameraImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        cameraImageUri?.let { takePictureLauncher.launch(it) }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun uploadImage(imageBase64: String) {
        val request = ImageRequest(image = imageBase64)
        retrofit2.getService(this).uploadMedicalImage(request).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(this@UploadpageActivity, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@UploadpageActivity, successpage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@UploadpageActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Toast.makeText(this@UploadpageActivity, "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}