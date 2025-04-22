package com.fundmate;

import org.mindrot.jbcrypt.BCrypt;

public class HelperClass {

    String name, email, username, password, imageUrl;

    // Getter and Setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Konstruktor HelperClass yang menerima password
    public HelperClass(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = hashPassword(password);  // Meng-hash password sebelum disimpan
    }

    // Konstruktor tanpa parameter
    public HelperClass() {}

    // Fungsi untuk melakukan hashing terhadap password menggunakan BCrypt
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt()); // Melakukan hashing terhadap password
    }
}
