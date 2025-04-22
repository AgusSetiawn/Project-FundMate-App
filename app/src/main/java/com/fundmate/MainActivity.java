package com.fundmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.fundmate.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initial fragment - HomeFragment
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        // Initial state: make sure the button is visible
        binding.tambahkan.setVisibility(View.VISIBLE);

        // Set the listener for bottom navigation item selection
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            // Handle fragment replacement
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
                binding.tambahkan.setVisibility(View.VISIBLE); // Show button
            } else if (itemId == R.id.graph) {
                // Pass the data to GraphFragment before replacing
                GraphFragment graphFragment = new GraphFragment();
                replaceFragment(graphFragment);
                binding.tambahkan.setVisibility(View.GONE); // Hide button
            } else if (itemId == R.id.feedback) {
                replaceFragment(new FragmentCalculator());
                binding.tambahkan.setVisibility(View.GONE); // Hide button
            } else if (itemId == R.id.more) {
                replaceFragment(new MoreFragment());
                binding.tambahkan.setVisibility(View.GONE); // Show button
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
