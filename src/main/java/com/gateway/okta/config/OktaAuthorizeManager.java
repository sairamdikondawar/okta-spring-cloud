package com.gateway.okta.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class OktaAuthorizeManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication.map(auth -> {
            ServerWebExchange exchange = authorizationContext.getExchange();
            ServerHttpRequest
                    request = exchange.getRequest();
//			for (GrantedAuthority authority : authorities) {
//				String authorityAuthority = authority.getAuthority();
//				String path = request.getURI().getPath();
//				if (antPathMatcher.match(authorityAuthority, path)) {
//					log.info(String.format("GrantedAuthority:{%s}  Path:{%s} ", authorityAuthority, path));
//					return new AuthorizationDecision(true);
//				}
//			}
            if (this.validatePath(request, auth)) {
                return new AuthorizationDecision(true);
            }

            return new AuthorizationDecision(false);
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        return ReactiveAuthorizationManager.super.verify(authentication, object);
    }

    public boolean validatePath(ServerHttpRequest request, Authentication auth) {
        if (auth == null) {
            return Boolean.FALSE;
        }

        String requestPathInfo = request.getURI().getPath().split("-service")[1]; // TODO add it in try catch log error and log actual URL
        String method = request.getMethodValue();
        String[] stringArr = requestPathInfo.split("/");
        String dupRequestPath = requestPathInfo;

        if (dupRequestPath.matches("/test")) {
            return Boolean.TRUE
                    ;
        }

        return false;
    }
}
