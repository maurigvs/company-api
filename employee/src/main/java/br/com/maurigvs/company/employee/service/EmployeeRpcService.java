package br.com.maurigvs.company.employee.service;

import br.com.maurigvs.company.employee.EmployeeData;
import br.com.maurigvs.company.employee.EmployeeGrpc;
import br.com.maurigvs.company.employee.ExistsRequest;
import br.com.maurigvs.company.employee.ExistsResponse;
import br.com.maurigvs.company.employee.model.Employee;
import br.com.maurigvs.company.employee.repository.EmployeeRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeRpcService extends EmployeeGrpc.EmployeeImplBase {

    private final EmployeeRepository repository;

    @Override
    public void existsByEmailAddress(ExistsRequest request,
                                     StreamObserver<ExistsResponse> observer) {
        observer.onNext(process(request));
        observer.onCompleted();
    }

    private ExistsResponse process(ExistsRequest request) {
        return repository.findByEmailAddress(request.getEmailAddress())
                .map(this::mapExistsResponse)
                .orElseGet(() -> ExistsResponse.newBuilder().setExists(false).build());
    }

    private ExistsResponse mapExistsResponse(Employee e) {
        var employeeData = EmployeeData.newBuilder()
                .setId(e.getId())
                .setName(e.getName())
                .setSurname(e.getSurname())
                .setEmailAddress(e.getEmailAddress())
                .setBirthDate(e.getBirthDate().toString())
                .setTaxId(e.getTaxId())
                .build();
        return ExistsResponse.newBuilder()
                .setExists(true)
                .setEmployee(employeeData)
                .build();
    }
}