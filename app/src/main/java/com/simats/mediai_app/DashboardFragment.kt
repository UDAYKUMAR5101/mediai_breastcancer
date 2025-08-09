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
import kotlin.random.Random
import com.simats.mediai_app.retrofit.ApiService
import com.simats.mediai_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

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

    private lateinit var apiService: ApiService
    private lateinit var riskLevelValueText: TextView
    private lateinit var riskLastUpdatedText: TextView

    private fun setupClickListeners() {
        // Profile Button - Navigate to Profile Activity
        view?.findViewById<ImageButton>(R.id.profileButton)?.setOnClickListener {
            val intent = Intent(requireContext(), myprofilepage::class.java)
            startActivity(intent)
        }

        // Risk Level Card - Navigate to Risk Fragment
//        view?.findViewById<CardView>(R.id.riskLevelCard)?.setOnClickListener {
//            findNavController().navigate(R.id.nav_risk)
//        }

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
        riskLevelValueText = requireView().findViewById(R.id.riskLevelValueText)
        riskLastUpdatedText = requireView().findViewById(R.id.riskLastUpdatedText)
        apiService = RetrofitClient.getClient().create(ApiService::class.java)
        
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

        // Load latest risk for dashboard
        loadLatestRisk()
    }

    private fun loadLatestRisk() {
        val token = Sessions.getAccessToken(requireContext()) ?: return
        apiService.getUserHistory("Bearer $token").enqueue(object : Callback<List<com.simats.mediai_app.responses.HistoryV2Item>> {
            override fun onResponse(
                call: Call<List<com.simats.mediai_app.responses.HistoryV2Item>>,
                response: Response<List<com.simats.mediai_app.responses.HistoryV2Item>>
            ) {
                val list = response.body().orEmpty()
                if (list.isNotEmpty()) {
                    val latest = list.maxByOrNull { it.created_at } ?: list.first()
                    updateRiskCard(latest.risk_level, latest.created_at)
                }
            }

            override fun onFailure(
                call: Call<List<com.simats.mediai_app.responses.HistoryV2Item>>,
                t: Throwable
            ) { /* ignore */ }
        })
    }

    private fun updateRiskCard(riskLevel: String, createdAtIso: String) {
        riskLevelValueText.text = "• ${riskLevel.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
        riskLevelValueText.setTextColor(
            when (riskLevel.lowercase(Locale.getDefault())) {
                "high" -> resources.getColor(R.color.risk_high, null)
                "moderate" -> resources.getColor(R.color.risk_moderate, null)
                else -> resources.getColor(R.color.risk_low, null)
            }
        )

        val label = formatLastUpdated(createdAtIso)
        riskLastUpdatedText.text = "Last updated: $label"
    }

    private fun formatLastUpdated(createdAtIso: String): String {
        return try {
            // Expecting ISO 8601. Try common patterns.
            val patterns = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
            )
            val parsed = patterns.firstNotNullOfOrNull { p ->
                try { java.text.SimpleDateFormat(p, Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.parse(createdAtIso) } catch (_: Exception) { null }
            } ?: return createdAtIso

            val cal = Calendar.getInstance()
            val today = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
            val thatDay = Calendar.getInstance().apply { time = parsed }

            val sameDay = today.get(Calendar.YEAR) == thatDay.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == thatDay.get(Calendar.DAY_OF_YEAR)
            if (sameDay) return "Today"

            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val isYesterday = yesterday.get(Calendar.YEAR) == thatDay.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == thatDay.get(Calendar.DAY_OF_YEAR)
            if (isYesterday) return "Yesterday"

            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(parsed)
        } catch (_: Exception) {
            createdAtIso
        }
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