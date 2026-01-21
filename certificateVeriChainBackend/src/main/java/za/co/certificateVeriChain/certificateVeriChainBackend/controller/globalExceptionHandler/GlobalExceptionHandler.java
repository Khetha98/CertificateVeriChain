package za.co.certificateVeriChain.certificateVeriChainBackend.controller.globalExceptionHandler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch all unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        ex.printStackTrace(); // logs the error

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "error", "Internal Server Error",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        // ex.getStatusCode() returns HttpStatusCode
        int statusCode = ex.getStatusCode().value(); // numeric value
        HttpStatus status = HttpStatus.resolve(statusCode); // can be null if unknown

        return ResponseEntity.status(statusCode).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", statusCode,
                "error", status != null ? status.getReasonPhrase() : "Error",
                "message", ex.getReason() // optional, may be null
        ));
    }


    // Handle Access Denied (Spring Security)
    @ExceptionHandler({org.springframework.security.access.AccessDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleAccessDenied(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 403,
                "error", "Forbidden",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(io.jsonwebtoken.ExpiredJwtException.class)
    public ResponseEntity<Map<String, Object>> handleExpiredJwt(io.jsonwebtoken.ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 401,
                "error", "Unauthorized",
                "message", "JWT has expired, please log in again"
        ));
    }

}
