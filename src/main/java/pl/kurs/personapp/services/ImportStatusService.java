package pl.kurs.personapp.services;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.kurs.personapp.models.ImportStatus;

@Service
public class ImportStatusService {

    private ImportStatus importStatus;


    public ImportStatusService(ImportStatus importStatus) {
        this.importStatus = importStatus;
    }

    @CachePut(cacheNames = "importStatusCurrent", key = "'importStatus'")
    public ImportStatus updateImportStatus(ImportStatus importStatus) {
        return importStatus;
    }
    @Cacheable(cacheNames = "importStatusCurrent", key = "'importStatus'")
    public ImportStatus getCurrentImportStatus() {
        return importStatus;
    }

}
