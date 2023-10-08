package pl.kurs.personapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.pl.PESEL;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "people")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DynamicUpdate
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Version
    private Long version;
    @Column(nullable = false)
    @NotBlank
    private String personType;
    @Column(nullable = false)
    @NotBlank
    private String firstName;
    @Column(nullable = false)
    @NotBlank
    private String lastName;
    @Column(unique = true, nullable = false)
    @PESEL
    private String pesel;
    @Column(nullable = false)
    @NotNull
    private double heightInCm;
    @Column(nullable = false)
    @NotNull
    private double weightInKg;
    @Column(nullable = false)
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email cannot be empty")
    private String emailAddress;

    public Person() {
    }

    public Person(@NotBlank String personType, @NotBlank String firstName, @NotBlank String lastName, String pesel, @NotNull double heightInCm, @NotNull double weightInKg, @Email String emailAddress) {
        this.personType = personType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pesel = pesel;
        this.heightInCm = heightInCm;
        this.weightInKg = weightInKg;
        this.emailAddress = emailAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public double getHeightInCm() {
        return heightInCm;
    }

    public void setHeightInCm(double height) {
        this.heightInCm = height;
    }

    public double getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(double weightInKg) {
        this.weightInKg = weightInKg;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Double.compare(person.heightInCm, heightInCm) == 0 && Double.compare(person.weightInKg, weightInKg) == 0 && Objects.equals(id, person.id) && Objects.equals(version, person.version) && Objects.equals(personType, person.personType) && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(pesel, person.pesel) && Objects.equals(emailAddress, person.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, personType, firstName, lastName, pesel, heightInCm, weightInKg, emailAddress);
    }

}
