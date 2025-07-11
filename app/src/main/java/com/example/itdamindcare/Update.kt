package com.example.itdamindcare

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.itdamindcare.databinding.ActivityUpdateBinding
import com.google.firebase.auth.FirebaseAuth
// Import untuk Realtime Database
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Update : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        binding.cardView.visibility = View.GONE
        loadUserData()
        binding.btnSimpanPerubahan.setOnClickListener {
            updateProfileData()
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            binding.etEmailPengguna.setText(user.email)
            database.child(user.uid).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val nama = dataSnapshot.child("nama").getValue(String::class.java)
                    val nim = dataSnapshot.child("nim").getValue(String::class.java)
                    val prodi = dataSnapshot.child("prodi").getValue(String::class.java)

                    binding.etNamaPengguna.setText(nama)
                    binding.etNimPengguna.setText(nim)
                    binding.etProdiPengguna.setText(prodi)
                } else {
                    Toast.makeText(this, "Data pengguna tidak ditemukan di database.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfileData() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Sesi berakhir, silakan login kembali.", Toast.LENGTH_SHORT).show()
            return
        }

        val newUsername = binding.etNamaPengguna.text.toString().trim()
        val newNim = binding.etNimPengguna.text.toString().trim()
        val newProdi = binding.etProdiPengguna.text.toString().trim()
        val newEmail = binding.etEmailPengguna.text.toString().trim()

        if (newUsername.isEmpty() || newNim.isEmpty() || newProdi.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBarUpdate.visibility = View.VISIBLE
        binding.btnSimpanPerubahan.isEnabled = false

        user.updateEmail(newEmail).addOnCompleteListener { emailTask ->
            if (emailTask.isSuccessful) {
                val userUpdates = mapOf<String, Any>(
                    "nama" to newUsername,
                    "nim" to newNim,
                    "prodi" to newProdi
                )

                database.child(user.uid).updateChildren(userUpdates)
                    .addOnSuccessListener {
                        binding.progressBarUpdate.visibility = View.GONE
                        binding.btnSimpanPerubahan.isEnabled = true
                        Toast.makeText(this, "Profil berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        binding.progressBarUpdate.visibility = View.GONE
                        binding.btnSimpanPerubahan.isEnabled = true
                        Toast.makeText(this, "Gagal memperbarui data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                binding.progressBarUpdate.visibility = View.GONE
                binding.btnSimpanPerubahan.isEnabled = true
                Toast.makeText(this, "Gagal memperbarui email: ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
