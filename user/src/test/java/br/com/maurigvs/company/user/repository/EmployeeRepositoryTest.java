package br.com.maurigvs.company.user.repository;

import br.com.maurigvs.company.employee.EmployeeData;
import br.com.maurigvs.company.employee.EmployeeGrpc;
import br.com.maurigvs.company.employee.ExistsRequest;
import br.com.maurigvs.company.employee.ExistsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = {EmployeeRepository.class})
class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository employeeRepository;

    @MockBean
    EmployeeGrpc.EmployeeBlockingStub employeeBlockingStub;

    @Test
    void should_ReturnExistsFalse_when_CallExistsByEmailAddress() {
        // given
        given(employeeBlockingStub.existsByEmailAddress(any(ExistsRequest.class)))
            .willReturn(ExistsResponse.newBuilder().setExists(false).build());
        // when
        ExistsResponse result = employeeRepository.existsByEmailAddress("john@wayne.com");
        // then
        assertThat(result.getExists()).isFalse();
        assertThat(result.getEmployee()).isEqualTo(EmployeeData.getDefaultInstance());
        verify(employeeBlockingStub, times(1)).existsByEmailAddress(any(ExistsRequest.class));
        verifyNoMoreInteractions(employeeBlockingStub);
    }

    @Test
    void should_ReturnExistsTrue_when_CallExistsByEmailAddress() {
        // given
        var employeeData = EmployeeData.newBuilder().setId(1L).build();
        given(employeeBlockingStub.existsByEmailAddress(any(ExistsRequest.class)))
            .willReturn(ExistsResponse.newBuilder().setExists(true).setEmployee(employeeData).build());
        // whenR
        ExistsResponse result = employeeRepository.existsByEmailAddress("john@wayne.com");
        // then
        assertThat(result.getExists()).isTrue();
        assertThat(result.getEmployee()).isEqualTo(employeeData);
        verify(employeeBlockingStub, times(1)).existsByEmailAddress(any(ExistsRequest.class));
        verifyNoMoreInteractions(employeeBlockingStub);
    }
}