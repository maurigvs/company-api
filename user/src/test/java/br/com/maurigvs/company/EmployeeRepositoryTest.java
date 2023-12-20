package br.com.maurigvs.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import br.com.maurigvs.company.employee.EmployeeGrpcGrpc;
import br.com.maurigvs.company.employee.EmployeeReply;
import br.com.maurigvs.company.employee.FindRequest;
import br.com.maurigvs.company.exception.TechnicalException;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {EmployeeRepository.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository repository;

    @MockBean
    EmployeeGrpcGrpc.EmployeeGrpcBlockingStub stub;

    @Test
    void should_return_employee_data_when_find_by_email_address_success() throws TechnicalException {
        // given
        final var grpcResponse = EmployeeReply.newBuilder()
                .setId(1L)
                .setFullName("John Wayne")
                .setEmailAddress("john@wayne.com")
                .build();
        given(stub.findByEmailAddress(any(FindRequest.class))).willReturn(grpcResponse);

        // when
        final var response = repository.findByEmailAddress("john@wayne.com");

        // then
        assertThat(response).isPresent();
        assertThat(response.get().getId()).isNotZero();
        assertThat(response.get().getFullName()).isEqualTo("John Wayne");
        assertThat(response.get().getEmailAddress()).isEqualTo("john@wayne.com");
        verify(stub, times(1)).findByEmailAddress(any(FindRequest.class));
        verifyNoMoreInteractions(stub);
    }

    @Test
    void should_return_empty_when_find_by_email_address_not_found() throws TechnicalException {
        // given
        final var exception = new StatusRuntimeException(
                Status.NOT_FOUND.withDescription("Employee not found"));
        given(stub.findByEmailAddress(any(FindRequest.class))).willThrow(exception);

        // when
        final var response = repository.findByEmailAddress("john@wayne.com");

        // then
        assertThat(response).isEmpty();
        verify(stub, times(1)).findByEmailAddress(any(FindRequest.class));
        verifyNoMoreInteractions(stub);
    }

    @Test
    void should_throw_exception_when_find_by_email_address_fails() {
        // given
        final var exception = new StatusRuntimeException(
                Status.INTERNAL.withDescription("Internal server error"));
        given(stub.findByEmailAddress(any(FindRequest.class))).willThrow(exception);

        // when, then
        assertThatExceptionOfType(TechnicalException.class)
                .isThrownBy(() -> repository.findByEmailAddress("john@wayne.com"))
                .withMessage("Internal server error");
        verify(stub, times(1)).findByEmailAddress(any(FindRequest.class));
        verifyNoMoreInteractions(stub);
    }
}