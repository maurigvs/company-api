package br.com.maurigvs.company.employee.exception.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class StatusInternalException extends StatusRuntimeException {

    public StatusInternalException(String description) {
        super(Status.INTERNAL.withDescription(description));
    }
}