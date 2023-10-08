package pl.kurs.personapp.respositories.specifications;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import pl.kurs.personapp.models.ImportStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class ImportStatusSpecification {

    public Specification<ImportStatus> getImportStatuses(Map<String, String> queryParams) {
        return ((root, query, cb) -> {

            Expression<Long> durationTime = cb.diff(root.get("endTime").as(Long.class), root.get("startTime").as(Long.class));

            List<Predicate> predicates = new ArrayList<>();
            if (queryParams.containsKey("state")) {
                String stateValue = queryParams.get("state");
                ImportStatus.State stateEnum = ImportStatus.State.valueOf(stateValue.toUpperCase());
                predicates.add(cb.equal(root.get("state"), stateEnum));
            }
            if (queryParams.containsKey("startsAfter")) {
                String startTimeStr = queryParams.get("startsAfter");
                LocalDate startDate = LocalDate.parse(startTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Instant startTime = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
                predicates.add(cb.greaterThanOrEqualTo(root.get("startsAfter"), startTime));
            }
            if (queryParams.containsKey("startsBefore")) {
                String startTimeStr = queryParams.get("startsBefore");
                LocalDate startDate = LocalDate.parse(startTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Instant startTime = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
                predicates.add(cb.lessThanOrEqualTo(root.get("startsBefore"), startTime));
            }
            if (queryParams.containsKey("durationFrom")) {
                long duration = Long.parseLong(queryParams.get("durationFrom"));
                predicates.add(cb.greaterThanOrEqualTo(durationTime, duration));
            }
            if (queryParams.containsKey("durationTo")) {
                long duration = Long.parseLong(queryParams.get("durationTo"));
                predicates.add(cb.lessThanOrEqualTo(durationTime, duration));
            }
            if (queryParams.containsKey("processedRowsFrom")) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("processedRows"), queryParams.get("processedRowsFrom")));
            }
            if (queryParams.containsKey("processedRowsTo")) {
                predicates.add(cb.lessThanOrEqualTo(root.get("processedRows"), queryParams.get("processedRowsTo")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }


}
