package com.threethings.api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;
	private static final String[] WHITE_LIST = {"/api/sign-in", "/api/sign-up", "/test/enums"};

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.httpBasic(AbstractHttpConfigurer::disable)
			.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
			.formLogin(FormLoginConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authorize ->
				authorize.requestMatchers(WHITE_LIST).permitAll()
					.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
					.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(userDetailsService),
				UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(authenticationManager ->
				authenticationManager.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
			.build();
	}
}
