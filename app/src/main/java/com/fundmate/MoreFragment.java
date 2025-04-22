package com.fundmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MoreFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imagePreview;
    private EditText editNama;
    private Button buttonPilih, buttonSimpan, buttonLogout;
    private String encodedImage = "";
    private SharedPreferences sharedPreferences;

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        imagePreview = view.findViewById(R.id.image_preview2);
        editNama = view.findViewById(R.id.edit_nama2);
        buttonPilih = view.findViewById(R.id.button_pilih_foto2);
        buttonSimpan = view.findViewById(R.id.button_simpan2);
        buttonLogout = view.findViewById(R.id.button_logout2);

        sharedPreferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username == null) {
            Toast.makeText(getContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Set up the Choose Image Button click listener
        buttonPilih.setOnClickListener(v -> openImagePicker());

        // Set up the Save Button click listener
        buttonSimpan.setOnClickListener(v -> saveProfile(username));

        // Set up the Logout Button click listener
        buttonLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imagePreview.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveProfile(String username) {
        String newName = editNama.getText().toString().trim();


        if (newName.isEmpty()) {
            Toast.makeText(getContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);
        userRef.child("name").setValue(newName);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cached_name", newName);

        if (!encodedImage.isEmpty()) {
            userRef.child("profileImageUrl").setValue(encodedImage);
            editor.putString("cached_image", encodedImage);

        }

        editor.apply();

        Toast.makeText(getContext(), "Profil berhasil disimpan", Toast.LENGTH_SHORT).show();
    }
    

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
