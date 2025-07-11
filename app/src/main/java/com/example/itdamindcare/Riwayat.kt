package com.example.itdamindcare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.itdamindcare.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Riwayat : Fragment() {

    private lateinit var rvRiwayat: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var riwayatAdapter: RiwayatAdapter
    private val historyList = mutableListOf<GameHistory>()

    // Get Firestore and Auth instances
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Inisialisasi semua View dari layout fragment
        rvRiwayat = view.findViewById(R.id.rvRiwayat)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        progressBar = view.findViewById(R.id.progressBar)

        setupRecyclerView()
        fetchGameHistory()
    }

    private fun setupRecyclerView() {
        riwayatAdapter = RiwayatAdapter(historyList)
        rvRiwayat.layoutManager = LinearLayoutManager(context)
        rvRiwayat.adapter = riwayatAdapter
    }

    private fun fetchGameHistory() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        rvRiwayat.visibility = View.GONE

        val user = auth.currentUser
        if (user == null) {
            // Jika user tidak login, tampilkan pesan kosong
            progressBar.visibility = View.GONE
            tvEmpty.text = "Anda belum login.\nMainkan game untuk melihat riwayat."
            tvEmpty.visibility = View.VISIBLE
            Log.w("RiwayatFragment", "User is not signed in.")
            return
        }

        // PERUBAHAN KUNCI: Ambil data dari sub-koleksi milik pengguna yang sedang login
        db.collection("users").document(user.uid)
            .collection("MindCareScores")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                historyList.clear()
                if (result.isEmpty) {
                    tvEmpty.text = "Belum ada riwayat permainan."
                    tvEmpty.visibility = View.VISIBLE
                } else {
                    // Konversi setiap dokumen ke objek GameHistory
                    for (document in result) {
                        val historyItem = document.toObject(GameHistory::class.java)
                        historyList.add(historyItem)
                    }
                    riwayatAdapter.notifyDataSetChanged()
                    rvRiwayat.visibility = View.VISIBLE
                }
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                tvEmpty.text = "Gagal memuat riwayat."
                tvEmpty.visibility = View.VISIBLE
                Log.w("RiwayatFragment", "Error getting documents for user ${user.uid}", exception)
            }
    }
}
