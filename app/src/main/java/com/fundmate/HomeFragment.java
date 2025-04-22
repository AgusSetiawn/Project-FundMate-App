package com.fundmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeFragment extends Fragment implements TambahTransaksiBottomSheet.OnTransaksiSelesaiListener {

    private ImageView fotoProfil;
    private TextView namaUser, jumlahBulanan, bulanText, bulanTahunText;
    private TextView jumlahHarian, jumlahMingguan;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<TransactionModel> transaksiList = new ArrayList<>();
    private DatabaseReference transaksiRef;
    private SharedPreferences sharedPreferences;
    private String profileImageUrl;
    private String currentMonth, currentYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fotoProfil = view.findViewById(R.id.foto_profil);
        namaUser = view.findViewById(R.id.nama_user);
        jumlahBulanan = view.findViewById(R.id.JumlahPengeluaranBulanan);
        jumlahHarian = view.findViewById(R.id.JumlahPengeluaranHarian);
        jumlahMingguan = view.findViewById(R.id.JumlahPengeluaranMingguan);
        bulanText = view.findViewById(R.id.Bulan);
        bulanTahunText = view.findViewById(R.id.BulandanTahun);
        recyclerView = view.findViewById(R.id.recyclerView);
        fotoProfil = view.findViewById(R.id.foto_profil);
        namaUser = view.findViewById(R.id.nama_user);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter(requireContext(), transaksiList);
        recyclerView.setAdapter(adapter);

        sharedPreferences = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        profileImageUrl = sharedPreferences.getString("profileImageUrl", null);


        if (profileImageUrl != null) {
            byte[] decodedString = Base64.decode(profileImageUrl, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            fotoProfil.setImageBitmap(decodedByte);
        }

        // Ambil nama pengguna dari SharedPreferences atau Firebase
        String name = sharedPreferences.getString("name", "Default Name");
        namaUser.setText(name);

        if (username != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);
            transaksiRef = userRef.child("transactions");

            tampilkanTanggalSaatIni();
            tampilkanDataProfil(userRef);
            ambilDanTampilkanTransaksi();
            updateTransaksiHarian();
            updateTransaksiMingguan();

            fotoProfil.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            });

            requireActivity().findViewById(R.id.tambahkan).setOnClickListener(v -> {
                new TambahTransaksiBottomSheet().show(getChildFragmentManager(), "TambahTransaksi");
            });

            // Menambahkan ItemTouchHelper untuk swipe to delete
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;  // Tidak ada pergerakan item
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    // Menghapus transaksi dari daftar dan database
                    hapusTransaksi(position);
                }

                @Override
                public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                        float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    // Bisa menambahkan animasi atau efek geser di sini
                }
            };

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);

        } else {
            Toast.makeText(getContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void tampilkanTanggalSaatIni() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat bulanFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat bulanTahunFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());

        bulanText.setText(bulanFormat.format(cal.getTime()));
        bulanTahunText.setText(bulanTahunFormat.format(cal.getTime()));

        currentMonth = new SimpleDateFormat("MMM", Locale.getDefault()).format(cal.getTime());
        currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(cal.getTime());
    }

    private void tampilkanDataProfil(DatabaseReference userRef) {
        // Retrieve cached name and image from SharedPreferences
        String cachedName = sharedPreferences.getString("cached_name", "Hai👋");
        String cachedImage = sharedPreferences.getString("cached_image", "");
        namaUser.setText(cachedName);

        // Display cached image if available
        if (!cachedImage.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(cachedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                if (bitmap != null) {
                    Glide.with(requireContext())
                            .load(bitmap)
                            .circleCrop() // Ensure the image is cropped in a circle
                            .into(fotoProfil);
                } else {
                    fotoProfil.setImageResource(R.drawable.user); // Default image if failed to decode
                }
            } catch (Exception e) {
                Log.e("HOME_FRAG_LOG", "Failed to decode image: " + e.getMessage());
                fotoProfil.setImageResource(R.drawable.user); // Default image on error
            }
        } else {
            fotoProfil.setImageResource(R.drawable.user); // Default image if no cached image
        }

        // Fetch updated name from Firebase and update SharedPreferences
        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String newName = snapshot.getValue(String.class);
                if (newName != null && !newName.equals(cachedName)) {
                    namaUser.setText(newName);
                    sharedPreferences.edit().putString("cached_name", newName).apply();
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                // Handle error
            }
        });
    }

    private void ambilDanTampilkanTransaksi() {
        String cachedTransactions = sharedPreferences.getString("cached_transactions", "");
        if (!cachedTransactions.isEmpty()) updateRecyclerView(cachedTransactions);

        transaksiRef.orderByChild("tanggal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                SimpleDateFormat formatTanggal = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                List<TransactionModel> transaksiTempList = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()) {
                    TransactionModel transaksi = item.getValue(TransactionModel.class);
                    if (transaksi != null && transaksi.getTanggal() != null) {
                        try {
                            Date tanggalTransaksi = formatTanggal.parse(transaksi.getTanggal());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(tanggalTransaksi);
                            String month = new SimpleDateFormat("MMM", Locale.getDefault()).format(cal.getTime());
                            String year = String.valueOf(cal.get(Calendar.YEAR));
                            if (month.equals(currentMonth) && year.equals(currentYear)) {
                                transaksiTempList.add(transaksi);
                                total += transaksi.getJumlah();
                            }
                        } catch (Exception e) {
                            Log.e("TRANSAKSI_PARSE", "Tanggal salah format: " + e.getMessage());
                        }
                    }
                }

                // Menampilkan transaksi dengan urutan terbaru di atas
                Collections.reverse(transaksiTempList); // Membalik urutan transaksi, agar yang terbaru di atas
                jumlahBulanan.setText("Rp " + NumberFormat.getInstance(Locale.getDefault()).format(total));
                sharedPreferences.edit().putString("cached_transactions", new Gson().toJson(transaksiTempList)).apply();
                transaksiList.clear();
                transaksiList.addAll(transaksiTempList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("TRANSAKSI_ERROR", "Error retrieving data: " + error.getMessage());
            }
        });

    }

    private void updateRecyclerView(String transactionsJson) {
        List<TransactionModel> transaksiTempList = new Gson().fromJson(transactionsJson, new TypeToken<List<TransactionModel>>() {}.getType());
        transaksiList.clear();
        if (transaksiTempList != null) transaksiList.addAll(transaksiTempList);
        adapter.notifyDataSetChanged();
    }

    private void hapusTransaksi(int position) {
        // Ambil transaksi yang ingin dihapus
        TransactionModel transaksi = transaksiList.get(position);

        // Ambil ID transaksi dari objek TransactionModel
        String transaksiId = transaksi.getId();

        // Hapus transaksi dari Firebase
        String username = sharedPreferences.getString("username", null);
        if (username != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);
            userRef.child("transactions").child(transaksiId).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Transaksi berhasil dihapus", Toast.LENGTH_SHORT).show();
                            // Update informasi di card utama dan card lainnya
                            updateTransaksiHarian();
                            updateTransaksiMingguan();
                            updateTransaksiBulanan();  // Memperbarui pengeluaran bulanan
                        } else {
                            Toast.makeText(getContext(), "Gagal menghapus transaksi", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Hapus transaksi dari daftar lokal
        transaksiList.remove(position);
        adapter.notifyItemRemoved(position);

        // Update data transaksi setelah dihapus
        sharedPreferences.edit().putString("cached_transactions", new Gson().toJson(transaksiList)).apply();
    }


    // Update jumlah pengeluaran harian setelah transaksi dihapus
    private void updateTransaksiHarian() {
        String hariIni = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        String lastChecked = sharedPreferences.getString("last_checked_date", "");
        if (!lastChecked.equals(hariIni)) {
            sharedPreferences.edit().remove("cached_transactions_harian").putString("last_checked_date", hariIni).apply();
        }
        transaksiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                String today = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
                List<TransactionModel> transaksiHarian = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    TransactionModel t = item.getValue(TransactionModel.class);
                    if (t != null && today.equals(t.getTanggal())) {
                        transaksiHarian.add(t);
                        total += t.getJumlah();
                    }
                }
                jumlahHarian.setText("Rp " + NumberFormat.getInstance(Locale.getDefault()).format(total));
                sharedPreferences.edit().putString("cached_transactions_harian", new Gson().toJson(transaksiHarian)).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Update jumlah pengeluaran mingguan setelah transaksi dihapus
    private void updateTransaksiMingguan() {
        Calendar now = Calendar.getInstance();
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        int currentYear = now.get(Calendar.YEAR);
        String weekId = currentYear + "-" + currentWeek;

        String lastChecked = sharedPreferences.getString("last_checked_week", "");
        if (!lastChecked.equals(weekId)) {
            sharedPreferences.edit().remove("cached_transactions_mingguan").putString("last_checked_week", weekId).apply();
        }

        transaksiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                List<TransactionModel> transaksiMingguan = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    TransactionModel t = item.getValue(TransactionModel.class);
                    if (t != null && t.getTanggal() != null) {
                        try {
                            Date tanggal = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(t.getTanggal());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(tanggal);
                            if (cal.get(Calendar.WEEK_OF_YEAR) == currentWeek && cal.get(Calendar.YEAR) == currentYear) {
                                transaksiMingguan.add(t);
                                total += t.getJumlah();
                            }
                        } catch (Exception ignored) {}
                    }
                }
                jumlahMingguan.setText("Rp " + NumberFormat.getInstance(Locale.getDefault()).format(total));
                sharedPreferences.edit().putString("cached_transactions_mingguan", new Gson().toJson(transaksiMingguan)).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateTransaksiBulanan() {
        String bulanTahun = new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(new Date());
        String lastChecked = sharedPreferences.getString("last_checked_month_year", "");
        if (!lastChecked.equals(bulanTahun)) {
            sharedPreferences.edit().remove("cached_transactions_bulanan").putString("last_checked_month_year", bulanTahun).apply();
        }

        // Ambil semua transaksi dari Firebase
        transaksiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                List<TransactionModel> transaksiBulanan = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    TransactionModel t = item.getValue(TransactionModel.class);
                    if (t != null && t.getTanggal() != null) {
                        try {
                            Date tanggal = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(t.getTanggal());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(tanggal);
                            String month = new SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(cal.getTime());
                            if (month.equals(bulanTahun)) {
                                transaksiBulanan.add(t);
                                total += t.getJumlah();  // Menambahkan jumlah transaksi ke total bulanan
                            }
                        } catch (Exception ignored) {}
                    }
                }
                // Update tampilan jumlah bulanan di UI
                jumlahBulanan.setText("Rp " + NumberFormat.getInstance(Locale.getDefault()).format(total));
                sharedPreferences.edit().putString("cached_transactions_bulanan", new Gson().toJson(transaksiBulanan)).apply(); // Simpan kembali data transaksi bulanan
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    @Override
    public void onTransaksiDitambahkan() {
        ambilDanTampilkanTransaksi();
        updateTransaksiHarian();
        updateTransaksiMingguan();
    }

    @Override
    public void onResume() {
        super.onResume();
        String username = sharedPreferences.getString("username", null);
        if (username != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);
            tampilkanDataProfil(userRef);
        }
    }
}
