package com.simats.mediai_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.simats.mediai_app.R
import com.simats.mediai_app.models.ChatMessage
// No need for these imports as we're using the method from ChatMessage
// import java.text.SimpleDateFormat
// import java.util.Locale

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {
    private val messages = mutableListOf<ChatMessage>()
    
    fun getMessages(): List<ChatMessage> {
        return messages.toList()
    }
    // Using the getFormattedTime method from ChatMessage class

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun clear() {
        val size = messages.size
        messages.clear()
        notifyItemRangeRemoved(0, size)
    }
    
    fun getLastMessageText(): String? {
        return if (messages.isNotEmpty()) {
            messages.last().message
        } else {
            null
        }
    }
    
    fun removeLastMessage() {
        if (messages.isNotEmpty()) {
            messages.removeAt(messages.size - 1)
            notifyItemRemoved(messages.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isFromUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_message_user
        } else {
            R.layout.item_message_ai
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val timeText: TextView = itemView.findViewById(R.id.timeText)
        private val messageCard: CardView = itemView.findViewById(R.id.messageCard)

        fun bind(message: ChatMessage) {
            messageText.text = message.message
            timeText.text = message.getFormattedTime()
        }
    }

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }
}