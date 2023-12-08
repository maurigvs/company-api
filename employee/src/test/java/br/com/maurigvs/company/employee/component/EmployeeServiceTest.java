package br.com.maurigvs.company.employee.component;

import br.com.maurigvs.company.employee.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = {EmployeeService.class})
class EmployeeServiceTest {

    @Autowired
    EmployeeService employeeService;

    @MockBean
    EmployeeRepository employeeRepository;

    @Test
    void should_ReturnEmployee_when_Save() throws BusinessException {
        // given
        given(employeeRepository.save(any(Employee.class))).willReturn(mockEmployee());

        // when
        Employee result = employeeService.create(
                "Mauri Celio", "Goncalves Junior",
                "maurigvs@icloud.com", "04/06/1987");

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Mauri Celio");
        assertThat(result.getSurname()).isEqualTo("Goncalves Junior");
        assertThat(result.getEmailAddress()).isEqualTo("maurigvs@icloud.com");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1987,6,4));
        verify(employeeRepository).save(any(Employee.class));
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void should_ThrowBusinessException_when_BirthDateHasInvalidFormatt(){
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> employeeService.create(
                        "Mauri", "Goncalves",
                        "mauri@email.com", "4/6/87"))
                .withMessage("The birth date must be in the format: dd/MM/yyyy");
    }

    @Test
    void should_ThrowBusinessException_when_EmployeeIsUnderage(){
        // given
        var birthDate = LocalDate.now().minusYears(18).minusDays(-1)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // when... then
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> employeeService.create(
                        "Mauri", "Goncalves",
                        "mauri@email.com", birthDate))
                .withMessage("The employee must have more than 18 years of age");
    }

    private Employee mockEmployee() {
        return new Employee(1L,
                "Mauri Celio",
                "Goncalves Junior",
                "maurigvs@icloud.com",
                LocalDate.of(1987,6,4));
    }
}