package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {
    private UserService userService;
    private Environment env;
    private BCryptPasswordEncoder bCryptPasswordEncoder ;

    public static final String ALLOWED_IP_ADDRESS = "127.0.0.1";
    public static final String SUBNET = "/32";
    public static final IpAddressMatcher ALLOWED_IP_ADDRESS_MATCHER = new IpAddressMatcher(ALLOWED_IP_ADDRESS + SUBNET);


    public WebSecurity(UserService userService, Environment env, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.env = env;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()   // 특정 경로 허용
                        .requestMatchers("/actuator/**").permitAll()    //  특정 경로 허용
                        .requestMatchers("/health-check/**").permitAll()    // 특정 경로 허용
                        .requestMatchers("/**").access(
                                new WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1') or hasIpAddress('::1') or " +
                                                                                        "hasIpAddress('10.56.130.80') or hasIpAddress('::1')"))
                        .anyRequest().authenticated())
                .authenticationManager(authenticationManager).addFilter(getAuthenticationFilter(authenticationManager))
                .httpBasic(Customizer.withDefaults()).headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin()));

        return http.build();
    }

    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, env, authenticationManager);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        return authenticationFilter;
    }
}
