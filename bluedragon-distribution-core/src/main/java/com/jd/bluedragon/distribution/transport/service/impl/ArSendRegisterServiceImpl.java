package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.dao.ArSendRegisterDao;
import com.jd.bluedragon.distribution.transport.service.ArSendRegisterService;

/**
 *
 * @ClassName: ArSendRegisterServiceImpl
 * @Description: 发货登记表--Service接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
@Service("arSendRegisterService")
public class ArSendRegisterServiceImpl extends BaseService<ArSendRegister> implements ArSendRegisterService {

	@Autowired
	@Qualifier("arSendRegisterDao")
	private ArSendRegisterDao arSendRegisterDao;

	@Override
	public Dao<ArSendRegister> getDao() {
		return this.arSendRegisterDao;
	}

}
