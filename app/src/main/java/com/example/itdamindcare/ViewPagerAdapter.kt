package com.example.itdamindcare

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // Jumlah total halaman/fragment
    override fun getItemCount(): Int = 4

    // Membuat fragment untuk setiap posisi
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Beranda()
            1 -> Riwayat()
            2 -> Profile()
            3 -> Pengaturan()
            else -> Beranda() // Fragment default
        }
    }
}
