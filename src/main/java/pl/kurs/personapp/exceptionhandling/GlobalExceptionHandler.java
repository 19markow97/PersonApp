package pl.kurs.personapp.exceptionhandling;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.personapp.exceptionhandling.exceptions.BadCsvImportException;
import pl.kurs.personapp.exceptionhandling.exceptions.BadEntityException;
import pl.kurs.personapp.exceptionhandling.exceptions.BadPositionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
       ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
                List.of(e.getMessage()),
                "NOT FOUND",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseDto);
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<ExceptionResponseDto> handleIOException(IOException e) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
                List.of(e.getMessage()),
                "NOT FOUND",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseDto);
    }

    @ExceptionHandler({BadEntityException.class, IllegalArgumentException.class, StaleObjectStateException.class,
            BadPositionException.class, BadCsvImportException.class, NumberFormatException.class})
    public ResponseEntity<ExceptionResponseDto> handleBadEntityExceptionAndIllegalArgumentException(RuntimeException e) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
                List.of(e.getMessage()),
                "BAD REQUEST",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseDto);
    }

    @ExceptionHandler({ParseException.class, SQLIntegrityConstraintViolationException.class, IOException.class})
    public ResponseEntity<ExceptionResponseDto> handleParseException(Exception e) {
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
                List.of(e.getMessage()),
                "BAD REQUEST",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseDto);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorMessages =e.getFieldErrors()
                .stream()
                .map(fieldError -> "field: " + fieldError.getField() + " / rejected value: '"
                         + fieldError.getRejectedValue() + "' / message: "
                        + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(
                errorMessages,
                "BAD REQUEST",
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseDto);
    }

}
