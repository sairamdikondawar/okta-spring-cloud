package com.gateway.okta.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthenticatedReactiveAuthorizationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.authorization.AuthorizationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcherEntry;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Resource;

//@AllArgsConstructor
@Configuration

@EnableWebFluxSecurity
//@EnableReactiveMethodSecurity(proxyTargetClass = true)
public class OktaSecurityConfiguration {

    @Bean
    public ReactiveAuthorizationManager authorizationManager()
    {
//        ServerWebExchangeMatcherEntry<ReactiveAuthorizationManager<AuthorizationContext>> fads = new ServerWebExchangeMatcherEntry<ReactiveAuthorizationManager<AuthorizationContext>>();
        return    OktaAuthorizeConfiguManager.builder().build();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http.exceptionHandling()
                //.authenticationEntryPoint(unauthorizedHandler)
                //.accessDeniedHandler(accessDeniedHandler)
                //Disable frame options to allow SVG maps to display on tracking dashboard
//				.and().headers().frameOptions().disable()
                //.and().headers().xssProtection().disable()
                .and().cors()
                .and().csrf().disable()
				.formLogin().disable().httpBasic().disable()
         .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/test").permitAll()
                .anyExchange().authenticated()
//                .anyExchange().access(reactiveAuthorizationManager())



        ;
        http.oauth2Login(oauth2 -> oauth2
//				.authenticationConverter(super.authenticationConverter())
//				.authenticationMatcher(this.authenticationMatcher())
//				.authenticationManager(this.authenticationManager())
                        .authenticationSuccessHandler(this.authenticationSuccessHandler())
//				.authenticationFailureHandler(this.authenticationFailureHandler())
//				.clientRegistrationRepository(clientRegistrationRepository)
//				.authorizedClientRepository(this.authorizedClientRepository())
//				.authorizedClientService(this.authorizedClientService())
//				.authorizationRequestResolver(this.authorizationRequestResolver())
//				.authorizationRequestRepository(this.authorizationRequestRepository())
//				.securityContextRepository(this.securityContextRepository()
        );
        http.addFilterBefore(new AuthorizationWebFilter(authorizationManager()), SecurityWebFiltersOrder.AUTHORIZATION);

        return http.build();

    }

    @Bean
    public ServerAuthenticationSuccessHandler authenticationSuccessHandler(){
        return new OktaRedirectServerAuthenticationSuccessHandler("http://localhost:3200/test");
    }

    @Bean
    public ReactiveAuthorizationManager<AuthorizationContext> reactiveAuthorizationManager() {
        OktaAuthorizeManager reactiveAuthorizationManager = new OktaAuthorizeManager();
//        reactiveAuthorizationManager.setAuthorizedClientRepository(authorizedClientRepository);
        return reactiveAuthorizationManager;
    }


}
