package br.com.maurigvs.company.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import br.com.maurigvs.company.employee.EmployeeData;
import br.com.maurigvs.company.employee.EmployeeGrpc;
import br.com.maurigvs.company.employee.EmployeeResponse;
import br.com.maurigvs.company.employee.ExistsRequest;
import br.com.maurigvs.company.employee.ExistsResponse;
import br.com.maurigvs.company.employee.FindRequest;
import br.com.maurigvs.company.user.exception.TechnicalException;

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
    EmployeeGrpc.EmployeeBlockingStub stub;

    @Test
    void should_ReturnExistsFalse_when_CallExistsByEmailAddress() {
        // given
        given(stub.existsByEmailAddress(any(ExistsRequest.class)))
            .willReturn(ExistsResponse.newBuilder().setExists(false).build());
        // when
        ExistsResponse result = repository.existsByEmailAddress("john@wayne.com");
        // then
        assertThat(result.getExists()).isFalse();
        assertThat(result.getEmployee()).isEqualTo(EmployeeData.getDefaultInstance());
        verify(stub, times(1)).existsByEmailAddress(any(ExistsRequest.class));
        verifyNoMoreInteractions(stub);
    }

    @Test
    void should_ReturnExistsTrue_when_CallExistsByEmailAddress() {
        // given
        var employeeData = EmployeeData.newBuilder().setId(1L).build();
        given(stub.existsByEmailAddress(any(ExistsRequest.class)))
            .willReturn(ExistsResponse.newBuilder().setExists(true).setEmployee(employeeData).build());
        // whenR
        ExistsResponse result = repository.existsByEmailAddress("john@wayne.com");
        // then
        assertThat(result.getExists()).isTrue();
        assertThat(result.getEmployee()).isEqualTo(employeeData);
        verify(stub, times(1)).existsByEmailAddress(any(ExistsRequest.class));
        verifyNoMoreInteractions(stub);
    }

    @Test
    void should_return_employee_data_when_find_by_email_address_success() throws TechnicalException {
        // given
        var grpcResponse = EmployeeResponse.newBuilder()
                .setId(1L)
                .setFullName("John Wayne")
                .setEmailAddress("john@wayne.com")
                .build();
        given(stub.findByEmailAddress(any(FindRequest.class))).willReturn(grpcResponse);

        // when
        var response = repository.findByEmailAddress("john@wayne.com");

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
        var exception = new StatusRuntimeException(
                Status.NOT_FOUND.withDescription("Employee not found"));
        given(stub.findByEmailAddress(any(FindRequest.class))).willThrow(exception);

        // when
        var response = repository.findByEmailAddress("john@wayne.com");

        // then
        assertThat(response).isEmpty();
        verify(stub, times(1)).findByEmailAddress(any(FindRequest.class));
        verifyNoMoreInteractions(stub);
    }

    @Test
    void should_throw_exception_when_find_by_email_address_fails() {
        // given
        var exception = new StatusRuntimeException(
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