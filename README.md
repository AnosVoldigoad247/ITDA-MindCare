# ITDA MindCare - Aplikasi Tes Fokus dan Memori

ITDA MindCare adalah aplikasi Android yang dirancang untuk membantu pengguna melatih dan mengukur fokus serta memori jangka pendek melalui permainan interaktif Simon Says. Aplikasi ini juga menyediakan fitur untuk melihat riwayat permainan dan mengelola profil pengguna.

## Daftar Isi
- [Fitur Utama](#fitur-utama)
- [Screenshot (Placeholder)](#screenshot-placeholder)
- [Teknologi yang Digunakan](#teknologi-yang-digunakan)
- [Struktur Proyek](#struktur-proyek)
- [Persiapan dan Instalasi](#persiapan-dan-instalasi)
  - [Prasyarat](#prasyarat)
  - [Konfigurasi Firebase](#konfigurasi-firebase)
  - [Build Aplikasi](#build-aplikasi)
- [Alur Kerja Aplikasi](#alur-kerja-aplikasi)
- [Kontribusi](#kontribusi)
- [Lisensi](#lisensi)

## Fitur Utama
*   **Autentikasi Pengguna:**
    *   Pendaftaran akun baru dengan Email dan Password.
    *   Login untuk pengguna yang sudah terdaftar.
    *   Logout dari akun.
*   **Manajemen Profil:**
    *   Pengguna dapat mendaftarkan informasi profil dasar (Nama, NIM, Program Studi).
    *   Kemampuan untuk memperbarui informasi profil.
*   **Game Simon Says:**
    *   Permainan interaktif untuk menguji dan melatih fokus serta memori jangka pendek.
    *   Level kesulitan yang meningkat seiring kemajuan pemain.
    *   Visual feedback untuk setiap input tombol.
*   **Riwayat Permainan:**
    *   Skor terakhir yang dicapai dalam game Simon Says akan disimpan secara lokal.
    *   Menampilkan daftar riwayat permainan beserta tanggal dan interpretasi hasil skor.
*   **Pengaturan Aplikasi:**
    *   Akses ke halaman update profil.
    *   Informasi "Tentang Aplikasi".
    *   Opsi untuk "Pusat Bantuan" (placeholder).
*   **Antarmuka Pengguna Modern:**
    *   Menggunakan Material Design 3.
    *   Mendukung warna dinamis (Dynamic Color) pada perangkat Android 12+.

## Screenshot (Placeholder)
<p align="center">
  <img src="https://github.com/user-attachments/assets/690e955b-5bde-4256-9de7-2a75290ccfe8" width="100">
  <img src="https://github.com/user-attachments/assets/7e2c0118-7a9f-4540-8017-4d17a1faf5dc" width="100">
  <img src="https://github.com/user-attachments/assets/87fc3a95-95df-4a73-ac45-4c5ba4dde4b7" width="100">
  <img src="https://github.com/user-attachments/assets/35406151-0502-42a2-b65f-bde97f0003b2" width="100">
</p>
<p align="center">
  <em>Contoh: Halaman Login, Halaman Beranda, Gameplay.</em>
</p>

*   *Contoh: Halaman Login, Halaman Beranda, Gameplay.*

## Teknologi yang Digunakan
*   **Bahasa Pemrograman:** Kotlin
*   **Arsitektur:** (Sebutkan jika Anda menerapkan pola arsitektur tertentu, misal MVVM, MVI)
*   **Android Jetpack:**
    *   **View Binding:** Untuk mengakses view di layout XML dengan aman.
    *   **Navigation Component:** Untuk mengelola alur navigasi antar fragment.
    *   **ViewModel:** Untuk menyimpan dan mengelola data terkait UI secara lifecycle-aware.
    *   **LiveData/StateFlow:** Untuk mengamati perubahan data secara reaktif.
    *   **Lifecycle:** Untuk mengelola siklus hidup komponen aplikasi.
*   **Firebase:**
    *   **Firebase Authentication:** Untuk autentikasi pengguna.
    *   **Firebase Firestore:** Untuk menyimpan data profil pengguna.
    *   *(Sebutkan Firebase Realtime Database jika masih digunakan)*
*   **Coroutines:** Untuk manajemen tugas asynchronous.
*   **Material Design Components:** Untuk komponen UI yang modern dan konsisten.
*   **SharedPreferences:** Untuk menyimpan riwayat game secara lokal.
*   **Gson:** Untuk serialisasi/deserialisasi objek riwayat game saat disimpan di SharedPreferences.
*   **Gradle:** Untuk build automation.

## Struktur Proyek
Proyek ini mengikuti struktur standar aplikasi Android:

## Persiapan dan Instalasi

### Prasyarat
*   Android Studio (versi terbaru direkomendasikan, misal Giraffe atau Iguana)
*   JDK 17 atau yang lebih baru
*   Emulator Android atau perangkat fisik dengan Android API Level 26 (Oreo) atau lebih tinggi.

### Konfigurasi Firebase
Aplikasi ini menggunakan Firebase untuk autentikasi dan penyimpanan data.
1.  **Buat Proyek Firebase:** Kunjungi [Firebase Console](https://console.firebase.google.com/) dan buat proyek baru.
2.  **Tambahkan Aplikasi Android ke Proyek Firebase:**
    *   Ikuti petunjuk untuk menambahkan aplikasi Android ke proyek Firebase Anda.
    *   Masukkan nama paket aplikasi: `com.example.itdamindcare` (sesuaikan jika berbeda).
    *   Unduh file `google-services.json` dan letakkan di direktori `app/` proyek Android Studio Anda.
3.  **Aktifkan Layanan Firebase:**
    *   Di Firebase Console, aktifkan **Authentication** dan pilih metode "Email/Password".
    *   Aktifkan **Firestore Database**. Buat database dalam mode produksi atau tes (sesuai kebutuhan awal pengembangan) dan atur aturan keamanannya.
        *Contoh aturan keamanan dasar untuk Firestore (untuk pengembangan awal, perketat untuk produksi):*

### Build Aplikasi
1.  **Clone Repository:**
2.  **Buka Proyek di Android Studio.**
3.  **Sinkronkan Proyek dengan Gradle:** Android Studio biasanya akan melakukan ini secara otomatis. Jika tidak, klik "Sync Project with Gradle Files".
4.  **Jalankan Aplikasi:** Pilih target emulator atau perangkat fisik, lalu klik tombol "Run" (Shift+F10).

## Alur Kerja Aplikasi
1.  **Splash Screen/Halaman Awal:** Aplikasi memeriksa apakah pengguna sudah login.
2.  **Login/Daftar:**
    *   Jika belum login, pengguna diarahkan ke halaman Login.
    *   Pengguna dapat memilih untuk Login dengan akun yang ada atau Daftar akun baru.
    *   Saat mendaftar, informasi dasar profil juga akan disimpan.
3.  **Beranda:**
    *   Setelah berhasil login, pengguna masuk ke halaman Beranda.
    *   Dari Beranda, pengguna dapat mengakses:
        *   **Main Game:** Memulai permainan.
        *   **Riwayat:** Melihat skor permainan sebelumnya.
        *   **Pengaturan:** Mengakses pengaturan aplikasi.
4.  **Game:**
    *   Aplikasi akan menampilkan urutan warna yang harus diingat dan diulangi oleh pengguna.
    *   Level akan meningkat jika pengguna berhasil.
    *   Jika salah, permainan berakhir, dan skor akan dicatat.
5.  **Riwayat Permainan:** Menampilkan daftar skor, tanggal, dan interpretasi dari game yang telah dimainkan.
6.  **Pengaturan:**
    *   **Perbarui Akun:** Memungkinkan pengguna mengubah data profil mereka
