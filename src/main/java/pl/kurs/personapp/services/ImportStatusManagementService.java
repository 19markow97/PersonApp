package pl.kurs.personapp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kurs.personapp.exceptionhandling.exceptions.BadEntityException;
import pl.kurs.personapp.exceptionhandling.exceptions.BadIdException;
import pl.kurs.personapp.models.ImportStatus;
import pl.kurs.personapp.respositories.ImportStatusRepository;
import pl.kurs.personapp.respositories.specifications.ImportStatusSpecification;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class ImportStatusManagementService {

    private final ImportStatusRepository repository;
    private final ImportStatusSpecification importStatusSpecification;

    public ImportStatusManagementService(ImportStatusRepository repository, ImportStatusSpecification importStatusSpecification) {
        this.repository = repository;
        this.importStatusSpecification = importStatusSpecification;
    }

    public ImportStatus add(ImportStatus entity) {
        return repository.save(
                Optional.ofNullable(entity)
                        .filter(x -> Objects.isNull(x.getId()))
                        .orElseThrow(() -> new BadEntityException("Bad entity for persist")));
    }


    public Page<ImportStatus> getAllStatuses(Map<String, String> queryParams, Pageable pageable) {
        return repository.findAll(importStatusSpecification.getImportStatuses(queryParams), pageable);
    }


}
