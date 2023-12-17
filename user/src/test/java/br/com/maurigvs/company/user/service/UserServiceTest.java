package br.com.maurigvs.company.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Optional;

import br.com.maurigvs.company.employee.EmployeeResponse;
import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.exception.TechnicalException;
import br.com.maurigvs.company.user.model.User;
import br.com.maurigvs.company.user.model.UserResponse;
import br.com.maurigvs.company.user.repository.EmployeeRepository;
import br.com.maurigvs.company.user.repository.UserRepository;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {UserService.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserServiceTest {

    @Autowired
    UserService service;

    @MockBean
    UserRepository userRepository;

    @MockBean
    EmployeeRepository employeeRepository;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    void should_create_user_successfully() throws BusinessException, TechnicalException {
        // given
        var employeeResponse = EmployeeResponse.newBuilder().setId(2L).build();

        given(userRepository.existsByLogin(anyString()))
                .willReturn(false);

        given(employeeRepository.findByEmailAddress(anyString()))
                .willReturn(Optional.of(employeeResponse));

        given(userRepository.save(any(User.class)))
                .willReturn(new User(1L, "john@wayne.com", 2L));

        // when
        var result = service.create("john@wayne.com");

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLogin()).isEqualTo("john@wayne.com");
        verify(userRepository, times(1)).existsByLogin(anyString());
        verify(employeeRepository, times(1)).findByEmailAddress(anyString());
        verify(userRepository, times(1)).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmployeeId()).isEqualTo(employeeResponse.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void should_throw_business_exception_when_login_already_exists(){
        // given
        given(userRepository.existsByLogin(anyString()))
            .willReturn(true);

        // when, then
        assertThatExceptionOfType(BusinessException.class)
            .isThrownBy(() -> service.create("john@wayne.com"))
            .withMessage("The user is already registered");

        verify(userRepository, times(1)).existsByLogin("john@wayne.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void should_throw_business_exception_when_user_is_not_employee() throws TechnicalException {
        // given
        given(userRepository.existsByLogin(anyString()))
                .willReturn(false);

        given(employeeRepository.findByEmailAddress(anyString()))
                .willReturn(Optional.empty());

        // when, then
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.create("john@wayne.com"))
                .withMessage("The user must be a employee registered");

        verify(userRepository, times(1)).existsByLogin("john@wayne.com");
        verify(employeeRepository, times(1)).findByEmailAddress(anyString());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void should_throw_technical_exception_when_employee_verification_fails() throws TechnicalException {
        // given
        given(userRepository.existsByLogin(anyString()))
                .willReturn(false);

        given(employeeRepository.findByEmailAddress(anyString()))
                .willThrow(new TechnicalException("Server error message", null));

        // when, then
        assertThatExceptionOfType(TechnicalException.class)
                .isThrownBy(() -> service.create("john@wayne.com"))
                .withMessage("Server error message");

        verify(userRepository, times(1)).existsByLogin("john@wayne.com");
        verify(employeeRepository, times(1)).findByEmailAddress(anyString());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void should_return_user_when_get_by_login() throws TechnicalException, BusinessException {
        // given
        var login = "john@wayne.com";
        var user = Optional.of(new User(1L, "john@wayne.com", 2L));
        var employee = Optional.of(EmployeeResponse.newBuilder().setFullName("John Wayne").build());
        var expected = new UserResponse("John Wayne", "john@wayne.com");

        given(userRepository.findByLogin(anyString())).willReturn(user);
        given(employeeRepository.findByEmailAddress(anyString())).willReturn(employee);

        // when
        var result = service.getByLogin(login);

        // then
        assertThat(result).isEqualTo(expected);
        verify(userRepository, times(1)).findByLogin(login);
        verify(employeeRepository, times(1)).findByEmailAddress(login);
        verifyNoMoreInteractions(userRepository, employeeRepository);
    }

    @Test
    void should_throw_business_exception_when_user_not_found_by_login() {
        // given
        var login = "john@wayne.com";
        given(userRepository.findByLogin(anyString())).willReturn(Optional.empty());

        // when, then
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.getByLogin(login))
                .withMessage("User not found");

        verify(userRepository, times(1)).findByLogin(login);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void should_throw_business_exception_when_employee_find_fails() throws TechnicalException {
        // given
        var login = "john@wayne.com";
        var user = Optional.of(new User(1L, "john@wayne.com", 2L));

        given(userRepository.findByLogin(anyString())).willReturn(user);
        given(employeeRepository.findByEmailAddress(anyString())).willReturn(Optional.empty());

        // when, then
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> service.getByLogin(login))
                .withMessage("User's information is missing");

        verify(userRepository, times(1)).findByLogin(login);
        verify(employeeRepository, times(1)).findByEmailAddress(login);
        verifyNoMoreInteractions(userRepository, employeeRepository);
    }

    @Test
    void should_throw_technical_exception_when_employee_find_fails() throws TechnicalException {
        // given
        var login = "john@wayne.com";

        given(userRepository.findByLogin(anyString()))
                .willReturn(Optional.of(new User(1L, "john@wayne.com", 2L)));

        given(employeeRepository.findByEmailAddress(anyString()))
                .willThrow(new TechnicalException("Server error", null));

        // when, then
        assertThatExceptionOfType(TechnicalException.class)
                .isThrownBy(() -> service.getByLogin(login))
                .withMessage("Server error");

        verify(userRepository, times(1)).findByLogin(login);
        verify(employeeRepository, times(1)).findByEmailAddress(login);
        verifyNoMoreInteractions(userRepository, employeeRepository);
    }
}