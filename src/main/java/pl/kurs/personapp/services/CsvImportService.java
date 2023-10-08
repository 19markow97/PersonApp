package pl.kurs.personapp.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs.personapp.exceptionhandling.exceptions.BadCsvImportException;
import pl.kurs.personapp.models.IPersonFactory;
import pl.kurs.personapp.models.ImportStatus;
import pl.kurs.personapp.models.Person;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CsvImportService {

    private final PersonFactoryService personFactoryService;
    private final PersonManagementService personManagementService;
    private final ImportStatus importStatus;
    private final ImportStatusService importStatusService;
    private final ImportStatusManagementService importStatusManagementService;
    private final Lock importLock = new ReentrantLock();

    public CsvImportService(PersonFactoryService personFactoryService, PersonManagementService personManagementService,
                            ImportStatus importStatus, ImportStatusService importStatusService,
                            ImportStatusManagementService importStatusManagementService) {
        this.personFactoryService = personFactoryService;
        this.personManagementService = personManagementService;
        this.importStatus = importStatus;
        this.importStatusService = importStatusService;
        this.importStatusManagementService = importStatusManagementService;
    }

    public CompletableFuture<ImportStatus> importCsvData(MultipartFile file) {
        CompletableFuture<ImportStatus> future = new CompletableFuture<>();
        if (importLock.tryLock()) {
            ImportStatus currentImportStatus = new ImportStatus();
            currentImportStatus.setState(ImportStatus.State.RUNNING);
            currentImportStatus.setStartTime(Instant.now());
            currentImportStatus.setProcessedRows(0L);
            try {
                List<Person> people = performCsvImport(file, currentImportStatus);
                personManagementService.saveAll(people);
                currentImportStatus.setEndTime(Instant.now());
                currentImportStatus.setState(ImportStatus.State.COMPLETED);
                future.complete(importStatus);
                importStatusService.updateImportStatus(currentImportStatus);
            } catch (RuntimeException e) {
                currentImportStatus.setState(ImportStatus.State.FAILED);
                currentImportStatus.setEndTime(Instant.now());
                currentImportStatus.setProcessedRows(0L);
                importStatusService.updateImportStatus(currentImportStatus);
                future.completeExceptionally(e);
            } finally {
                importStatusManagementService.add(currentImportStatus);
                importLock.unlock();
            }
        } else {
            throw new IllegalStateException("Other import currently takes place");
        }
        return future;
    }

    private List<Person> performCsvImport(MultipartFile file, ImportStatus importStatus) {
        List<Person> people = new ArrayList<>();
        try (
                CSVParser parser = CSVFormat.DEFAULT.parse(new InputStreamReader(file.getInputStream()))) {
            for (CSVRecord record : parser) {
                String[] row = record.values();
                if (row.length > 0) {
                    String type = row[0];
                    IPersonFactory matchingFactory = personFactoryService.getFactory(type);
                    Person person = matchingFactory.createPersonFromCsvRow(row);
                    if (person != null) {
                        people.add(person);
                        importStatus.setProcessedRows(importStatus.getProcessedRows() + 1);
                    }
                }
            }
        } catch (IOException | ParseException | NumberFormatException e ) {
            throw new BadCsvImportException("Incorrect CSV file");
        }

        return people;
    }

}