package com.jd.bluedragon.distribution.version.service;

import com.jd.bluedragon.distribution.version.dao.ClientVersionDao;
import com.jd.bluedragon.distribution.version.domain.ClientVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service("clientVersionService")
public class ClientVersionServiceImpl implements ClientVersionService {

	@Autowired
	private ClientVersionDao clientVersionDao;
	
    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public List<ClientVersion> getAll() {
		return clientVersionDao.getAll();
	}

    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public List<ClientVersion> getAllAvailable() {
		return clientVersionDao.getAllAvailable();
	}

    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public ClientVersion getById(Long id) {
		Assert.notNull(id, "id must not be null");
        return clientVersionDao.getById(id);
	}

    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public List<ClientVersion> getByVersionCode(String versionCode) {
		Assert.notNull(versionCode, "versionCode must not be null");
        return clientVersionDao.getByVersionCode(versionCode);
	}

    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public List<ClientVersion> getByVersionType(Integer versionType) {
		Assert.notNull(versionType, "versionType must not be null");
        return clientVersionDao.getByVersionType(versionType);
	}
	
    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public boolean exists(ClientVersion clientVersion){
		Assert.notNull(clientVersion, "clientVersion must not be null");
        return clientVersionDao.exists(clientVersion);
	}
	
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(ClientVersion clientVersion) {
		Assert.notNull(clientVersion, "clientVersion must not be null");
        return clientVersionDao.add(clientVersion);
	}

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(ClientVersion clientVersion) {
		Assert.notNull(clientVersion, "clientVersion must not be null");
        return clientVersionDao.update(clientVersion);
	}

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean delete(Long id) {
		Assert.notNull(id, "id must not be null");
        return clientVersionDao.delete(id);
	}

}
