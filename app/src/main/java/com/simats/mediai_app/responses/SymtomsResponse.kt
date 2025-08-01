package com.simats.mediai_app.responses

data class SymtomsRequest(
    val age: Int,
    val menopausal_status: String,
    val family_history: String,
    val bmi: Float,
    val menarche_age: Int,
    val breastfeeding_history: String,
    val alcohol_consumption: String,
    val hormonal_treatment_history: String,
    val physical_activity: String,
    val breast_pain: String,
    val breast_cancer_history: String
)

data class SymtomsResponse(
    val id: Int?,
    val age: Int?,
    val menopausal_status: String?,
    val family_history: String?,
    val bmi: Float?,
    val menarche_age: Int?,
    val breastfeeding_history: String?,
    val alcohol_consumption: String?,
    val hormonal_treatment_history: String?,
    val physical_activity: String?,
    val breast_pain: String?,
    val breast_cancer_history: String?
)