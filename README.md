Struktur Arsitektur Aplikasi
aplikasi ini dibangun menggunakan bahasa pemrograman Java dengan pendekatan Service-Oriented Architecture (SOA) sederhana. Struktur kodenya memisahkan antara tampilan GUI, logika bisnis (Service), dan representasi data (Object/Model):
* MainGUI.java: Bertindak sebagai pusat kendali antarmuka pengguna yang mengatur JTabbedPane untuk berpindah antar fungsi.
* Package objects: Berisi kelas POJO (Plain Old Java Object) seperti Buku.java, Siswa.java, dan Peminjaman.java yang mendefinisikan atribut data.
* Package service: Berisi logika operasional seperti BukuService.java dan SiswaService.java yang memproses data sebelum disimpan.
* FileService.java: Komponen krusial yang menangani pembacaan dan penulisan data ke dalam file teks di folder data/.

Analisis Berdasarkan Tampilan Antarmuka
1. Manajemen Inventaris Buku
   Tab Kelola Buku.
  * Input Data: Form ini memungkinkan petugas memasukkan Kode Buku, Judul Buku, dan Jenis Buku.
  * Mekanisme Kode: Saat tombol Tambah ditekan, MainGUI memanggil metode di BukuService untuk memvalidasi input tersebut. Jika valid, FileService akan menambahkan baris baru ke dalam file buku.txt.
  * Tampilan Tabel: Tabel di bawah form mengambil data secara dinamis dari buku.txt. Contoh data yang terlihat seperti "hujan" dan "wanpis" menunjukkan bahwa aplikasi berhasil memetakan string dari file teks ke dalam baris tabel.
<img width="979" height="736" alt="image" src="https://github.com/user-attachments/assets/368b1499-b89d-40d5-8b80-31eaac86e9d4" />

2. Manajemen Data Siswa
   Tab Kelola Siswa.
  * Identitas Siswa: Fokus utama di sini adalah pengelolaan NIS sebagai unique identifier.
  * Logika Penyimpanan: Data yang diinput melalui JTextField (Nama dan Alamat) dikonversi menjadi objek Siswa. Objek ini kemudian diserialisasi ke dalam format teks oleh SiswaService untuk disimpan di siswa.txt.
<img width="977" height="731" alt="image" src="https://github.com/user-attachments/assets/ac2f1126-a159-4c66-88dc-e83ae6dec4f7" />

3. Transaksi dan Sirkulasi Perpustakaan
   Tab Transaksi Peminjaman.
  * Form Peminjaman:
    * Pengguna memasukkan NIS Siswa dan Kode Buku yang akan dipinjam.
    * Sistem secara otomatis menangani Tgl Pinjam dan Tgl Tempo. Logika perhitungan tanggal ini diatur di dalam PeminjamanService.java.
  * Form Pengembalian:
    * Petugas hanya perlu memasukkan Kode TRX dan tanggal kembali.
    * Proses ini akan memicu pembaruan status pada file peminjaman.txt.
    * Status Transaksi: Tabel menunjukkan kolom Status yang sangat penting. Status "Dipinjam" akan berubah menjadi "Sudah Kembali" setelah proses pengembalian selesai. Hal ini melibatkan logika pembaruan baris spesifik dalam file teks, yang dikelola oleh FileService.
<img width="976" height="732" alt="image" src="https://github.com/user-attachments/assets/815a15c7-aaea-443f-b449-42994fa7ebb8" />


Sistem Penyimpanan Data (Persistence)
penggunaan file teks sebagai database.
* Lokasi Data: Semua informasi disimpan dalam folder data/ yang berisi buku.txt, siswa.txt, dan peminjaman.txt.
* Keuntungan: Metode ini membuat aplikasi menjadi portabel, namun memerlukan ketelitian pada FileService agar data tidak korup saat proses tulis-baca bersamaan.
* Sinkronisasi: Setiap kali ada perubahan, aplikasi akan menulis ulang file terkait atau menambahkan baris baru, lalu memanggil fungsi loadData() untuk menyegarkan tampilan tabel pada GUI.
