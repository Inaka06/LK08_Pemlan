package service;

import objects.Peminjaman;
import objects.Buku;
import objects.Siswa;
import java.io.IOException;

public class PeminjamanService {
    private final String filePath;
    private static final int MAKS_PINJAM = 2;

    private final SiswaService siswaService;
    private final BukuService  bukuService;

    public PeminjamanService(String dataDir, SiswaService siswaService, BukuService bukuService) {
        this.filePath     = dataDir + "/peminjaman.txt";
        this.siswaService = siswaService;
        this.bukuService  = bukuService;
        try {
            FileService.createIfNotExists(filePath);
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal akses file peminjaman.");
        }
    }

    // Mengambil semua data (Tetap sama)
    public Peminjaman[] getAll() throws IOException {
        String[] lines = FileService.readLines(filePath);
        Peminjaman[] list = new Peminjaman[lines.length];
        for (int i = 0; i < lines.length; i++) {
            list[i] = Peminjaman.fromFileString(lines[i]);
        }
        return list;
    }

    /**
     * Mendapatkan daftar buku yang belum dikembalikan
     * Menggunakan teknik temporary array + arraycopy
     */
    public Peminjaman[] getBelumDikembalikan() throws IOException {
        Peminjaman[] all = getAll();
        Peminjaman[] temp = new Peminjaman[all.length];
        int count = 0;

        for (int i = 0; i < all.length; i++) {
            // Cek status belum kembali
            if (all[i].getStatus() == Peminjaman.STATUS_BELUM_KEMBALI) {
                temp[count] = all[i];
                count++;
            }
        }

        // Perkecil array agar ukurannya pas dengan jumlah data yang ditemukan
        Peminjaman[] result = new Peminjaman[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }

    /**
     * Mendapatkan statistik transaksi
     * Index 0: Total Transaksi
     * Index 1: Sedang Dipinjam
     * Index 2: Sudah Kembali
     */
    public int[] getStatistik() throws IOException {
        Peminjaman[] all = getAll();
        int total = all.length;
        int dipinjam = 0;
        int kembali = 0;

        for (int i = 0; i < all.length; i++) {
            if (all[i].getStatus() == Peminjaman.STATUS_BELUM_KEMBALI) {
                dipinjam++;
            } else {
                kembali++;
            }
        }

        return new int[]{total, dipinjam, kembali};
    }

    /**
     * PROSES PINJAM (Dipersimpel)
     * Tanggal sekarang diinput sebagai String biasa dari Main atau manual
     */
    public boolean pinjam(String nis, String kodeBuku, String tglHariIni, String tglJatuhTempo) throws IOException {
        Siswa s = siswaService.findByNis(nis);
        Buku b = bukuService.findByKode(kodeBuku);

        if (s == null || b == null) return false;
        if (jumlahPinjamanAktif(nis) >= MAKS_PINJAM) return false;
        if (isBukuDipinjam(kodeBuku)) return false;

        // Generate kode TRX sederhana (Jumlah baris + 1)
        String kodeTrx = "TRX" + (FileService.readLines(filePath).length + 1);

        Peminjaman p = new Peminjaman(kodeTrx, nis, kodeBuku, tglHariIni, tglJatuhTempo, Peminjaman.STATUS_BELUM_KEMBALI);

        FileService.appendLine(filePath, p.toFileString());
        return true;
    }

    /**
     * PROSES KEMBALI (Dipersimpel)
     * Tidak perlu hitung denda di sini, cukup ubah status
     */
    public boolean kembalikan(String kodeTransaksi, String tglKembali) throws IOException {
        Peminjaman[] list = getAll();
        String[] newLines = new String[list.length];
        boolean ditemukan = false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].getKodeTransaksi().equalsIgnoreCase(kodeTransaksi)) {
                list[i].setStatus(Peminjaman.STATUS_SUDAH_KEMBALI);
                list[i].setTanggalKembali(tglKembali); // Catat tanggal kembali dari input
                ditemukan = true;
            }
            newLines[i] = list[i].toFileString();
        }

        if (ditemukan) FileService.writeLines(filePath, newLines);
        return ditemukan;
    }

    // --- Method Helper (Manual Loop Array) ---

    public int jumlahPinjamanAktif(String nis) throws IOException {
        int count = 0;
        Peminjaman[] all = getAll();
        for (int i = 0; i < all.length; i++) {
            if (all[i].getNis().equals(nis) && all[i].getStatus() == Peminjaman.STATUS_BELUM_KEMBALI) {
                count++;
            }
        }
        return count;
    }

    public boolean isBukuDipinjam(String kodeBuku) throws IOException {
        Peminjaman[] all = getAll();
        for (int i = 0; i < all.length; i++) {
            if (all[i].getKodeBuku().equalsIgnoreCase(kodeBuku) && all[i].getStatus() == Peminjaman.STATUS_BELUM_KEMBALI) {
                return true;
            }
        }
        return false;
    }
}