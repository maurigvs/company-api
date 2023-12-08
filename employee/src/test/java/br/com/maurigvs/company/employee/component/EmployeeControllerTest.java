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
import static org.mockito.ArgumentMatchers.any;
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
        var requestAsJson = parseToJson(mockRequest());

        // when
        mockMvc.perform(post("/employee")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestAsJson))
                .andExpect(status().isCreated());

        // then
        verify(employeeService, times(1)).create(
                "Mauri Celio", "Goncalves Junior",
                "maurigvs@icloud.com", "04/06/1987");
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    void should_ReturnBadRequest_when_BusinessExceptionIsThrown() throws Exception {
        // given
        var messageExpected = "The birth date must be in the format: dd/MM/yyyy";

        var requestAsJson = parseToJson(new EmployeeRequest(
                "Mauri Celio","Goncalves Junior",
                "maurigvs@icloud.com", "4/6/87"));

        var responseAsJson = parseToJson(new MessageResponse(
                HttpStatus.BAD_REQUEST.getReasonPhrase(), messageExpected));

        given(employeeService.create(any(), any(), any(), any())).willThrow(
                new BusinessException(messageExpected));

        // when... then
        var resultActions = mockMvc.perform(post("/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAsJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseAsJson));

        MessageResponse response = parseToObject(
                resultActions.andReturn().getResponse().getContentAsString());

        assertThat(response.getError()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(response.getMessage()).isEqualTo(messageExpected);
    }

    private EmployeeRequest mockRequest() {
        return new EmployeeRequest(
                "Mauri Celio", "Goncalves Junior",
                "maurigvs@icloud.com", "04/06/1987");
    }

    public static String parseToJson(Object object) {
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