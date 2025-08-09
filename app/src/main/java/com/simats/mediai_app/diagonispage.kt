package com.simats.mediai_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import com.simats.mediai_app.retrofit.ApiService
import com.simats.mediai_app.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class diagonispage : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var riskStatusText: TextView
    private lateinit var submittedDateText: TextView
    private lateinit var doctorNotesContentText: TextView
    private lateinit var downloadButton: Button
    private lateinit var shareButton: Button
    private var latestRiskLevel: String = ""
    private var latestCreatedAt: String = ""
    private var latestPrediction: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_diagonispage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Back arrow navigation to myprofilepage
        findViewById<View>(R.id.backButton).setOnClickListener {
            val intent = Intent(this, myprofilepage::class.java)
            startActivity(intent)
            finish()
        }

        apiService = RetrofitClient.getClient().create(ApiService::class.java)
        riskStatusText = findViewById(R.id.riskStatusText)
        submittedDateText = findViewById(R.id.submittedDateText)
        doctorNotesContentText = findViewById(R.id.doctorNotesContentText)
        downloadButton = findViewById(R.id.btn_download_pdf)
        shareButton = findViewById(R.id.btn_share_report)

        downloadButton.setOnClickListener { exportAndShareReport(share = false) }
        shareButton.setOnClickListener { exportAndShareReport(share = true) }

        loadLatestRisk()
    }

    private fun loadLatestRisk() {
        val token = Sessions.getAccessToken(this) ?: return
        apiService.getUserHistory("Bearer $token").enqueue(object : Callback<List<com.simats.mediai_app.responses.HistoryV2Item>> {
            override fun onResponse(
                call: Call<List<com.simats.mediai_app.responses.HistoryV2Item>>,
                response: Response<List<com.simats.mediai_app.responses.HistoryV2Item>>
            ) {
                val list = response.body().orEmpty()
                if (list.isNotEmpty()) {
                    val latest = list.maxByOrNull { it.created_at } ?: list.first()
                    latestRiskLevel = latest.risk_level
                    latestCreatedAt = latest.created_at
                    latestPrediction = latest.prediction_result
                    updateUi()
                }
            }

            override fun onFailure(
                call: Call<List<com.simats.mediai_app.responses.HistoryV2Item>>,
                t: Throwable
            ) {
                Toast.makeText(this@diagonispage, "Failed to fetch latest diagnosis", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUi() {
        riskStatusText.text = riskLabel(latestRiskLevel)
        val color = when (latestRiskLevel.lowercase(Locale.getDefault())) {
            "high" -> ContextCompat.getColor(this, R.color.risk_high)
            "moderate" -> ContextCompat.getColor(this, R.color.risk_moderate)
            else -> ContextCompat.getColor(this, R.color.risk_low)
        }
        riskStatusText.setTextColor(color)
        submittedDateText.text = "Submitted on ${formatDate(latestCreatedAt)}"
        doctorNotesContentText.text = latestPrediction.ifEmpty { "No additional notes provided." }
    }

    private fun riskLabel(level: String): String {
        val pretty = level.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return "$pretty Risk"
    }

    private fun formatDate(iso: String): String {
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        val parsed = patterns.firstNotNullOfOrNull { p ->
            try { java.text.SimpleDateFormat(p, Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }.parse(iso) } catch (_: Exception) { null }
        } ?: return iso
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(parsed)
    }

    private fun exportAndShareReport(share: Boolean) {
        try {
            val file = createTextReport()
            if (share) {
                shareFile(file)
            } else {
                Toast.makeText(this, "Report downloaded to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error exporting report: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createTextReport(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
        val fileName = "diagnosis_report_$timeStamp.txt"
        val dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: filesDir
        val file = File(dir, fileName)
        val content = buildString {
            appendLine("Diagnosis Report")
            appendLine("=================")
            appendLine("Risk Level: ${riskLabel(latestRiskLevel)}")
            appendLine("Submitted: ${formatDate(latestCreatedAt)}")
            if (latestPrediction.isNotEmpty()) {
                appendLine()
                appendLine("Notes:")
                appendLine(latestPrediction)
            }
        }
        FileOutputStream(file).use { it.write(content.toByteArray()) }
        return file
    }

    private fun shareFile(file: File) {
        val uri: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Diagnosis Report")
            putExtra(Intent.EXTRA_TEXT, "Please find my diagnosis report attached.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share Report"))
    }
}