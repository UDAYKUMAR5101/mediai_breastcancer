package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.content.Intent
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.simats.mediai_app.adapters.HistoryAdapter
import com.simats.mediai_app.responses.HistoryItem
import com.simats.mediai_app.responses.GetHistoryResponse
import com.simats.mediai_app.retrofit.ApiService
import com.simats.mediai_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class historypage : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var loadingLayout: LinearLayout
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historypage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadHistoryData()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.historyRecyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        loadingLayout = findViewById(R.id.loadingLayout)
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(emptyList()) { historyItem ->
            // Handle item click - could navigate to detail view
            Toast.makeText(this, "Selected: ${historyItem.mode} - ${historyItem.riskLevel}", Toast.LENGTH_SHORT).show()
        }
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@historypage)
            adapter = historyAdapter
        }
    }

    private fun setupClickListeners() {
        // Back arrow navigation to dashboard
        findViewById<View>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadHistoryData()
        }
    }

    private fun loadHistoryData() {
        // Check if user is logged in
        if (!Sessions.isLoggedIn(this)) {
            showEmptyState("Please log in to view your history")
            return
        }

        showLoading()

        val token = Sessions.getAccessToken(this)
        if (token == null) {
            showEmptyState("Authentication error. Please log in again.")
            return
        }

        val apiService = RetrofitClient.getClient().create(ApiService::class.java)
        apiService.getHistory("Bearer $token").enqueue(object : Callback<GetHistoryResponse> {
            override fun onResponse(call: Call<GetHistoryResponse>, response: Response<GetHistoryResponse>) {
                swipeRefreshLayout.isRefreshing = false
                hideLoading()

                if (response.isSuccessful && response.body() != null) {
                    val historyResponse = response.body()!!
                    if (historyResponse.success) {
                        val historyList = historyResponse.data ?: emptyList()
                        if (historyList.isNotEmpty()) {
                            showHistoryList(historyList)
                        } else {
                            showEmptyState("No history found. Save your first risk assessment to see it here.")
                        }
                    } else {
                        showEmptyState(historyResponse.message)
                    }
                } else {
                    showEmptyState("Failed to load history. Please try again.")
                }
            }

            override fun onFailure(call: Call<GetHistoryResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                hideLoading()
                showEmptyState("Network error. Please check your connection.")
            }
        })
    }

    private fun showLoading() {
        loadingLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingLayout.visibility = View.GONE
    }

    private fun showHistoryList(historyList: List<HistoryItem>) {
        recyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE
        historyAdapter.updateHistoryList(historyList)
    }

    private fun showEmptyState(message: String) {
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
        findViewById<TextView>(R.id.emptyStateText).text = message
    }
}