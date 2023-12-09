package br.com.maurigvs.company.employee.controller;

import br.com.maurigvs.company.employee.model.EmployeeDto;
import br.com.maurigvs.company.employee.service.EmployeeService;
import br.com.maurigvs.company.employee.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postEmployee(@RequestBody EmployeeDto request) throws BusinessException {
        service.create(request.getNome(), request.getSobrenome(), request.getEmail(),
                request.getDataDeNascimento(), request.getNumeroCpf());
    }
}