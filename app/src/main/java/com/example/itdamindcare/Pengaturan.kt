package com.example.itdamindcare

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.itdamindcare.databinding.FragmentPengaturanBinding // Import View Binding class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Pengaturan : Fragment() {

    private var _binding: FragmentPengaturanBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPengaturanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.bantuan.setOnClickListener {
            Toast.makeText(context, "Pusat Bantuan diklik (belum diimplementasikan)", Toast.LENGTH_SHORT).show()
            // Contoh: buka activity baru, tampilkan dialog, atau navigasi ke fragment lain
            // val intent = Intent(activity, PusatBantuanActivity::class.java)
            // startActivity(intent)
        }

        binding.tentang.setOnClickListener {
            Toast.makeText(context, "Tentang Aplikasi", Toast.LENGTH_SHORT).show()
            showTentangAplikasiDialog()
        }

        binding.uAkun.setOnClickListener {
            // Meminta Activity induk untuk memulai Activity baru
            val intent = Intent(requireActivity(), Update::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showTentangAplikasiDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Tentang Aplikasi")
            .setMessage("ITDA MindCare\nVersi Aplikasi: Alpha 1.0 \n\nDikembangkan oleh: \n\nDeveloper:\n[Adhitya Maulana Zada]\n\nIde & Konsep:\n[Rochfis Subiantoro]")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performLogout() {
        auth.signOut()
        activity?.let {
            val intent = Intent(it, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            it.finishAffinity()
        }
        Toast.makeText(context, "Anda telah logout.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}