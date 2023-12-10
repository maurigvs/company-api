package br.com.maurigvs.company.employee.service;

import br.com.maurigvs.company.employee.EmployeeData;
import br.com.maurigvs.company.employee.ExistsRequest;
import br.com.maurigvs.company.employee.ExistsResponse;
import br.com.maurigvs.company.employee.enums.Status;
import br.com.maurigvs.company.employee.model.Employee;
import br.com.maurigvs.company.employee.repository.EmployeeRepository;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = {EmployeeGrpcStub.class})
class EmployeeGrpcStubTest {

    @Autowired
    EmployeeGrpcStub employeeGrpcStub;

    @MockBean
    EmployeeRepository employeeRepository;

    @Test
    void should_ReturnResponseFalse_when_CallExistsByEmailAddressProcedure() {
        // given
        var emailAddress = "john@wayne.com";
        var request = ExistsRequest.newBuilder().setEmailAddress(emailAddress).build();
        var completed = new AtomicBoolean(true);
        given(employeeRepository.findByEmailAddress(anyString())).willReturn(Optional.empty());

        // when
        employeeGrpcStub.existsByEmailAddress(request, new StreamObserver<>(){

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
        verify(employeeRepository, times(1)).findByEmailAddress(emailAddress);
        verifyNoMoreInteractions(employeeRepository);
    }

    @Test
    void should_ReturnResponseTrue_when_CallExistsByEmailAddressProcedure() {
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
        given(employeeRepository.findByEmailAddress(anyString())).willReturn(Optional.of(employee));

        // when
        employeeGrpcStub.existsByEmailAddress(request, new StreamObserver<>(){

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
        verify(employeeRepository, times(1)).findByEmailAddress(emailAddress);
        verifyNoMoreInteractions(employeeRepository);
    }
}