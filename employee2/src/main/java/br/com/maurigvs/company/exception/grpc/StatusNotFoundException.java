package br.com.maurigvs.company.exception.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class StatusNotFoundException extends StatusRuntimeException {

    public StatusNotFoundException(String message) {
        super(Status.NOT_FOUND.withDescription(message));
    }
}