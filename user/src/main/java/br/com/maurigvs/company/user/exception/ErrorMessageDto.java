package br.com.maurigvs.company.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ErrorMessageDto implements Serializable {

    private String error;
    private String message;
}