package br.com.maurigvs.company;

import static br.com.maurigvs.company.Utils.jsonStringOf;
import static br.com.maurigvs.company.Utils.messageResponseOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.maurigvs.company.exception.BusinessException;
import br.com.maurigvs.company.exception.ErrorResponse;
import br.com.maurigvs.company.model.EmployeeRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmployeeService employeeService;

    @Test
    void should_ReturnCreated_when_PostEmployee() throws Exception {
        // given
        final var requestAsJson = jsonStringOf(new EmployeeRequest("John", "Wayne",
                "john@wayne.com", "25/08/1963", "40360193099"));

        // when
        mockMvc.perform(
            post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson))
            .andExpect(status().isCreated());

        // then
        verify(employeeService, times(1))
            .create("John", "Wayne",
                "john@wayne.com",
                "25/08/1963",
                "40360193099");
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void should_ReturnBadRequest_when_BusinessExceptionIsThrown() throws Exception {
        // given
        final var messageExpected = "The birth date must be in the format: dd/MM/yyyy";

        final var requestAsJson = jsonStringOf(new EmployeeRequest("John", "Wayne",
                "john@wayne.com", "4/6/87", "40360193099"));

        final var responseAsJson = jsonStringOf(new ErrorResponse(
            HttpStatus.BAD_REQUEST.getReasonPhrase(), messageExpected));

        given(employeeService.create(anyString(), anyString(), anyString(), anyString(),
            anyString())).willThrow(new BusinessException(messageExpected));

        // when, then
        final var resultActions = mockMvc.perform(
            post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(responseAsJson));

        final var response = messageResponseOf(resultActions
            .andReturn().getResponse().getContentAsString());

        assertThat(response.error()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(response.message()).isEqualTo(messageExpected);
    }
}