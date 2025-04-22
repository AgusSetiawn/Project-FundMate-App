package com.fundmate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import android.graphics.Color;

import java.util.ArrayList;

public class GraphFragment extends Fragment {
    private BarChart barChart;
    private PieChart pieChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        barChart = view.findViewById(R.id.barChart);
        pieChart = view.findViewById(R.id.pieChart);

        if (barChart == null) {
            Log.e("GraphFragment", "BarChart not found");
        }
        if (pieChart == null) {
            Log.e("GraphFragment", "PieChart not found");
        }

        // Call the function to load dummy data
        loadDummyData();

        return view;
    }

    // Method to load dummy data into charts
    private void loadDummyData() {
        // Dummy data for BarChart
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 100f));
        barEntries.add(new BarEntry(1, 200f));
        barEntries.add(new BarEntry(2, 150f));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Pengeluaran Kategori");

        // Set different colors for each Bar
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FF6347")); // Tomato Red
        colors.add(Color.parseColor("#32CD32")); // Lime Green
        colors.add(Color.parseColor("#FFD700")); // Gold
        colors.add(Color.parseColor("#1E90FF")); // Dodger Blue

        barDataSet.setColors(colors); // Set the colors for the bars

        // Show value on top of each bar
        barDataSet.setDrawValues(true);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTextColor(Color.BLACK);

        // Create data for BarChart
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet);

        BarData barData = new BarData(dataSets);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false); // Disable description
        barChart.invalidate(); // Refresh the BarChart

        // Dummy data for PieChart
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(30f, "Shopping"));
        pieEntries.add(new PieEntry(40f, "Makanan"));
        pieEntries.add(new PieEntry(20f, "Hobi"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");

        // Set different colors for each PieEntry
        ArrayList<Integer> pieColors = new ArrayList<>();
        pieColors.add(Color.parseColor("#FF6347")); // Tomato Red
        pieColors.add(Color.parseColor("#32CD32")); // Lime Green
        pieColors.add(Color.parseColor("#FFD700")); // Gold
        pieColors.add(Color.parseColor("#1E90FF")); // Dodger Blue

        pieDataSet.setColors(pieColors); // Set the colors for each pie slice

        // Show percentage on each slice
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(Color.BLACK);

        // Set the label color to biru_sec (blue)
        pieChart.setEntryLabelColor(ContextCompat.getColor(getContext(), R.color.ireng)); // Set label color

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false); // Disable description
        pieChart.invalidate(); // Refresh the PieChart
    }
}
