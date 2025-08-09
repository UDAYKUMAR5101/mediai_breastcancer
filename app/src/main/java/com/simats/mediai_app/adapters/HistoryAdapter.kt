package com.simats.mediai_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.mediai_app.R
import com.simats.mediai_app.responses.HistoryItem
import com.simats.mediai_app.responses.HistoryV2Item
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private var historyList: List<Any>,
    private val onItemClick: (Any) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val modeIcon: ImageView = itemView.findViewById(R.id.modeIcon)
        val modeText: TextView = itemView.findViewById(R.id.modeText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val riskLevelText: TextView = itemView.findViewById(R.id.riskLevelText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        val historyItem = when (item) {
            is HistoryV2Item -> null
            is HistoryItem -> item
            else -> null
        }
        val v2 = item as? HistoryV2Item
        
        // Set mode icon and text
        val mode = when {
            v2?.image_url != null -> "image"
            historyItem != null -> historyItem.mode
            else -> "symptoms"
        }
        when (mode.lowercase()) {
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

        // Format date - handle multiple date formats
        val displayDate = try {
            val dateFormats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd"
            )
            
            var parsedDate: java.util.Date? = null
            for (format in dateFormats) {
                try {
                    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
                    val createdAt = v2?.created_at ?: historyItem?.createdAt ?: ""
                    parsedDate = dateFormat.parse(createdAt)
                    if (parsedDate != null) break
                } catch (e: Exception) {
                    continue
                }
            }
            
            if (parsedDate != null) {
                val displayFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                displayFormat.format(parsedDate)
            } else {
                v2?.created_at ?: historyItem?.createdAt ?: ""
            }
        } catch (e: Exception) {
            v2?.created_at ?: historyItem?.createdAt ?: ""
        }
        
        holder.dateText.text = displayDate

        // Set risk level and percentage
        val risk = v2?.risk_level ?: historyItem?.riskLevel ?: ""
        holder.riskLevelText.text = risk

        // Set risk level background color
        val colorRes = when (risk.lowercase()) {
            "high" -> R.drawable.bg_risk_high
            "moderate" -> R.drawable.bg_risk_moderate
            else -> R.drawable.bg_risk_low
        }
        holder.riskLevelText.setBackgroundResource(colorRes)

        // Bind extra sections
        val symptomsLabel = holder.itemView.findViewById<TextView>(R.id.symptomsLabel)
        val symptomsText = holder.itemView.findViewById<TextView>(R.id.symptomsText)
        val predictionLabel = holder.itemView.findViewById<TextView>(R.id.predictionLabel)
        val predictionText = holder.itemView.findViewById<TextView>(R.id.predictionText)

        val symptoms = v2?.symptoms
        if (!symptoms.isNullOrBlank()) {
            symptomsLabel.visibility = View.VISIBLE
            symptomsText.visibility = View.VISIBLE
            symptomsText.text = symptoms
        } else {
            symptomsLabel.visibility = View.GONE
            symptomsText.visibility = View.GONE
        }

        val prediction = v2?.prediction_result ?: ""
        predictionLabel.visibility = View.VISIBLE
        predictionText.visibility = View.VISIBLE
        predictionText.text = prediction

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = historyList.size

    fun updateHistoryList(newList: List<Any>) {
        historyList = newList.sortedByDescending {
            val createdAt = when (it) {
                is HistoryV2Item -> it.created_at
                is HistoryItem -> it.createdAt
                else -> ""
            }
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                dateFormat.parse(createdAt)?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
        notifyDataSetChanged()
    }
}
