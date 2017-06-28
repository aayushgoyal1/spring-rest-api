import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="applicationService")
public class ApplicationServiceImpl extends DefaultServiceImpl implements ApplicationService {

	ApplicationJdbcDao applicationJdbcDao;

	@Autowired
	public ApplicationServiceImpl (ApplicationJdbcDao applicationJdbcDao) {
		this.applicationJdbcDao = applicationJdbcDao;
	}

	public void setApplicationJdbcDao (ApplicationJdbcDao applicationJdbcDao) {
		this.applicationJdbcDao = applicationJdbcDao;
	}
	
	@Override
	public List<Application> findAll() {
		return applicationJdbcDao.findAll();
	}

	@Override
	public List<Application> findApplicationsByRegisteredUser(String registeredBy) {
		return applicationJdbcDao.findApplicationsByRegisteredUser(registeredBy);
	}

	@Override
	public Application findApplicationByAppId(String appId) {
		return applicationJdbcDao.findApplicationByAppId(appId);
	}

	@Override
	public boolean isApplicationExist(Application app) {
		return applicationJdbcDao.isApplicationExist(app);
	}

	@Override
	public int delete(String appId) {
		return applicationJdbcDao.delete(appId);
	}

	@Override
	public Application create(Application app) {
		return applicationJdbcDao.create(app);
	}

	@Override
	public Application update(Application app) {
		return applicationJdbcDao.update(app);
	}
	

}
