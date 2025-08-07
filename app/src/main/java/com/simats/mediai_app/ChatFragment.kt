package com.simats.mediai_app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simats.mediai_app.adapters.ChatAdapter
import com.simats.mediai_app.models.ChatMessage
import com.simats.mediai_app.responses.ChatRequest
import com.simats.mediai_app.responses.ChatResponse
// import com.simats.mediai_app.retrofit.ChatService (removed unused import)
import com.simats.mediai_app.retrofit.retrofit2
import com.simats.mediai_app.Sessions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class ChatFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: TextInputEditText
    private lateinit var sendButton: MaterialButton
    private lateinit var clearChatButton: ImageButton
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var chatAdapter: ChatAdapter
    private var isLoading = false

    companion object {
        private const val TAG = "ChatFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
        setupClickListeners()
        setupRecyclerView()
        loadChatHistory()
    }
    
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(messageInput.windowToken, 0)
    }

    private fun initializeViews() {
        chatRecyclerView = requireView().findViewById(R.id.chatRecyclerView)
        messageInput = requireView().findViewById(R.id.messageInput)
        sendButton = requireView().findViewById(R.id.sendButton)
        clearChatButton = requireView().findViewById(R.id.clearChatButton)
        loadingIndicator = requireView().findViewById(R.id.loadingIndicator)
    }

    private fun setupClickListeners() {
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Handle Enter key press in the message input field
        messageInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND ||
                (event != null && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN)
            ) {
                sendMessage()
                true
            } else {
                false
            }
        }
        
        clearChatButton.setOnClickListener {
            showClearChatConfirmation()
        }
    }
    
    private fun showClearChatConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear Chat History")
            .setMessage("Are you sure you want to clear all chat messages? This action cannot be undone.")
            .setPositiveButton("Clear") { _, _ ->
                clearChatHistory()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun clearChatHistory() {
        chatAdapter.clear()
        Sessions.saveChatHistory(requireContext(), "")
        showWelcomeMessage()
        Toast.makeText(requireContext(), "Chat history cleared", Toast.LENGTH_SHORT).show()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        chatRecyclerView.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true // Messages appear from bottom
        }
        chatRecyclerView.adapter = chatAdapter
        // Add scroll listener to detect when user scrolls up
        chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    val itemCount = layoutManager.itemCount
                    
                    // If the last item is visible, show the scroll to bottom button
                    if (lastVisibleItemPosition < itemCount - 1) {
                        // Could add a scroll to bottom button here in the future
                    }
                }
            }
        })
    }
    
    private fun scrollToBottom() {
        if (chatAdapter.itemCount > 0) {
            chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }
    }
    
    private fun loadChatHistory() {
        val chatHistoryJson = Sessions.getChatHistory(requireContext())
        if (!chatHistoryJson.isNullOrEmpty()) {
            try {
                val type: Type = object : TypeToken<List<ChatMessage>>() {}.type
                val chatHistory: List<ChatMessage> = Gson().fromJson(chatHistoryJson, type)
                
                if (chatHistory.isNotEmpty()) {
                    chatAdapter.clear()
                    chatHistory.forEach { message ->
                        chatAdapter.addMessage(message)
                    }
                    chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    return
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading chat history: ${e.message}")
            }
        }
        
        // If no history or error, show welcome message
        showWelcomeMessage()
    }
    
    private fun saveChatHistory() {
        try {
            val messages = chatAdapter.getMessages()
            val chatHistoryJson = Gson().toJson(messages)
            Sessions.saveChatHistory(requireContext(), chatHistoryJson)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving chat history: ${e.message}")
        }
    }

    private fun showWelcomeMessage() {
        val welcomeMessage = "Welcome to AI Health Assistant! Ask me anything about health."
        chatAdapter.addMessage(ChatMessage(
            message = welcomeMessage,
            isFromUser = false
        ))
    }

    private fun sendMessage() {
        if (isLoading) {
            Toast.makeText(context, "Please wait while I process your previous message", Toast.LENGTH_SHORT).show()
            return
        }
        
        val message = messageInput.text.toString().trim()
        if (message.isEmpty()) {
            Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        // Add user message to chat
        chatAdapter.addMessage(ChatMessage(
            message = message,
            isFromUser = true
        ))
        
        // Clear input and scroll to bottom
        messageInput.text?.clear()
        hideKeyboard()
        scrollToBottom()
        saveChatHistory()

        // Check network connectivity before making API call
        if (isNetworkAvailable()) {
            // Show typing indicator
            showTypingIndicator()
            
            callChatApi(message)
        } else {
            // Show network error message
            chatAdapter.addMessage(ChatMessage(
                message = "No internet connection. Please check your network settings and try again.",
                isFromUser = false
            ))
            scrollToBottom()
            saveChatHistory()
        }
    }
    
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun showTypingIndicator() {
        isLoading = true
        sendButton.isEnabled = false
        loadingIndicator.visibility = View.VISIBLE
        chatAdapter.addMessage(ChatMessage(
            message = "Typing...",
            isFromUser = false
        ))
        scrollToBottom()
    }
    
    private fun hideTypingIndicator() {
        isLoading = false
        sendButton.isEnabled = true
        loadingIndicator.visibility = View.GONE
        // Remove the last message if it's the typing indicator
        if (chatAdapter.getLastMessageText() == "Typing...") {
            chatAdapter.removeLastMessage()
        }
    }
    
    private fun callChatApi(userMessage: String) {
        try {
            val token = Sessions.getAccessToken(requireContext())
            if (token == null) {
                Toast.makeText(context, "Authentication token missing. Please login again.", Toast.LENGTH_LONG).show()
                return
            }

            Log.d(TAG, "Sending message to chatbot API")

            val apiService = retrofit2.getService(requireContext())
            val request = ChatRequest(userMessage)

            apiService.chatbot("Bearer $token", request).enqueue(object : Callback<ChatResponse> {
                override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                    Log.d(TAG, "Chat response code: ${response.code()}")

                    // Hide typing indicator
                    Handler(Looper.getMainLooper()).post {
                        hideTypingIndicator()
                        
                        if (response.isSuccessful && response.body() != null) {
                            val reply = response.body()!!.response
                            // Add AI response to chat
                            chatAdapter.addMessage(ChatMessage(
                                message = reply,
                                isFromUser = false
                            ))
                            // Scroll to the bottom to show the latest message
                            scrollToBottom()
                            saveChatHistory()
                        } else {
                            val errorMsg = "Error: ${response.code()} - ${response.message()}"
                            Log.e(TAG, errorMsg)
                            // Add error message to chat
                            chatAdapter.addMessage(ChatMessage(
                                message = "Sorry, I couldn't process your request. Please try again later.",
                                isFromUser = false
                            ))
                            scrollToBottom()
                            saveChatHistory()
                        }
                    }
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    Log.e(TAG, "Chat network failure", t)
                    // Hide typing indicator and show error
                    Handler(Looper.getMainLooper()).post {
                        hideTypingIndicator()
                        // Add network error message to chat
                        chatAdapter.addMessage(ChatMessage(
                            message = "Network error. Please check your connection and try again.",
                            isFromUser = false
                        ))
                        scrollToBottom()
                        saveChatHistory()
                    }
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "Exception in chat API call", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
