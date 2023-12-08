package br.com.maurigvs.company.employee.component;

import br.com.maurigvs.company.employee.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee create(String name, String surname, String emailAddress, String birthDate)
            throws BusinessException {

        LocalDate birthLocalDate = parseStringDate(birthDate);

        if(birthLocalDate.isAfter(LocalDate.now().minusYears(18)))
            throw new BusinessException("The employee must have more than 18 years of age");

        return save(new Employee(null, name, surname, emailAddress, birthLocalDate));
    }

    private Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    private static LocalDate parseStringDate(String birthDate) throws BusinessException {
        var dateFormat = "dd/MM/yyyy";
        try{
            return LocalDate.from(DateTimeFormatter.ofPattern(dateFormat).parse(birthDate));
        } catch (DateTimeException exception){
            throw new BusinessException("The birth date must be in the format: " + dateFormat);
        }
    }
}