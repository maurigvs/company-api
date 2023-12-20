package br.com.maurigvs.company.employee.exception.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class StatusNotFoundException extends StatusRuntimeException {

    public StatusNotFoundException(String description) {
        super(Status.NOT_FOUND.withDescription(description));
    }
}