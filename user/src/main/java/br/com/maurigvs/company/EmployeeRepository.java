package br.com.maurigvs.company;

import java.util.Optional;

import br.com.maurigvs.company.employee.EmployeeGrpcGrpc;
import br.com.maurigvs.company.employee.EmployeeReply;
import br.com.maurigvs.company.employee.FindRequest;
import br.com.maurigvs.company.exception.TechnicalException;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmployeeRepository {

    private final EmployeeGrpcGrpc.EmployeeGrpcBlockingStub repository;

    public Optional<EmployeeReply> findByEmailAddress(String emailAddress) throws TechnicalException {
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
                throw new TechnicalException(ex.getStatus().getDescription(), ex.getCause());
            }
        }
    }
}