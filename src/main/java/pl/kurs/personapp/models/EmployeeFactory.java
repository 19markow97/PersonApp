package pl.kurs.personapp.models;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pl.kurs.personapp.dto.EmployeeDto;
import pl.kurs.personapp.dto.PersonDto;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class EmployeeFactory implements IPersonFactory {

    @Override
    public Person createPerson(JsonNode jsonCommand) throws ParseException {
        if (jsonCommand.get("personType").asText().equalsIgnoreCase("employee")) {
            Employee employee = new Employee();
            employee.setFirstName(jsonCommand.get("firstName").asText());
            employee.setLastName(jsonCommand.get("lastName").asText());
            employee.setWeightInKg(jsonCommand.get("weightInKg").asDouble());
            employee.setHeightInCm(jsonCommand.get("heightInCm").asDouble());
            employee.setEmailAddress(jsonCommand.get("emailAddress").asText());
            employee.setPesel(jsonCommand.get("pesel").asText());
            employee.setPersonType(jsonCommand.get("personType").asText());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            employee.setEmploymentStart(new Date(dateFormat.parse(jsonCommand.get("employmentStart").asText()).getTime()));
            employee.setCurrentPosition(jsonCommand.get("currentPosition").asText());
            employee.setCurrentSalary(jsonCommand.get("currentSalary").asDouble());
            Set<Position> positions = new LinkedHashSet<>();
            Position position = new Position();
            position.setName(jsonCommand.get("currentPosition").asText());
            position.setSalary(jsonCommand.get("currentSalary").asDouble());
            Date startOfWork = new Date(dateFormat.parse(jsonCommand.get("employmentStart").asText()).getTime());
            position.setStartOfWork(startOfWork);
            positions.add(position);
            employee.setPositions(positions);
            return employee;
        }
        return null;
    }

    @Override
    public String getSupportedType() {
        return "EMPLOYEE";
    }

    @Override
    public Class<? extends PersonDto> createPersonDto(Person person) {
        return EmployeeDto.class;
    }

    @Override
    public Person createPersonFromCsvRow(String[] csvRow) throws ParseException, NumberFormatException {
            Employee employee = new Employee();
            employee.setPersonType(csvRow[0]);
            employee.setFirstName(csvRow[1]);
            employee.setLastName(csvRow[2]);
            employee.setPesel(csvRow[3]);
            employee.setHeightInCm(Double.parseDouble(csvRow[4]));
            employee.setWeightInKg(Double.parseDouble(csvRow[5]));
            employee.setEmailAddress(csvRow[6]);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            employee.setEmploymentStart(new Date(dateFormat.parse(csvRow[7]).getTime()));
            employee.setCurrentPosition(csvRow[8]);
            employee.setCurrentSalary(Double.parseDouble(csvRow[9]));
            Set<Position> positions = new LinkedHashSet<>();
            Position position = new Position();
            position.setName(csvRow[8]);
            position.setSalary(Double.parseDouble(csvRow[9]));
            Date startOfWork = new Date(dateFormat.parse(csvRow[7]).getTime());
            position.setStartOfWork(startOfWork);
            positions.add(position);
            employee.setPositions(positions);
            return employee;

    }
}


