package com.simats.mediai_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import android.widget.Toast

class RiskFragment : Fragment() {

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