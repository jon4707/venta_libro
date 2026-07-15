package venta.libro.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VentaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleVentaNotFound(VentaNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        String message = ex.getMessage();

        if (message != null && message.contains("Stock insuficiente")) {
            body.put("status", 409);
            body.put("error", "Conflict");
            body.put("message", message);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
        }

        if (message != null && (message.contains("Error al obtener precio")
                || message.contains("Error al validar stock")
                || message.contains("Error al reducir stock"))) {
            body.put("status", 502);
            body.put("error", "Bad Gateway");
            body.put("message", "El microservicio de Inventario no está disponible: " + message);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
        }

        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", message != null ? message : "Error desconocido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
