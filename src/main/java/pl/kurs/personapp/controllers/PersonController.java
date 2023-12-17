package pl.kurs.personapp.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.kurs.personapp.dto.EmployeeDto;
import pl.kurs.personapp.dto.ImportStatusDto;
import pl.kurs.personapp.dto.ImportStatusSimpleDto;
import pl.kurs.personapp.dto.PersonDto;
import pl.kurs.personapp.models.*;
import pl.kurs.personapp.services.*;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private ModelMapper mapper;
    private PersonManagementService personManagementService;
    private PersonFactoryService personFactoryService;
    private EmployeePositionsService employeePositionsService;
    private CsvImportService csvImportService;
    private ImportStatusManagementService importStatusManagementService;
    private ImportStatusService importStatusService;

    public PersonController(ModelMapper mapper, PersonManagementService personManagementService,
                            PersonFactoryService personFactoryService, EmployeePositionsService employeePositionsService,
                            CsvImportService csvImportService, ImportStatusManagementService importStatusManagementService,
                            ImportStatusService importStatusService) {
        this.mapper = mapper;
        this.personManagementService = personManagementService;
        this.personFactoryService = personFactoryService;
        this.employeePositionsService = employeePositionsService;
        this.csvImportService = csvImportService;
        this.importStatusManagementService = importStatusManagementService;
        this.importStatusService = importStatusService;
    }

    @PostMapping
    public ResponseEntity<PersonDto> createPerson(@RequestBody JsonNode jsonNode) throws ParseException {
        IPersonFactory matchingFactory = personFactoryService.getFactory(jsonNode.get("personType").asText());
        Person newPerson = matchingFactory.createPerson(jsonNode);
        personManagementService.add(newPerson);
        PersonDto personDto = mapper.map(newPerson, matchingFactory.createPersonDto(newPerson));
        return ResponseEntity.status(HttpStatus.CREATED).body(personDto);
    }

    @GetMapping
    public ResponseEntity<Page<PersonDto>> getPeople(@RequestParam Map<String, String> parameters,
                                                     @PageableDefault Pageable pageable) {
        Page<Person> allPeople = personManagementService.getAllPeople(parameters, pageable);
        Page<PersonDto> allPeopleDtoPage = allPeople.map(x -> {
            IPersonFactory personFactory = personFactoryService.getFactory(x.getPersonType());
            return mapper.map(x, personFactory.createPersonDto(x));
        });
        return ResponseEntity.ok(allPeopleDtoPage);
    }

    @PutMapping
    public ResponseEntity<PersonDto> updatePerson(@RequestBody Map<String, String> personToUpdate) {
        Person personForUpdate = personManagementService.get(Long.parseLong(personToUpdate.get("id")));
        Person updatedPerson = mapper.map(personToUpdate, personForUpdate.getClass());
        IPersonFactory matchingFactory = personFactoryService.getFactory(personToUpdate.get("personType"));
        personManagementService.edit(updatedPerson);
        PersonDto personDto = mapper.map(updatedPerson, matchingFactory.createPersonDto(updatedPerson));
        return ResponseEntity.ok(personDto);
    }


    @PutMapping(value = "employee/{id}/position")
    public ResponseEntity<EmployeeDto> manageEmployeePositions(@PathVariable("id") Long id,
                                                               @RequestBody Map<String, String> positionUpdate) {
        Employee employee = (Employee) personManagementService.get(id);
        Position position = mapper.map(positionUpdate, Position.class);
        Employee editedEmployee = employeePositionsService.updatePositions(employee, position);
        personManagementService.edit(editedEmployee);
        System.out.println(position);
        EmployeeDto employeeDto = mapper.map(editedEmployee, EmployeeDto.class);
        return ResponseEntity.ok(employeeDto);
    }


    @PostMapping("/import")
    public ResponseEntity<ImportStatusSimpleDto> importCsv(@RequestParam("file") MultipartFile inputFile) {
        try {
            String currentImportStatusId = csvImportService.initiateCsvImport(inputFile);
            ImportStatusSimpleDto importStatusSimpleDto = new ImportStatusSimpleDto();
            importStatusSimpleDto.setCurrentImportId(currentImportStatusId);
            importStatusSimpleDto.setMessage("Import started");
            return ResponseEntity.ok(importStatusSimpleDto);
        } catch (IllegalStateException e) {
            ImportStatusSimpleDto importStatusSimpleDto = new ImportStatusSimpleDto();
            importStatusSimpleDto.setMessage("Import failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(importStatusSimpleDto);
        }
    }

    @GetMapping("import/progress/{id}")
    public ResponseEntity<ImportStatusDto> getCurrentImportState(@PathVariable("id") String id) {
        ImportStatus currentStatus = csvImportService.getImportStatusById(id);
        ImportStatusDto importStatusDto = mapper.map(currentStatus, ImportStatusDto.class);
        return ResponseEntity.ok(importStatusDto);
    }


    @GetMapping("import/history")
    public ResponseEntity<Page<ImportStatusDto>> getStatuses(@RequestParam Map<String, String> parameters,
                                                             @PageableDefault Pageable pageable) {
        Page<ImportStatus> importStatusesPage = importStatusManagementService.getAllStatuses(parameters, pageable);
        Page<ImportStatusDto> importStatusDtosPage = importStatusesPage.map(x -> mapper.map(x, ImportStatusDto.class));
        return ResponseEntity.ok(importStatusDtosPage);
    }

}



