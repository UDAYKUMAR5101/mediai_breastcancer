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
import com.simats.mediai_app.responses.ImagePredictionResponse
import com.simats.mediai_app.responses.SaveHistoryRequest
import com.simats.mediai_app.responses.SaveHistoryResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        private const val MIN_IMAGE_RESOLUTION = 1000 // Minimum width/height
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
            // Validate image size and resolution
            val validationResult = validateImage(uri)
            if (!validationResult.isValid) {
                Toast.makeText(this, validationResult.errorMessage, Toast.LENGTH_LONG).show()
                return
            }

            // Load and display image
            val bitmap = loadBitmapFromUri(uri)
            if (bitmap == null) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                return
            }

            val file = uriToFile(uri)

            // Display image
            displayImage(bitmap)
            selectedImageFile = file
            Toast.makeText(this, "Image selected successfully", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e(TAG, "Error handling image selection", e)
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateImage(uri: Uri): ImageValidationResult {
        return try {
            // Check file size first
            val fileSize = getFileSize(uri)
            if (fileSize > MAX_IMAGE_SIZE) {
                return ImageValidationResult(false, "Image size exceeds 10MB limit")
            }

            // Check image resolution
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            
            val width = options.outWidth
            val height = options.outHeight
            
            if (width < MIN_IMAGE_RESOLUTION || height < MIN_IMAGE_RESOLUTION) {
                return ImageValidationResult(false, "Image resolution too low. Minimum required: 1000x1000px")
            }
            
            ImageValidationResult(true, "")
        } catch (e: Exception) {
            Log.e(TAG, "Error validating image", e)
            ImageValidationResult(false, "Error validating image: ${e.message}")
        }
    }

    private fun getFileSize(uri: Uri): Long {
        return try {
            when (uri.scheme) {
                "file" -> {
                    val file = File(uri.path!!)
                    file.length()
                }
                "content" -> {
                    contentResolver.openInputStream(uri)?.use { stream ->
                        stream.available().toLong()
                    } ?: 0L
                }
                else -> 0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting file size", e)
            0L
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
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from URI", e)
            null
        }
    }

    private fun displayImage(bitmap: Bitmap) {
        imagePreview.setImageBitmap(bitmap)
        imagePreview.visibility = View.VISIBLE
        uploadPlaceholder.visibility = View.GONE
    }

    private fun uploadImage() {
        if (selectedImageFile == null) {
            Toast.makeText(this, "Please select an image to analyze", Toast.LENGTH_SHORT).show()
            return
        }

        // Check authentication
        val token = Sessions.getAccessToken(this)
        if (token == null) {
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show()
            navigateToLogin()
            return
        }

        // Show progress
        setUploadingState(true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = predictImageRisk(selectedImageFile!!, "Bearer $token")

                withContext(Dispatchers.Main) {
                    setUploadingState(false)
                    handleImagePredictionResponse(response)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error predicting image risk", e)
                withContext(Dispatchers.Main) {
                    setUploadingState(false)
                    handleUploadError(e)
                }
            }
        }
    }

    private suspend fun predictImageRisk(file: File, authToken: String): ImagePredictionResponse {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        return withContext(Dispatchers.IO) {
            try {
                val call = apiService.predictImageRisk(authToken, body)
                val response = call.execute()
                
                when {
                    response.isSuccessful && response.body() != null -> {
                        response.body()!!
                    }
                    response.code() == 401 -> {
                        throw Exception("Authentication failed. Please login again.")
                    }
                    response.code() == 403 -> {
                        throw Exception("Access denied. Please check your permissions.")
                    }
                    else -> {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        throw Exception("API call failed: ${response.code()} - $errorBody")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error during image prediction", e)
                throw e
            }
        }
    }

    private fun handleImagePredictionResponse(response: ImagePredictionResponse) {
        try {
            // Save to history first
            saveToHistory(response)
            
            // Navigate to Risk fragment with the prediction results
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("navigate_to", "risk")
                putExtra("risk_level", response.risk_level)
                putExtra("prediction_percentage", response.prediction_percentage)
                putExtra("mode", response.mode)
            }
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to risk level page", e)
            Toast.makeText(this, "Error displaying results: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun saveToHistory(response: ImagePredictionResponse) {
        val token = Sessions.getAccessToken(this)
        if (token == null) {
            Log.w(TAG, "No access token available for saving history")
            // Still append locally so history shows up
            appendLocalHistory(response)
            return
        }

        val historyRequest = SaveHistoryRequest(
            riskLevel = response.risk_level,
            predictionPercentage = response.prediction_percentage.toFloat(),
            mode = response.mode,
            createdAt = response.created_at
        )

        apiService.saveHistory("Bearer $token", historyRequest).enqueue(object : Callback<SaveHistoryResponse> {
            override fun onResponse(call: Call<SaveHistoryResponse>, resp: Response<SaveHistoryResponse>) {
                if (resp.isSuccessful) {
                    Log.d(TAG, "History saved successfully")
                } else {
                    Log.w(TAG, "Failed to save history: ${resp.code()}")
                }
                // Append to local cache regardless so UI shows it immediately
                appendLocalHistory(response)
            }

            override fun onFailure(call: Call<SaveHistoryResponse>, t: Throwable) {
                Log.e(TAG, "Error saving history", t)
                appendLocalHistory(response)
            }
        })
    }

    private fun appendLocalHistory(response: ImagePredictionResponse) {
        try {
            val gson = com.google.gson.Gson()
            val type = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, com.simats.mediai_app.responses.HistoryItem::class.java).type
            val existingJson = Sessions.getLocalHistoryJson(this)
            val existing: MutableList<com.simats.mediai_app.responses.HistoryItem> = if (!existingJson.isNullOrEmpty()) {
                gson.fromJson(existingJson, type) ?: mutableListOf()
            } else mutableListOf()
            existing.add(
                com.simats.mediai_app.responses.HistoryItem(
                    id = response.id,
                    riskLevel = response.risk_level,
                    predictionPercentage = response.prediction_percentage.toFloat(),
                    mode = response.mode,
                    createdAt = response.created_at,
                    userId = response.user
                )
            )
            val json = gson.toJson(existing)
            Sessions.saveLocalHistoryJson(this, json)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to append local history", e)
        }
    }

    private fun handleUploadError(error: Exception) {
        when {
            error.message?.contains("Authentication failed", ignoreCase = true) == true -> {
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_LONG).show()
                navigateToLogin()
            }
            error.message?.contains("Network error", ignoreCase = true) == true -> {
                Toast.makeText(this, "Network error. Please check your connection.", Toast.LENGTH_LONG).show()
            }
            error.message?.contains("timeout", ignoreCase = true) == true -> {
                Toast.makeText(this, "Request timeout. Please try again.", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, "Upload failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToLogin() {
        Sessions.clearAuthTokens(this)
        val intent = Intent(this, LoginPageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun setUploadingState(uploading: Boolean) {
        btnSubmitImage.isEnabled = !uploading
        btnCaptureCamera.isEnabled = !uploading
        btnUploadImage.isEnabled = !uploading
        progressBar.visibility = if (uploading) View.VISIBLE else View.GONE

        if (uploading) {
            btnSubmitImage.text = "Analyzing Image..."
        } else {
            btnSubmitImage.text = "Analyze Image"
        }
    }

    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage(message)
            .setPositiveButton("Settings") { _, _ ->
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
                requestCameraPermission()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private data class ImageValidationResult(val isValid: Boolean, val errorMessage: String)
}
