package br.com.maurigvs.company;

import br.com.maurigvs.company.model.EmployeeRequest;
import br.com.maurigvs.company.exception.BusinessException;
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
    public void postEmployee(@RequestBody EmployeeRequest request) throws BusinessException {
        service.create(request.nome(), request.sobrenome(), request.email(),
                request.dataDeNascimento(), request.numeroCpf());
    }
}