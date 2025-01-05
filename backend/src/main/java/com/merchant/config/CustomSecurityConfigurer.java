package com.merchant.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CustomSecurityConfigurer extends AbstractHttpConfigurer<CustomSecurityConfigurer, HttpSecurity> {
    @Override
    public void init(HttpSecurity builder) throws Exception {
        builder.csrf().disable()
               .anonymous().disable()
               .cors().disable()
               .headers().disable()
               .httpBasic().disable()
               .formLogin().disable()
               .logout().disable()
               .sessionManagement().disable();
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        builder.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    }
}
