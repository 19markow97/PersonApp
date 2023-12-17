package pl.kurs.personapp.models;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "import_statuses")
@Component
public class ImportStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private State state;
    private Instant startTime;
    private Instant endTime;
    private Long processedRows;

    public ImportStatus() {
    }

    public ImportStatus(State state, Instant startTime, Instant endTime, Long processedRows) {
        this.state = state;
        this.startTime = startTime;
        this.endTime = endTime;
        this.processedRows = processedRows;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Long getProcessedRows() {
        return processedRows;
    }

    public void setProcessedRows(Long processedRows) {
        this.processedRows = processedRows;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportStatus that = (ImportStatus) o;
        return Objects.equals(id, that.id) && state == that.state && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(processedRows, that.processedRows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, startTime, endTime, processedRows);
    }

    public enum State {
        RUNNING,
        COMPLETED,
        FAILED
    }




}
