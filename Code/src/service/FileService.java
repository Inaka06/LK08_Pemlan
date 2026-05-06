package service;

import java.io.*;

/**
 * Kelas utilitas untuk operasi File untuk input dan output
 */
public class FileService {

    private static final int MAX_LINES = 1000; // Kapasitas maksimum baris per file(

    /**
     * Memastikan direktori data tersedia
     */
    public static void ensureDataDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Membaca semua baris dari file ke dalam Array String.
     * @return Array String berisi baris-baris file (sisanya null)
     */
    public static String[] readLines(String filePath) throws IOException {
        String[] lines = new String[MAX_LINES];
        File file = new File(filePath);

        if (!file.exists()) {
            return new String[0]; // Kembalikan array kosong jika file tidak ada
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < MAX_LINES) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines[count] = line;
                    count++;
                }
            }

            String[] result = new String[count];
            for (int i = 0; i < count; i++) {
                result[i] = lines[i];
            }
            return result;
        }
    }

    /**
     * Menulis semua baris dari Array ke file (overwrite)
     */
    public static void writeLines(String filePath, String[] lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (int i = 0; i < lines.length; i++) {
                if (lines[i] != null) {
                    writer.write(lines[i]);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Menambahkan satu baris ke akhir file (append)
     */
    public static void appendLine(String filePath, String line) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(line);
            writer.newLine();
        }
    }

    /**
     * Membuat file kosong jika belum ada
     */
    public static void createIfNotExists(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
    }
}