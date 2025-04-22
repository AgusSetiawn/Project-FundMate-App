// TransactionAdapter.java - FIXED FINAL
package com.fundmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final Context context;
    private final List<TransactionModel> transaksiList;

    public TransactionAdapter(Context context, List<TransactionModel> transaksiList) {
        this.context = context;
        this.transaksiList = transaksiList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionModel transaksi = transaksiList.get(position);

        // Pastikan kategori dan icon dipasang dengan benar
        String category = transaksi.getKategori();
        int iconResId = 0;

        // Menentukan icon berdasarkan kategori
        switch (category) {
            case "Shopping":
                iconResId = context.getResources().getIdentifier("basket", "drawable", context.getPackageName());
                break;
            case "Makanan":
                iconResId = context.getResources().getIdentifier("hamburger_soda", "drawable", context.getPackageName());
                break;
            case "Hobi":
                iconResId = context.getResources().getIdentifier("trophy", "drawable", context.getPackageName());
                break;
            default:
                iconResId = context.getResources().getIdentifier("basket", "drawable", context.getPackageName()); // Default icon
        }

        // Set icon dan kategori di RecyclerView
        holder.icon.setImageResource(iconResId);
        holder.nama.setText(transaksi.getKategori());
        holder.tanggal.setText(transaksi.getTanggal() + " " + transaksi.getWaktu());

        String formattedAmount = formatCurrencyWithDots(transaksi.getJumlah());
        holder.pengeluaran.setText("- Rp " + formattedAmount);
    }



    // Function to format currency with dots
    private String formatCurrencyWithDots(int amount) {
        return String.format(Locale.getDefault(), "%,d", amount).replace(',', '.');
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView nama, tanggal, pengeluaran;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.list_icon);
            nama = itemView.findViewById(R.id.list_nama);
            tanggal = itemView.findViewById(R.id.list_tanggal);
            pengeluaran = itemView.findViewById(R.id.list_pengeluaran);
        }
    }
}
