package br.com.maurigvs.company;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmployeeGrpcConfig {

    @Bean
    public Server serverBuilder(EmployeeRepository repository){
        return ServerBuilder.forPort(8185)
                .directExecutor()
                .addService(new EmployeeGrpcService(repository))
                .build();
    }
}