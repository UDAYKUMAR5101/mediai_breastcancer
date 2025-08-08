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
import androidx.lifecycle.ViewModelProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.simats.mediai_app.retrofit.ApiService
import com.simats.mediai_app.retrofit.RetrofitClient
import com.bumptech.glide.Glide
import com.simats.mediai_app.responses.ProfileData

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
    private lateinit var progressBar: View
    
    // Photo selection variables
    private var selectedImageFile: File? = null
    private var cameraImageUri: Uri? = null
    private var currentProfileImageUrl: String? = null
    
    // Activity Result Launchers
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var storagePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val apiDateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // API Service
    private lateinit var apiService: ApiService
    private var profileId: Int? = null
    private var profileExists = false
    
    // Store original values for comparison
    private var originalProfileData: ProfileData? = null
    
    // ViewModel
    private lateinit var profileViewModel: ProfileViewModel
    
    companion object {
        private const val TAG = "EditProfileActivity"
        private const val MAX_IMAGE_SIZE = 10 * 1024 * 1024 // 10MB
        
        // Intent extras for data sharing
        const val EXTRA_PROFILE_UPDATED = "profile_updated"
        const val EXTRA_PROFILE_DATA = "profile_data"
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
        
        // Initialize ViewModel
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        
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
        
        // Initialize API Service
        apiService = RetrofitClient.getClient().create(ApiService::class.java)
        
        // Load profile data
        loadProfileData()
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
        
        // Progress indicator
        progressBar = findViewById(R.id.progressBar)
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
            saveProfile()
        }
    }
    
    private fun setupBackButton() {
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }
    }
    
    private fun loadProfileData() {
        val token = Sessions.getAccessToken(this)
        if (token == null) {
            Log.e(TAG, "No access token found")
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        setLoadingState(true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getProfile("Bearer $token")
                val profileResponse = response.execute()

                withContext(Dispatchers.Main) {
                    if (profileResponse.isSuccessful && profileResponse.body() != null) {
                        val profile = profileResponse.body()!!
                        profileExists = true
                        profileId = 1 // Assuming the API returns profile with ID 1
                        originalProfileData = profile.data
                        populateProfileData(profile.data)
                        Log.d(TAG, "Profile loaded successfully")
                    } else {
                        // Profile doesn't exist, load local data as fallback
                        loadLocalUserData()
                        Log.d(TAG, "Profile doesn't exist, using local data")
                    }
                    setLoadingState(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile", e)
                withContext(Dispatchers.Main) {
                    loadLocalUserData()
                    setLoadingState(false)
                    Toast.makeText(this@editprofile, "Error loading profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadLocalUserData() {
        // Load saved username from signup/login and populate the full name field
        val savedUsername = Sessions.getUsername(this)
        val savedEmail = Sessions.getUserEmail(this)
        
        Log.d(TAG, "Loading local user data - Username: $savedUsername, Email: $savedEmail")
        
        if (!savedUsername.isNullOrEmpty()) {
            fullNameEditText.setText(savedUsername)
            // Update character counter
            fullNameCounter.text = "${savedUsername.length}/50"
            Log.d(TAG, "Username loaded and displayed: $savedUsername")
        } else {
            Log.w(TAG, "No saved username found")
        }
    }

    private fun populateProfileData(profileData: ProfileData?) {
        // Populate form fields with profile data
        fullNameEditText.setText(profileData?.username)
        fullNameCounter.text = "${profileData?.username?.length}/50"
        
        ageEditText.setText(profileData?.age.toString())
        genderEditText.setText(profileData?.gender)
        
        // Convert API date format (yyyy-MM-dd) to display format (dd/MM/yyyy)
        try {
            val apiDate = apiDateFormatter.parse(profileData?.date_of_birth)
            if (apiDate != null) {
                dateOfBirthEditText.setText(dateFormatter.format(apiDate))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date", e)
        }
        
        medicalNotesEditText.setText(profileData?.notes)
        medicalNotesCounter.text = "${profileData?.notes?.length}/300"
        
        // Load profile image if available
        if (!profileData?.image.isNullOrEmpty()) {
            currentProfileImageUrl = profileData?.image
            loadProfileImage(profileData?.image)
        }
    }

    private fun loadProfileImage(imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.profile_photo)
                .error(R.drawable.profile_photo)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.profile_photo)
        }
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
        if (!validateInputs()) {
            return
        }

        val token = Sessions.getAccessToken(this)
        if (token == null) {
            Toast.makeText(this, "Authentication required", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        setLoadingState(true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val profileData = prepareProfileData()
                
                if (profileExists && profileId != null) {
                    // Update existing profile
                    updateProfileOnServer(token, profileId!!, profileData)
                } else {
                    // Create new profile
                    createProfileOnServer(token, profileData)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving profile", e)
                withContext(Dispatchers.Main) {
                    setLoadingState(false)
                    Toast.makeText(this@editprofile, "Error saving profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun prepareProfileData(): Map<String, String> {
        val username = fullNameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim()
        val gender = genderEditText.text.toString().trim()
        val dateOfBirth = convertDateForAPI(dateOfBirthEditText.text.toString().trim())
        val notes = medicalNotesEditText.text.toString().trim()

        return mapOf(
            "username" to username,
            "age" to age,
            "gender" to gender,
            "date_of_birth" to dateOfBirth,
            "notes" to notes
        )
    }

    private fun convertDateForAPI(displayDate: String): String {
        return try {
            val parsedDate = dateFormatter.parse(displayDate)
            if (parsedDate != null) {
                apiDateFormatter.format(parsedDate)
            } else {
                displayDate
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting date format", e)
            displayDate
        }
    }

    private suspend fun createProfileOnServer(token: String, profileData: Map<String, String>) {
        val requestBodyMap = profileData.mapValues { (_, value) ->
            value.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val imagePart = selectedImageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        val call = apiService.createProfile(
            "Bearer $token",
            requestBodyMap["username"]!!,
            requestBodyMap["age"]!!,
            requestBodyMap["gender"]!!,
            requestBodyMap["date_of_birth"]!!,
            requestBodyMap["notes"]!!,
            imagePart
        )

        val response = call.execute()
        
        withContext(Dispatchers.Main) {
            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                profileExists = true
                profileId = 1 // Assuming the API returns profile with ID 1
                
                // Update local storage
                Sessions.saveUserData(this@editprofile, profileData["username"]!!, Sessions.getUserEmail(this@editprofile) ?: "")
                
                // Update current profile data
                originalProfileData = ProfileData(
                    username = profileData["username"],
                    age = profileData["age"]?.toIntOrNull(),
                    gender = profileData["gender"],
                    date_of_birth = profileData["date_of_birth"],
                    notes = profileData["notes"],
                    image = profileResponse.data?.image
                )
                
                // Update profile image if uploaded
                if (selectedImageFile != null) {
                    displayImage(loadBitmapFromFile(selectedImageFile!!))
                }
                
                setLoadingState(false)
                Toast.makeText(this@editprofile, "Profile created successfully", Toast.LENGTH_SHORT).show()
                
                // Share updated data with other activities
                shareUpdatedProfileData()
            } else {
                // Profile already exists, try PATCH
                if (response.code() == 400 && response.errorBody()?.string()?.contains("Profile already exists") == true) {
                    Log.d(TAG, "Profile already exists, trying PATCH")
                    profileExists = true
                    profileId = 1
                    updateProfileOnServer(token, 1, profileData)
                } else {
                    setLoadingState(false)
                    Toast.makeText(this@editprofile, "Error creating profile: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun updateProfileOnServer(token: String, profileId: Int, profileData: Map<String, String>) {
        val requestBodyMap = profileData.mapValues { (_, value) ->
            value.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        val imagePart = selectedImageFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        }

        val call = apiService.updateProfile(
            "Bearer $token",
            profileId,
            requestBodyMap["username"]!!,
            requestBodyMap["age"]!!,
            requestBodyMap["gender"]!!,
            requestBodyMap["date_of_birth"]!!,
            requestBodyMap["notes"]!!,
            imagePart
        )

        val response = call.execute()
        
        withContext(Dispatchers.Main) {
            if (response.isSuccessful && response.body() != null) {
                val profileResponse = response.body()!!
                
                // Update local storage
                Sessions.saveUserData(this@editprofile, profileData["username"]!!, Sessions.getUserEmail(this@editprofile) ?: "")
                
                // Update current profile data
                originalProfileData = ProfileData(
                    username = profileData["username"],
                    age = profileData["age"]?.toIntOrNull(),
                    gender = profileData["gender"],
                    date_of_birth = profileData["date_of_birth"],
                    notes = profileData["notes"],
                    image = profileResponse.data?.image ?: originalProfileData?.image
                )
                
                // Update profile image if uploaded
                if (selectedImageFile != null) {
                    displayImage(loadBitmapFromFile(selectedImageFile!!))
                }
                
                setLoadingState(false)
                Toast.makeText(this@editprofile, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                
                // Share updated data with other activities
                shareUpdatedProfileData()
            } else {
                setLoadingState(false)
                Toast.makeText(this@editprofile, "Error updating profile: ${response.message()}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun shareUpdatedProfileData() {
        // Update ViewModel with new profile data
        profileViewModel.updateProfile(originalProfileData)
        
        // Create intent with updated profile data
        val intent = Intent().apply {
            putExtra(EXTRA_PROFILE_UPDATED, true)
            putExtra(EXTRA_PROFILE_DATA, originalProfileData)
        }
        
        // Set result for any activity that started this one
        setResult(RESULT_OK, intent)
        
        // Broadcast to other activities that might be interested
        sendBroadcast(intent)
    }
    
    private fun setLoadingState(loading: Boolean) {
        saveChangesButton.isEnabled = !loading
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        
        if (loading) {
            saveChangesButton.text = "Saving..."
        } else {
            saveChangesButton.text = "Save Changes"
        }
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
    
    private fun loadBitmapFromFile(file: File): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
            
            // Calculate sample size for memory optimization
            val sampleSize = calculateInSampleSize(options, 800, 800)
            
            val finalOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            BitmapFactory.decodeFile(file.absolutePath, finalOptions)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from file", e)
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
    
    private fun displayImage(bitmap: Bitmap?) {
        if (bitmap != null) {
            profileImageView.apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageBitmap(bitmap)
                // Remove the tint when showing actual photo
                imageTintList = null
                background = null
            }
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
}
