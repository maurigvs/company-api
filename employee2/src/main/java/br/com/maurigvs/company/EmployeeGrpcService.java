package br.com.maurigvs.company;

import java.util.NoSuchElementException;

import br.com.maurigvs.company.employee.EmployeeGrpcGrpc;
import br.com.maurigvs.company.employee.EmployeeReply;
import br.com.maurigvs.company.employee.FindRequest;
import br.com.maurigvs.company.exception.grpc.StatusInternalException;
import br.com.maurigvs.company.exception.grpc.StatusNotFoundException;
import br.com.maurigvs.company.model.Employee;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class EmployeeGrpcService extends EmployeeGrpcGrpc.EmployeeGrpcImplBase {

    private final EmployeeRepository repository;

    @Override
    public void findByEmailAddress(FindRequest request, StreamObserver<EmployeeReply> observer) {
        try {
            observer.onNext(process(request.getEmailAddress()));

        } catch (NoSuchElementException ex){
            observer.onError(new StatusNotFoundException("Employee not found by email address"));

        } catch (RuntimeException ex){
            observer.onError(new StatusInternalException(ex.getMessage()));
        }
        observer.onCompleted();
    }

    private EmployeeReply process(String emailAddress) {
        return repository.findByEmailAddress(emailAddress).stream()
                .map(this::mapEmployeeResponse)
                .findFirst()
                .orElseThrow();
    }

    private EmployeeReply mapEmployeeResponse(Employee employee) {
        return EmployeeReply.newBuilder()
                .setId(employee.getId())
                .setFullName(employee.getName() + " " + employee.getSurname())
                .setEmailAddress(employee.getEmailAddress())
                .build();
    }
}