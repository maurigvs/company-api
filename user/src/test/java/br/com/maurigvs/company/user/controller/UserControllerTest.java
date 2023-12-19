package br.com.maurigvs.company.user.controller;

import br.com.maurigvs.company.user.exception.BusinessException;
import br.com.maurigvs.company.user.exception.ErrorResponse;
import br.com.maurigvs.company.user.exception.TechnicalException;
import br.com.maurigvs.company.user.model.UserRequest;
import br.com.maurigvs.company.user.model.UserResponse;
import br.com.maurigvs.company.user.service.UserService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static br.com.maurigvs.company.user.utils.Utils.errorMessageOf;
import static br.com.maurigvs.company.user.utils.Utils.jsonStringOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    void should_return_created_when_post_user() throws Exception {
        // given
        final var requestAsJson = jsonStringOf(new UserRequest("john@wayne.com"));

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
    void should_return_ok_when_get_user_by_login() throws Exception {
        // given
        final var user = new UserResponse("John Wayne", "john@wayne.com");
        final var responseAsJson = jsonStringOf(user);
        given(userService.getByLogin(anyString())).willReturn(user);

        // when
        final var result = mockMvc.perform(
                get("/user/john@wayne.com"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(responseAsJson));

        // then
        assertThat(result.andReturn().getResponse().getContentAsString()).isEqualTo(responseAsJson);
        verify(userService, times(1)).getByLogin("john@wayne.com");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void should_return_bad_request_when_business_exception_is_thrown() throws Exception {
        // given
        final var messageExpected = "The user is already registered";
        final var requestAsJson = jsonStringOf(new UserRequest("john@wayne.com"));
        final var responseAsJson = jsonStringOf(new ErrorResponse(
                HttpStatus.BAD_REQUEST.getReasonPhrase(), messageExpected));

        given(userService.create(anyString()))
                .willThrow(new BusinessException(messageExpected));

        // when
        final var resultActions = mockMvc.perform(
            post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(responseAsJson));

        // then
        final var response = errorMessageOf(resultActions
            .andReturn().getResponse().getContentAsString());

        assertThat(response.error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(response.message()).isEqualTo(messageExpected);

        verify(userService, times(1)).create("john@wayne.com");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void should_return_internal_server_error_when_technical_exception_is_thrown() throws Exception {
        // given
        final var messageExpected = "Connection refused: null";
        final var requestAsJson = jsonStringOf(new UserRequest("john@wayne.com"));
        final var responseAsJson = jsonStringOf(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), messageExpected));

        given(userService.create(anyString()))
                .willThrow(new TechnicalException("Connection refused", null));

        // when
        final var resultActions = mockMvc.perform(
                        post("/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestAsJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseAsJson));

        // then
        final var response = errorMessageOf(resultActions
                .andReturn().getResponse().getContentAsString());

        assertThat(response.error()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(response.message()).isEqualTo(messageExpected);

        verify(userService, times(1)).create("john@wayne.com");
        verifyNoMoreInteractions(userService);
    }
}