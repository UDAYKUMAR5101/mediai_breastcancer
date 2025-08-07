package com.simats.mediai_app

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class editprofile : AppCompatActivity() {
    private lateinit var dateOfBirthEditText: EditText
    private lateinit var fullNameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var medicalNotesEditText: EditText
    private lateinit var fullNameCounter: TextView
    private lateinit var medicalNotesCounter: TextView
    private lateinit var saveChangesButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var changePhotoText: TextView
    
    // Photo selection variables
    private var selectedImageFile: File? = null
    private var cameraImageUri: Uri? = null
    
    // Activity Result Launchers
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    companion object {
        private const val TAG = "EditProfileActivity"
        private const val MAX_IMAGE_SIZE = 10 * 1024 * 1024 // 10MB
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editprofile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Initialize all views
        initializeViews()
        
        // Setup photo functionality
        setupPermissionLaunchers()
        setupActivityResultLaunchers()
        setupPhotoSelection()
        
        setupDatePicker()
        setupCharacterCounters()
        setupSaveButton()
        setupBackButton()
        loadUserData()
    }
    
    private fun initializeViews() {
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText)
        fullNameEditText = findViewById(R.id.fullNameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        genderEditText = findViewById(R.id.genderEditText)
        medicalNotesEditText = findViewById(R.id.medicalNotesEditText)
        fullNameCounter = findViewById(R.id.fullNameCounter)
        medicalNotesCounter = findViewById(R.id.medicalNotesCounter)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        
        // Profile image views
        profileImageView = findViewById(R.id.profileImageView)
        changePhotoText = findViewById(R.id.changePhotoText)
    }
    
    private fun setupDatePicker() {
        // Set maximum date to 80 years ago
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -80)
        
        dateOfBirthEditText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    dateOfBirthEditText.setText(dateFormatter.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            
            // Set maximum date to 80 years ago
            datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
            
            // Set minimum date to 18 years ago (optional, you can remove this if you want to allow younger ages)
            val minDate = Calendar.getInstance()
            minDate.add(Calendar.YEAR, -18)
            datePickerDialog.datePicker.minDate = minDate.timeInMillis
            
            datePickerDialog.show()
        }
    }
    
    private fun setupCharacterCounters() {
        // Full name character counter
        fullNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                fullNameCounter.text = "$currentLength/50"
            }
        })
        
        // Medical notes character counter
        medicalNotesEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                medicalNotesCounter.text = "$currentLength/300"
            }
        })
    }
    
    private fun setupSaveButton() {
        saveChangesButton.setOnClickListener {
            if (validateInputs()) {
                saveProfile()
            }
        }
    }
    
    private fun setupBackButton() {
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
    
    private fun loadUserData() {
        // Load saved username from signup/login and populate the full name field
        val savedUsername = Sessions.getUsername(this)
        val savedEmail = Sessions.getUserEmail(this)
        
        Log.d(TAG, "Loading user data - Username: $savedUsername, Email: $savedEmail")
        
        if (!savedUsername.isNullOrEmpty()) {
            fullNameEditText.setText(savedUsername)
            // Update character counter
            fullNameCounter.text = "${savedUsername.length}/50"
            Log.d(TAG, "Username loaded and displayed: $savedUsername")
        } else {
            Log.w(TAG, "No saved username found")
        }
        
        // Optionally, you can also pre-fill other fields if you have that data
        // For example, if you save age, gender, etc. during signup
    }
    
    private fun validateInputs(): Boolean {
        val fullName = fullNameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()
        val dateOfBirth = dateOfBirthEditText.text.toString().trim()
        val medicalNotes = medicalNotesEditText.text.toString().trim()
        
        if (fullName.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_full_name), Toast.LENGTH_SHORT).show()
            fullNameEditText.requestFocus()
            return false
        }
        
        if (age.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_age), Toast.LENGTH_SHORT).show()
            ageEditText.requestFocus()
            return false
        }
        
        val ageInt = age.toIntOrNull()
        if (ageInt == null || ageInt < 18 || ageInt > 100) {
            Toast.makeText(this, getString(R.string.enter_valid_age), Toast.LENGTH_SHORT).show()
            ageEditText.requestFocus()
            return false
        }
        
        if (dateOfBirth.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_date_of_birth), Toast.LENGTH_SHORT).show()
            dateOfBirthEditText.requestFocus()
            return false
        }
        
        return true
    }
    
    private fun saveProfile() {
        // Save updated profile data locally
        val updatedName = fullNameEditText.text.toString().trim()
        val currentEmail = Sessions.getUserEmail(this) ?: ""
        
        // Update the stored username with the new name
        Sessions.saveUserData(this, updatedName, currentEmail)
        Log.d(TAG, "Profile data updated - Name: $updatedName")
        
        // Show success message
        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    // Photo Selection Methods
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
    
    private fun setupPhotoSelection() {
        changePhotoText.setOnClickListener {
            showPhotoSelectionDialog()
        }
        
        profileImageView.setOnClickListener {
            showPhotoSelectionDialog()
        }
    }
    
    private fun showPhotoSelectionDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        
        AlertDialog.Builder(this)
            .setTitle("Select Profile Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission()
                    1 -> requestStoragePermission()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun requestCameraPermission() {
        when {
            checkSelfPermission(Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationaleDialog(
                    "Camera Permission Required",
                    "This app needs camera permission to take photos for your profile picture.",
                    Manifest.permission.CAMERA
                ) { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        when {
            checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationaleDialog(
                    "Storage Permission Required",
                    "This app needs storage permission to select images from your gallery for your profile picture.",
                    permission
                ) { storagePermissionLauncher.launch(permission) }
            }
            else -> {
                storagePermissionLauncher.launch(permission)
            }
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
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "PROFILE_${timeStamp}_"
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }
    
    private fun handleImageSelection(uri: Uri) {
        try {
            val bitmap = loadBitmapFromUri(uri)
            if (bitmap == null) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Convert URI to File and check size
            val file = uriToFile(uri)
            if (file.length() > MAX_IMAGE_SIZE) {
                Toast.makeText(this, "Image size must be less than 10MB", Toast.LENGTH_LONG).show()
                return
            }
            
            // Display image and store file reference
            displayImage(bitmap)
            selectedImageFile = file
            Toast.makeText(this, "Profile photo updated successfully", Toast.LENGTH_SHORT).show()
            
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
            val inputStream = contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            
            // Calculate sample size for memory optimization
            val sampleSize = calculateInSampleSize(options, 800, 800)
            
            val finalInputStream = contentResolver.openInputStream(uri)
            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            val bitmap = BitmapFactory.decodeStream(finalInputStream, null, finalOptions)
            finalInputStream?.close()
            bitmap
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
            
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
    
    private fun displayImage(bitmap: Bitmap) {
        profileImageView.apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageBitmap(bitmap)
            // Remove the tint when showing actual photo
            imageTintList = null
            background = null
        }
    }
    
    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
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
    
    private fun showPermissionRationaleDialog(
        title: String,
        message: String,
        permission: String,
        onPositive: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Grant Permission") { _, _ -> onPositive() }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    // Optional: Convert image to MultipartBody.Part for future upload
    fun createImageMultipart(): MultipartBody.Part? {
        return selectedImageFile?.let { file ->
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("profile_image", file.name, requestBody)
        }
    }
}
