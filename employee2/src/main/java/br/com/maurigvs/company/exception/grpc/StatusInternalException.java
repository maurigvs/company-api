package br.com.maurigvs.company.exception.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class StatusInternalException extends StatusRuntimeException {

    public StatusInternalException(String message) {
        super(Status.INTERNAL.withDescription(message));
    }
}