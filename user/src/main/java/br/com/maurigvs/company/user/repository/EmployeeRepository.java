package br.com.maurigvs.company.user.repository;

import br.com.maurigvs.company.employee.EmployeeGrpc;
import br.com.maurigvs.company.employee.ExistsRequest;
import br.com.maurigvs.company.employee.ExistsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmployeeRepository {

    private final EmployeeGrpc.EmployeeBlockingStub repository;

    public ExistsResponse existsByEmailAddress(String emailAddress){
        return repository.existsByEmailAddress(
                ExistsRequest.newBuilder()
                    .setEmailAddress(emailAddress)
                    .build());
    }
}