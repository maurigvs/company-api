package br.com.maurigvs.company.employee.service;

import java.util.NoSuchElementException;

import br.com.maurigvs.company.employee.EmployeeGrpc;
import br.com.maurigvs.company.employee.EmployeeResponse;
import br.com.maurigvs.company.employee.FindRequest;
import br.com.maurigvs.company.employee.exception.rpc.DataNotFoundException;
import br.com.maurigvs.company.employee.exception.rpc.InternalServerException;
import br.com.maurigvs.company.employee.model.Employee;
import br.com.maurigvs.company.employee.repository.EmployeeRepository;

import io.grpc.stub.StreamObserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeRpcService extends EmployeeGrpc.EmployeeImplBase {

    private final EmployeeRepository repository;

    @Override
    public void findByEmailAddress(FindRequest request, StreamObserver<EmployeeResponse> observer) {
        try {
            observer.onNext(process(request.getEmailAddress()));

        } catch (NoSuchElementException ex){
            observer.onError(new DataNotFoundException("Employee not found by email address"));

        } catch (RuntimeException ex){
            observer.onError(new InternalServerException(ex.getMessage()));
        }
        observer.onCompleted();
    }

    private EmployeeResponse process(String emailAddress) {
        return repository.findByEmailAddress(emailAddress).stream()
                .map(this::mapEmployeeResponse)
                .findFirst()
                .orElseThrow();
    }

    private EmployeeResponse mapEmployeeResponse(Employee employee) {
        return EmployeeResponse.newBuilder()
                .setId(employee.getId())
                .setFullName(employee.getName() + " " + employee.getSurname())
                .setEmailAddress(employee.getEmailAddress())
                .build();
    }
}