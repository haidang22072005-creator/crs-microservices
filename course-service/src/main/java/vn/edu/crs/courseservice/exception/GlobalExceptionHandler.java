package vn.edu.crs.courseservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    // =========================================================
    // 404 - Không tìm thấy dữ liệu
    // =========================================================
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>>
    handleNotFound(NoSuchElementException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "message",
                                ex.getMessage()
                        )
                );
    }


    // =========================================================
    // 400 - Dữ liệu nghiệp vụ không hợp lệ
    // Ví dụ: tên môn học đã tồn tại
    // =========================================================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>>
    handleBadRequest(IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of(
                                "message",
                                ex.getMessage()
                        )
                );
    }


    // =========================================================
    // BUỔI 3:
    // 409 - Conflict
    // Ví dụ môn học hết chỗ
    // =========================================================
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>>
    handleConflict(IllegalStateException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of(
                                "message",
                                ex.getMessage()
                        )
                );
    }


    // =========================================================
    // 400 - Validation lỗi
    // =========================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>>
    handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(
                        fieldError ->
                                errors.put(
                                        fieldError.getField(),
                                        fieldError.getDefaultMessage()
                                )
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }
}