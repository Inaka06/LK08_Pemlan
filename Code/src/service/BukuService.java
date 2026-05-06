package service;

import objects.Buku;
import java.io.IOException;

/**
 * Service untuk operasi CRUD data Buku
 */
public class BukuService {
    private final String filePath;
    private static final int MAX_BUKU = 1000; // Batas maksimal data buku di memori

    public BukuService(String dataDir) {
        this.filePath = dataDir + "/buku.txt";
        try {
            FileService.createIfNotExists(filePath);
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal membuat file buku: " + e.getMessage());
        }
    }

    /**
     * Mengambil semua data buku ke dalam Array
     */
    public Buku[] getAll() throws IOException {
        // Langsung terima String[] dari FileService
        String[] lines = FileService.readLines(filePath);

        Buku[] list = new Buku[lines.length];
        for (int i = 0; i < lines.length; i++) {
            list[i] = Buku.fromFileString(lines[i]);
        }
        return list;
    }

    /**
     * Mendapatkan jumlah data aktual dari file
     */
    public int getJumlahData() throws IOException {
        return FileService.readLines(filePath).length;
    }

    /**
     * Mencari buku berdasarkan kode
     */
    public Buku findByKode(String kode) throws IOException {
        Buku[] list = getAll();
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null && list[i].getKode().equalsIgnoreCase(kode)) {
                return list[i];
            }
        }
        return null;
    }

    /**
     * Menambahkan buku baru
     */
    public boolean tambah(Buku buku) throws IOException {
        if (getJumlahData() >= MAX_BUKU) {
            System.out.println("[!] Kapasitas memori buku penuh.");
            return false;
        }
        if (findByKode(buku.getKode()) != null) {
            System.out.println("[!] Kode buku " + buku.getKode() + " sudah ada.");
            return false;
        }
        FileService.appendLine(filePath, buku.toFileString());
        return true;
    }

    /**
     * Mengedit data buku
     */
    public boolean edit(String kode, Buku baru) throws IOException {
        Buku[] list = getAll();
        String[] newLines = new String[list.length];
        boolean ditemukan = false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].getKode().equalsIgnoreCase(kode)) {
                newLines[i] = baru.toFileString();
                ditemukan = true;
            } else {
                newLines[i] = list[i].toFileString();
            }
        }

        if (ditemukan) {
            FileService.writeLines(filePath, newLines);
        }
        return ditemukan;
    }

    /**
     * Menghapus data buku (Teknik Geser Indeks/Filter)
     */
    public boolean hapus(String kode) throws IOException {
        Buku[] list = getAll();
        String[] temporaryLines = new String[list.length];
        int newCount = 0;
        boolean ditemukan = false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].getKode().equalsIgnoreCase(kode)) {
                ditemukan = true;
            } else {
                temporaryLines[newCount] = list[i].toFileString();
                newCount++;
            }
        }

        if (ditemukan) {
            // Potong array agar tidak ada null di akhir file
            String[] finalLines = new String[newCount];
            System.arraycopy(temporaryLines, 0, finalLines, 0, newCount);
            FileService.writeLines(filePath, finalLines);
        }
        return ditemukan;
    }

    /**
     * Mencari buku berdasarkan judul
     */
    public Buku[] cariByJudul(String keyword) throws IOException {
        Buku[] data = getAll();
        Buku[] temp = new Buku[data.length];
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i].getJudul().toLowerCase().contains(keyword.toLowerCase())) {
                temp[count++] = data[i];
            }
        }
        Buku[] result = new Buku[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }

    public Buku[] cariByJenis(String keyword) throws IOException {
        Buku[] data = getAll();
        Buku[] temp = new Buku[data.length];
        int count = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i].getJenisBuku().toLowerCase().contains(keyword.toLowerCase())) {
                temp[count++] = data[i];
            }
        }
        Buku[] result = new Buku[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }
}