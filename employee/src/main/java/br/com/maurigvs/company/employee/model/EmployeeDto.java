package br.com.maurigvs.company.employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class EmployeeDto implements Serializable {

    private String nome;
    private String sobrenome;
    private String email;
    private String dataDeNascimento;
    private String numeroCpf;
}