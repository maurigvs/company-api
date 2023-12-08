package br.com.maurigvs.company.employee.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageResponse implements Serializable {

    private String error;
    private String message;
}