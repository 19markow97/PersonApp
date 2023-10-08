package pl.kurs.personapp.models;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import pl.kurs.personapp.dto.PensionerDto;
import pl.kurs.personapp.dto.PersonDto;
import java.text.ParseException;

@Component
public class PensionerFactory implements IPersonFactory {


    @Override
    public Person createPerson(JsonNode jsonCommand) {
        if (jsonCommand.get("personType").asText().equalsIgnoreCase("pensioner")) {
            Pensioner pensioner = new Pensioner();
            pensioner.setFirstName(jsonCommand.get("firstName").asText());
            pensioner.setLastName(jsonCommand.get("lastName").asText());
            pensioner.setWeightInKg(jsonCommand.get("weightInKg").asDouble());
            pensioner.setHeightInCm(jsonCommand.get("heightInCm").asDouble());
            pensioner.setPesel(jsonCommand.get("pesel").asText());
            pensioner.setEmailAddress(jsonCommand.get("emailAddress").asText());
            pensioner.setPersonType(jsonCommand.get("personType").asText());
            pensioner.setPensionAmount(jsonCommand.get("pensionAmount").asDouble());
            pensioner.setWorkedYears(jsonCommand.get("workedYears").asDouble());
            return pensioner;
        }
        return null;
    }


    @Override
    public String getSupportedType() {
        return "PENSIONER";
    }

    @Override
    public Class<? extends PersonDto> createPersonDto(Person person) {
        return PensionerDto.class;
    }

    @Override
    public Person createPersonFromCsvRow(String[] csvRow) {
            Pensioner pensioner = new Pensioner();
            pensioner.setPersonType(csvRow[0]);
            pensioner.setFirstName(csvRow[1]);
            pensioner.setLastName(csvRow[2]);
            pensioner.setPesel(csvRow[3]);
            pensioner.setHeightInCm(Double.parseDouble(csvRow[4]));
            pensioner.setWeightInKg(Double.parseDouble(csvRow[5]));
            pensioner.setEmailAddress(csvRow[6]);
            pensioner.setPensionAmount(Double.parseDouble(csvRow[7]));
            pensioner.setWorkedYears(Double.parseDouble(csvRow[8]));
            return pensioner;

    }
}
