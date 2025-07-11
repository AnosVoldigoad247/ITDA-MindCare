package com.example.itdamindcare

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.itdamindcare.databinding.FragmentBerandaBinding
import com.example.itdamindcare.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val realtimeDatabase = FirebaseDatabase.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        namaPengguna()
        prodiPengguna()
        nimPengguna()
        emailPengguna()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun namaPengguna() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = realtimeDatabase.getReference("users").child(uid).child("nama")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    _binding?.let {
                        val nama = snapshot.getValue(String::class.java)
                        if (!nama.isNullOrEmpty()) {
                            it.tNama?.text = nama
                        } else {
                            it.tNama?.text = "Pengguna"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Gagal memuat nama: ${error.message}", error.toException())
                    if (isAdded && context != null) {
                        Toast.makeText(requireContext(), "Gagal memuat nama pengguna", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            _binding?.let {
                it.tNama?.text = "Belum login"
            }
        }
    }

    private fun prodiPengguna() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = realtimeDatabase.getReference("users").child(uid).child("prodi")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    _binding?.let {
                        val prodi = snapshot.getValue(String::class.java)
                        if (!prodi.isNullOrEmpty()) {
                            it.tProdi?.text = prodi
                        } else {
                            it.tProdi?.text = "Pengguna"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Gagal memuat prodi: ${error.message}", error.toException())
                    if (isAdded && context != null) {
                        Toast.makeText(requireContext(), "Gagal memuat prodi pengguna", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            _binding?.let {
                it.tProdi?.text = "Belum login"
            }
        }
    }

    private fun nimPengguna() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = realtimeDatabase.getReference("users").child(uid).child("nim")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    _binding?.let {
                        val nim = snapshot.getValue(String::class.java)
                        if (!nim.isNullOrEmpty()) {
                            it.tNim?.text = nim
                        } else {
                            it.tNim?.text = "Pengguna"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Gagal memuat NIM: ${error.message}", error.toException())
                    if (isAdded && context != null) {
                        Toast.makeText(requireContext(), "Gagal memuat NIM pengguna", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            _binding?.let {
                it.tProdi?.text = "Belum login"
            }
        }
    }

    private fun emailPengguna() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = realtimeDatabase.getReference("users").child(uid).child("email")

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    _binding?.let {
                        val email = snapshot.getValue(String::class.java)
                        if (!email.isNullOrEmpty()) {
                            it.tEmail?.text = email
                        } else {
                            it.tEmail?.text = "Pengguna"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Gagal memuat Email: ${error.message}", error.toException())
                    if (isAdded && context != null) {
                        Toast.makeText(requireContext(), "Gagal memuat Email pengguna", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            _binding?.let {
                it.tProdi?.text = "Belum login"
            }
        }
    }

}