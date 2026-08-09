package com.example.AIpowered_Application.Exceptions;

import java.time.LocalDateTime;


public record ErrorResponse(

        LocalDateTime timestamp,
        int status,
        String error,
        String message

) {
}