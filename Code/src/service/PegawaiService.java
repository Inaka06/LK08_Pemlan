package service;

import objects.Pegawai;
import java.io.IOException;

/**
 * Service untuk operasi CRUD data Pegawai dan Login menggunakan Array Standar
 */
public class PegawaiService {
    private final String filePath;
    private static final int MAX_PEGAWAI = 50; // Kapasitas maksimal pegawai

    public PegawaiService(String dataDir) {
        this.filePath = dataDir + "/pegawai.txt";
        try {
            FileService.createIfNotExists(filePath);
            // Seed akun admin default jika file kosong
            seedDefaultAdmin();
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal menginisialisasi file pegawai: " + e.getMessage());
        }
    }

    /**
     * Membuat akun admin default jika belum ada pegawai sama sekali
     */
    private void seedDefaultAdmin() throws IOException {
        if (getJumlahData() == 0) {
            Pegawai admin = new Pegawai("10001", "Admin", "01-01-1990", "admin123");
            FileService.appendLine(filePath, admin.toFileString());
            System.out.println("[INFO] Akun default dibuat. NIP: 10001 | Password: admin123");
        }
    }

    /**
     * Mengambil semua data pegawai ke dalam Array
     */
    public Pegawai[] getAll() throws IOException {
        String[] lines = FileService.readLines(filePath);
        Pegawai[] list = new Pegawai[lines.length];

        for (int i = 0; i < lines.length; i++) {
            list[i] = Pegawai.fromFileString(lines[i]);
        }
        return list;
    }

    /**
     * Mendapatkan jumlah baris data yang ada
     */
    public int getJumlahData() throws IOException {
        return FileService.readLines(filePath).length;
    }

    /**
     * Login pegawai berdasarkan NIP dan password
     */
    public Pegawai login(String nip, String password) throws IOException {
        Pegawai[] list = getAll();
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null &&
                    list[i].getNip().equals(nip) &&
                    list[i].getPassword().equals(password)) {
                return list[i];
            }
        }
        return null;
    }

    /**
     * Mencari pegawai berdasarkan NIP
     */
    public Pegawai findByNip(String nip) throws IOException {
        Pegawai[] list = getAll();
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null && list[i].getNip().equals(nip)) {
                return list[i];
            }
        }
        return null;
    }

    /**
     * Menambahkan pegawai baru
     */
    public boolean tambah(Pegawai pegawai) throws IOException {
        if (getJumlahData() >= MAX_PEGAWAI) {
            System.out.println("[!] Kapasitas memori pegawai penuh.");
            return false;
        }
        if (findByNip(pegawai.getNip()) != null) {
            System.out.println("[!] NIP " + pegawai.getNip() + " sudah terdaftar.");
            return false;
        }
        FileService.appendLine(filePath, pegawai.toFileString());
        return true;
    }

    /**
     * Mengedit data pegawai berdasarkan NIP
     */
    public boolean edit(String nip, Pegawai baru) throws IOException {
        Pegawai[] list = getAll();
        String[] newLines = new String[list.length];
        boolean ditemukan = false;

        for (int i = 0; i < list.length; i++) {
            if (list[i].getNip().equals(nip)) {
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
     * Menghapus data pegawai berdasarkan NIP
     */
    public boolean hapus(String nip) throws IOException {
        Pegawai[] list = getAll();
        int total = list.length;

        if (total <= 1) {
            System.out.println("[!] Tidak bisa menghapus. Minimal harus ada 1 pegawai.");
            return false;
        }

        String[] temporaryLines = new String[total];
        int count = 0;
        boolean ditemukan = false;

        for (int i = 0; i < total; i++) {
            if (list[i].getNip().equals(nip)) {
                ditemukan = true;
            } else {
                temporaryLines[count] = list[i].toFileString();
                count++;
            }
        }

        if (ditemukan) {
            // Buat array baru dengan ukuran yang sudah dikurangi
            String[] finalLines = new String[count];
            for (int i = 0; i < count; i++) {
                finalLines[i] = temporaryLines[i];
            }
            FileService.writeLines(filePath, finalLines);
        }
        return ditemukan;
    }
}