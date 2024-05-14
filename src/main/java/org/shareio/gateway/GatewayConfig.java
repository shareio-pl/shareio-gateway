package org.shareio.gateway;


import lombok.Getter;
import org.shareio.gateway.config.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayConfig {

    @Autowired
    AuthFilter authFilter;

    private Buildable<Route> openRoute(PredicateSpec r, String path, String method, String originalPath, String rewrittenPath, String uri){
        return r.path(path)
                .and().method(method)
                .filters(f -> f.rewritePath(originalPath, rewrittenPath))
                .uri(uri);
    }

    private Buildable<Route> authorizedRoute(PredicateSpec r, String path, String method, String originalPath, String rewrittenPath, String uri){
        return r.path(path)
                .and().method(method)
                .filters(f -> f.filter(authFilter).rewritePath(originalPath, rewrittenPath))
                .uri(uri);
    }
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        EnvGetter urls = new EnvGetter();

        return builder.routes()
                .route(r -> openRoute(r, "/login", "POST" , "/login","/jwt/generate", urls.jwt) )
                // DEBUG
                .route(r -> openRoute(r, "/debug/createUser", "GET", "/debug/createUser", "/debug/createUser", urls.backend))
                .route(r -> openRoute(r, "/debug/createOffers/**", "GET", "/debug/createOffers/?<id>.*", "/debug/createOffers/${id}", urls.backend))
                .route(r -> openRoute(r, "/debug/getOfferIds", "GET", "/debug/getOfferIds", "/debug/getOfferIds", urls.backend))
                .route(r -> openRoute(r, "/debug/getOffersByName/**", "GET","/debug/getOffersByName/?<name>.*", "/debug/getOffersByName/${name}", urls.backend))
                // USER
                .route(r -> openRoute(r, "/user/get/**",    "GET",      "/user/get/(?<id>.*)",      "/user/get/${id}",      urls.backend))
                .route(r -> openRoute(r, "/user/add",       "POST",     "/user/add",                "/user/add",            urls.backend))
                .route(r -> openRoute(r, "/user/modify/**", "PUT",      "/user/modify/(?<id>.*)",   "/user/modify/${id}",   urls.backend))
                .route(r -> openRoute(r, "/user/delete/**", "DELETE",   "/user/delete/(?<id>.*)",   "/user/delete/${id}",   urls.backend))
                // OFFER
                .route(r -> openRoute(r, "/offer/get/**",   "GET",      "/offer/get/(?<id>.*)",     "/offer/get/${id}",     urls.backend))
                .route(r -> openRoute(r, "/offer/getClosestOfferForUser/**","GET",      "/offer/getClosestOfferForUser/(?<userId>.*)",     "/offer/getClosestOfferForUser/${userId}",     urls.backend))
                .route(r -> openRoute(r, "/offer/getConditions","GET",      "/offer/getConditions",     "/offer/getConditions",     urls.backend))
                .route(r -> openRoute(r, "/offer/add","POST",      "/offer/add",     "/offer/add",     urls.backend))
                .route(r -> openRoute(r, "/offer/modify/**","PUT",      "/offer/modify/(?<id>.*)",     "/offer/modify/${id}",     urls.backend))
                .route(r -> openRoute(r, "/offer/reserve/**","PUT",      "/offer/reserve/(?<id>.*)",     "/offer/reserve/${id}",     urls.backend))
                .route(r -> openRoute(r, "/offer/delete/**","DELETE",      "/offer/delete/(?<id>.*)",     "/offer/delete/${id}",     urls.backend))
                // ADDRESS
                .route(r -> openRoute(r, "/address/get/**",   "GET",      "/address/get/(?<id>.*)",     "/address/get/${id}",     urls.backend))
                .route(r -> openRoute(r, "/address/location/get/**",   "GET",      "/address/location/get/(?<id>.*)",     "/address/location/get/${id}",     urls.backend))
                .route(r -> openRoute(r, "/address/modify/**",   "PUT",      "/address/modify/(?<id>.*)",     "/address/modify/${id}",     urls.backend))
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
