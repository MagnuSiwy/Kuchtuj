package com.example.kuchtuj_v2

import com.google.firebase.Timestamp

data class Recipe(
    var name: String = "",
    var component: String = "",
    var content: String = "",
    var timestamp: Timestamp? = null
)
