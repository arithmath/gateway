package com.example.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MyAuthFilter extends AbstractGatewayFilterFactory<MyAuthFilter.Config> {
    public MyAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Authorization ヘッダの取得
            ServerHttpRequest request = exchange.getRequest();
            String authorizationHeader = Optional.ofNullable(request.getHeaders().get("Authorization"))
                    .map(h -> {return h.get(0);}).orElse("");

            // Authorizationヘッダがxxxなら認証成功、
            // そうでなければ401 Unauthorizedのレスポンスを返す
            if(authorizationHeader.startsWith("xxx")) {
                return chain.filter(exchange.mutate().request(request).build());
            } else {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        };
    }

    public static class Config {

    }
}
