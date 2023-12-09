package br.com.maurigvs.company.employee.component;

import br.com.maurigvs.company.employee.exception.BusinessException;
import br.com.maurigvs.company.employee.exception.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        var request = new EmployeeRequest(
            "John",
            "Wayne",
            "john@wayne.com",
            "25/08/1963",
            "40360193099");
        var requestAsJson = jsonStringOf(request);

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
        var messageExpected = "The birth date must be in the format: dd/MM/yyyy";

        var requestAsJson = jsonStringOf(new EmployeeRequest(
            "John",
            "Wayne",
            "john@wayne.com",
            "4/6/87",
            "40360193099"));

        var responseAsJson = jsonStringOf(new MessageResponse(
            HttpStatus.BAD_REQUEST.getReasonPhrase(), messageExpected));

        given(employeeService.create(anyString(), anyString(), anyString(), anyString(),
            anyString())).willThrow(new BusinessException(messageExpected));

        // when... then
        var resultActions = mockMvc.perform(
            post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(responseAsJson));

        var response = parseToObject(resultActions
            .andReturn().getResponse().getContentAsString());

        assertThat(response.getError()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(response.getMessage()).isEqualTo(messageExpected);
    }

    public static String jsonStringOf(Object object) {
        try {
            var om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static MessageResponse parseToObject(String response){
        try {
            var om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            var errorMessage = om.readValue(response, MessageResponse.class);
            System.out.println(errorMessage);
            return errorMessage;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}