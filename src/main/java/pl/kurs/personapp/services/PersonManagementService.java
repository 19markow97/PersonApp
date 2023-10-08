package pl.kurs.personapp.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kurs.personapp.models.Person;
import pl.kurs.personapp.respositories.PersonRepository;
import java.util.Map;

@Service
public class PersonManagementService extends AbstractGenericManagementService<Person, PersonRepository> {

    private PersonSpecificationService personSpecificationService;


    public PersonManagementService(PersonRepository repository, PersonSpecificationService personSpecificationService) {
        super(repository);
        this.personSpecificationService = personSpecificationService;
    }


    public Page<Person> getAllPeople(Map<String,String> parameters, Pageable pageable) {
        return repository.findAll(personSpecificationService.getCombinedSpecification(parameters),pageable);

    }

}

