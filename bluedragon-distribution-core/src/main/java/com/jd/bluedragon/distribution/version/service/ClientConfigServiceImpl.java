package com.jd.bluedragon.distribution.version.service;

import com.jd.bluedragon.distribution.version.dao.ClientConfigDao;
import com.jd.bluedragon.distribution.version.dao.ClientConfigHistoryDao;
import com.jd.bluedragon.distribution.version.domain.ClientConfig;
import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;
import com.jd.bluedragon.distribution.version.domain.VersionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service("clientConfigService")
public class ClientConfigServiceImpl implements ClientConfigService {
	
	@Autowired
    private ClientConfigDao clientConfigDao;
	
	@Autowired
    private ClientConfigHistoryDao clientConfigHistoryDao;
	
    public List<ClientConfig> getAll() {
        return this.clientConfigDao.getAll();
	}

    public ClientConfig getById(Long id) {
		Assert.notNull(id, "id must not be null");
        return this.clientConfigDao.getById(id);
	}

    public List<ClientConfig> getBySiteCode(String siteCode) {
		Assert.notNull(siteCode, "siteCode must not be null");
        return this.clientConfigDao.getBySiteCode(siteCode);
	}

    public VersionEntity getVersionEntity(VersionEntity versionEntity) {
		Assert.notNull(versionEntity, "versionEntity must not be null");
        return this.clientConfigDao.getVersionEntity(versionEntity);
	}

    public List<ClientConfig> getByProgramType(Integer programType) {
		Assert.notNull(programType, "programType must not be null");
        return this.clientConfigDao.getByProgramType(programType);
	}
	
    public boolean exists(ClientConfig clientConfig){
		Assert.notNull(clientConfig, "clientConfig must not be null");
        return this.clientConfigDao.exists(clientConfig);
	}

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(ClientConfig clientConfig) {
		Assert.notNull(clientConfig, "clientConfig must not be null");
        return this.clientConfigDao.add(clientConfig);
	}

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(ClientConfig clientConfig) {
		Assert.notNull(clientConfig, "clientConfig must not be null");
		clientConfigHistoryDao.add(toClientConfigHistory(clientConfig));
        return this.clientConfigDao.update(clientConfig);
	}

	private ClientConfigHistory toClientConfigHistory(ClientConfig clientConfig) {
	    ClientConfigHistory clientConfigHistory=new ClientConfigHistory();
	    clientConfigHistory.setProgramType(clientConfig.getProgramType());
	    clientConfigHistory.setSiteCode(clientConfig.getSiteCode());
	    clientConfigHistory.setVersionCode(clientConfig.getVersionCode());
        return clientConfigHistory;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean delete(Long id) {
		Assert.notNull(id, "id must not be null");
        return this.clientConfigDao.delete(id);
	}

}
