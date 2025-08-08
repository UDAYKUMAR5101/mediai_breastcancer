package com.simats.mediai_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.ImageButton
import android.util.Log
import com.simats.mediai_app.retrofit.RetrofitClient
import com.simats.mediai_app.responses.SaveHistoryRequest
import com.simats.mediai_app.responses.SaveHistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RiskFragment : Fragment() {

    companion object {
        private const val TAG = "RiskFragment"
        fun newInstance(riskLevel: String, predictionPercentage: Double, mode: String): RiskFragment {
            return RiskFragment().apply {
                arguments = Bundle().apply {
                    putString("risk_level", riskLevel)
                    putDouble("prediction_percentage", predictionPercentage)
                    putString("mode", mode)
                }
            }
        }
    }

    // API Service
    private lateinit var apiService: com.simats.mediai_app.retrofit.ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_risk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize API Service
        apiService = RetrofitClient.getClient().create(com.simats.mediai_app.retrofit.ApiService::class.java)
        
        // Initialize views and set up click listeners
        setupClickListeners()
        
        // Check if we have risk data from arguments (nav) or from activity intent
        val args = arguments
        val riskLevelFromArgs = args?.getString("risk_level")
        val predictionFromArgs = args?.getDouble("prediction_percentage", 15.0) ?: 15.0
        val modeFromArgs = args?.getString("mode", "symptoms") ?: "symptoms"

        if (riskLevelFromArgs != null) {
            updateRiskDisplay(riskLevelFromArgs, predictionFromArgs, modeFromArgs)
        } else {
            activity?.intent?.let { intent ->
                val risk = intent.getStringExtra("risk_level")
                val percent = intent.getDoubleExtra("prediction_percentage", 15.0)
                val mode = intent.getStringExtra("mode") ?: "symptoms"
                if (!risk.isNullOrEmpty()) {
                    updateRiskDisplay(risk, percent, mode)
                }
            }
        }
    }
    
    private fun updateRiskDisplay(riskLevel: String, predictionPercentage: Double, mode: String) {
        // Update the UI elements with the received data
        view?.findViewById<TextView>(R.id.riskLevelTextView)?.text = riskLevel
        view?.findViewById<TextView>(R.id.percentageTextView)?.text = "${predictionPercentage.toInt()}%"
        view?.findViewById<ProgressBar>(R.id.progressBar)?.progress = predictionPercentage.toInt()
        view?.findViewById<TextView>(R.id.modeTextView)?.text = "Mode: $mode"
        
        // Set color based on risk level
        val colorRes = when (riskLevel.lowercase()) {
            "high" -> R.color.risk_high
            "moderate" -> R.color.risk_moderate
            else -> R.color.risk_low
        }
        
        val color = ContextCompat.getColor(requireContext(), colorRes)
        view?.findViewById<TextView>(R.id.riskLevelTextView)?.setTextColor(color)
        view?.findViewById<TextView>(R.id.percentageTextView)?.setTextColor(color)
        
        // Update info card text based on risk level and mode
        val infoText = when {
            mode.lowercase() == "image" -> {
                when (riskLevel.lowercase()) {
                    "high" -> "Based on image analysis, you have a high risk of developing breast cancer. Please consult with a healthcare provider immediately for further evaluation and screening."
                    "moderate" -> "Based on image analysis, you have a moderate risk of developing breast cancer. Regular screening and consultation with a healthcare provider is recommended."
                    else -> "Based on image analysis, you have a low risk of developing breast cancer. Regular screening is still recommended."
                }
            }
            else -> {
                when (riskLevel.lowercase()) {
                    "high" -> "Based on your symptoms assessment, you have a high risk of developing breast cancer. Please consult with a healthcare provider immediately for further evaluation and screening."
                    "moderate" -> "Based on your symptoms assessment, you have a moderate risk of developing breast cancer. Regular screening and consultation with a healthcare provider is recommended."
                    else -> "Based on your symptoms assessment, you have a low risk of developing breast cancer. Regular screening is still recommended."
                }
            }
        }
        view?.findViewById<TextView>(R.id.infoCardTextView)?.text = infoText
    }

    private fun setupClickListeners() {
        // Back Button
        view?.findViewById<ImageButton>(R.id.backButton)?.setOnClickListener {
            navigateToDashboard()
        }

        // Save Result Button
        view?.findViewById<MaterialButton>(R.id.btnSaveResult)?.setOnClickListener { 
            saveRiskResult()
        }

        // Breast Cancer Guide Button
        view?.findViewById<MaterialButton>(R.id.btnBreastCancerGuide)?.setOnClickListener {
            navigateToBreastCancerGuide()
        }
    }

    private fun saveRiskResult() {
        // Get current risk data
        val riskLevel = arguments?.getString("risk_level") ?: 
                       activity?.intent?.getStringExtra("risk_level") ?: "low"
        val predictionPercentage = arguments?.getDouble("prediction_percentage", 15.0) ?: 
                                 activity?.intent?.getDoubleExtra("prediction_percentage", 15.0) ?: 15.0
        val mode = arguments?.getString("mode") ?: 
                  activity?.intent?.getStringExtra("mode") ?: "symptoms"
        
        // Get current timestamp
        val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            .format(java.util.Date())
        
        // Check authentication
        val token = Sessions.getAccessToken(requireContext())
        if (token == null) {
            Toast.makeText(context, "Authentication required. Please login again.", Toast.LENGTH_LONG).show()
            navigateToLogin()
            return
        }
        
        // Create history request
        val historyRequest = SaveHistoryRequest(
            riskLevel = riskLevel,
            predictionPercentage = predictionPercentage.toFloat(),
            mode = mode,
            createdAt = currentTime
        )
        
        // Show loading state
        view?.findViewById<MaterialButton>(R.id.btnSaveResult)?.apply {
            isEnabled = false
            text = "Saving..."
        }
        
        // Save to history via API
        apiService.saveHistory("Bearer $token", historyRequest).enqueue(object : Callback<SaveHistoryResponse> {
            override fun onResponse(call: Call<SaveHistoryResponse>, response: Response<SaveHistoryResponse>) {
                view?.findViewById<MaterialButton>(R.id.btnSaveResult)?.apply {
                    isEnabled = true
                    text = "Save Result"
                }
                
                if (response.isSuccessful) {
                    Toast.makeText(context, "Risk assessment saved to history successfully!", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "History saved successfully")
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Authentication failed. Please login again."
                        403 -> "Access denied. Please check your permissions."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to save history: ${response.code()}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    Log.w(TAG, "Failed to save history: ${response.code()}")
                    
                    if (response.code() == 401) {
                        navigateToLogin()
                    }
                }
            }
            
            override fun onFailure(call: Call<SaveHistoryResponse>, t: Throwable) {
                view?.findViewById<MaterialButton>(R.id.btnSaveResult)?.apply {
                    isEnabled = true
                    text = "Save Result"
                }
                
                val errorMessage = when {
                    t.message?.contains("timeout", ignoreCase = true) == true -> "Request timeout. Please try again."
                    t.message?.contains("network", ignoreCase = true) == true -> "Network error. Please check your connection."
                    else -> "Error saving history: ${t.message}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error saving history", t)
            }
        })
    }

    private fun navigateToDashboard() {
        try {
            // Navigate to dashboard using navigation component
            findNavController().navigate(R.id.nav_home)
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to dashboard", e)
            // Fallback: try to navigate to MainActivity with dashboard intent
            val intent = android.content.Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("navigate_to", "dashboard")
            }
            startActivity(intent)
        }
    }

    private fun navigateToLogin() {
        Sessions.clearAuthTokens(requireContext())
        val intent = android.content.Intent(requireContext(), LoginPageActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        activity?.finish()
    }

    private fun navigateToBreastCancerGuide() {
        // Navigate to existing breast cancer guide activity
        val intent = android.content.Intent(requireContext(), breastcancerguide::class.java)
        startActivity(intent)
    }
} 