package com.simats.mediai_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import androidx.cardview.widget.CardView
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.View
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private lateinit var quoteText: TextView
    private lateinit var quoteAuthor: TextView
    private lateinit var dots: Array<View>
    private lateinit var handler: Handler
    private var currentQuoteIndex = 0
    
    // Quotes data for breast cancer patients
    private val quotes = listOf(
        Quote("You are stronger than you think. Every day is a new opportunity to heal and grow.", "Anonymous"),
        Quote("Your strength is inspiring. You are not alone in this journey.", "Anonymous"),
        Quote("Hope is the thing with feathers that perches in the soul.", "Emily Dickinson"),
        Quote("The human spirit is stronger than anything that can happen to it.", "C.C. Scott"),
        Quote("You have within you right now, everything you need to deal with whatever the world can throw at you.", "Brian Tracy"),
        Quote("Every day is a new beginning. Take a deep breath and start again.", "Anonymous"),
        Quote("Your body hears everything your mind says. Stay positive.", "Naomi Judd"),
        Quote("The only way to do great work is to love what you do.", "Steve Jobs"),
        Quote("You are braver than you believe, stronger than you seem, and smarter than you think.", "A.A. Milne"),
        Quote("Life is not about waiting for the storm to pass but learning to dance in the rain.", "Vivian Greene"),
        Quote("Your journey has molded you for your greater good.", "Asha Tyson"),
        Quote("The greatest glory in living lies not in never falling, but in rising every time we fall.", "Nelson Mandela"),
        Quote("You are never too old to set another goal or to dream a new dream.", "C.S. Lewis"),
        Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
        Quote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt")
    )

    data class Quote(val text: String, val author: String)

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
        setupQuotesRotation()
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

        view?.findViewById<CardView>(R.id.quotesCard)?.setOnClickListener {
            // Show next quote immediately when card is tapped
            showNextQuote()
        }

        view?.findViewById<CardView>(R.id.healthTipsCard)?.setOnClickListener {
            val intent = Intent(requireContext(), healthguide::class.java)
            startActivity(intent)
        }
    }

    private fun setupQuotesRotation() {
        // Initialize quote views
        quoteText = requireView().findViewById(R.id.quoteText)
        quoteAuthor = requireView().findViewById(R.id.quoteAuthor)
        
        // Initialize dots
        dots = arrayOf(
            requireView().findViewById(R.id.dot1),
            requireView().findViewById(R.id.dot2),
            requireView().findViewById(R.id.dot3),
            requireView().findViewById(R.id.dot4)
        )
        
        // Initialize handler
        handler = Handler(Looper.getMainLooper())
        
        // Show first quote
        showQuote(currentQuoteIndex)
        
        // Start auto-rotation
        startQuoteRotation()
    }

    private fun startQuoteRotation() {
        val runnable = object : Runnable {
            override fun run() {
                showNextQuote()
                // Schedule next rotation in 3-5 seconds
                val delay = Random.nextInt(3000, 5000).toLong()
                handler.postDelayed(this, delay)
            }
        }
        handler.postDelayed(runnable, 3000) // Start after 3 seconds
    }

    private fun showNextQuote() {
        currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
        showQuote(currentQuoteIndex)
    }

    private fun showQuote(index: Int) {
        val quote = quotes[index]
        quoteText.text = quote.text
        quoteAuthor.text = "- ${quote.author}"
        
        // Update dots to show current position
        updateDots(index % 4)
    }

    private fun updateDots(activeIndex: Int) {
        for (i in dots.indices) {
            if (i == activeIndex) {
                dots[i].setBackgroundResource(R.drawable.dot_active)
            } else {
                dots[i].setBackgroundResource(R.drawable.dot_inactive)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null)
    }
} 