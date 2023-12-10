package br.com.maurigvs.company.user.service;

import br.com.maurigvs.company.employee.ExistsResponse;
import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.model.User;
import br.com.maurigvs.company.user.repository.EmployeeRepository;
import br.com.maurigvs.company.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = {UserService.class})
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    EmployeeRepository employeeRepository;

    @Test
    void should_ReturnUser_when_SaveUser() throws BusinessException {
        // given
        given(userRepository.existsByLogin(anyString()))
            .willReturn(false);

        given(employeeRepository.existsByEmailAddress(anyString()))
            .willReturn(ExistsResponse.newBuilder().setExists(true).build());

        given(userRepository.save(any(User.class)))
            .willReturn(new User(1L, "john@wayne.com"));

        // when
        var result = userService.create("john@wayne.com");

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLogin()).isEqualTo("john@wayne.com");
        verify(userRepository, times(1)).existsByLogin(anyString());
        verify(employeeRepository, times(1)).existsByEmailAddress(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void should_ThrowBusinessException_when_LoginAlreadyExists(){
        // given
        given(userRepository.existsByLogin(anyString()))
            .willReturn(true);

        // when, then
        assertThatExceptionOfType(BusinessException.class)
            .isThrownBy(() -> userService.create("john@wayne.com"))
            .withMessage("The user is already registered");

        verify(userRepository, times(1)).existsByLogin("john@wayne.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void should_ThrowBusinessException_when_UserIsNotEmployee(){
        // given
        given(userRepository.existsByLogin(anyString()))
                .willReturn(false);

        given(employeeRepository.existsByEmailAddress(anyString()))
                .willReturn(ExistsResponse.newBuilder().setExists(false).build());

        // when, then
        assertThatExceptionOfType(BusinessException.class)
                .isThrownBy(() -> userService.create("john@wayne.com"))
                .withMessage("The user must be a employee registered");

        verify(userRepository, times(1)).existsByLogin("john@wayne.com");
        verify(employeeRepository, times(1)).existsByEmailAddress(anyString());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(employeeRepository);
    }
}