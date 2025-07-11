package com.example.itdamindcare

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.itdamindcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlin.random.Random

class Game : AppCompatActivity(), View.OnClickListener {

    private lateinit var tvStatus: TextView
    private lateinit var buttons: Map<Int, Button>
    private lateinit var btnBackToHome: Button
    private lateinit var btnGoToHistory: Button
    private lateinit var btnStartGame: Button // Tombol Mulai yang baru

    private val gameSequence = mutableListOf<Int>()
    private val playerSequence = mutableListOf<Int>()
    private var level = 0
    private var isPlayerTurn = false
    private val scope = MainScope()

    // Get Firestore and Auth instances
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize Firebase Auth
        auth = Firebase.auth
        signInAnonymously() // Sign in user to get a unique ID

        tvStatus = findViewById(R.id.tvStatus)
        buttons = mapOf(
            1 to findViewById(R.id.btnGreen),
            2 to findViewById(R.id.btnRed),
            3 to findViewById(R.id.btnYellow),
            4 to findViewById(R.id.btnBlue)
        )
        // Inisialisasi tombol navigasi dan tombol mulai
        btnBackToHome = findViewById(R.id.btnBackToHome)
        btnGoToHistory = findViewById(R.id.btnGoToHistory)
        btnStartGame = findViewById(R.id.btnStartGame)


        // Set OnClickListener untuk semua tombol
        buttons.values.forEach { it.setOnClickListener(this) }
        btnBackToHome.setOnClickListener(this)
        btnGoToHistory.setOnClickListener(this)
        btnStartGame.setOnClickListener(this)

        // Set teks awal
        tvStatus.text = "Tekan Mulai untuk Bermain"

        // Tampilkan dialog tutorial saat pertama kali masuk
        showTutorialDialog()
    }

    private fun showTutorialDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cara Bermain")
            .setMessage("1. Perhatikan dan hafalkan urutan warna yang menyala.\n\n2. Ulangi urutan tersebut dengan menekan tombol warna yang benar.\n\n3. Permainan akan berlanjut ke level berikutnya jika urutan Anda benar.")
            .setPositiveButton("Mengerti") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false) // Mencegah dialog ditutup dengan tombol kembali
            .show()
    }

    private fun signInAnonymously() {
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "signInAnonymously:success")
                    } else {
                        Log.w("Firebase", "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // Logika untuk tombol navigasi
            R.id.btnBackToHome -> {
                finish()
            }
            R.id.btnGoToHistory -> {
                // Pastikan Anda memiliki Activity bernama RiwayatContainerActivity
                val intent = Intent(this, RiwayatActivityContainer::class.java)
                startActivity(intent)
            }
            // Logika untuk tombol Mulai
            R.id.btnStartGame -> {
                startGame()
            }
            // Logika untuk tombol game
            else -> {
                // Hanya proses klik tombol warna jika giliran pemain
                if (isPlayerTurn) {
                    val buttonId = buttons.entries.find { it.value == v }?.key ?: return
                    playerSequence.add(buttonId)
                    highlightButton(buttonId, 150)
                    checkPlayerInput()
                }
            }
        }
    }

    private fun startGame() {
        btnStartGame.visibility = View.GONE // Sembunyikan tombol Mulai
        level = 1
        gameSequence.clear()
        playerSequence.clear()
        nextRound()
    }

    private fun nextRound() {
        isPlayerTurn = false
        playerSequence.clear()
        tvStatus.text = "Level: $level"
        gameSequence.add(Random.nextInt(1, 5))
        scope.launch {
            // Nonaktifkan tombol selama urutan ditampilkan untuk mencegah input yang tidak disengaja
            buttons.values.forEach { it.isClickable = false }
            delay(1000)
            for (buttonId in gameSequence) {
                // Panggil fungsi yang memastikan kedipan selesai
                highlightButtonAndWait(buttonId, 400)
                // PERBAIKAN: Tambahkan jeda singkat setelah lampu mati agar kedipan terpisah
                delay(200)
            }
            isPlayerTurn = true
            tvStatus.text = "Ulangi Urutannya"
            // Aktifkan kembali tombol untuk giliran pemain
            buttons.values.forEach { it.isClickable = true }
        }
    }

    private fun checkPlayerInput() {
        val index = playerSequence.size - 1
        if (playerSequence[index] != gameSequence[index]) {
            gameOver()
            return
        }
        if (playerSequence.size == gameSequence.size) {
            level++
            // Nonaktifkan tombol sementara sebelum ronde berikutnya
            buttons.values.forEach { it.isClickable = false }
            Handler(Looper.getMainLooper()).postDelayed({ nextRound() }, 1000)
        }
    }

    private fun gameOver() {
        isPlayerTurn = false
        val finalScore = level - 1
        val feedbackMessage = when {
            finalScore in 1..4 -> "Mungkin Anda sedang banyak pikiran atau lelah."
            finalScore in 5..8 -> "Ingatan dan fokus yang baik! Ini menunjukkan kamu masih bisa fokus."
            finalScore >= 9 -> "Luar biasa! Fokus dan memori jangka pendek Anda sangat tajam saat ini."
            else -> "Coba lagi untuk melihat hasilnya!"
        }
        tvStatus.text = "Salah! Skor: $finalScore\n$feedbackMessage"
        saveScoreToFirebase(finalScore, feedbackMessage)
        level = 0
        btnStartGame.visibility = View.VISIBLE // Tampilkan kembali tombol Mulai
    }

    private fun saveScoreToFirebase(score: Int, feedback: String) {
        val user = auth.currentUser
        if (user == null) {
            Log.w("Firebase", "User not signed in, cannot save score.")
            return
        }

        val scoreData = hashMapOf(
            "score" to score,
            "feedback" to feedback,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users").document(user.uid)
            .collection("MindCareScores")
            .add(scoreData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firebase", "Score saved for user ${user.uid} with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error adding document for user ${user.uid}", e)
            }
    }

    // Fungsi highlight biasa untuk feedback klik pemain (tidak perlu menunggu)
    private fun highlightButton(buttonId: Int, duration: Long) {
        scope.launch {
            val button = buttons[buttonId]
            button?.alpha = 0.5f
            delay(duration)
            button?.alpha = 1.0f
        }
    }

    // Fungsi suspend baru untuk memastikan kedipan selesai sebelum lanjut
    private suspend fun highlightButtonAndWait(buttonId: Int, duration: Long) {
        val button = buttons[buttonId]
        button?.alpha = 0.5f // Lampu nyala
        delay(duration)
        button?.alpha = 1.0f // Lampu mati
    }


    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
