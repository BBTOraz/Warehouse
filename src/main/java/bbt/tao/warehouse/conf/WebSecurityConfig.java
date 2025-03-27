package bbt.tao.warehouse.conf;

import bbt.tao.warehouse.dto.audit.AuditLogDTO;
import bbt.tao.warehouse.mapper.UserMapper;
import bbt.tao.warehouse.model.AuditLog;
import bbt.tao.warehouse.model.User;
import bbt.tao.warehouse.model.enums.ActionType;
import bbt.tao.warehouse.security.service.CustomUserDetailsServiceImpl;
import bbt.tao.warehouse.service.impl.AuditLogServiceImpl;
import bbt.tao.warehouse.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsServiceImpl userDetailsService;
    private final UserServiceImpl userService;
    private final AuditLogServiceImpl auditLogService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity https, UserMapper userMapper) throws Exception {
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
                                    var username = authentication.getName();
                                    var userOpt = userService.findUserByUsername(username);

                                    if (userOpt.isPresent()) {
                                        var userDTO = userOpt.get();
                                        User user = userMapper.toEntity(userDTO);
                                        String ipAddress = getClientIpAddress(request);

                                        auditLogService.logAction(user, ActionType.LOGIN, "USER", user.getId(), "ВХОД В СИСТЕМУ", ipAddress);
                                        response.sendRedirect("/dashboard");
                                    }
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

    public String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_REAL_IP"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }


}
