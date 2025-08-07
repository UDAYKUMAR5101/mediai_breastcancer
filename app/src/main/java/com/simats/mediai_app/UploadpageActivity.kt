package com.simats.mediai_app

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.simats.mediai_app.retrofit.ApiService
import com.simats.mediai_app.retrofit.RetrofitClient
import com.simats.mediai_app.responses.UploadResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class UploadpageActivity : AppCompatActivity() {
    
    private lateinit var imagePreview: ImageView
    private lateinit var uploadPlaceholder: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSubmitImage: Button
    private lateinit var btnCaptureCamera: Button
    private lateinit var btnUploadImage: Button
    
    private var selectedImageFile: File? = null
    private var cameraImageUri: Uri? = null
    
    // Activity Result Launchers
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    
    // API Service
    private lateinit var apiService: ApiService
    
    companion object {
        private const val TAG = "UploadpageActivity"
        private const val MAX_IMAGE_SIZE = 10 * 1024 * 1024 // 10MB
        private const val MIN_IMAGE_DIMENSION = 1000
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uploadpage)
        
        // Initialize API Service
        apiService = RetrofitClient.getClient().create(ApiService::class.java)
        
        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize views
        initializeViews()
        
        // Setup permission launchers
        setupPermissionLaunchers()
        
        // Setup activity result launchers
        setupActivityResultLaunchers()
        
        // Setup click listeners
        setupClickListeners()
    }
    
    private fun initializeViews() {
        imagePreview = findViewById(R.id.image_preview)
        uploadPlaceholder = findViewById(R.id.upload_placeholder)
        progressBar = findViewById(R.id.progress_bar)
        btnSubmitImage = findViewById(R.id.btn_submit_image)
        btnCaptureCamera = findViewById(R.id.btn_capture_camera)
        btnUploadImage = findViewById(R.id.btn_upload_image)
    }
    
    private fun setupPermissionLaunchers() {
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openCamera()
            } else {
                showPermissionDeniedDialog("Camera permission is required to take photos")
            }
        }
        
        storagePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openGallery()
            } else {
                showPermissionDeniedDialog("Storage permission is required to select images")
            }
        }
    }
    
    private fun setupActivityResultLaunchers() {
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                handleImageSelection(cameraImageUri!!)
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
        
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleImageSelection(it) }
        }
    }
    
    private fun setupClickListeners() {
        // Camera button
        btnCaptureCamera.setOnClickListener {
            requestCameraPermission()
        }
        
        // File upload button
        btnUploadImage.setOnClickListener {
            requestStoragePermission()
        }
        
        // Submit button
        btnSubmitImage.setOnClickListener {
            uploadImage()
        }
        
        // Back button
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
    
    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                checkSelfPermission(Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    openCamera()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    showPermissionRationaleDialog("Camera permission is needed to take photos")
                }
                else -> {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        } else {
            openCamera()
        }
    }
    
    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    openGallery()
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    showPermissionRationaleDialog("Storage permission is needed to select images")
                }
                else -> {
                    storagePermissionLauncher.launch(permission)
                }
            }
        } else {
            openGallery()
        }
    }
    
    private fun openCamera() {
        try {
            val imageFile = createImageFile()
            cameraImageUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                imageFile
            )
            takePictureLauncher.launch(cameraImageUri!!)
        } catch (e: IOException) {
            Log.e(TAG, "Error creating image file", e)
            Toast.makeText(this, "Error setting up camera", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }
    
    private fun createImageFile(): File {
        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }
    
    private fun handleImageSelection(uri: Uri) {
        try {
            // Validate image dimensions
            val bitmap = loadBitmapFromUri(uri)
            if (bitmap == null) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Check image dimensions
            if (bitmap.width < MIN_IMAGE_DIMENSION || bitmap.height < MIN_IMAGE_DIMENSION) {
                Toast.makeText(this, "Image resolution too low. Minimum 1000x1000px required", Toast.LENGTH_LONG).show()
                return
            }
            
            // Convert URI to File and check size
            val file = uriToFile(uri)
            if (file.length() > MAX_IMAGE_SIZE) {
                Toast.makeText(this, "Image size must be less than 10MB", Toast.LENGTH_LONG).show()
                return
            }
            
            // Display image
            displayImage(bitmap)
            selectedImageFile = file
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling image selection", e)
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun uriToFile(uri: Uri): File {
        return when (uri.scheme) {
            "file" -> File(uri.path!!)
            "content" -> {
                val inputStream = contentResolver.openInputStream(uri)
                val file = createImageFile()
                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                file
            }
            else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
        }
    }
    
    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                // First decode with inJustDecodeBounds=true to check dimensions
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(stream, null, options)
                
                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
                options.inJustDecodeBounds = false
                
                // Decode bitmap with inSampleSize set
                contentResolver.openInputStream(uri)?.use { finalStream ->
                    BitmapFactory.decodeStream(finalStream, null, options)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from URI", e)
            null
        }
    }
    
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    private fun displayImage(bitmap: Bitmap) {
        imagePreview.setImageBitmap(bitmap)
        imagePreview.visibility = View.VISIBLE
        uploadPlaceholder.visibility = View.GONE
    }
    
    private fun uploadImage() {
        if (selectedImageFile == null) {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show progress
        setUploadingState(true)
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = uploadImageToServer(selectedImageFile!!)
                
                withContext(Dispatchers.Main) {
                    setUploadingState(false)
                    handleUploadResponse(response)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading image", e)
                withContext(Dispatchers.Main) {
                    setUploadingState(false)
                    Toast.makeText(this@UploadpageActivity, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private suspend fun uploadImageToServer(file: File): UploadResponse {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        
        return withContext(Dispatchers.IO) {
            try {
                val call = apiService.uploadImage(body)
                val response = call.execute()
                response.body() ?: UploadResponse(false, "No response from server")
            } catch (e: Exception) {
                Log.e(TAG, "Network error during upload", e)
                UploadResponse(false, "Network error: ${e.message}")
            }
        }
    }
    
    private fun handleUploadResponse(response: UploadResponse) {
        if (response.success) {
            Toast.makeText(this, "Image uploaded successfully!", Toast.LENGTH_LONG).show()
            // Navigate to success page or finish activity
            finish()
        } else {
            Toast.makeText(this, "Upload failed: ${response.error ?: response.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setUploadingState(uploading: Boolean) {
        btnSubmitImage.isEnabled = !uploading
        btnCaptureCamera.isEnabled = !uploading
        btnUploadImage.isEnabled = !uploading
        progressBar.visibility = if (uploading) View.VISIBLE else View.GONE
        
        if (uploading) {
            btnSubmitImage.text = "Uploading..."
        } else {
            btnSubmitImage.text = "Submit Image"
        }
    }
    
    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Settings") { _, _ ->
                // Open app settings
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showPermissionRationaleDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Grant") { _, _ ->
                // Request permission again
                requestCameraPermission()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}