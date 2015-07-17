package com.jd.bluedragon.distribution.version.service;

import java.util.List;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.jd.bluedragon.distribution.version.dao.ClientVersionDao;
import com.jd.bluedragon.distribution.version.domain.ClientVersion;

@Service("clientVersionService")
public class ClientVersionServiceImpl implements ClientVersionService {

	@Autowired
	private ClientVersionDao clientVersionDao;
	
	@Profiled(tag = "ClientVersionService.getAll")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ClientVersion> getAll() {
		return clientVersionDao.getAll();
	}

	@Profiled(tag = "ClientVersionService.getAllAvailable")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ClientVersion> getAllAvailable() {
		return clientVersionDao.getAllAvailable();
	}

	@Profiled(tag = "ClientVersionService.getById")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ClientVersion getById(Long id) {
		Assert.notNull(id, "id must not be null");
        return clientVersionDao.getById(id);
	}

	@Profiled(tag = "ClientVersionService.getByVersionCode")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ClientVersion> getByVersionCode(String versionCode) {
		Assert.notNull(versionCode, "versionCode must not be null");
        return clientVersionDao.getByVersionCode(versionCode);
	}

	@Profiled(tag = "ClientVersionService.getByVersionType")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ClientVersion> getByVersionType(Integer versionType) {
		Assert.notNull(versionType, "versionType must not be null");
        return clientVersionDao.getByVersionType(versionType);
	}
	
	@Profiled(tag = "ClientVersionService.exists")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public boolean exists(ClientVersion clientVersion){
		Assert.notNull(clientVersion, "clientVersion must not be null");
        return clientVersionDao.exists(clientVersion);
	}
	
	@Profiled(tag = "ClientVersionService.add")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean add(ClientVersion clientVersion) {
		Assert.notNull(clientVersion, "clientVersion must not be null");
        return clientVersionDao.add(clientVersion);
	}

	@Profiled(tag = "ClientVersionService.update")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean update(ClientVersion clientVersion) {
		Assert.notNull(clientVersion, "clientVersion must not be null");
        return clientVersionDao.update(clientVersion);
	}

	@Profiled(tag = "ClientVersionService.delete")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean delete(Long id) {
		Assert.notNull(id, "id must not be null");
        return clientVersionDao.delete(id);
	}

}
