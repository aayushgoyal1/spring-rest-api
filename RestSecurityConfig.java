package com.identityservices.template.config;

import java.util.Arrays;
import java.util.HashSet;

import org.mitre.oauth2.introspectingfilter.IntrospectingTokenService;
import org.mitre.oauth2.introspectingfilter.service.impl.StaticIntrospectionConfigurationService;
import org.mitre.oauth2.model.ClientDetailsEntity.AppType;
import org.mitre.oauth2.model.ClientDetailsEntity.AuthMethod;
import org.mitre.oauth2.model.ClientDetailsEntity.SubjectType;
import org.mitre.oauth2.model.RegisteredClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
public class RestSecurityConfig extends ResourceServerConfigurerAdapter {

	@Value("${oidc.client.client-id}")
	private String clientId;
	@Value("${oidc.client.client-secret}")
	private String clientSecret;
	@Value("${oidc.client.client-name}")
	private String clientName;
	@Value("${oidc.client.introspect-url}")
	private String introspectUrl;

	@Override
	public void configure(HttpSecurity http) throws Exception {
        http
			.authorizeRequests()
				.antMatchers("/static/**", "/fonts/**", "/css/**", "/js/**", "/images/**").permitAll()
				.anyRequest().authenticated()
				.and().httpBasic()
				.and().csrf().disable();
	}	
	
	@Bean
    public ResourceServerTokenServices tokenServices() {
		
		// limited client configuration for introspect only
		RegisteredClient client = new RegisteredClient();
		client.setClientId(clientId);
		client.setClientSecret(clientSecret);
		client.setAllowIntrospection(Boolean.TRUE);
		client.setApplicationType(AppType.WEB);
		client.setClientName(clientName);
		client.setScope(new HashSet<String>(Arrays.asList("openid")));
		client.setSubjectType(SubjectType.PUBLIC);
		client.setTokenEndpointAuthMethod(AuthMethod.SECRET_POST);

		StaticIntrospectionConfigurationService introspectConfigurationService = new StaticIntrospectionConfigurationService();
		introspectConfigurationService.setClientConfiguration(client);
		introspectConfigurationService.setIntrospectionUrl(introspectUrl);
		
		IntrospectingTokenService its = new IntrospectingTokenService();
		its.setIntrospectionConfigurationService(introspectConfigurationService);
		its.setIntrospectionAuthorityGranter(new W3IDIntrospectionAuthorityGranter());
	
        return its;
    } 
}
