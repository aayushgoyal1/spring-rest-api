package com.identityservices.template.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.identityservices.template.domain.Application;
import com.identityservices.template.service.ApplicationService;
import com.identityservices.template.service.LoggingService;
 
@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api")
public class ApplicationController {
 
	ApplicationService applicationService;
	 
    public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@Autowired
    LoggingService loggingService;
    
	@Autowired
	public ApplicationController (ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

    // -------------------Retrieve All Applications---------------------------------------------
 
    @RequestMapping(value = "/application/", method = RequestMethod.GET)
    public ResponseEntity<List<Application>> listAllApplications() {
        List<Application> apps = applicationService.findAll();
        if (apps.isEmpty()) {
            return new ResponseEntity<List<Application>>(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Application>>(apps, HttpStatus.OK);
    }

    
    // -------------------Retrieve All Applications by User ---------------------------------------------
    
    @RequestMapping(value = "/application/user/{registeredBy}", method = RequestMethod.GET)
    public ResponseEntity<List<Application>> listApplicationsForUser(@PathVariable("registeredBy") String registeredBy) {
    	loggingService.info(this.getClass(), "Looking up applications for user: " + registeredBy);
    	List<Application> apps = applicationService.findApplicationsByRegisteredUser(registeredBy);
        if (apps.isEmpty()) {
            return new ResponseEntity<List<Application>>(HttpStatus.NO_CONTENT);
            // You many decide to return HttpStatus.NOT_FOUND
        }
        return new ResponseEntity<List<Application>>(apps, HttpStatus.OK);
    }
    // -------------------Retrieve Single Application ------------------------------------------
 
	@RequestMapping(value = "/application/{appId}", method = RequestMethod.GET)
    public ResponseEntity<?> getApplication(@PathVariable("appId") String appId) {
		OAuth2Authentication token = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		loggingService.debug(this.getClass(), "Fetching Application with appId {} for user {}", appId, token!=null?token.getPrincipal():"unauth");
        Application app = applicationService.findApplicationByAppId(appId);
        if (app == null) {
        	loggingService.error(this.getClass(), "Application with appId {} not found.", appId);
            return new ResponseEntity<Object>(new CustomErrorType("Application with appId " + appId 
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Application>(app, HttpStatus.OK);
    }
 
    // -------------------Create an Application -------------------------------------------
 
    @RequestMapping(value = "/application/", method = RequestMethod.POST)
    public ResponseEntity<?> createApplicaiton(@RequestBody Application app, UriComponentsBuilder ucBuilder) {
		OAuth2Authentication token = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
		loggingService.debug(this.getClass(), "Creating Application : {}", app);
 
        if (applicationService.isApplicationExist(app)) {
        	loggingService.error(this.getClass(), "Unable to create. An application with name {} already exist", app.getName());
            return new ResponseEntity<Object>(new CustomErrorType("Unable to create. An application with name " + 
            app.getName() + " already exist."),HttpStatus.CONFLICT);
        }
        app.setRegisteredBy(token!=null?token.getPrincipal().toString():"anonymous");  // the identity calling it registers it.
        applicationService.create(app);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/application/{appId}").buildAndExpand(app.getAppId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
 
    // ------------------- Update an Application ------------------------------------------------
 
    @RequestMapping(value = "/application/{appId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateApplication(@PathVariable("appId") String appId, @RequestBody Application app) {
    	loggingService.debug(this.getClass(), "Updating Application with appId {}", appId);
 
        Application currentApp = applicationService.findApplicationByAppId(appId);
 
        if (currentApp == null) {
        	loggingService.error(this.getClass(), "Unable to update. Application with appId {} not found.", appId);
            return new ResponseEntity<Object>(new CustomErrorType("Unable to upate. Application with appId " + appId + " not found."),
                    HttpStatus.NOT_FOUND);
        }
 
        currentApp.setName(app.getName());
        currentApp.setDescription(app.getDescription());
        currentApp.setUrl(app.getUrl());
 
        applicationService.update(currentApp);
        return new ResponseEntity<Application>(currentApp, HttpStatus.OK);
    }
 
    // ------------------- Delete an Application -----------------------------------------
    @RequestMapping(value = "/application/{appId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteApplication(@PathVariable("appId") String appId) {
		OAuth2Authentication token = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    	loggingService.info(this.getClass(), "Fetching & Deleting Application with appId {}", appId);
 
        Application app = applicationService.findApplicationByAppId(appId);
        if (app == null) {
        	loggingService.error(this.getClass(), "Unable to delete. Application with appId {} not found.", appId);
            return new ResponseEntity<Object>(new CustomErrorType("Unable to delete. Application with appId " + appId + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        if (!app.getRegisteredBy().equals(token!=null?token.getPrincipal().toString():"anonymous")) {
        	loggingService.error(this.getClass(), "Unable to delete. User {} is not authorized to delete this application.", token!=null?token.getPrincipal().toString():"anonymous");
            return new ResponseEntity<Object>(new CustomErrorType("Unable to delete. User " + token!=null?token.getPrincipal().toString():"anonymous" + " is not authorized to delete this application."),
                    HttpStatus.FORBIDDEN);
        }
        applicationService.delete(appId);
        return new ResponseEntity<Application>(HttpStatus.NO_CONTENT);
    }
 
}
