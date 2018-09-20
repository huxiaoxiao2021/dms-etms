package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.bluedragon.distribution.transport.dao.ArSendCodeDao;
import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.bluedragon.distribution.transport.service.ArSendCodeService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public boolean insert(ArSendCode arSendCode) {
		return arSendCodeDao.insert(arSendCode);
	}

	@Override
	public boolean update(ArSendCode arSendCode) {
		return arSendCodeDao.update(arSendCode);
	}

	@Override
	public boolean batchAdd(Long sendRegisterId, String[] sendCodes, String createUser) {
		List<ArSendCode> list = new ArrayList<ArSendCode>(sendCodes.length);
		for (String sendCode : sendCodes) {
			ArSendCode arSendCode = new ArSendCode();
			arSendCode.setSendRegisterId(sendRegisterId);
			arSendCode.setSendCode(sendCode);
			arSendCode.setCreateUser(createUser);
			list.add(arSendCode);
		}
		if (list.size() > 0){
			return this.batchAdd(list);
		}
		return true;
	}

	@Override
	public List<ArSendCode> getBySendRegisterId(Long sendRegisterId) {
		if (sendRegisterId != null && sendRegisterId > 0){
			return arSendCodeDao.getBySendRegisterId(sendRegisterId);
		}
		return null;
	}

	@Override
	public List<ArSendCode> getBySendRegisterIds(List<Long> sendRegisterIds) {
		if (sendRegisterIds != null && !sendRegisterIds.isEmpty()){
			return arSendCodeDao.getBySendRegisterIds(sendRegisterIds);
		}
		return null;
	}

	@Override
	public boolean deleteBySendRegisterId(Long sendRegisterId, String userCode) {
		return arSendCodeDao.deleteBySendRegisterId(sendRegisterId, userCode) > 0 ? true : false;
	}

}
