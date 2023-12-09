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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = {EmployeeService.class})
class EmployeeServiceTest {

    @Autowired
    EmployeeService employeeService;

    @MockBean
    EmployeeRepository employeeRepository;

    @Test
    void should_ReturnEmployee_when_SaveEmployee() throws BusinessException {
        // given
        given(employeeRepository.existsByTaxId(anyString()))
            .willReturn(false);

        given(employeeRepository.save(any(Employee.class)))
            .willReturn(new Employee(1L,
                "John",
                "Wayne",
                "john@wayne.com",
                LocalDate.of(1987,6,4),
                "40360193099"));

        // when
        var result = employeeService.create(
            "John",
            "Wayne",
            "john@wayne.com",
            "25/08/1963",
            "40360193099");

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getSurname()).isEqualTo("Wayne");
        assertThat(result.getEmailAddress()).isEqualTo("john@wayne.com");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1987,6,4));
        assertThat(result.getTaxId()).isEqualTo("40360193099");
        verify(employeeRepository, times(1)).existsByTaxId("40360193099");
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void should_ThrowBusinessException_when_BirthDateHasInvalidFormat(){
        assertThatExceptionOfType(BusinessException.class)
            .isThrownBy(() -> employeeService.create(
                "John",
                "Wayne",
                "john@wayne.com",
                "4/6/87",
                "40360193099"))
            .withMessage("The birth date must be in the format: dd/MM/yyyy");
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void should_ThrowBusinessException_when_EmployeeIsUnderage(){
        // given
        var birthDate = LocalDate.now().minusYears(18).plusDays(1)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // when, then
        assertThatExceptionOfType(BusinessException.class)
            .isThrownBy(() -> employeeService.create(
                "John",
                "Wayne",
                "john@wayne.com",
                birthDate,
                "40360193099"))
            .withMessage("The employee must have more than 18 years of age");

        verifyNoInteractions(employeeRepository);
    }

    @Test
    void should_ThrowBusinessException_when_EmployeeAlreadyExists(){
        // given
        given(employeeRepository.existsByTaxId(anyString())).willReturn(true);

        // when, then
        assertThatExceptionOfType(BusinessException.class)
            .isThrownBy(() -> employeeService.create(
                "John",
                "Wayne",
                "john@wayne.com",
                "28/07/1985",
                "40360193099"))
            .withMessage("The employee already exists");

        verify(employeeRepository, times(1)).existsByTaxId(anyString());
        verifyNoMoreInteractions(employeeRepository);
    }
}