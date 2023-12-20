package br.com.maurigvs.company.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException ex) {
        Response.Status status = Response.Status.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(status.getReasonPhrase(), ex.getMessage());
        return Response.status(status).entity(error).build();
    }
}