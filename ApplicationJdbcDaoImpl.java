package com.identityservices.template.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.identityservices.template.dao.ApplicationJdbcDao;
import com.identityservices.template.domain.Application;

@Repository(value="applicationJdbcDao")
public class ApplicationJdbcDaoImpl implements ApplicationJdbcDao
{
    public static final Logger logger = LoggerFactory.getLogger(ApplicationJdbcDaoImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
 
    /**
     * Returns all applications in the application table.
     * @return
     */
    @Transactional(readOnly=true)
    public List<Application> findAll() {
        return jdbcTemplate.query("select * from db2inst1.application", 
                new ApplicationRowMapper());
    }

    /**
     * Returns all rows in the application table for specified registered_by user.
     * @return
     */
    @Transactional(readOnly=true)
    public List<Application> findApplicationsByRegisteredUser(String registered_by) {
    	try {
            return jdbcTemplate.query("select * from db2inst1.application where registered_by=?",
            		new Object[]{registered_by+".com"}, // appending .com because something strips it.
                    new ApplicationRowMapper());		// when using email parm in REST client.
    	} catch (EmptyResultDataAccessException e) {
    		logger.debug("Application not found for registered_by " + registered_by);
    		return null;
    	}
    }
   
    /**
     * Check if the application exists for the specified appId.
     * @param appId
     * @return
     */
    @Transactional(readOnly=true)
    public Application findApplicationByAppId(String appId) {
    	try {
            return jdbcTemplate.queryForObject(
                    "select * from db2inst1.application where app_id=?",
                    new Object[]{appId}, new ApplicationRowMapper());
    	} catch (EmptyResultDataAccessException e) {
    		logger.debug("Application not found for appId " + appId);
    		return null;
    	}
    }
 
    /**
     * Check if the application already exists.
     * @param app
     * @return
     */
    @Transactional(readOnly=true)
    public boolean isApplicationExist(Application app) {
    	Application foundApp = null;
    	try {
    		foundApp = jdbcTemplate.queryForObject(
                    "select * from db2inst1.application where name=?",
                    new Object[]{app.getName()}, new ApplicationRowMapper());
    	} catch (EmptyResultDataAccessException e) {
    		logger.debug("Application not found for name " + app.getName());
    		// don't log exception
    	}
    	return (foundApp != null ? true : false);
    	
    }    
    /**
     * Deletes a record in the Application table that matches appId.  Returns # of rows affected.
     * @param appId
     * @return
     */
    public int delete(String appId) {
    	try {
            return jdbcTemplate.update(
                    "delete from db2inst1.application where app_id=?",
                    new Object[]{appId});
    	} catch (DataAccessException e) {
    		e.printStackTrace();
    		throw e;
    	}
    }
 
    /**
     * Creates a record in the Application table.
     * @param app
     * @return
     */
    public Application create(final Application app) 
    {
        app.setAppId(UUID.randomUUID().toString());

        final String sql = "insert into db2inst1.application " +
        		"(app_id,name,description,registered_by,url) " +
        		"values (?,?,?,?,?)";
 
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, app.getAppId());
                    ps.setString(2, app.getName());
                    ps.setString(3, app.getDescription());
                    ps.setString(4, app.getRegisteredBy());
                    ps.setString(5, app.getUrl());
                    return ps;
                }
            });
        } catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        }
 
        return app;
    }
    
    /**
     * Updates a record in the Application table.
     * @param app
     * @return
     */
    public Application update(final Application app) 
    {
        final String sql = "update db2inst1.application set " +
        		"name=?,description=?,registered_by=?,url=?,update_application_ts=? " +
        		"where app_id = ?";
 
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, app.getName());
                    ps.setString(2, app.getDescription());
                    ps.setString(3, app.getRegisteredBy());
                    ps.setString(4, app.getUrl());
                    ps.setTimestamp(5, Timestamp.from(new Date().toInstant()));
                    ps.setString(6, app.getAppId());
                    return ps;
                }
            });
        } catch (Exception e) {
        	e.printStackTrace();
        	throw e;
        }
 
        return app;
    }
}
 
class ApplicationRowMapper implements RowMapper<Application>
{
    @Override
    public Application mapRow(ResultSet rs, int rowNum) throws SQLException {
        Application app = new Application();
        app.setAppId(rs.getString("APP_ID"));
        app.setName(rs.getString("NAME"));
        app.setDescription(rs.getString("DESCRIPTION"));
        app.setRegisteredBy(rs.getString("REGISTERED_BY"));
        app.setUrl(rs.getString("URL"));
        app.setCreateTimestamp(Date.from(rs.getTimestamp("CREATE_APPLICATION_TS").toInstant()));
        app.setUpdateTimestamp(Date.from(rs.getTimestamp("UPDATE_APPLICATION_TS").toInstant()));
        return app;
    }
}
