package com.ibm.identityservices.template.config;

import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import com.ibm.identityservices.template.domain.LdapUser;

@Configuration
public class LdapAuthenticationProvider implements AuthenticationProvider {

    protected final Log logger = LogFactory.getLog(getClass());
    
	@Autowired
	LdapConfiguration ldapConfig = new LdapConfiguration();
	@Value("${ldap.client.base}")
	private String searchBase="ou=bluepages,o=ibm.com";
	@Value("${ldap.client.filter}")
	private String searchFilter="(mail={0})";
    
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        logger.debug("Beginning authentication...");
        if(!StringUtils.hasText((String) authentication.getPrincipal()) || !StringUtils.hasText((String) authentication.getCredentials())){
            logger.info("Invalid username/password :"+authentication.getPrincipal()+" / "+authentication.getCredentials());
            throw new BadCredentialsException("Invalid username/password");
        }
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
 
        if (username != null && password != null) {
        	
        	boolean authenticated = ldapConfig.ldapTemplate().authenticate("", "(mail="+username+")", password);
        	
        	if (authenticated) {
        		logger.debug("User " + username + " successfully authenticated.");
        		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_API");

    			LdapAttributesMapper attributeMapper = new LdapAttributesMapper();
    			searchFilter = searchFilter.replace((CharSequence)"{0}", (CharSequence)username);
    			SearchControls controls = new SearchControls();
    			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    			controls.setReturningAttributes(new String[] {"preferredIdentity", "ibm-allgroups", "preferredFirstName", 
    					"hrFirstName", "givenName", "preferredLastName", "hrLastName", "sn", "uid"});
    			List<Object> userList = (List<Object>) ldapConfig.ldapTemplate().search("", searchFilter, controls, attributeMapper);
    			
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
                    return new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), authorities);
    			} else {
                    logger.info("Invalid username/password:"+username+" / "+password);
                    throw new BadCredentialsException("Could not find user in LDAP.");
    			}
        	} else {
                logger.info("Invalid username/password:"+username+" / "+password);
                throw new BadCredentialsException("Invalid username/password");
        	}
        }
        else{
            logger.info("Invalid username/password:"+username+" / "+password);
            throw new BadCredentialsException("Username/password not specified.");
        }
    }

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(
		          UsernamePasswordAuthenticationToken.class);
	}

}
