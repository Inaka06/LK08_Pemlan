import objects.*;
import service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class MainGUI extends JFrame {

    private static final String DATA_DIR = "data/";

    private SiswaService siswaService;
    private BukuService bukuService;
    private PegawaiService pegawaiService;
    private PeminjamanService peminjamanService;

    private Pegawai pegawaiLogin = null;

    // Komponen GUI Buku
    private JTable tabelBuku;
    private DefaultTableModel modelBuku;
    private JTextField txtKodeBuku, txtJudulBuku, txtJenisBuku;

    // Komponen GUI Siswa
    private JTable tabelSiswa;
    private DefaultTableModel modelSiswa;
    private JTextField txtNisSiswa, txtNamaSiswa, txtAlamatSiswa;

    // Komponen GUI Transaksi
    private JTable tabelTransaksi;
    private DefaultTableModel modelTransaksi;
    private JTextField txtTrxNis, txtTrxKodeBuku, txtTrxTglPinjam, txtTrxTglTempo, txtTrxKembaliKode, txtTrxTglKembali;

    public MainGUI() {
        // 1. Inisialisasi Service (Sama seperti versi CLI)
        FileService.ensureDataDir(DATA_DIR);
        pegawaiService = new PegawaiService(DATA_DIR);
        siswaService = new SiswaService(DATA_DIR);
        bukuService = new BukuService(DATA_DIR);
        peminjamanService = new PeminjamanService(DATA_DIR, siswaService, bukuService);

        // 2. Setup Frame Utama
        setTitle("Sistem Informasi Perpustakaan SMP");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Posisi di tengah layar
    }

    /**
     * Menampilkan Dialog Login
     */
    public void showLogin() {
        JDialog loginDialog = new JDialog(this, "Login Sistem", true);
        loginDialog.setSize(300, 200);
        loginDialog.setLayout(new GridLayout(4, 1, 10, 10));
        loginDialog.setLocationRelativeTo(this);

        JPanel panelUser = new JPanel(new FlowLayout());
        panelUser.add(new JLabel("NIP:       "));
        JTextField txtNip = new JTextField(15);
        panelUser.add(txtNip);

        JPanel panelPass = new JPanel(new FlowLayout());
        panelPass.add(new JLabel("Password:"));
        JPasswordField txtPass = new JPasswordField(15);
        panelPass.add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> {
            String nip = txtNip.getText();
            String pass = new String(txtPass.getPassword());
            try {
                pegawaiLogin = pegawaiService.login(nip, pass);
                if (pegawaiLogin != null) {
                    JOptionPane.showMessageDialog(loginDialog, "Login berhasil! Selamat datang, " + pegawaiLogin.getNama());
                    loginDialog.dispose();
                    initMainScreen(); // Muat layar utama jika berhasil
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "NIP atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(loginDialog, "Gagal mengakses data: " + ex.getMessage());
            }
        });

        loginDialog.add(new JLabel("Silahkan Login", SwingConstants.CENTER));
        loginDialog.add(panelUser);
        loginDialog.add(panelPass);

        JPanel panelBtn = new JPanel();
        panelBtn.add(btnLogin);
        loginDialog.add(panelBtn);

        loginDialog.setVisible(true);
    }

    /**
     * Memuat Layar Utama (Tabbed Pane)
     */
    private void initMainScreen() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Kelola Buku", createPanelBuku());
        tabbedPane.addTab("Kelola Siswa", createPanelSiswa());        // <-- Update ini
        tabbedPane.addTab("Transaksi Peminjaman", createPanelTransaksi()); // <-- Update ini

        add(tabbedPane);
        setVisible(true);
    }

    /**
     * Membuat Panel untuk CRUD Buku
     */
    private JPanel createPanelBuku() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- Panel Form Input (Utara) ---
        JPanel panelInput = new JPanel(new GridLayout(2, 4, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Data Buku"));

        txtKodeBuku = new JTextField();
        txtJudulBuku = new JTextField();
        txtJenisBuku = new JTextField();

        panelInput.add(new JLabel("Kode Buku:"));
        panelInput.add(txtKodeBuku);
        panelInput.add(new JLabel("Judul Buku:"));
        panelInput.add(txtJudulBuku);
        panelInput.add(new JLabel("Jenis Buku:"));
        panelInput.add(txtJenisBuku);

        JButton btnTambah = new JButton("Tambah");
        JButton btnHapus = new JButton("Hapus Terpilih");

        JPanel panelTombol = new JPanel(new FlowLayout());
        panelTombol.add(btnTambah);
        panelTombol.add(btnHapus);
        panelInput.add(panelTombol);

        panel.add(panelInput, BorderLayout.NORTH);

        // --- Tabel Data (Tengah) ---
        String[] kolom = {"No", "Kode", "Judul", "Jenis"};
        modelBuku = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };
        tabelBuku = new JTable(modelBuku);
        JScrollPane scrollPane = new JScrollPane(tabelBuku);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Event Listeners ---
        // Tombol Tambah
        btnTambah.addActionListener(e -> {
            String kode = txtKodeBuku.getText();
            String judul = txtJudulBuku.getText();
            String jenis = txtJenisBuku.getText();

            if (kode.isEmpty() || judul.isEmpty() || jenis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }

            try {
                Buku bukuBaru = new Buku(kode, judul, jenis);
                if (bukuService.tambah(bukuBaru)) {
                    JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan.");
                    refreshTabelBuku();
                    txtKodeBuku.setText("");
                    txtJudulBuku.setText("");
                    txtJenisBuku.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambah buku (Kode sudah ada / memori penuh).");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error file: " + ex.getMessage());
            }
        });


        // Tombol Hapus
        btnHapus.addActionListener(e -> {
            int row = tabelBuku.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih buku di tabel terlebih dahulu untuk dihapus.");
                return;
            }

            String kode = modelBuku.getValueAt(row, 1).toString();
            int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus buku " + kode + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

            if (konfirmasi == JOptionPane.YES_OPTION) {
                try {
                    if (bukuService.hapus(kode)) {
                        JOptionPane.showMessageDialog(this, "Buku berhasil dihapus.");
                        refreshTabelBuku();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error file: " + ex.getMessage());
                }
            }
        });

        refreshTabelBuku(); // Muat data awal
        return panel;
    }

    /**
     * Membuat Panel untuk CRUD Siswa
     */
    private JPanel createPanelSiswa() {
        JPanel panel = new JPanel(new BorderLayout());

        // --- Panel Input ---
        JPanel panelInput = new JPanel(new GridLayout(2, 4, 10, 10));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Data Siswa"));

        txtNisSiswa = new JTextField();
        txtNamaSiswa = new JTextField();
        txtAlamatSiswa = new JTextField();

        panelInput.add(new JLabel("NIS:"));
        panelInput.add(txtNisSiswa);
        panelInput.add(new JLabel("Nama:"));
        panelInput.add(txtNamaSiswa);
        panelInput.add(new JLabel("Alamat:"));
        panelInput.add(txtAlamatSiswa);

        JButton btnTambah = new JButton("Tambah");
        JButton btnHapus = new JButton("Hapus Terpilih");

        JPanel panelTombol = new JPanel(new FlowLayout());
        panelTombol.add(btnTambah);
        panelTombol.add(btnHapus);
        panelInput.add(panelTombol);

        panel.add(panelInput, BorderLayout.NORTH);

        // --- Tabel Data ---
        String[] kolom = {"No", "NIS", "Nama", "Alamat"};
        modelSiswa = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelSiswa = new JTable(modelSiswa);
        panel.add(new JScrollPane(tabelSiswa), BorderLayout.CENTER);

        // --- Event Listeners ---
        btnTambah.addActionListener(e -> {
            String nis = txtNisSiswa.getText();
            String nama = txtNamaSiswa.getText();
            String alamat = txtAlamatSiswa.getText();

            if (nis.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
                return;
            }
            try {
                if (siswaService.tambah(new Siswa(nis, nama, alamat))) {
                    JOptionPane.showMessageDialog(this, "Siswa berhasil ditambahkan.");
                    refreshTabelSiswa();
                    txtNisSiswa.setText(""); txtNamaSiswa.setText(""); txtAlamatSiswa.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambah (NIS sudah ada / harus angka / memori penuh).");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        btnHapus.addActionListener(e -> {
            int row = tabelSiswa.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih siswa di tabel terlebih dahulu.");
                return;
            }
            String nis = modelSiswa.getValueAt(row, 1).toString();
            if (JOptionPane.showConfirmDialog(this, "Hapus NIS " + nis + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    if (siswaService.hapus(nis)) {
                        JOptionPane.showMessageDialog(this, "Siswa dihapus.");
                        refreshTabelSiswa();
                    }
                } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
            }
        });

        refreshTabelSiswa();
        return panel;
    }

    private void refreshTabelSiswa() {
        modelSiswa.setRowCount(0);
        try {
            Siswa[] daftarSiswa = siswaService.getAll();
            for (int i = 0; i < daftarSiswa.length; i++) {
                if (daftarSiswa[i] != null) {
                    modelSiswa.addRow(new Object[]{i + 1, daftarSiswa[i].getNis(), daftarSiswa[i].getNama(), daftarSiswa[i].getAlamat()});
                }
            }
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Gagal memuat: " + e.getMessage()); }
    }

    /**
     * Membuat Panel untuk Transaksi Peminjaman dan Pengembalian
     */
    private JPanel createPanelTransaksi() {
        JPanel panelUtama = new JPanel(new BorderLayout());

        // --- Panel Form (Utara) ---
        JPanel panelFormUtama = new JPanel(new GridLayout(1, 2, 10, 0)); // Dibagi dua kolom: Pinjam & Kembali

        // Form Peminjaman
        JPanel panelPinjam = new JPanel(new GridLayout(5, 2, 5, 5));
        panelPinjam.setBorder(BorderFactory.createTitledBorder("Form Peminjaman"));
        txtTrxNis = new JTextField();
        txtTrxKodeBuku = new JTextField();
        txtTrxTglPinjam = new JTextField("DD-MM-YYYY");
        txtTrxTglTempo = new JTextField("DD-MM-YYYY");
        JButton btnPinjam = new JButton("Proses Pinjam");

        panelPinjam.add(new JLabel("NIS Siswa:")); panelPinjam.add(txtTrxNis);
        panelPinjam.add(new JLabel("Kode Buku:")); panelPinjam.add(txtTrxKodeBuku);
        panelPinjam.add(new JLabel("Tgl Pinjam:")); panelPinjam.add(txtTrxTglPinjam);
        panelPinjam.add(new JLabel("Tgl Tempo:")); panelPinjam.add(txtTrxTglTempo);
        panelPinjam.add(new JLabel("")); panelPinjam.add(btnPinjam);

        // Form Pengembalian
        JPanel panelKembali = new JPanel(new GridLayout(5, 2, 5, 5));
        panelKembali.setBorder(BorderFactory.createTitledBorder("Form Pengembalian"));
        txtTrxKembaliKode = new JTextField();
        txtTrxTglKembali = new JTextField("DD-MM-YYYY");
        JButton btnKembali = new JButton("Proses Kembali");

        panelKembali.add(new JLabel("Kode TRX:")); panelKembali.add(txtTrxKembaliKode);
        panelKembali.add(new JLabel("Tgl Kembali:")); panelKembali.add(txtTrxTglKembali);
        panelKembali.add(new JLabel("")); panelKembali.add(new JLabel("")); // Spacer
        panelKembali.add(new JLabel("")); panelKembali.add(new JLabel("")); // Spacer
        panelKembali.add(new JLabel("")); panelKembali.add(btnKembali);

        panelFormUtama.add(panelPinjam);
        panelFormUtama.add(panelKembali);
        panelUtama.add(panelFormUtama, BorderLayout.NORTH);

        // --- Tabel Transaksi ---
        String[] kolom = {"Kode TRX", "NIS", "Kode Buku", "Tgl Pinjam", "Tgl Tempo/Kembali", "Status"};
        modelTransaksi = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelTransaksi = new JTable(modelTransaksi);
        panelUtama.add(new JScrollPane(tabelTransaksi), BorderLayout.CENTER);

        // --- Event Listeners ---
        btnPinjam.addActionListener(e -> {
            String nis = txtTrxNis.getText();
            String kode = txtTrxKodeBuku.getText();
            String tgl = txtTrxTglPinjam.getText();
            String tempo = txtTrxTglTempo.getText();

            try {
                if (peminjamanService.pinjam(nis, kode, tgl, tempo)) {
                    JOptionPane.showMessageDialog(this, "Peminjaman berhasil dicatat.");
                    refreshTabelTransaksi();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal: Siswa/Buku tidak ada, buku dipinjam, atau limit pinjam tercapai.");
                }
            } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        btnKembali.addActionListener(e -> {
            String trx = txtTrxKembaliKode.getText();
            String tglK = txtTrxTglKembali.getText();

            try {
                if (peminjamanService.kembalikan(trx, tglK)) {
                    JOptionPane.showMessageDialog(this, "Buku berhasil dikembalikan.");
                    refreshTabelTransaksi();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal: Kode TRX tidak ditemukan.");
                }
            } catch (IOException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        });

        // Event: Klik baris di tabel mengisi textfield Pengembalian otomatis
        tabelTransaksi.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelTransaksi.getSelectedRow() != -1) {
                int row = tabelTransaksi.getSelectedRow();
                // Isi otomatis field Kode TRX di panel pengembalian
                txtTrxKembaliKode.setText(modelTransaksi.getValueAt(row, 0).toString());
            }
        });

        refreshTabelTransaksi();
        return panelUtama;
    }

    private void refreshTabelTransaksi() {
        modelTransaksi.setRowCount(0);
        try {
            Peminjaman[] daftarTrx = peminjamanService.getAll();
            for (Peminjaman p : daftarTrx) {
                if (p != null) {
                    String status = (p.getStatus() == Peminjaman.STATUS_SUDAH_KEMBALI) ? "Sudah Kembali" : "Dipinjam";
                    modelTransaksi.addRow(new Object[]{
                            p.getKodeTransaksi(), p.getNis(), p.getKodeBuku(),
                            p.getTanggalPinjam(), p.getTanggalKembali(), status
                    });
                }
            }
        } catch (IOException e) { JOptionPane.showMessageDialog(this, "Gagal memuat: " + e.getMessage()); }
    }

    /**
     * Memuat ulang data dari file txt ke dalam tabel GUI
     */
    private void refreshTabelBuku() {
        modelBuku.setRowCount(0); // Kosongkan tabel
        try {
            Buku[] daftarBuku = bukuService.getAll();
            for (int i = 0; i < daftarBuku.length; i++) {
                if (daftarBuku[i] != null) {
                    Object[] baris = {
                            i + 1,
                            daftarBuku[i].getKode(),
                            daftarBuku[i].getJudul(),
                            daftarBuku[i].getJenisBuku()
                    };
                    modelBuku.addRow(baris);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }

    // --- Entry Point Utama ---
    public static void main(String[] args) {
        // Menggunakan Event Dispatch Thread untuk thread safety di Swing
        SwingUtilities.invokeLater(() -> {
            MainGUI app = new MainGUI();
            app.showLogin();
        });
    }
}