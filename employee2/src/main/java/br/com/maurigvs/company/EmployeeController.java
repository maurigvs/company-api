package br.com.maurigvs.company;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import br.com.maurigvs.company.exception.BusinessException;
import br.com.maurigvs.company.model.EmployeeRequest;

import lombok.RequiredArgsConstructor;

@Path("/employee")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @POST
    public Response postEmployee(EmployeeRequest request) throws BusinessException {
        service.create(request.nome(), request.sobrenome(), request.email(),
                request.dataDeNascimento(), request.numeroCpf());
        return Response.status(Response.Status.CREATED).build();
    }
}