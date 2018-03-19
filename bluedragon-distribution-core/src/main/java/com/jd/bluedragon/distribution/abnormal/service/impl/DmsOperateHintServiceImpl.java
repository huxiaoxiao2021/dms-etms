package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.dao.DmsOperateHintDao;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;

/**
 *
 * @ClassName: DmsOperateHintServiceImpl
 * @Description: 运单操作提示--Service接口实现
 * @author wuyoude
 * @date 2018年03月19日 10:10:39
 *
 */
@Service("dmsOperateHintService")
public class DmsOperateHintServiceImpl extends BaseService<DmsOperateHint> implements DmsOperateHintService {

	@Autowired
	@Qualifier("dmsOperateHintDao")
	private DmsOperateHintDao dmsOperateHintDao;

	@Override
	public Dao<DmsOperateHint> getDao() {
		return this.dmsOperateHintDao;
	}

}
