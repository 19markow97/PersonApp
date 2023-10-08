package pl.kurs.personapp.models;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Component;
import pl.kurs.personapp.dto.PersonDto;
import pl.kurs.personapp.dto.StudentDto;


@Component
public class StudentFactory implements IPersonFactory {

    @Override
    public Person createPerson(JsonNode jsonCommand) {
        if (jsonCommand.get("personType").asText().equalsIgnoreCase("student")) {
            Student student = new Student();
            student.setFirstName(jsonCommand.get("firstName").asText());
            student.setLastName(jsonCommand.get("lastName").asText());
            student.setWeightInKg(jsonCommand.get("weightInKg").asDouble());
            student.setHeightInCm(jsonCommand.get("heightInCm").asDouble());
            student.setPesel(jsonCommand.get("pesel").asText());
            student.setEmailAddress(jsonCommand.get("emailAddress").asText());
            student.setPersonType(jsonCommand.get("personType").asText());
            student.setStudyField(jsonCommand.get("studyField").asText());
            student.setUniversityName(jsonCommand.get("universityName").asText());
            student.setScholarship(jsonCommand.get("scholarship").asDouble());
            student.setYearOfStudies(jsonCommand.get("yearOfStudies").asDouble());
            return student;
        }
        return null;
    }

    @Override
    public String getSupportedType() {
        return "student";
    }

    @Override
    public Class<? extends PersonDto> createPersonDto(Person person) {
        return StudentDto.class;
    }

    @Override
    public Person createPersonFromCsvRow(String[] csvRow) {
            Student student = new Student();
            student.setPersonType(csvRow[0]);
            student.setFirstName(csvRow[1]);
            student.setLastName(csvRow[2]);
            student.setPesel(csvRow[3]);
            student.setHeightInCm(Double.parseDouble(csvRow[4]));
            student.setWeightInKg(Double.parseDouble(csvRow[5]));
            student.setEmailAddress(csvRow[6]);
            student.setUniversityName(csvRow[7]);
            student.setYearOfStudies(Double.parseDouble(csvRow[8]));
            student.setStudyField(csvRow[9]);
            student.setScholarship(Double.parseDouble(csvRow[10]));
            return student;

    }

}
