package com.gateway.okta.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class OktaRedirectServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private URI location = URI.create("/");

    private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    private ServerRequestCache requestCache = new WebSessionServerRequestCache();

    /**
     * Creates a new instance with location of "/"
     */
    public OktaRedirectServerAuthenticationSuccessHandler() {
    }

    /**
     * Creates a new instance with the specified location
     * @param location the location to redirect if the no request is cached in
     * {@link #setRequestCache(ServerRequestCache)}
     */
    public OktaRedirectServerAuthenticationSuccessHandler(String location) {
        this.location = URI.create(location);
    }

    /**
     * Sets the {@link ServerRequestCache} used to redirect to. Default is
     * {@link WebSessionServerRequestCache}.
     * @param requestCache the cache to use
     */
    public void setRequestCache(ServerRequestCache requestCache) {
        Assert.notNull(requestCache, "requestCache cannot be null");
        this.requestCache = requestCache;
//        this.requestCache.
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();

        return
                //this.requestCache.getRedirectUri(exchange).defaultIfEmpty(this.location)
//                .flatMap(
                          this.redirectStrategy.sendRedirect(exchange, this.location)
//        )
        ;
    }

    /**
     * Where the user is redirected to upon authentication success
     * @param location the location to redirect to. The default is "/"
     */
    public void setLocation(URI location) {
        Assert.notNull(location, "location cannot be null");
        this.location = location;
    }

    /**
     * The RedirectStrategy to use.
     * @param redirectStrategy the strategy to use. Default is DefaultRedirectStrategy.
     */
    public void setRedirectStrategy(ServerRedirectStrategy redirectStrategy) {
        Assert.notNull(redirectStrategy, "redirectStrategy cannot be null");
        this.redirectStrategy = redirectStrategy;
    }

}
