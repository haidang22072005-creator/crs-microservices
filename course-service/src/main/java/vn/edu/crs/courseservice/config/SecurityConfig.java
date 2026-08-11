package vn.edu.crs.courseservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.crs.courseservice.security.JwtAuthFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // API nội bộ cho registration-service
                        .requestMatchers("/internal/**")
                        .permitAll()

                        // GET course: ai cũng xem được
                        .requestMatchers(
                                HttpMethod.GET,
                                "/courses/**"
                        )
                        .permitAll()

                        // POST course: chỉ ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/courses/**"
                        )
                        .hasRole("ADMIN")

                        // PUT course: chỉ ADMIN
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/courses/**"
                        )
                        .hasRole("ADMIN")

                        // DELETE course: chỉ ADMIN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/courses/**"
                        )
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}