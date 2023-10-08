package pl.kurs.personapp.exceptionhandling.exceptions;

public class BadCsvImportException extends RuntimeException{


    public BadCsvImportException(String message) {
        super(message);
    }

    public BadCsvImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
