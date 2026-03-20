package com.nexus.exception;

public class NexusValidationException extends RuntimeException {
    public static int totalValidationErrors = 0;
    // Escolhemos incrementar a contagem de erros aqui.
    public NexusValidationException(String message) {
        super(message);
        totalValidationErrors++;
    }
}