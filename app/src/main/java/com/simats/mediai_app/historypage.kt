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
import android.util.Log

class historypage : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var loadingLayout: LinearLayout
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var apiService: ApiService
    
    companion object {
        private const val TAG = "HistoryPage"
    }

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
        setupApiService()
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
        findViewById<View>(R.id.backButton)?.setOnClickListener {
            finish()
        }

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadHistoryData()
        }
    }
    
    private fun setupApiService() {
        apiService = RetrofitClient.getClient().create(ApiService::class.java)
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

        Log.d(TAG, "Loading history data with token: ${token.take(10)}...")

        apiService.getHistory("Bearer $token").enqueue(object : Callback<GetHistoryResponse> {
            override fun onResponse(call: Call<GetHistoryResponse>, response: Response<GetHistoryResponse>) {
                swipeRefreshLayout.isRefreshing = false
                hideLoading()

                Log.d(TAG, "History response code: ${response.code()}")
                Log.d(TAG, "History response body: ${response.body()}")
                Log.d(TAG, "History response error body: ${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body() != null) {
                    val historyResponse = response.body()!!
                    Log.d(TAG, "History response success: ${historyResponse.success}")
                    Log.d(TAG, "History response message: ${historyResponse.message}")
                    Log.d(TAG, "History data size: ${historyResponse.data?.size ?: 0}")
                    
                    if (historyResponse.success) {
                        val historyList = historyResponse.data ?: emptyList()
                        if (historyList.isNotEmpty()) {
                            showHistoryList(historyList)
                            Log.d(TAG, "Showing ${historyList.size} history items")
                        } else {
                            showEmptyState("No history found. Save your first risk assessment to see it here.")
                            Log.d(TAG, "No history items found")
                        }
                    } else {
                        val errorMessage = historyResponse.message ?: "Failed to load history"
                        showEmptyState(errorMessage)
                        Log.e(TAG, "History API returned error: $errorMessage")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Authentication failed. Please log in again."
                        403 -> "Access denied. Please check your permissions."
                        404 -> "History not found."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to load history. Please try again. (Code: ${response.code()})"
                    }
                    showEmptyState(errorMessage)
                    Log.e(TAG, "History API failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<GetHistoryResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                hideLoading()
                val errorMessage = when {
                    t.message?.contains("timeout", ignoreCase = true) == true -> "Request timeout. Please try again."
                    t.message?.contains("network", ignoreCase = true) == true -> "Network error. Please check your connection."
                    else -> "Network error. Please check your connection."
                }
                showEmptyState(errorMessage)
                Log.e(TAG, "History API call failed", t)
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
        findViewById<TextView>(R.id.emptyStateText)?.text = message
    }
}