package org.shareio.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.shareio.gateway.config.util.JWTUtil;
import org.shareio.gateway.config.validator.RouteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RefreshScope
public class TokenFilter implements GatewayFilter {

    @Autowired
    private RouteValidator routerValidator;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();

            if (routerValidator.isSecured.test(request)) {
                if (this.isAuthMissing(request))
                    return chain.filter(exchange);

                String token = this.getAuthHeader(request);
                if (Objects.isNull(token))
                    return chain.filter(exchange);

                token = token.substring(7);

                this.populateRequestWithHeaders(exchange, token);
            }
            return chain.filter(exchange);
        }
        catch(ExpiredJwtException e){
            return chain.filter(exchange);
        }
    }


    /*PRIVATE*/

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").getFirst();
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.getALlClaims(token);
        exchange.getRequest().mutate()
                .header("id", String.valueOf(claims.get("id")))
                .header("role", String.valueOf(claims.get("role")))
                .build();
    }
}
