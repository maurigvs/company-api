package br.com.maurigvs.company.user.utils;

import br.com.maurigvs.company.user.exception.ErrorMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Utils {

    public static String jsonStringOf(Object object) {
        try {
            final var om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ErrorMessageDto errorMessageOf(String response){
        try {
            final var om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            final var errorMessage = om.readValue(response, ErrorMessageDto.class);
            System.out.println(errorMessage);
            return errorMessage;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
