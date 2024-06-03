package org.shareio.gateway;


import lombok.Getter;
import org.shareio.gateway.config.AuthFilter;
import org.shareio.gateway.config.TokenFilter;
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

    final TokenFilter tokenFilter;

    final
    AuthFilter authFilter;

    public GatewayConfig(AuthFilter authFilter, TokenFilter tokenFilter) {
        this.authFilter = authFilter;
        this.tokenFilter = tokenFilter;
    }

    private Buildable<Route> openRoute(PredicateSpec r, String path, String method, String originalPath, String rewrittenPath, String uri) {
        return r.path(path)
                .and().method(method)
                .filters(f -> f.filter(tokenFilter).rewritePath(originalPath, rewrittenPath))
                .uri(uri);
    }

    private Buildable<Route> authorizedRoute(PredicateSpec r, String path, String method, String originalPath, String rewrittenPath, String uri) {
        return r.path(path)
                .and().method(method)
                .filters(f -> f.filter(authFilter).rewritePath(originalPath, rewrittenPath))
                .uri(uri);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        EnvGetter urls = new EnvGetter();

        return builder.routes()
                // ------------------ JWT -------------------
                .route(r -> openRoute(r, "/login", "POST", "/login", "/jwt/generate", urls.jwt))

                // ----------------- IMAGE ------------------
                .route(r -> openRoute(r, "/image/get/**", "GET", "/image/get/?<id>.*", "/image/get/${id}", urls.image))

                // ---------------- BACKEND -----------------
                // DEBUG
                .route(r -> openRoute(r, "/debug/createUser", "GET", "/debug/createUser", "/debug/createUser", urls.backend))
                .route(r -> openRoute(r, "/debug/createOffers/**", "GET", "/debug/createOffers/?<id>.*", "/debug/createOffers/${id}", urls.backend))
                .route(r -> openRoute(r, "/debug/getOfferIds", "GET", "/debug/getOfferIds", "/debug/getOfferIds", urls.backend))
                // USER
                .route(r -> openRoute(r, "/user/get/**", "GET", "/user/get/(?<id>.*)", "/user/get/${id}", urls.backend))
                .route(r -> openRoute(r, "/user/getAll", "GET", "/user/getAll", "/user/getAll", urls.backend))
                .route(r -> openRoute(r, "/user/add", "POST", "/user/add", "/user/add", urls.backend))
                .route(r -> authorizedRoute(r, "/user/modify/**", "PUT", "/user/modify/(?<userId>.*)", "/user/modify/${userId}", urls.backend))
                .route(r -> authorizedRoute(r, "/user/setPhoto/**", "POST", "/user/setPhoto/(?<userId>.*)", "/user/setPhoto/${userId}", urls.backend))
                .route(r -> authorizedRoute(r, "/user/changePassword/**", "PUT", "/user/changePassword/(?<userId>.*)", "/user/changePassword/${userId}", urls.backend))
                .route(r -> openRoute(r, "/user/delete/**", "DELETE", "/user/delete/(?<id>.*)", "/user/delete/${id}", urls.backend))
                // OFFER
                .route(r -> openRoute(r, "/offer/get/**", "GET", "/offer/get/(?<id>.*)", "/offer/get/${id}", urls.backend))
                .route(r -> openRoute(r, "/offer/getClosestOfferForUser/**", "GET", "/offer/getClosestOfferForUser/(?<userId>.*)", "/offer/getClosestOfferForUser/${userId}", urls.backend))
                .route(r -> openRoute(r, "/offer/getCreatedOffersByUser/**", "GET", "/offer/getCreatedOffersByUser/(?<userId>.*)", "/offer/getCreatedOffersByUser/${userId}", urls.backend))
                .route(r -> openRoute(r, "/offer/getReservedOffersByUser/**", "GET", "/offer/getReservedOffersByUser/(?<userId>.*)", "/offer/getReservedOffersByUser/${userId}", urls.backend))
                .route(r -> openRoute(r, "/offer/getFinishedOffersByUser/**", "GET", "/offer/getFinishedOffersByUser/(?<userId>.*)", "/offer/getFinishedOffersByUser/${userId}", urls.backend))
                .route(r -> openRoute(r, "/offer/getOffersByName/**", "GET", "/offer/getOffersByName/?<name>.*", "/offer/getOffersByName/${name}", urls.backend))
                .route(r -> openRoute(r, "/offer/getCategories", "GET", "/offer/getCategories", "/offer/getCategories", urls.backend))
                .route(r -> openRoute(r, "/offer/getConditions", "GET", "/offer/getConditions", "/offer/getConditions", urls.backend))
                .route(r -> openRoute(r, "/offer/search", "GET", "/offer/search", "/offer/search", urls.backend))
                .route(r -> openRoute(r, "/offer/generateDescription", "GET", "/offer/generateDescription", "/offer/generateDescription", urls.backend))
                .route(r -> openRoute(r, "/offer/getNewest", "GET", "/offer/getNewest", "/offer/getNewest", urls.backend))
                .route(r -> openRoute(r, "/offer/getAllOffers", "GET", "/offer/getAllOffers", "/offer/getAllOffers", urls.backend))
                .route(r -> openRoute(r, "/offer/getScore/**", "GET", "/offer/getScore/(?<userId>.*)", "/offer/getScore/${userId}", urls.backend))
                .route(r -> openRoute(r, "/offer/getTopScoreUserList", "GET", "/offer/getTopScoreUserList", "/offer/getTopScoreUserList", urls.backend))
                .route(r -> openRoute(r, "/offer/getReservedOffersByReciever/**", "GET", "/offer/getReservedOffersByReciever/(?<userId>.*)", "/offer/getReservedOffersByReciever/${userId}", urls.backend))
                .route(r -> openRoute(r, "/offer/getFinishedOffersByReciever/**", "GET", "/offer/getFinishedOffersByReciever/(?<userId>.*)", "/offer/getFinishedOffersByReciever/${userId}", urls.backend))
                .route(r -> openRoute(r, "/offer/add", "POST", "/offer/add", "/offer/add", urls.backend))
                .route(r -> authorizedRoute(r, "/offer/reserve", "POST", "/offer/reserve", "/offer/reserve", urls.backend))
                .route(r -> authorizedRoute(r, "/offer/cancel", "POST", "/offer/cancel", "/offer/cancel", urls.backend))
                .route(r -> authorizedRoute(r, "/offer/finish", "POST", "/offer/finish", "/offer/finish", urls.backend))
                .route(r -> openRoute(r, "/offer/addReview", "POST", "/offer/addReview", "/offer/addReview", urls.backend))
                .route(r -> openRoute(r, "/offer/modify/**", "PUT", "/offer/modify/(?<id>.*)", "/offer/modify/${id}", urls.backend))
                .route(r -> openRoute(r, "/offer/delete/**", "DELETE", "/offer/delete/(?<id>.*)", "/offer/delete/${id}", urls.backend))
                // ADDRESS
                .route(r -> openRoute(r, "/address/get/**", "GET", "/address/get/(?<id>.*)", "/address/get/${id}", urls.backend))
                .route(r -> openRoute(r, "/address/location/get/**", "GET", "/address/location/get/(?<id>.*)", "/address/location/get/${id}", urls.backend))
                // EMAIL
                .route(r -> authorizedRoute(r, "/email/send", "POST", "/email/send", "/email/send", urls.backend))
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
        private final String image;

        public EnvGetter() {
            this.backend = System.getenv("BACKEND_ADDRESS");
            this.frontend = System.getenv("FRONTEND_ADDRESS");
            this.jwt = System.getenv("JWT_ADDRESS");
            this.image = System.getenv("IMAGE_ADDRESS");
            if (this.backend.isBlank() || this.frontend.isBlank() || this.jwt.isBlank() || this.image.isBlank()) {
                throw new RuntimeException("Could not load service addressed from envs!");
            }
        }
    }

}
