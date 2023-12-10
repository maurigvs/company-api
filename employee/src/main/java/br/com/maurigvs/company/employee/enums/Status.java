package br.com.maurigvs.company.employee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {

    ACTIVE(1, "Ativo"),
    INACTIVE(2, "Inativo");

    private final Integer code;
    private final String label;
}