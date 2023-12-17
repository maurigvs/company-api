package br.com.maurigvs.company.employee.exception.rpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class DataNotFoundException extends StatusRuntimeException {

    public DataNotFoundException(String description) {
        super(Status.NOT_FOUND.withDescription(description));
    }
}