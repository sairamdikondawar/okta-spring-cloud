package com.gateway.okta.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.authorization.DelegatingReactiveAuthorizationManager;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcherEntry;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//@Component
@Slf4j
public class OktaAuthorizeConfiguManager implements ReactiveAuthorizationManager<ServerWebExchange> {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, ServerWebExchange exchange) {
        return authentication.map(auth -> {
            auth.getPrincipal();
            ServerHttpRequest request = exchange.getRequest();
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                String authorityAuthority = authority.getAuthority();
                String path = request.getURI().getPath();
                // TODO

                if (antPathMatcher.match("/test", path)) {
                    log.info(String.format("用户请求API校验通过，GrantedAuthority:{%s}  Path:{%s} ", authorityAuthority, path));
                    return new AuthorizationDecision(true);
                }
                if (antPathMatcher.match("/testnew", path)) {
                    log.info(String.format("用户请求API校验通过，GrantedAuthority:{%s}  Path:{%s} ", authorityAuthority, path));
                    return new AuthorizationDecision(false);
                }
            }
            return new AuthorizationDecision(false);
        }).defaultIfEmpty(new AuthorizationDecision(false));
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, ServerWebExchange object) {
        return ReactiveAuthorizationManager.super.verify(authentication, object);
    }

    private static final Log logger = LogFactory.getLog(DelegatingReactiveAuthorizationManager.class);
    private final List<ServerWebExchangeMatcherEntry<ReactiveAuthorizationManager<AuthorizationContext>>> mappings;

    private OktaAuthorizeConfiguManager(List<ServerWebExchangeMatcherEntry<ReactiveAuthorizationManager<AuthorizationContext>>> mappings) {
        this.mappings = mappings;
    }

    public Mono<AuthorizationDecision> check1(Mono<Authentication> authentication, ServerWebExchange exchange) {
       String url= exchange.getRequest().getPath().value();
        return Flux.fromIterable(this.mappings).concatMap((mapping) -> {
            return mapping.getMatcher().matches(exchange).filter(ServerWebExchangeMatcher.MatchResult::isMatch).map(ServerWebExchangeMatcher.MatchResult::getVariables).flatMap((variables) -> {
                logger.debug(LogMessage.of(() -> {
                    return "Checking authorization on '" + exchange.getRequest().getPath().pathWithinApplication() + "' using " + mapping.getEntry();
                }));
                return ((ReactiveAuthorizationManager)mapping.getEntry()).check(authentication, new AuthorizationContext(exchange, variables));
            });
        }).next().defaultIfEmpty(new AuthorizationDecision(false));
    }

    public static OktaAuthorizeConfiguManager.Builder builder() {
        return new OktaAuthorizeConfiguManager.Builder();
    }

    public static final class Builder {
        private final List<ServerWebExchangeMatcherEntry<ReactiveAuthorizationManager<AuthorizationContext>>> mappings;

        private Builder() {
            this.mappings = new ArrayList();
        }

        public OktaAuthorizeConfiguManager.Builder add(ServerWebExchangeMatcherEntry<ReactiveAuthorizationManager<AuthorizationContext>> entry) {
            this.mappings.add(entry);
            return this;
        }

        public OktaAuthorizeConfiguManager build() {
            return new OktaAuthorizeConfiguManager(this.mappings);
        }
    }
}