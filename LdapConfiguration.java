package com.identityservices.template.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfiguration {

	@Value("${ldap.client.url}")
	private String ldapUrl="null";

	@Bean
    @ConfigurationProperties(prefix="ldap.client")
    public LdapContextSource contextSource(){
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
		contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(){
    	System.out.println("LDAP URL: " + ldapUrl);
        LdapTemplate template = new LdapTemplate(contextSource());
        return template;
    }


}
