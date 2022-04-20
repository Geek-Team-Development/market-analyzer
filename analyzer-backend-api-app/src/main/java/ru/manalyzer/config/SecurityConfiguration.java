package ru.manalyzer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.manalyzer.service.AuthenticationServiceImpl;

import javax.servlet.http.HttpServletResponse;


@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    public void authConfig(AuthenticationManagerBuilder authenticationManagerBuilder,
                           PasswordEncoder passwordEncoder,
                           AuthenticationServiceImpl authenticationUserService) throws Exception {

        authenticationManagerBuilder
                .userDetailsService(authenticationUserService)
                .passwordEncoder(passwordEncoder);
    }



    @Configuration
    @Order(1)
    public static class ApiWebSecurityAdapter extends WebSecurityConfigurerAdapter {

        // Token repository with proper configuration for Angular CSRF support
        private CookieCsrfTokenRepository cookieCsrfTokenRepository() {
            CookieCsrfTokenRepository csrfTokenRepository = new CookieCsrfTokenRepository();
            csrfTokenRepository.setCookieHttpOnly(false);
            csrfTokenRepository.setCookiePath("/");

            return csrfTokenRepository;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll()
                    .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessHandler((req, resp, auth) -> resp.setStatus(HttpServletResponse.SC_OK))
                    .and()
                    .httpBasic()
                    .authenticationEntryPoint((req, resp, ex) -> {
                        resp.setContentType("application/json");
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.setCharacterEncoding("UTF-8");
                        resp.getWriter().println("{\"error\": \"" + ex.getMessage() + "\"}");
                    })
                    .and()
                    .csrf()
                    .csrfTokenRepository(cookieCsrfTokenRepository());
        }
    }
}
