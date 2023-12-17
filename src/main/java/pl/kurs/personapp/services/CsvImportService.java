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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class CsvImportService {

    private final PersonFactoryService personFactoryService;
    private final PersonManagementService personManagementService;
    private final ImportStatusManagementService importStatusManagementService;
    private static final Lock importLock = new ReentrantLock();
    private final Map<String, ImportStatus> activeImports = new ConcurrentHashMap<>();

    public CsvImportService(PersonFactoryService personFactoryService, PersonManagementService personManagementService,
                            ImportStatusManagementService importStatusManagementService) {
        this.personFactoryService = personFactoryService;
        this.personManagementService = personManagementService;
        this.importStatusManagementService = importStatusManagementService;
    }

    public String initiateCsvImport(MultipartFile file) {
        String currentImportId = UUID.randomUUID().toString();
        if (importLock.tryLock()) {
            CompletableFuture.runAsync(() -> {
                try {
                    ImportStatus currentImportStatus = new ImportStatus();
                    currentImportStatus.setState(ImportStatus.State.RUNNING);
                    currentImportStatus.setStartTime(Instant.now());
                    currentImportStatus.setProcessedRows(0L);
                    activeImports.put(currentImportId, currentImportStatus);

                    try {
                        List<Person> people = performCsvImport(file, currentImportStatus);
                        personManagementService.saveAll(people);
                        currentImportStatus.setEndTime(Instant.now());
                        currentImportStatus.setState(ImportStatus.State.COMPLETED);
                        importStatusManagementService.add(currentImportStatus);
                    } catch (RuntimeException e) {
                        currentImportStatus.setState(ImportStatus.State.FAILED);
                        currentImportStatus.setEndTime(Instant.now());
                        currentImportStatus.setProcessedRows(0L);
                        importStatusManagementService.add(currentImportStatus);
                    }
                } finally {
                    importLock.unlock();
                }
            });
        } else {
            throw new IllegalStateException("Other import currently takes place");
        }
        return currentImportId;
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
                    Thread.sleep(15000);
                    if (person != null) {
                        people.add(person);
                        importStatus.setProcessedRows(importStatus.getProcessedRows() + 1);
                    }
                }
            }
        } catch (IOException | ParseException | NumberFormatException | InterruptedException e) {
            throw new BadCsvImportException("Incorrect CSV file");
        }

        return people;
    }


    public ImportStatus getImportStatusById(String id) {
        return activeImports.get(id);
    }


}