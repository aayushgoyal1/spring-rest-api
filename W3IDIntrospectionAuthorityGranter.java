package com.identityservices.template.config;

import java.util.List;

import javax.naming.directory.SearchControls;

import org.mitre.oauth2.introspectingfilter.service.IntrospectionAuthorityGranter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.identityservices.template.domain.LdapUser;

@Component
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix="ldap.client")
public class W3IDIntrospectionAuthorityGranter implements IntrospectionAuthorityGranter {

	@Autowired
	LdapConfiguration ldapConfig = new LdapConfiguration();
	
	@Value("${ldap.client.base}")
	private String searchBase="ou=bluepages,o=null";
	@Value("${ldap.client.filter}")
	private String searchFilter="(mail={0})";
	
	private List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_API");

	/* (non-Javadoc)
	 * @see org.mitre.oauth2.introspectingfilter.IntrospectionAuthorityGranter#getAuthorities(net.minidev.json.JSONObject)
	 */
	@Override
	public List<GrantedAuthority> getAuthorities(JsonObject introspectionResponse) {
		
		JsonElement userid = introspectionResponse.get("sub");
		if (userid!=null) {
			String userString = userid.getAsString();
			System.out.println("user: "+userString);
			// grab bluegroups and add roles for each.
			
			LdapAttributesMapper attributeMapper = new LdapAttributesMapper();
			searchFilter = searchFilter.replace((CharSequence)"{0}", (CharSequence)userString);
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(new String[] {"preferredIdentity", "id-allgroups", "preferredFirstName", 
					"hrFirstName", "givenName", "preferredLastName", "hrLastName", "sn", "uid"});
			List<Object> userList = (List<Object>) ldapConfig.ldapTemplate().search(searchBase, searchFilter, controls, attributeMapper);
			if (userList != null && userList.size() > 0) {
				LdapUser user = (LdapUser)userList.get(0);
				List<String> blueGroups = (List<String>) user.getBlueGroups();
				for (int i=0; i<blueGroups.size(); i++) {
					String group = (String)blueGroups.get(i);
					if (group.contains("cn=")) {
						group = group.substring(group.indexOf("cn=")+3, group.indexOf(","));
					}
					authorities.add(new SimpleGrantedAuthority("ROLE_"+group));
				}
			}
		}
		return authorities;
	}

	/**
	 * @return the authorities
	 */
	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/**
	 * @param authorities the authorities to set
	 */
	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

}
