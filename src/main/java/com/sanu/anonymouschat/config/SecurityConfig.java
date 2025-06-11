package com.sanu.anonymouschat.config;

import com.sanu.anonymouschat.service.CustomUserDetailsService;
import com.sanu.anonymouschat.service.UserService; // Keep UserService import
// import com.sanu.anonymouschat.util.CustomLoginSuccessHandler; // REMOVE THIS IMPORT
import com.sanu.anonymouschat.util.CustomLogoutHandler;
import com.sanu.anonymouschat.util.CustomLogoutSuccessHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final UserService userService; // Keep UserService injection
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomLogoutHandler customLogoutHandler;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          UserService userService,
                          CustomLogoutSuccessHandler customLogoutSuccessHandler,
                          CustomLogoutHandler customLogoutHandler,
                          PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.customLogoutHandler = customLogoutHandler;
        this.passwordEncoder = passwordEncoder;
    }

    // REMOVE THE customLoginSuccessHandlerBean() method as it will no longer be used directly here.

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/register", "/auth/login", "/", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/postlogin-redirect").authenticated() // Allow access to our new redirect endpoint
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("GIRL", "BOY", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/authenticateTheUser")
                        // >>>>>> ALL successful logins now redirect to this single endpoint <<<<<<
                        // This handles both initial login and "back button" scenarios for authenticated users.
                        .defaultSuccessUrl("/postlogin-redirect", true)
                        // .successHandler(customLoginSuccessHandlerBean()) // REMOVE THIS LINE
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}