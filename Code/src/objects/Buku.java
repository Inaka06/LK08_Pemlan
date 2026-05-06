package objects;

/**
 * Format: Kode, Judul, dan Jenis Buku
 */

public class Buku {
    private String kode;
    private String judul;
    private String jenisBuku;

    public Buku(String kode, String judul, String jenisBuku) {
        this.kode = kode;
        this.judul = judul;
        this.jenisBuku = jenisBuku;
    }

    // Getter dan Setter
    public String getKode()      { return kode; }
    public String getJudul()     { return judul; }
    public String getJenisBuku() { return jenisBuku; }

    public void setKode(String kode)           { this.kode = kode; }
    public void setJudul(String judul)         { this.judul = judul; }
    public void setJenisBuku(String jenisBuku) { this.jenisBuku = jenisBuku; }


    public String toFileString() {
        return kode + "|" + judul + "|" + jenisBuku;
    }


    public static Buku fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 3) return null;
        return new Buku(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    @Override
    public String toString() {
        return String.format("Kode: %-10s | Judul: %-35s | Jenis: %s", kode, judul, jenisBuku);
    }
}
