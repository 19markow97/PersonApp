package pl.kurs.personapp.respositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pl.kurs.personapp.models.ImportStatus;

@Repository
public interface ImportStatusRepository extends JpaRepository<ImportStatus, Long>, JpaSpecificationExecutor<ImportStatus>,
        PagingAndSortingRepository<ImportStatus, Long> {
}
