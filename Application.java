package com.identityservices.template.domain;

import java.util.Date;

public class Application {

	String appId;
	String name;
	String description;
	String url;
	String registeredBy;
	Date createTimestamp;
	Date updateTimestamp;
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRegisteredBy() {
		return registeredBy;
	}

	public void setRegisteredBy(String registeredBy) {
		this.registeredBy = registeredBy;
	}

	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	@Override
	public String toString() {
		return "Application [appId=" + appId + ", name=" + name
				+ ", description=" + description + ", url=" + url
				+ ", registeredBy=" + registeredBy + ", createTimestamp="
				+ createTimestamp + ", updateTimestamp=" + updateTimestamp
				+ "]";
	}
}
