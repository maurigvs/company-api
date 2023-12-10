package br.com.maurigvs.company.employee.utils;

import br.com.maurigvs.company.employee.exception.ErrorMessageDto;
import br.com.maurigvs.company.employee.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;

public class Utils {

    public static String jsonStringOf(Object object) {
        try {
            var om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ErrorMessageDto messageResponseOf(String response){
        try {
            var om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            var errorMessage = om.readValue(response, ErrorMessageDto.class);
            System.out.println(errorMessage);
            return errorMessage;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Employee mockEmployee() {
        return new Employee(1L,
                "John",
                "Wayne",
                "john@wayne.com",
                LocalDate.of(1985,5,23),
                "12345678912");
    }
}