package br.com.maurigvs.company.user.component;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    void should_ReturnUser_when_SaveUser(){
        // given
        var user = new User(1L, "john@wayne.com");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        var result = userService.create("john@wayne.com");

        // then
        assertThat(result.getLogin()).isEqualTo("john@wayne.com");
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }
}