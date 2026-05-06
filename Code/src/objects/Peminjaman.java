package objects;

/**
 * Status: 0 = Belum dikembalikan, 1 = Sudah dikembalikan
 */

public class Peminjaman {
    public static final int STATUS_BELUM_KEMBALI = 0;
    public static final int STATUS_SUDAH_KEMBALI = 1;
    public static final int BATAS_HARI_PINJAM    = 7;

    private String kodeTransaksi;
    private String nis;
    private String kodeBuku;
    private String tanggalPinjam;
    private String tanggalKembali;
    private int status;

    public Peminjaman(String kodeTransaksi, String nis, String kodeBuku,
                      String tanggalPinjam, String tanggalKembali, int status) {
        this.kodeTransaksi  = kodeTransaksi;
        this.nis            = nis;
        this.kodeBuku       = kodeBuku;
        this.tanggalPinjam  = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.status         = status;
    }

    // Getter dan Setter
    public String getKodeTransaksi()  { return kodeTransaksi; }
    public String getNis()            { return nis; }
    public String getKodeBuku()       { return kodeBuku; }
    public String getTanggalPinjam()  { return tanggalPinjam; }
    public String getTanggalKembali() { return tanggalKembali; }
    public int    getStatus()         { return status; }

    public void setStatus(int status)               { this.status = status; }
    public void setTanggalKembali(String tgl)       { this.tanggalKembali = tgl; }

    /**
     * Konversi objek ke format satu baris untuk disimpan di file teks
     */
    public String toFileString() {
        return kodeTransaksi + "|" + nis + "|" + kodeBuku + "|"
                + tanggalPinjam + "|" + tanggalKembali + "|" + status;
    }

    /**
     * Membuat objek Peminjaman dari baris teks file
     */
    public static Peminjaman fromFileString(String line) {
        // split dengan limit -1 agar field kosong tetap terbaca
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) return null;

        try {
            int st = Integer.parseInt(parts[5].trim());
            return new Peminjaman(
                    parts[0].trim(), // kodeTransaksi
                    parts[1].trim(), // nis
                    parts[2].trim(), // kodeBuku
                    parts[3].trim(), // tanggalPinjam
                    parts[4].trim(), // tanggalKembali
                    st               // status
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        String statusStr = (status == STATUS_SUDAH_KEMBALI) ? "Sudah Kembali" : "Belum Kembali";
        return "TRX: " + kodeTransaksi + " | NIS: " + nis + " | Buku: " + kodeBuku +
                " | Pinjam: " + tanggalPinjam + " | Kembali/Tempo: " + tanggalKembali +
                " | Status: " + statusStr;
    }
}