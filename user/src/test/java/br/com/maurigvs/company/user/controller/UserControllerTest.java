package br.com.maurigvs.company.user.controller;

import br.com.maurigvs.company.user.controller.UserController;
import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.exception.ErrorMessageDto;
import br.com.maurigvs.company.user.model.UserDto;
import br.com.maurigvs.company.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.maurigvs.company.user.utils.Utils.jsonStringOf;
import static br.com.maurigvs.company.user.utils.Utils.messageResponseOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    void should_ReturnCreated_when_PostUser() throws Exception {
        // given
        var requestAsJson = jsonStringOf(new UserDto("john@wayne.com"));

        // when
        mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson))
            .andExpect(status().isCreated());

        // then
        verify(userService, times(1)).create("john@wayne.com");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void should_ReturnBadRequest_when_BusinessExceptionIsThrown() throws Exception {
        // given
        var messageExpected = "The user is already registered";

        var requestAsJson = jsonStringOf(
            new UserDto("john@wayne.com"));

        var responseAsJson = jsonStringOf(
            new ErrorMessageDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), messageExpected));

        given(userService.create(anyString()))
            .willThrow(new BusinessException(messageExpected));

        // when
        var resultActions = mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(responseAsJson));

        // then
        var response = messageResponseOf(resultActions
            .andReturn().getResponse().getContentAsString());

        assertThat(response.getError()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(response.getMessage()).isEqualTo(messageExpected);
    }
}