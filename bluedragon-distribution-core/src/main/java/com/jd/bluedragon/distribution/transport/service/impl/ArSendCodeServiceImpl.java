package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.dao.ArSendCodeDao;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;

/**
 *
 * @ClassName: ArSendCodeServiceImpl
 * @Description: 发货批次表--Service接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
@Service("arSendCodeService")
public class ArSendCodeServiceImpl extends BaseService<ArSendCode> implements ArSendCodeService {

	@Autowired
	@Qualifier("arSendCodeDao")
	private ArSendCodeDao arSendCodeDao;

	@Override
	public Dao<ArSendCode> getDao() {
		return this.arSendCodeDao;
	}

}
