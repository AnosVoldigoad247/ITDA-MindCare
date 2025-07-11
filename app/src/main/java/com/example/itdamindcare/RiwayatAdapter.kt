package com.example.itdamindcare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Adapter sekarang menerima List dari data class GameHistory
class RiwayatAdapter(private val riwayatList: List<GameHistory>) :
    RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder>() {

    class RiwayatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTgl: TextView = itemView.findViewById(R.id.tvTanggalLevelDicapai)
        val tvLevel: TextView = itemView.findViewById(R.id.tvLevel)
        val tvHasil: TextView = itemView.findViewById(R.id.tvHasilLevelDicapai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        // Inflate layout item yang benar
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_riwayat_adapter, parent, false)
        return RiwayatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        val riwayat = riwayatList[position]

        // Set data dari objek GameHistory ke TextViews
        holder.tvLevel.text = riwayat.score.toString()
        holder.tvHasil.text = riwayat.feedback

        // Format timestamp menjadi tanggal yang bisa dibaca
        try {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = Date(riwayat.timestamp)
            holder.tvTgl.text = sdf.format(date)
        } catch (e: Exception) {
            holder.tvTgl.text = "Tanggal tidak valid"
        }
    }

    override fun getItemCount(): Int = riwayatList.size
}
