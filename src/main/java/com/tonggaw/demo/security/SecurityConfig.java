package com.tonggaw.demo.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tonggaw.demo.service.CustomUserDetailsService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF protection matcher สำหรับ endpoints ที่ต้องการป้องกัน CSRF
        RequestMatcher requireCsrfProtectionMatcher = new OrRequestMatcher(
            request -> "POST".equals(request.getMethod()) &&
                       request.getServletPath().equals("/auth/login"),
            request -> "POST".equals(request.getMethod()) &&
                       request.getServletPath().equals("/auth/register"),
            request -> "POST".equals(request.getMethod()) &&
                       request.getServletPath().equals("/auth/logout")
        );

        http
            // Security Context configuration
            .securityContext(securityContext -> securityContext
                .securityContextRepository(new DelegatingSecurityContextRepository(
                    new RequestAttributeSecurityContextRepository(),
                    new HttpSessionSecurityContextRepository()
                ))
                .requireExplicitSave(true)
            )
            
           
            .csrf(csrf -> csrf
                .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                .requireCsrfProtectionMatcher(requireCsrfProtectionMatcher)
            )
            // .csrf(csrf -> csrf.disable())
            
            // CORS configuration
            .cors(cors -> cors
                .configurationSource(apiConfigurationSource())
            )
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - ไม่ต้อง authenticate
                .requestMatchers(
                    "/auth/login",
                    "/auth/register",
                    "/csrfApi/getCsrf",
                    "/error",
                    "/auth/me"
                )
                .permitAll()

                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()
                
                // Endpoints ที่ต้องมี authority "CEO" เท่านั้น
                .requestMatchers(
                    "/api/add-product-cost",
                    "/api/products/cost"
                )
                .hasAnyAuthority("CEO")

                .requestMatchers(HttpMethod.GET, "/saleApi", "/saleApi/**")
                .hasAnyAuthority("CEO")
                
                // ทุก request อื่นๆ ต้อง authenticate
                .anyRequest()
                .authenticated()
            )
            .logout(logout -> logout
					.invalidateHttpSession(true)
					.clearAuthentication(true)
					.deleteCookies("JSESSIONID")
					.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
					.permitAll()
				);


        return http.build();
    }
    @Bean
	protected BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	protected AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService());
		
		authProvider.setPasswordEncoder(passwordEncoder());
		ProviderManager providerManager = new ProviderManager(authProvider);
		providerManager.setEraseCredentialsAfterAuthentication(false);
		
		return providerManager;
	}
	
	@Bean
	protected CustomUserDetailsService customUserDetailsService() {
		return new CustomUserDetailsService();
	}

    @Bean
    protected CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://frontend-demo-qptz.vercel.app",
                "http://localhost:4200"
        ));
        configuration.setAllowedOrigins(Arrays.asList(
             "https://frontend-demo-qptz.vercel.app"
        ));
       
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(Arrays.asList("X-CSRF-TOKEN", "Set-Cookie"));
		configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
		return source;

    }
}
