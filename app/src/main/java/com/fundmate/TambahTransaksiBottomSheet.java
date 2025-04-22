// TambahTransaksiBottomSheet.java - FIXED FINAL
package com.fundmate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TambahTransaksiBottomSheet extends BottomSheetDialogFragment {

    private GridLayout gridKategori;
    private EditText inputJumlah;
    private Button buttonSimpan;
    private String selectedKategori = "";
    private String selectedIcon = "";

    private final String[][] kategoriList = {
            {"Shopping", "ic_basket"},
            {"Makanan", "ic_hamburger_soda"},
            {"Hobi", "ic_trophy"}
    };

    public interface OnTransaksiSelesaiListener {
        void onTransaksiDitambahkan();
    }

    private OnTransaksiSelesaiListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnTransaksiSelesaiListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment harus mengimplementasikan OnTransaksiSelesaiListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_transaction, container, false);

        gridKategori = view.findViewById(R.id.grid_kategori);
        inputJumlah = view.findViewById(R.id.input_jumlah);
        buttonSimpan = view.findViewById(R.id.button_simpan);

        buatGridKategori();
        autoFormatJumlah();

        buttonSimpan.setOnClickListener(v -> {
            String jumlahStr = inputJumlah.getText().toString().trim();
            if (TextUtils.isEmpty(jumlahStr) || TextUtils.isEmpty(selectedKategori)) {
                Toast.makeText(getContext(), "Kategori & Jumlah harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            String tanggal = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
            String waktu = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

            SharedPreferences prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
            String username = prefs.getString("username", null);

            if (username == null) {
                Toast.makeText(getContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
                return;
            }

            // Membuat transaksi tanpa ID
            TransactionModel transaksi = new TransactionModel(
                    selectedKategori,
                    jumlahStr,
                    tanggal,
                    waktu,
                    selectedIcon
            );

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(username)
                    .child("transactions");

            // Menambahkan transaksi ke Firebase dan mendapatkan key/ID
            String key = ref.push().getKey();
            if (key != null) {
                // Set ID transaksi
                transaksi.setId(key);
                ref.child(key).setValue(transaksi); // Menyimpan transaksi dengan ID yang baru
            }

            Toast.makeText(getContext(), "Transaksi ditambahkan", Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onTransaksiDitambahkan();
            }

            dismiss();
        });

        return view;
    }

    private void buatGridKategori() {
        for (String[] kategori : kategoriList) {
            String nama = kategori[0];
            String icon = kategori[1];  // Nama icon

            // Meng-inflate layout item_kategori_icon
            View item = LayoutInflater.from(getContext()).inflate(R.layout.item_kategori_icon, gridKategori, false);
            ImageView iconView = item.findViewById(R.id.icon_image);

            // Set icon secara langsung
            switch (icon) {
                case "ic_shopping_cart":
                    iconView.setImageResource(R.drawable.ic_basket);
                    break;
                case "ic_hamburger_soda":
                    iconView.setImageResource(R.drawable.ic_hamburger_soda);
                    break;
                case "ic_trophy":
                    iconView.setImageResource(R.drawable.ic_trophy);
                    break;
                default:
                    iconView.setImageResource(R.drawable.basket);  // Default icon
            }

            // Menambahkan listener untuk memilih kategori
            iconView.setOnClickListener(v -> {
                selectedKategori = nama;  // Menyimpan kategori yang dipilih
                selectedIcon = icon;      // Menyimpan icon yang dipilih
                Toast.makeText(getContext(), "Kategori dipilih: " + nama, Toast.LENGTH_SHORT).show();
            });

            gridKategori.addView(item); // Menambahkan icon ke GridLayout
        }
    }

    private void autoFormatJumlah() {
        inputJumlah.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    inputJumlah.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    try {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getInstance(Locale.getDefault()).format(parsed);
                        current = formatted;
                        inputJumlah.setText(formatted);
                        inputJumlah.setSelection(formatted.length());
                    } catch (NumberFormatException e) {
                        current = "";
                    }

                    inputJumlah.addTextChangedListener(this);
                }
            }
        });
    }
}

