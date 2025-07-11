package com.example.itdamindcare

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.itdamindcare.R

class RiwayatActivityContainer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mengatur layout activity ke activity_riwayat_container.xml
        setContentView(R.layout.activity_riwayat_container)

        // Cek jika activity baru pertama kali dibuat (bukan karena rotasi layar)
        // Ini mencegah fragment dibuat ulang setiap kali layar diputar.
        if (savedInstanceState == null) {
            // Buat instance dari Riwayat Fragment
            val riwayatFragment = Riwayat()

            // Gunakan FragmentManager untuk menempatkan fragment ke dalam container
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, riwayatFragment)
                .commit()
        }
    }
}
