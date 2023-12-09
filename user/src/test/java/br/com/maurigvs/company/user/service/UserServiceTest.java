package br.com.maurigvs.company.user.service;

import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.model.User;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = {UserService.class})
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Test
    void should_ReturnUser_when_SaveUser() throws BusinessException {
        // given
        given(userRepository.existsUserByLogin(anyString()))
            .willReturn(false);

        given(userRepository.save(any(User.class)))
            .willReturn(new User(1L, "john@wayne.com"));

        // when
        var result = userService.create("john@wayne.com");

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLogin()).isEqualTo("john@wayne.com");
        verify(userRepository, times(1)).existsUserByLogin(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void should_ThrowBusinessException_when_LoginAlreadyExists(){
        // given
        given(userRepository.existsUserByLogin(anyString()))
            .willReturn(true);

        // when, then
        assertThatExceptionOfType(BusinessException.class)
            .isThrownBy(() -> userService.create("john@wayne.com"))
            .withMessage("The user is already registered");

        verify(userRepository, times(1)).existsUserByLogin("john@wayne.com");
        verifyNoMoreInteractions(userRepository);
    }
}