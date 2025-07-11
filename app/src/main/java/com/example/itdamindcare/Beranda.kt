package com.example.itdamindcare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import android.os.Handler
import android.os.Looper
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.itdamindcare.databinding.FragmentBerandaBinding

// Tambahan Firebase
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import java.net.URLEncoder


data class Quote(val text: String = "")

class Beranda : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!
    private val dailyQuotesList = mutableListOf<Quote>()
    private val realtimeDatabase = FirebaseDatabase.getInstance()
    private var quotesListener: ValueEventListener? = null
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val AUTO_SCROLL_DELAY_MS = 6000L
    private var currentPosition = 0
    private lateinit var slowVerticalLayoutManager: SlowScrollLinearLayoutManager
    private lateinit var autoScrollRunnable: Runnable
    private lateinit var quotesAdapter: QuotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        slowVerticalLayoutManager = SlowScrollLinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        binding.quotes?.layoutManager = slowVerticalLayoutManager

        quotesAdapter = QuotesAdapter(dailyQuotesList)
        binding.quotes?.adapter = quotesAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.quotes)

        quotes()
        namaPengguna()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnMulai?.setOnClickListener {
            val intent = Intent(requireActivity(), Game::class.java)
            startActivity(intent)
        }

        // Listener untuk Floating Action Button (FAB)
        binding.fabWhatsapp?.setOnClickListener {
            // Ganti nomor telepon ini dengan nomor tujuan Anda
            val phoneNumberWithCountryCode = "+6285190000924"
            val message = "Halo, saya ingin berkonsultasi mengenai layanan ITDA MindCare."

            try {
                // Membuat URL untuk API WhatsApp
                val url = "https://api.whatsapp.com/send?phone=$phoneNumberWithCountryCode&text=${URLEncoder.encode(message, "UTF-8")}"

                // Membuat Intent untuk membuka URL
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)

                startActivity(intent)
            } catch (e: Exception) {
                // Menampilkan pesan jika terjadi error (misal: WhatsApp tidak terpasang)
                Toast.makeText(requireContext(), "WhatsApp tidak terpasang di perangkat Anda.", Toast.LENGTH_LONG).show()
                Log.e("BerandaFragment", "Gagal membuka WhatsApp", e)
            }
        }

        // Listener untuk CardView "portal"
        binding.portal?.setOnClickListener {
            // Ganti dengan link yang Anda inginkan
            val url = "https://www.itda.ac.id/"
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Tidak dapat membuka link.", Toast.LENGTH_SHORT).show()
                Log.e("BerandaFragment", "Gagal membuka link browser", e)
            }
        }
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
                            it.tvNama?.text = nama
                        } else {
                            it.tvNama?.text = "Pengguna"
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
                it.tvNama?.text = "Belum login"
            }
        }
    }

    private fun quotes() {
        val quotesRef = realtimeDatabase.getReference("quotes")

        quotesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dailyQuotesList.clear()
                for (quoteSnapshot in snapshot.children) {
                    val text = quoteSnapshot.child("text").getValue(String::class.java)
                    if (!text.isNullOrEmpty()) {
                        dailyQuotesList.add(Quote(text))
                    }
                }

                dailyQuotesList.shuffle()
                quotesAdapter.notifyDataSetChanged()

                if (dailyQuotesList.isNotEmpty()) {
                    setupAutoScroll()
                    autoScrollHandler.removeCallbacks(autoScrollRunnable)
                    autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY_MS)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Gagal mengambil data quotes: ${error.message}", error.toException())
                Toast.makeText(requireContext(), "Gagal memuat kutipan. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }

        quotesRef.addValueEventListener(quotesListener!!)
    }

    private fun setupAutoScroll() {
        autoScrollRunnable = object : Runnable {
            override fun run() {
                if (quotesAdapter.itemCount == 0 || !isAdded || _binding == null) {
                    autoScrollHandler.removeCallbacks(this)
                    return
                }
                val currentBinding = _binding!!

                val snapView = currentBinding.quotes?.let { rv ->
                    rv.layoutManager?.let { lm -> PagerSnapHelper().findSnapView(lm) }
                }
                currentPosition = snapView?.let { slowVerticalLayoutManager.getPosition(it) } ?: currentPosition
                currentPosition = (currentPosition + 1) % quotesAdapter.itemCount
                currentBinding.quotes?.smoothScrollToPosition(currentPosition)
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY_MS)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (dailyQuotesList.isNotEmpty() && ::autoScrollRunnable.isInitialized) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable)
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY_MS)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::autoScrollRunnable.isInitialized) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::autoScrollRunnable.isInitialized) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable)
        }
        quotesListener?.let {
            realtimeDatabase.getReference("quotes").removeEventListener(it)
        }
        _binding = null
    }
}

class SlowScrollLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    private val MILLISECONDS_PER_INCH = 300f

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val linearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}
