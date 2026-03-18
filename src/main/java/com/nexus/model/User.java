package com.nexus.model;

public class User {
    private final String username;
    private final String email;

    public User(String username, String email) {
        username = username.trim();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio.");
        }
        email = email.trim().toLowerCase();
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio.");
        }
        String emailPattern = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]+(\\.[a-z]+)*";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("Email inválido.");
        }
        this.username = username;
        this.email = email;
    }

    public String consultEmail() {
        return email;
    }

    public String consultUsername() {
        return username;
    }

    public long calculateWorkload() {
        return 0; 
    }
}