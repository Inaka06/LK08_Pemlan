package objects;

/**
 * Format: NIP, Nama, Tanggal Lahir, dan Password untuk login
 */

public class Pegawai {
    private String nip;
    private String nama;
    private String tanggalLahir;
    private String password;

    public Pegawai(String nip, String nama, String tanggalLahir, String password) {
        this.nip = nip;
        this.nama = nama;
        this.tanggalLahir = tanggalLahir;
        this.password = password;
    }

    // Getter dan Setter
    public String getNip()          { return nip; }
    public String getNama()         { return nama; }
    public String getTanggalLahir() { return tanggalLahir; }
    public String getPassword()     { return password; }

    public void setNip(String nip)                   { this.nip = nip; }
    public void setNama(String nama)                 { this.nama = nama; }
    public void setTanggalLahir(String tanggalLahir) { this.tanggalLahir = tanggalLahir; }
    public void setPassword(String password)         { this.password = password; }

    /**
     * Konversi ke format file
     */
    public String toFileString() {
        return nip + "|" + nama + "|" + tanggalLahir + "|" + password;
    }

    /**
     * Membuat objek Pegawai dari baris file
     */
    public static Pegawai fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        return new Pegawai(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
    }

    @Override
    public String toString() {
        return String.format("NIP: %-12s | Nama: %-25s | Tgl Lahir: %s", nip, nama, tanggalLahir);
    }
}
