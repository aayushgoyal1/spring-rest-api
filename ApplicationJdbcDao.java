package com.template.dao;

import java.util.List;

public interface ApplicationJdbcDao {

	/**
	 * Return all applications regardless of user.
	 * @return
	 */
    public List<Application> findAll();

    /**
     * Returns all rows in the application table for specified registered_by user.
     * @return
     */
    public List<Application> findApplicationsByRegisteredUser(String registered_by);
   
    /**
     * Check if the application exists for the specified appId.
     * @param appId
     * @return
     */
    public Application findApplicationByAppId(String appId);
 
    /**
     * Check if the application already exists.
     * @param app
     * @return
     */
    public boolean isApplicationExist(Application app);
    
    /**
     * Deletes a record in the Application table that matches appId.  Returns # of rows affected.
     * @param appId
     * @return
     */
    public int delete(String appId);
 
    /**
     * Creates a record in the Application table.
     * @param app
     * @return
     */
    public Application create(final Application app);
    
    /**
     * Updates a record in the Application table.
     * @param app
     * @return
     */
    public Application update(final Application app);
 
}

