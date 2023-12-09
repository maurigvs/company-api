package br.com.maurigvs.company.employee.utils;

import br.com.maurigvs.company.employee.exception.MessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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

    public static MessageResponse messageResponseOf(String response){
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
