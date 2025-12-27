package com.erval.argos.resource.config;

import com.erval.argos.resource.adapters.grpc.ResourceQueryGrpcService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ResourceGrpcServerConfig {

    private final ResourceQueryGrpcService resourceQueryGrpcService;

    private Server server;

    @Bean
    public Server grpcServer(
            @Value("${argos.resource.grpc.port:9091}") int port) throws Exception {
        this.server = ServerBuilder.forPort(port)
                .addService(resourceQueryGrpcService)
                .build()
                .start();

        log.info("gRPC Resource Server started, listening on port {}", port);
        return this.server;
    }

    @PreDestroy
    public void shutdown() {
        if (server != null) {
            server.shutdown();
        }
    }
}
