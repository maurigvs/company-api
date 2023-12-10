package br.com.maurigvs.company.employee.config;

import br.com.maurigvs.company.employee.repository.EmployeeRepository;
import br.com.maurigvs.company.employee.service.EmployeeRpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRpcConfig {

    @Bean
    public Server serverBuilder(EmployeeRepository repository){
        return ServerBuilder.forPort(8185)
                .directExecutor()
                .addService(new EmployeeRpcService(repository))
                .build();
    }
}