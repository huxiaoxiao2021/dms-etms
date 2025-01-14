package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.transport.domain.ArExcpRegister;
import com.jd.bluedragon.distribution.transport.dao.ArExcpRegisterDao;
import com.jd.bluedragon.distribution.transport.service.ArExcpRegisterService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @ClassName: ArExcpRegisterServiceImpl
 * @Description: 异常登记表--Service接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
@Service("arExcpRegisterService")
public class ArExcpRegisterServiceImpl extends BaseService<ArExcpRegister> implements ArExcpRegisterService {

	@Autowired
	@Qualifier("arExcpRegisterDao")
	private ArExcpRegisterDao arExcpRegisterDao;

	@Override
	public Dao<ArExcpRegister> getDao() {
		return this.arExcpRegisterDao;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean saveOrUpdate(ArExcpRegister arExcpRegister, String userCode, String userName) {
		ArExcpRegister oldData = this.find(arExcpRegister);

		arExcpRegister.setUpdateUser(userCode);

		if(oldData != null){
			arExcpRegister.setId(oldData.getId());
			return this.getDao().update(arExcpRegister);
		}else{

			arExcpRegister.setCreateUser(userCode);
			return this.getDao().insert(arExcpRegister);
		}
	}
}
