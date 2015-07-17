package com.jd.bluedragon.distribution.version.service;

import java.util.List;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.jd.bluedragon.distribution.version.dao.ClientConfigHistoryDao;
import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;

@Service("clientConfigHistoryService")
public class ClientConfigHistoryServiceImpl implements
		ClientConfigHistoryService {

	@Autowired
	private ClientConfigHistoryDao clientConfigHistoryDao;
	
	@Profiled(tag = "ClientConfigHistoryService.getAll")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ClientConfigHistory> getAll() {
		return clientConfigHistoryDao.getAll();
	}

	@Profiled(tag = "ClientConfigHistoryService.getBySiteCode")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ClientConfigHistory> getBySiteCode(String siteCode) {
		Assert.notNull(siteCode, "siteCode must not be null");
        return clientConfigHistoryDao.getBySiteCode(siteCode);
	}

	@Profiled(tag = "ClientConfigHistoryService.getByProgramType")
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ClientConfigHistory> getByProgramType(Integer programType) {
		Assert.notNull(programType, "programType must not be null");
        return clientConfigHistoryDao.getByProgramType(programType);
	}

}
