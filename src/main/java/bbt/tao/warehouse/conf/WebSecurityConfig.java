package bbt.tao.warehouse.conf;

import bbt.tao.warehouse.security.service.CustomUserDetailsServiceImpl;
import bbt.tao.warehouse.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsServiceImpl userDetailsService;
    private final UserServiceImpl userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity https) throws Exception {
        return https.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().authenticated())
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .permitAll()
                                .successHandler((request, response, authentication) -> {
                                    userService.recordLogin(authentication.getName());
                                    response.sendRedirect("/dashboard");
                                }))
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                                .deleteCookies("JSESSIONID")
                                .invalidateHttpSession(true))
                .rememberMe(rememberMe ->
                        rememberMe
                                .key("warehouse-remember-me-key")
                                .userDetailsService(userDetailsService)
                                .useSecureCookie(true))
                .build();
    }


}
