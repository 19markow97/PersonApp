package pl.kurs.personapp.dto;


public class ImportStatusSimpleDto {

    private String currentImportId;

    private String message;

    public String getCurrentImportId() {
        return currentImportId;
    }

    public void setCurrentImportId(String currentImportId) {
        this.currentImportId = currentImportId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
