package br.com.maurigvs.company.user.repository;

import java.util.Optional;

import br.com.maurigvs.company.employee.EmployeeGrpc;
import br.com.maurigvs.company.employee.EmployeeResponse;
import br.com.maurigvs.company.employee.ExistsRequest;
import br.com.maurigvs.company.employee.ExistsResponse;
import br.com.maurigvs.company.employee.FindRequest;
import br.com.maurigvs.company.user.exception.TechnicalException;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeRepository {

    private final EmployeeGrpc.EmployeeBlockingStub repository;

    public ExistsResponse existsByEmailAddress(String emailAddress){
        return repository.existsByEmailAddress(
                ExistsRequest.newBuilder()
                    .setEmailAddress(emailAddress)
                    .build());
    }

    public Optional<EmployeeResponse> findByEmailAddress(String emailAddress) throws TechnicalException {
        try {
            final var request = FindRequest.newBuilder()
                    .setEmailAddress(emailAddress)
                    .build();
            return Optional.of(repository.findByEmailAddress(request));

        } catch (StatusRuntimeException ex){
            if(Status.NOT_FOUND.getCode().equals(ex.getStatus().getCode())){
                return Optional.empty();
            } else {
                log.error("Error on findByEmailAddress with [" + emailAddress + "]", ex.getCause());
                throw new TechnicalException(ex.getMessage(), ex.getCause());
            }
        }
    }
}