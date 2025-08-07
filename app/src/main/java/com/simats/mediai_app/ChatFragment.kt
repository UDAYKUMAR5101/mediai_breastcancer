package com.simats.mediai_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.simats.mediai_app.responses.ChatRequest
import com.simats.mediai_app.responses.ChatResponse
import com.simats.mediai_app.retrofit.ChatService
import com.simats.mediai_app.retrofit.retrofit2
import com.simats.mediai_app.Sessions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: TextInputEditText
    private lateinit var sendButton: MaterialButton

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
        showWelcomeMessage()
    }

    private fun initializeViews() {
        chatRecyclerView = requireView().findViewById(R.id.chatRecyclerView)
        messageInput = requireView().findViewById(R.id.messageInput)
        sendButton = requireView().findViewById(R.id.sendButton)
    }

    private fun setupClickListeners() {
        sendButton.setOnClickListener {
            sendMessage()
        }

        messageInput.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun setupRecyclerView() {
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        // Adapter integration can go here later
    }

    private fun showWelcomeMessage() {
        Toast.makeText(context, "Welcome to AI Health Assistant! Ask me anything about health.", Toast.LENGTH_LONG).show()
    }

    private fun sendMessage() {
        val message = messageInput.text.toString().trim()
        if (message.isEmpty()) {
            Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "You: $message", Toast.LENGTH_SHORT).show()
        messageInput.text?.clear()

        callChatApi(message)
    }

    private fun callChatApi(userMessage: String) {
        try {
            val token = Sessions.getAccessToken(requireContext())
            if (token == null) {
                Toast.makeText(context, "Authentication token missing. Please login again.", Toast.LENGTH_LONG).show()
                return
            }

            Log.d(TAG, "Sending message to chatbot API")

            val chatService = retrofit2.getService(requireContext()).create(ChatService::class.java)
            val request = ChatRequest(userMessage)

            chatService.chatbot("Bearer $token", request).enqueue(object : Callback<ChatResponse> {
                override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                    Log.d(TAG, "Chat response code: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        val reply = response.body()!!.response
                        Handler(Looper.getMainLooper()).postDelayed({
                            Toast.makeText(context, "AI: $reply", Toast.LENGTH_LONG).show()
                        }, 1000)
                    } else {
                        val errorMsg = "Error: ${response.code()} - ${response.message()}"
                        Log.e(TAG, errorMsg)
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    Log.e(TAG, "Chat network failure", t)
                    Toast.makeText(context, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "Exception in chat API call", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
