package com.elizabeth.s36639095.medtrack.data.network

data class Drug(
    var name: String?,

    val purpose: List<String>,

    val warnings: List<String>,

    val when_using: List<String>,

    val dosage_and_administration: List<String>,

    val do_not_use: List<String>,

    val stop_use: List<String>,

    val indications_and_usage: List<String>,
)

data class DrugResponse(
    val results: List<Drug>
)