package br.com.maurigvs.company.employee.config;

import br.com.maurigvs.company.employee.repository.EmployeeRepository;
import br.com.maurigvs.company.employee.service.EmployeeGrpcService;
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