package br.com.maurigvs.company.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.maurigvs.company.employee.EmployeeData;
import br.com.maurigvs.company.employee.EmployeeResponse;
import br.com.maurigvs.company.employee.ExistsRequest;
import br.com.maurigvs.company.employee.ExistsResponse;
import br.com.maurigvs.company.employee.FindRequest;
import br.com.maurigvs.company.employee.enums.Status;
import br.com.maurigvs.company.employee.model.Employee;
import br.com.maurigvs.company.employee.repository.EmployeeRepository;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

@SpringBootTest(classes = {EmployeeRpcService.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmployeeRpcServiceTest {

    @Autowired
    EmployeeRpcService rpcService;

    @MockBean
    EmployeeRepository repository;

    @Test
    void should_throw_technical_exception_when_find_by_email_address_fails() {

        final var request = FindRequest.newBuilder()
                .setEmailAddress("john@wayne.com")
                .build();

        final var exception = new DataSourceLookupFailureException("Database error");
        final var future = new CompletableFuture<>();
        given(repository.findByEmailAddress(anyString())).willThrow(exception);

        rpcService.findByEmailAddress(request, new StreamObserver<>() {

            @Override
            public void onNext(EmployeeResponse response) {}

            @Override
            public void onError(Throwable exception) {
                assertThat(exception).isInstanceOf(StatusRuntimeException.class);
                assertThat(exception).hasMessageEndingWith("Database error");
            }

            @Override
            public void onCompleted() {
                future.complete(null);
            }
        });

        verify(repository, times(1)).findByEmailAddress(request.getEmailAddress());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_throw_business_exception_when_find_by_email_address_is_empty() {

        final var request = FindRequest.newBuilder()
                .setEmailAddress("john@wayne.com")
                .build();

        final var future = new CompletableFuture<>();
        given(repository.findByEmailAddress(anyString())).willReturn(Optional.empty());

        rpcService.findByEmailAddress(request, new StreamObserver<>() {

            @Override
            public void onNext(EmployeeResponse response) {}

            @Override
            public void onError(Throwable exception) {
                assertThat(exception).isInstanceOf(StatusRuntimeException.class);
                assertThat(exception).hasMessageEndingWith("Employee not found by email address");
            }

            @Override
            public void onCompleted() {
                future.complete(null);
            }
        });

        verify(repository, times(1)).findByEmailAddress(request.getEmailAddress());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_employee_data_when_find_by_email_address_sucesss() {

        final var request = FindRequest.newBuilder()
                .setEmailAddress("john@wayne.com")
                .build();

        final var employeeOpt = Optional.of(new Employee(1L, "John", "Wayne",
                "john@wayne.com", LocalDate.of(1985,5,23),
                "12345678912", Status.ACTIVE));

        final var future = new CompletableFuture<>();
        given(repository.findByEmailAddress(anyString())).willReturn(employeeOpt);

        rpcService.findByEmailAddress(request, new StreamObserver<>() {

            @Override
            public void onNext(EmployeeResponse response) {
                assertThat(response).isNotNull();
                assertThat(response.getId()).isNotZero();
                assertThat(response.getFullName()).isEqualTo("John Wayne");
                assertThat(response.getEmailAddress()).isEqualTo(request.getEmailAddress());
            }

            @Override
            public void onError(Throwable throwable) {}

            @Override
            public void onCompleted() {
                future.complete(null);
            }
        });

        verify(repository, times(1)).findByEmailAddress(request.getEmailAddress());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_Return_Response_False_when_Call_Exists_By_Email_Address_Procedure() {
        // given
        var emailAddress = "john@wayne.com";
        var request = ExistsRequest.newBuilder().setEmailAddress(emailAddress).build();
        var completed = new AtomicBoolean(true);
        given(repository.findByEmailAddress(anyString())).willReturn(Optional.empty());

        // when
        rpcService.existsByEmailAddress(request, new StreamObserver<>(){

            @Override
            public void onNext(ExistsResponse existsResponse) {
                assertThat(existsResponse.getExists()).isFalse();
                assertThat(existsResponse.getEmployee()).isEqualTo(EmployeeData.getDefaultInstance());
            }

            @Override
            public void onError(Throwable throwable) {
                assertThat(throwable).isNull();
            }

            @Override
            public void onCompleted() {
                completed.compareAndSet(false, true);
                assertThat(completed.get()).isTrue();
            }
        });

        // then
        verify(repository, times(1)).findByEmailAddress(emailAddress);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_Return_Response_True_when_Call_Exists_By_Email_Address_Procedure() {
        // given
        var employee = new Employee(1L,
                "John",
                "Wayne",
                "john@wayne.com",
                LocalDate.of(1985,5,23),
                "12345678912",
                Status.ACTIVE);
        var emailAddress = "john@wayne.com";

        var request = ExistsRequest.newBuilder()
                .setEmailAddress(emailAddress).build();

        var employeeData = EmployeeData.newBuilder()
                .setId(employee.getId())
                .setName(employee.getName())
                .setSurname(employee.getSurname())
                .setEmailAddress(employee.getEmailAddress())
                .setBirthDate(employee.getBirthDate().toString())
                .setTaxId(employee.getTaxId())
                .build();

        var completed = new AtomicBoolean(true);
        given(repository.findByEmailAddress(anyString())).willReturn(Optional.of(employee));

        // when
        rpcService.existsByEmailAddress(request, new StreamObserver<>(){

            @Override
            public void onNext(ExistsResponse existsResponse) {
                assertThat(existsResponse.getExists()).isTrue();
                assertThat(existsResponse.getEmployee()).isEqualTo(employeeData);
            }

            @Override
            public void onError(Throwable throwable) {
                assertThat(throwable).isNull();
            }

            @Override
            public void onCompleted() {
                completed.compareAndSet(false, true);
                assertThat(completed.get()).isTrue();
            }
        });

        // then
        verify(repository, times(1)).findByEmailAddress(emailAddress);
        verifyNoMoreInteractions(repository);
    }
}