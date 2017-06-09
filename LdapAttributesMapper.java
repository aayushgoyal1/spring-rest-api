package com.ibm.identityservices.template.config;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.AttributesMapper;

import com.ibm.identityservices.template.domain.LdapUser;

@Configuration
public class LdapAttributesMapper implements AttributesMapper<Object> {
      public Object mapFromAttributes(Attributes attrs) throws NamingException {
         LdapUser user = new LdapUser();
         System.out.println("attrs: "+ attrs.toString());
         
         String preferredFirstName = null;
         Attribute preferredFirstNameAttr = (Attribute)attrs.get("preferredFirstName");
         if (preferredFirstNameAttr != null) preferredFirstName = (String)preferredFirstNameAttr.get();
        	 
         String hrFirstName = null;
         Attribute hrFirstNameAttr = (Attribute)attrs.get("hrFirstName");
         if (hrFirstNameAttr != null) hrFirstName = (String)hrFirstNameAttr.get();

         String givenName = null;
         Attribute givenNameAttr = (Attribute)attrs.get("givenName");
         if (givenNameAttr != null) givenName = (String)givenNameAttr.get(0);
         
         String firstName = null;
         if (preferredFirstName != null) firstName = preferredFirstName;
         else if (hrFirstName != null) firstName = hrFirstName;
         else if (givenName != null) firstName = givenName;
         else firstName = "Not found";
         user.setFirstName(firstName);
         
         String preferredLastName = null;
         Attribute preferredLastNameAttr = (Attribute)attrs.get("preferredLastName");
         if (preferredLastNameAttr != null) preferredLastName = (String)preferredLastNameAttr.get();
        	 
         String hrLastName = null;
         Attribute hrLastNameAttr = (Attribute)attrs.get("hrLastName");
         if (hrLastNameAttr != null) hrLastName = (String)hrLastNameAttr.get();

         String surName = null;
         Attribute surNameAttr = (Attribute)attrs.get("sn");
         if (surNameAttr != null) surName = (String)surNameAttr.get(0);
         
         String lastName = null;
         if (preferredLastName != null) lastName = preferredLastName;
         else if (hrLastName != null) lastName = hrLastName;
         else if (surName != null) lastName = surName;
         else lastName = "Not found";
         user.setLastName(lastName);

         String email = null;
         Attribute emailAttr = (Attribute)attrs.get("preferredIdentity");
         if (emailAttr != null) email = (String)emailAttr.get();
         user.setEmail(email);
         
         String uid = null;
         Attribute uidAttr = (Attribute)attrs.get("uid");
         if (uidAttr != null) uid = (String)uidAttr.get();
         user.setUid(uid);

         List<String> blueGroups = new ArrayList<String>();
         Attribute blueGroupsAttr = (Attribute)attrs.get("ibm-allgroups");
         if (blueGroupsAttr != null) {
        	 @SuppressWarnings("unchecked")
			 NamingEnumeration<String> blueGroupsEnum = (NamingEnumeration<String>)blueGroupsAttr.getAll();
        	 while (blueGroupsEnum.hasMore()) {
        		 String item = (String) blueGroupsEnum.next();
        		 blueGroups.add(item);
        	 }
         }
         user.setBlueGroups(blueGroups);
         return user;
      }
}
