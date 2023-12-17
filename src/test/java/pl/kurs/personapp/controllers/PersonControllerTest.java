package pl.kurs.personapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.personapp.models.Employee;
import pl.kurs.personapp.models.ImportStatus;
import pl.kurs.personapp.models.Pensioner;
import pl.kurs.personapp.models.Position;
import pl.kurs.personapp.services.*;
import java.sql.Date;
import java.time.Instant;
import java.util.LinkedHashSet;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PersonManagementService personManagementService;
    @Autowired
    private PersonFactoryService personFactoryService;
    @Autowired
    private EmployeePositionsService employeePositionsService;
    @Autowired
    private CsvImportService csvImportService;
    @Autowired
    private ImportStatusManagementService importStatusManagementService;


    @BeforeAll
    @Sql
    public void setUp() {
        Pensioner pensioner = new Pensioner("pensioner", "Jan", "Kowalski", "43070219710", 175, 85, "jkowalski@gmail.com", 3540, 50);
        Employee employee = new Employee("employee", "Marcin", "Wisniewski", "95032399691", 182, 80, "mwis@gmail.com", new LinkedHashSet<>(), Date.valueOf("2023-08-18"), "Data Analyst", 6500);
        Position position = new Position("Data Analyst", 6500, Date.valueOf("2023-08-18"), null);
        employee.getPositions().add(position);
        personManagementService.add(pensioner);
        personManagementService.add(employee);
        ImportStatus importStatus = new ImportStatus(ImportStatus.State.RUNNING, Instant.now(), Instant.now(), 10L);
        importStatusManagementService.add(importStatus);
    }


    @Test
    public void shouldCreateEmployee() throws Exception {
        String jsonRequest = "{\"personType\":\"employee\",\"firstName\":\"Wacław\",\"lastName\":\"Michalski\",\"pesel\"" +
                ":\"72042729218\",\"heightInCm\":180.0,\"weightInKg\":85,\"emailAddress\":\"wac.mich@gmail.com\",\"currentPosition\"" +
                ":\"JuniorPayrollAnalyst\",\"currentSalary\":3500.0,\"employmentStart\":\"2023-09-20\"}";

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.personType").value("employee"))
                .andExpect(jsonPath("$.positions", hasSize(greaterThan(0))));
    }

    @Test
    public void shouldFailedOnStudentCreation() throws Exception {
        String jsonRequest = "{\"universityName\":\"agh\",\"yearOfStudies\":5,\"studyField\":\"Mechanic\",\"scholarship\":95.40," +
                "\"personType\":\"stud\",\"firstName\":\"Jacek\",\"lastName\":\"Placek\",\"pesel\":\"00062275110\"," +
                "\"heightInCm\":168.24,\"weightInKg\":90.48,\"emailAddress\":\"car@gmail.com\"}";
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldGetPensioner() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/person")
                .param("personType", "pensioner")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(equalTo(1))));
    }

    @Test
    public void shouldReturnNoResultOnPensionerSearch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/person")
                .param("personType", "pensner")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(equalTo(0))));
    }


    @Test
    public void shouldUpdateEmployeePosition() throws Exception {
        Long employeeId = 2L;
        String updatedPosition = "{\"id\":1,\"name\":\"DataAnalyst\",\"salary\":6700.0,\"version\":0,\"startOfWork\":" +
                "\"2023-08-18\",\"endOfWork\":\"2023-10-31\"}";

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/person/employee/{id}/position", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedPosition))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.positions[0].endOfWork", notNullValue()))
                .andExpect(jsonPath("$.positions[0].salary").value(6700.0));
    }

    @Test
    public void shouldFailedOnEmployeePositionUpdate() throws Exception {
        Long employeeId = 100L;
        String updatedPosition = "{\"id\":1,\"name\":\"DataAnalyst\",\"salary\":6700.0,\"version\":0,\"startOfWork\":" +
                "\"2023-08-18\",\"endOfWork\":\"2023-10-31\"}";

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/person/employee/{id}/position", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedPosition))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldImportCsvSuccessfully() throws Exception {

        String csvContent = "pensioner,Lech,Pidarowski,66071041333,175,102,pidor@interia.pl,3600,55";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", MediaType.TEXT_PLAIN_VALUE, csvContent.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/person/import").file(file))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }



    @Test
    public void shouldUpdatePerson() throws Exception {
        String jsonRequest = "{\"id\":1,\"version\":0,\"personType\":\"pensioner\",\"firstName\":\"Jan\",\"lastName\":" +
                "\"Kowalski\",\"pesel\":\"43070219710\",\"heightInCm\":182.0,\"weightInKg\":88,\"emailAddress\":" +
                "\"jkowalski@gmail.com\",\"pensionAmount\":5520,\"workedYears\":65.0}";

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pensionAmount").value(5520))
                .andExpect(jsonPath("$.heightInCm").value(182));
    }


    @Test
    public void shouldFailedToUpdatePerson() throws Exception {
        String jsonRequest = "{\"id\":1,\"version\":0,\"personType\":\"pensioner\",\"firstName\":\"Jan\",\"lastName\":" +
                "\"Kowalski\",\"pesel\":\"43070219710\",\"heightInCm\":182.0,\"weightInKg\":88,\"emailAddress\":" +
                "\"jkowalski@gmail.com\",\"pensionAmount\":5520,\"workedYears\":65.0}";

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pensionAmount").value(5520))
                .andExpect(jsonPath("$.heightInCm").value(182));
    }

    @Test
    public void shouldGetImportStatusHistory() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/person/import/history")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(equalTo(1))));
    }

}




