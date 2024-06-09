package com.example.kuchtuj_v2

import android.net.Uri
import com.google.firebase.Timestamp

data class Recipe(
    var image: String? = null,
    var name: String = "",
    var component: String = "",
    var content: String = "",
    var timestamp: Timestamp? = null
)
