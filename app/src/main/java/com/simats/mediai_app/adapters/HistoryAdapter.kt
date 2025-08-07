package com.simats.mediai_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.mediai_app.R
import com.simats.mediai_app.responses.HistoryItem
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private var historyList: List<HistoryItem>,
    private val onItemClick: (HistoryItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modeIcon: ImageView = itemView.findViewById(R.id.modeIcon)
        val modeText: TextView = itemView.findViewById(R.id.modeText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val riskLevelText: TextView = itemView.findViewById(R.id.riskLevelText)
        val percentageText: TextView = itemView.findViewById(R.id.percentageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyItem = historyList[position]
        
        // Set mode icon and text
        when (historyItem.mode.lowercase()) {
            "image" -> {
                holder.modeIcon.setImageResource(R.drawable.cam_logo)
                holder.modeText.text = "Image Scan"
            }
            "symptoms" -> {
                holder.modeIcon.setImageResource(R.drawable.symtos_first)
                holder.modeText.text = "Symptom Check"
            }
            else -> {
                holder.modeIcon.setImageResource(R.drawable.symtos_first)
                holder.modeText.text = "Assessment"
            }
        }

        // Format date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val displayFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        
        try {
            val date = dateFormat.parse(historyItem.createdAt)
            holder.dateText.text = date?.let { displayFormat.format(it) } ?: historyItem.createdAt
        } catch (e: Exception) {
            holder.dateText.text = historyItem.createdAt
        }

        // Set risk level and percentage
        holder.riskLevelText.text = historyItem.riskLevel
        holder.percentageText.text = "${historyItem.predictionPercentage.toInt()}%"

        // Set risk level background color
        val colorRes = when (historyItem.riskLevel.lowercase()) {
            "high" -> R.drawable.bg_risk_high
            "moderate" -> R.drawable.bg_risk_moderate
            else -> R.drawable.bg_risk_low
        }
        holder.riskLevelText.setBackgroundResource(colorRes)

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(historyItem)
        }
    }

    override fun getItemCount(): Int = historyList.size

    fun updateHistoryList(newList: List<HistoryItem>) {
        historyList = newList.sortedByDescending { 
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                dateFormat.parse(it.createdAt)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
        notifyDataSetChanged()
    }
}
