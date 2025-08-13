package com.example.fluxapigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    /**
     * 우선 순위를 갖는 Logging Filter 적용
     */
    @Override
    public GatewayFilter apply(Config config) {
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter Base Messages: {}, {}", config.getBaseMessage(), request.getRemoteAddress());

            // Global pre-filter
            if(config.isPreLogger()) {
                log.info("Logging Filter Start: request id -> {}", request.getURI().toString());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Custom post-filter
                if(config.isPostLogger()) {
                    log.info("Logging Filter End: request code -> {}", response.getStatusCode());
                }
            }));
        }, OrderedGatewayFilter.HIGHEST_PRECEDENCE);    // 우선순위 지정
        return filter;
    }

/*    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter Base Messages: {}, {}", config.getBaseMessage(), request.getRemoteAddress());

            // Global pre-filter
            if(config.isPreLogger()) {
                log.info("Logging Filter Start: request id -> {}", request.getURI().toString());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Custom post-filter
                if(config.isPostLogger()) {
                    log.info("Logging Filter End: request code -> {}", response.getStatusCode());
                }
            }));
        };
    }*/

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
