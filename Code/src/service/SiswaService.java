package service;

import objects.Siswa;
import java.io.IOException;

/**
 * Service untuk operasi CRUD data Siswa menggunakan Array Standar
 */
public class SiswaService {
    private final String filePath;
    private static final int MAX_SISWA = 200; // Kapasitas maksimal data siswa

    public SiswaService(String dataDir) {
        this.filePath = dataDir + "/siswa.txt";
        try {
            FileService.createIfNotExists(filePath);
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal membuat file siswa: " + e.getMessage());
        }
    }

    /**
     * Mengambil semua data siswa ke dalam Array
     */
    public Siswa[] getAll() throws IOException {
        String[] lines = FileService.readLines(filePath);
        Siswa[] list = new Siswa[lines.length];

        for (int i = 0; i < lines.length; i++) {
            list[i] = Siswa.fromFileString(lines[i]);
        }
        return list;
    }

    /**
     * Helper untuk mendapatkan jumlah data siswa
     */
    public int getJumlahData() throws IOException {
        return FileService.readLines(filePath).length;
    }

    /**
     * Mencari siswa berdasarkan NIS
     */
    public Siswa findByNis(String nis) throws IOException {
        Siswa[] list = getAll();
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null && list[i].getNis().equalsIgnoreCase(nis)) {
                return list[i];
            }
        }
        return null;
    }

    /**
     * Menambahkan siswa baru
     */
    public boolean tambah(Siswa siswa) throws IOException {
        if (getJumlahData() >= MAX_SISWA) {
            System.out.println("[!] Kapasitas memori siswa penuh.");
            return false;
        }
        if (findByNis(siswa.getNis()) != null) {
            System.out.println("[!] NIS " + siswa.getNis() + " sudah terdaftar.");
            return false;
        }
        // Validasi NIS (Hanya angka)
        if (!siswa.getNis().matches("\\d+")) {
            System.out.println("[!] NIS harus berupa angka.");
            return false;
        }
        FileService.appendLine(filePath, siswa.toFileString());
        return true;
    }

    /**
     * Mengedit data siswa berdasarkan NIS
     */
    public boolean edit(String nis, Siswa baru) throws IOException {
        Siswa[] list = getAll();
        String[] newLines = new String[list.length];
        boolean ditemukan = false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].getNis().equalsIgnoreCase(nis)) {
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
     * Menghapus data siswa berdasarkan NIS
     */
    public boolean hapus(String nis) throws IOException {
        Siswa[] list = getAll();
        String[] temporaryLines = new String[list.length];
        int count = 0;
        boolean ditemukan = false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].getNis().equalsIgnoreCase(nis)) {
                ditemukan = true;
            } else {
                temporaryLines[count] = list[i].toFileString();
                count++;
            }
        }

        if (ditemukan) {
            // Kecilkan array ke ukuran yang pas
            String[] finalLines = new String[count];
            System.arraycopy(temporaryLines, 0, finalLines, 0, count);
            FileService.writeLines(filePath, finalLines);
        }
        return ditemukan;
    }

    /**
     * Mencari siswa berdasarkan nama (Mengembalikan Array)
     */
    public Siswa[] cariByNama(String keyword) throws IOException {
        Siswa[] all = getAll();
        Siswa[] temp = new Siswa[all.length];
        int count = 0;

        for (int i = 0; i < all.length; i++) {
            if (all[i].getNama().toLowerCase().contains(keyword.toLowerCase())) {
                temp[count++] = all[i];
            }
        }

        Siswa[] hasil = new Siswa[count];
        System.arraycopy(temp, 0, hasil, 0, count);
        return hasil;
    }
}