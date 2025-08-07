package com.simats.mediai_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import android.widget.Toast
import android.widget.TextView
import android.widget.ProgressBar

class RiskFragment : Fragment() {

    companion object {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_risk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views and set up click listeners
        setupClickListeners()
        
        // Check if we have risk data from arguments
        arguments?.let { args ->
            val riskLevel = args.getString("risk_level")
            val predictionPercentage = args.getDouble("prediction_percentage", 15.0)
            val mode = args.getString("mode", "symptoms")
            
            if (riskLevel != null) {
                updateRiskDisplay(riskLevel, predictionPercentage, mode)
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
        
        // Update info card text based on risk level
        val infoText = when (riskLevel.lowercase()) {
            "high" -> "Based on your assessment, you have a high risk of developing breast cancer. Please consult with a healthcare provider immediately for further evaluation and screening."
            "moderate" -> "Based on your assessment, you have a moderate risk of developing breast cancer. Regular screening and consultation with a healthcare provider is recommended."
            else -> "Based on your assessment, you have a low risk of developing breast cancer. Regular screening is still recommended."
        }
        view?.findViewById<TextView>(R.id.infoCardTextView)?.text = infoText
    }

    private fun setupClickListeners() {
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
        // Show success message
        Toast.makeText(context, "Risk assessment saved successfully!", Toast.LENGTH_SHORT).show()
        
        // Here you would typically save to SharedPreferences or send to server
        // For now, just show a toast message
    }

    private fun navigateToBreastCancerGuide() {
        // Navigate to existing breast cancer guide activity
        val intent = android.content.Intent(requireContext(), breastcancerguide::class.java)
        startActivity(intent)
    }
} 