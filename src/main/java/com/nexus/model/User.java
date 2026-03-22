package com.nexus.model;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio.");
        }
        email = email.trim().toLowerCase();
        String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]+(\\.[a-z]+)*";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("Email inválido.");
        }
        this.username = username.trim();
        this.email = email;
    }

    // Getters
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public long getWorkload() { return 0; } // IMPLEMENTAR!!
}