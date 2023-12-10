package br.com.maurigvs.company.user.config;

import br.com.maurigvs.company.employee.EmployeeGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeRpcClient {

    @Bean
    public ManagedChannel managedChannel(){
        return ManagedChannelBuilder
                .forAddress("localhost", 8185)
                .usePlaintext()
                .build();
    }

    @Bean
    public EmployeeGrpc.EmployeeBlockingStub employeeBlockingStub(ManagedChannel channel){
        return EmployeeGrpc.newBlockingStub(channel);
    }

    @Bean
    public EmployeeGrpc.EmployeeStub employeeStub(ManagedChannel channel){
        return EmployeeGrpc.newStub(channel);
    }
}