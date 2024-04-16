package org.shareio.gateway;


import org.shareio.gateway.config.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayConfig {

    @Autowired
    AuthFilter authFilter;
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/user/**")
                        .and().method("GET")
                        .filters(f-> f.filter(authFilter).rewritePath("/user/(?<userId>.*)","/user?id=${userId}"))
                        .uri("lb://BACKEND"))
                .route(r -> r.path("/login")
                        .and().method("POST")
                        .filters(f-> f.rewritePath("/login","/jwt/generate"))
                        .uri("lb://JWT"))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayConfig.class, args);
    }


}
