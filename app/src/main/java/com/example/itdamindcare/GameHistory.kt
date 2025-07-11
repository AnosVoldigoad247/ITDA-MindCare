package com.example.itdamindcare

import com.google.firebase.firestore.IgnoreExtraProperties

// Data class ini HARUS cocok dengan struktur data di Firestore.
// Nama properti (score, feedback, timestamp) harus sama persis dengan field di Firestore.
@IgnoreExtraProperties
data class GameHistory(
    val score: Long = 0, // Firestore biasanya menyimpan angka sebagai Long
    val feedback: String = "",
    val timestamp: Long = 0
)
