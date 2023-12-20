package br.com.maurigvs.company.user.config;

import br.com.maurigvs.company.employee.EmployeeGrpcGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeGrpcClient {

    @Bean
    public ManagedChannel managedChannel(){
        return ManagedChannelBuilder
                .forAddress("localhost", 8185)
                .usePlaintext()
                .build();
    }

    @Bean
    public EmployeeGrpcGrpc.EmployeeGrpcBlockingStub employeeBlockingStub(ManagedChannel channel){
        return EmployeeGrpcGrpc.newBlockingStub(channel);
    }

    @Bean
    public EmployeeGrpcGrpc.EmployeeGrpcStub employeeStub(ManagedChannel channel){
        return EmployeeGrpcGrpc.newStub(channel);
    }
}