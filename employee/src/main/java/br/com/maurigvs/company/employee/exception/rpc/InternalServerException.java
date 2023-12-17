package br.com.maurigvs.company.employee.exception.rpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class InternalServerException extends StatusRuntimeException {

    public InternalServerException(String description) {
        super(Status.INTERNAL.withDescription(description));
    }
}