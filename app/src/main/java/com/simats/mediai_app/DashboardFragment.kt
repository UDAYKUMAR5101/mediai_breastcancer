package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import androidx.cardview.widget.CardView
import android.widget.Button
import android.widget.ImageButton

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views and set up click listeners
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Profile Button - Navigate to Profile Activity
        view?.findViewById<ImageButton>(R.id.profileButton)?.setOnClickListener {
            val intent = Intent(requireContext(), myprofilepage::class.java)
            startActivity(intent)
        }

        // Risk Level Card - Navigate to Risk Fragment
        view?.findViewById<CardView>(R.id.riskLevelCard)?.setOnClickListener {
            findNavController().navigate(R.id.nav_risk)
        }

        // Upload Image Card - Navigate to Upload Activity
        view?.findViewById<CardView>(R.id.uploadImageCard)?.setOnClickListener {
            val intent = Intent(requireContext(), UploadpageActivity::class.java)
            startActivity(intent)
        }

        // Symptoms Card - Navigate to Symptoms Activity
        view?.findViewById<CardView>(R.id.symptomsCard)?.setOnClickListener {
            val intent = Intent(requireContext(), symstomspage::class.java)
            startActivity(intent)
        }

        // History Card - Navigate to History Activity
        view?.findViewById<CardView>(R.id.historyCard)?.setOnClickListener {
            val intent = Intent(requireContext(), historypage::class.java)
            startActivity(intent)
        }

        // Recent Activity Cards - Navigate to respective activities
        view?.findViewById<CardView>(R.id.dailyCheckCard)?.setOnClickListener {
            val intent = Intent(requireContext(), todolist::class.java)
            startActivity(intent)
        }

        view?.findViewById<CardView>(R.id.symptomPhotoCard)?.setOnClickListener {
            val intent = Intent(requireContext(), uploadedhistory::class.java)
            startActivity(intent)
        }

        view?.findViewById<CardView>(R.id.symptomsUpdatedCard)?.setOnClickListener {
            val intent = Intent(requireContext(), documentupload::class.java)
            startActivity(intent)
        }

        view?.findViewById<CardView>(R.id.healthTipsCard)?.setOnClickListener {
            val intent = Intent(requireContext(), healthguide::class.java)
            startActivity(intent)
        }
    }
} 