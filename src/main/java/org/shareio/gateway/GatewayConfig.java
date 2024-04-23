package org.shareio.gateway;


import lombok.Getter;
import org.shareio.gateway.config.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
        EnvGetter urls = new EnvGetter();

        return builder.routes()
                .route(r -> r.path("/login")
                        .and().method("POST")
                        .filters(f -> f.rewritePath("/login", "/jwt/generate"))
                        .uri(urls.jwt))
                // USER
                .route(r -> r.path("/user/get/**")
                        .and().method("GET")
                        .filters(f -> f.filter(authFilter).rewritePath("/user/get/(?<id>.*)", "/user/get/${id}"))
                        .uri(urls.backend))
                .route(r -> r.path("/user/add")
                        .and().method("POST")
                        .filters(f -> f.filter(authFilter))
                        .uri(urls.backend))
                .route(r -> r.path("/user/modify/**")
                        .and().method("PUT")
                        .filters(f -> f.filter(authFilter).rewritePath("/user/modify/(?<id>.*)", "/user/modify/${id}"))
                        .uri(urls.backend))
                .route(r -> r.path("/user/delete/**")
                        .and().method("DELETE")
                        .filters(f -> f.filter(authFilter).rewritePath("/user/delete/(?<id>.*)", "/user/delete/${id}"))
                        .uri(urls.backend))
                // OFFER
                .route(r -> r.path("/offer/get/**")
                        .and().method("GET")
                        .filters(f -> f.filter(authFilter).rewritePath("/offer/get/(?<id>.*)", "/offer/get/${id}"))
                        .uri(urls.backend))
                .route(r -> r.path("/offer/getConditions")
                        .and().method("GET")
                        .filters(f -> f.filter(authFilter))
                        .uri(urls.backend))
//              .route(r -> r.path("/offer/search")
//                        .and().method("GET")
//                        .filters(f -> f.filter(authFilter))
//                        .uri(urls.backend))
//              .route(r -> r.path("/offer/searchForMap")
//                        .and().method("GET")
//                        .filters(f -> f.filter(authFilter))
//                        .uri(urls.backend))
                .route(r -> r.path("/offer/add")
                        .and().method("POST")
                        .filters(f -> f.filter(authFilter))
                        .uri(urls.backend))
                .route(r -> r.path("/offer/modify/**")
                        .and().method("PUT")
                        .filters(f -> f.filter(authFilter).rewritePath("/offer/modify/(?<id>.*)", "/offer/modify/${id}"))
                        .uri(urls.backend))
                .route(r -> r.path("/offer/reserve/**")
                        .and().method("PUT")
                        .filters(f -> f.filter(authFilter).rewritePath("/offer/reserve/(?<id>.*)", "/offer/reserve/${id}"))
                        .uri(urls.backend))
                .route(r -> r.path("/offer/delete/**")
                        .and().method("DELETE")
                        .filters(f -> f.filter(authFilter).rewritePath("/offer/delete/(?<id>.*)", "/offer/delete/${id}"))
                        .uri(urls.backend))
                // ADDRESS
                .route(r -> r.path("/address/get/**")
                        .and().method("GET")
                        .filters(f -> f.filter(authFilter).rewritePath("/address/get/(?<id>.*)", "/address/get/${id}"))
                        .uri(urls.backend))
                .route(r -> r.path("/address/location/get/**")
                        .and().method("GET")
                        .filters(f -> f.filter(authFilter).rewritePath("/address/location/get/(?<id>.*)", "/address/location/get/${id}"))
                        .uri(urls.backend))
                .route(r -> r.path("/address/modify/**")
                        .and().method("PUT")
                        .filters(f -> f.filter(authFilter).rewritePath("/address/modify/(?<id>.*)", "/address/modify/${id}"))
                        .uri(urls.backend))
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayConfig.class, args);
    }

    @Getter
    private static class EnvGetter {
        private final String backend;
        private final String frontend;
        private final String jwt;

        public EnvGetter() {
            this.backend = System.getenv("BACKEND_ADDRESS");
            this.frontend = System.getenv("FRONTEND_ADDRESS");
            this.jwt = System.getenv("JWT_ADDRESS");
            if (this.backend.isBlank() || this.frontend.isBlank() || this.jwt.isBlank()) {
                throw new RuntimeException("Could not load service addressed from envs!");
            }
        }
    }

}
