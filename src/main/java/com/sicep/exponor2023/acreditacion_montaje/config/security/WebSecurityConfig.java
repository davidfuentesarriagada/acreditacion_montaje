package com.sicep.exponor2023.acreditacion_montaje.config.security;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
	private final CustomUserDetailsService userDetailsService;
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	@Order(SecurityProperties.BASIC_AUTH_ORDER)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf((csrf) -> csrf.disable())// permite mantener la sesion en el cache
		.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
			//.anyRequest().permitAll()
			.requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
	        .requestMatchers(new AntPathRequestMatcher("/error/**")).permitAll()
			.requestMatchers(new AntPathRequestMatcher("/login*")).permitAll()
			.requestMatchers(new AntPathRequestMatcher("/personal/*/ver")).permitAll()// visualizacion de los datos de un participante
			.requestMatchers(new AntPathRequestMatcher("/personal/ticket/*")).permitAll()// visualizacion de los datos de un participante
			.requestMatchers(new AntPathRequestMatcher("/personal/plantilla/*")).permitAll()// visualizacion de los datos de un participante
			// librerias js y css
			.requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
			.anyRequest().authenticated()
 		)
		//.formLogin(Customizer.withDefaults())
		.formLogin((formLogin) -> formLogin
			.loginPage("/login")
			.usernameParameter("email")
			.defaultSuccessUrl("/")
			.permitAll()
		)
		//.logout(Customizer.withDefaults())
		.logout((logout) -> logout
			.invalidateHttpSession(true) 
			.clearAuthentication(true)
			.logoutSuccessUrl("/login")
			.permitAll()
		)
		;

		return http.build();
	}
	
	@Bean
	public UserDetailsService userDetailsService( ) {
		return userDetailsService;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}

	@Bean
	static RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
		hierarchy.setHierarchy(
				"ROLE_DEVELOPER > ROLE_ADMIN \n "+
				"ROLE_ADMIN > ROLE_USER"
				);
		return hierarchy;
	}

	// and, if using method security also add
	@Bean
	static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy);
		return expressionHandler;
	}	
	
}
