package br.com.maurigvs.company.employee.config;

import io.grpc.Server;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmployeeRpcServer {

    private final Server server;

    @PostConstruct
    public void start() {
        try {
            server.start();
            log.info("gRPC Server started, listening on port 8185");
        } catch (IOException exception) {
            log.error("Error while starting gRPC server", exception);
        } catch (IllegalStateException exception) {
            log.error("gRPC server is either already started or in shutdown mode", exception);
        }
    }

    @PreDestroy
    public void stop() {
        try {
            server.shutdown();
            server.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("gRPC thread server was unexpectedly interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}