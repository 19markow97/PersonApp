package pl.kurs.personapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.sql.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee extends Person implements Serializable {

    @OneToMany(cascade = CascadeType.ALL)
//    @NotEmpty
    private Set<Position> positions = new LinkedHashSet<>();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date employmentStart;
    @NotEmpty
    private String currentPosition;
    @NotNull
    private double currentSalary;


    public Employee() {
    }

    public Employee(@NotBlank String personType, @NotBlank String firstName, @NotBlank String lastName, String pesel, @NotNull double heightInCm, @NotNull double weightInKg, @Email String emailAddress, @NotEmpty Set<Position> positions, Date employmentStart, String currentPosition, double currentSalary) {
        super(personType, firstName, lastName, pesel, heightInCm, weightInKg, emailAddress);
        this.positions = positions;
        this.employmentStart = employmentStart;
        this.currentPosition = currentPosition;
        this.currentSalary = currentSalary;
    }

    public Set<Position> getPositions() {
        return positions;
    }
    @JsonProperty("positions")
    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public Date getEmploymentStart() {
        return employmentStart;
    }

    public void setEmploymentStart(Date employmentStart) {
        this.employmentStart = employmentStart;
    }

    public String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public double getCurrentSalary() {
        return currentSalary;
    }

    public void setCurrentSalary(double currentSalary) {
        this.currentSalary = currentSalary;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Employee employee = (Employee) o;
        return Double.compare(employee.currentSalary, currentSalary) == 0 && Objects.equals(positions, employee.positions) && Objects.equals(employmentStart, employee.employmentStart) && Objects.equals(currentPosition, employee.currentPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), positions, employmentStart, currentPosition, currentSalary);
    }

    @Override
    public String toString() {
        return super.toString() +
                "positions=" + positions +
                ", employmentStart=" + employmentStart +
                ", currentPosition='" + currentPosition + '\'' +
                ", currentSalary=" + currentSalary +
                '}';
    }
}
