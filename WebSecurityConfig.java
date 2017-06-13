package com.identityservices.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  LdapAuthenticationProvider authenticationProvider;

  @Autowired
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
          .authenticationProvider(authenticationProvider);
  }


}
