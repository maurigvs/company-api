package br.com.maurigvs.company;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.com.maurigvs.company.enums.Status;
import br.com.maurigvs.company.exception.BusinessException;
import br.com.maurigvs.company.model.Employee;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;

    public Employee create(String name, String surname, String emailAddress,
                           String birthDateStr, String taxId)
            throws BusinessException {

        LocalDate birthDate = localDateOf(birthDateStr);

        if(employeeUnderage(birthDate))
            throw new BusinessException("The employee must have more than 18 years of age");

        if(employeeAlreadyExists(taxId))
            throw new BusinessException("The employee already exists");

        return save(new Employee(null, name, surname, emailAddress,
                birthDate, taxId, Status.ACTIVE));
    }

    private boolean employeeUnderage(LocalDate birthDate) {
        return birthDate.isAfter(LocalDate.now().minusYears(18));
    }

    private Employee save(Employee employee) {
        return repository.save(employee);
    }

    private boolean employeeAlreadyExists(String taxId) {
        return repository.existsByTaxId(taxId);
    }

    private static LocalDate localDateOf(String birthDate) throws BusinessException {
        final var dateFormat = "dd/MM/yyyy";
        try{
            return LocalDate.from(DateTimeFormatter.ofPattern(dateFormat).parse(birthDate));
        } catch (DateTimeException ex){
            throw new BusinessException("The birth date must be in the format: " + dateFormat);
        }
    }
}