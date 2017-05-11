package com.jd.bluedragon.distribution.version.service;

import com.jd.bluedragon.distribution.version.dao.ClientConfigHistoryDao;
import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service("clientConfigHistoryService")
public class ClientConfigHistoryServiceImpl implements
		ClientConfigHistoryService {

	@Autowired
	private ClientConfigHistoryDao clientConfigHistoryDao;
	
    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public List<ClientConfigHistory> getAll() {
		return clientConfigHistoryDao.getAll();
	}

    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public List<ClientConfigHistory> getBySiteCode(String siteCode) {
		Assert.notNull(siteCode, "siteCode must not be null");
        return clientConfigHistoryDao.getBySiteCode(siteCode);
	}

    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public List<ClientConfigHistory> getByProgramType(Integer programType) {
		Assert.notNull(programType, "programType must not be null");
        return clientConfigHistoryDao.getByProgramType(programType);
	}

}
