package pl.kurs.personapp.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import pl.kurs.personapp.models.Employee;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "pl.kurs.personapp")
public class BeansConfig {


    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(Employee.class,Employee.class).implicitMappings();
        return modelMapper;
    }




}
