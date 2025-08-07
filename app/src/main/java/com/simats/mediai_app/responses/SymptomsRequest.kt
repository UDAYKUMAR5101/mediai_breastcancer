package com.simats.mediai_app.responses

data class SymptomsRequest(
    val age: Int,
    val menopausal_status: String,
    val family_history: String,
    val bmi: Float?,
    val menarche_age: Int?,
    val breastfeeding_history: String,
    val alcohol_consumption: String,
    val hormonal_treatment_history: String,
    val physical_activity: String,
    val breast_pain: Boolean,
    val breast_cancer: String
)
