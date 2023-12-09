package br.com.maurigvs.company.employee.component;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class EmployeeRequest implements Serializable {

    private String nome;
    private String sobrenome;
    private String email;
    private String dataDeNascimento;
    private String numeroCpf;
}