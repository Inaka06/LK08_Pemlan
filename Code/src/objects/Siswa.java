package objects;

/**
 * Format NIS, Nama, dan Alamat siswa
 */
public class Siswa {
    private String nis;
    private String nama;
    private String alamat;

    public Siswa(String nis, String nama, String alamat) {
        this.nis = nis;
        this.nama = nama;
        this.alamat = alamat;
    }

    // Getter dan Setter
    public String getNis()    { return nis; }
    public String getNama()   { return nama; }
    public String getAlamat() { return alamat; }

    public void setNis(String nis)       { this.nis = nis; }
    public void setNama(String nama)     { this.nama = nama; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    /**
     * Konversi ke format file (dipisah oleh '|')
     */
    public String toFileString() {
        return nis + "|" + nama + "|" + alamat;
    }

    /**
     * Membuat objek Siswa dari baris file
     */
    public static Siswa fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3) return null;
        return new Siswa(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    @Override
    public String toString() {
        return String.format("NIS: %-12s | Nama: %-25s | Alamat: %s", nis, nama, alamat);
    }
}
