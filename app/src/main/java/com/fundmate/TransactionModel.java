package com.fundmate;

import android.util.Log;

public class TransactionModel {
    private String id;  // ID transaksi yang akan diset oleh Firebase
    private String kategori;
    private Object jumlah; // Bisa String atau Long
    private String tanggal;
    private String waktu;
    private String iconName;

    // Default constructor
    public TransactionModel() {}

    // Konstruktor yang menerima 5 parameter
    public TransactionModel(String kategori, Object jumlah, String tanggal, String waktu, String iconName) {
        this.kategori = kategori;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.iconName = iconName;
    }

    // Konstruktor dengan ID, yang bisa digunakan ketika Anda ingin menyet ID
    public TransactionModel(String id, String kategori, Object jumlah, String tanggal, String waktu, String iconName) {
        this.id = id;
        this.kategori = kategori;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.iconName = iconName;
    }

    // Getter and Setter untuk id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter and Setter untuk kategori
    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    // Getter and Setter untuk tanggal
    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    // Getter and Setter untuk waktu
    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    // Getter and Setter untuk iconName
    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    // Getter untuk jumlah yang disesuaikan dengan tipe data yang berbeda (String, Long, Integer)
    public Object getJumlahRaw() {
        return jumlah;
    }

    public int getJumlah() {
        try {
            if (jumlah instanceof Long) {
                return ((Long) jumlah).intValue();
            } else if (jumlah instanceof Integer) {
                return (Integer) jumlah;
            } else if (jumlah instanceof String) {
                // Mengonversi string dengan format angka (misal 1.000,00 atau 1000)
                String jumlahString = ((String) jumlah).replace(".", "").replace(",", "");
                return Integer.parseInt(jumlahString);
            } else {
                return 0;
            }
        } catch (Exception e) {
            Log.e("TransactionModel", "Error converting jumlah: " + e.getMessage());
            return 0;
        }
    }

    // Setter untuk jumlah
    public void setJumlah(Object jumlah) {
        this.jumlah = jumlah;
    }
}


