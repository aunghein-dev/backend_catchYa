package com.catch_ya_group.catch_ya.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Map;

@Component
public class QueryParamHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        URI uri = request.getURI();
        MultiValueMap<String,String> qs = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
        String uid = qs.getFirst("uid");
        return new StompPrincipal((uid != null && !uid.isBlank()) ? uid : "0");
    }
}
