package com.jd.bluedragon.distribution.reverse.part.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.reverse.part.domain.ReversePartDetail;
import com.jd.bluedragon.distribution.reverse.part.dao.ReversePartDetailDao;
import com.jd.bluedragon.distribution.reverse.part.service.ReversePartDetailService;

/**
 *
 * @ClassName: ReversePartDetailServiceImpl
 * @Description: 半退明细表--Service接口实现
 * @author wuyoude
 * @date 2019年02月12日 11:40:45
 *
 */
@Service("reversePartDetailService")
public class ReversePartDetailServiceImpl extends BaseService<ReversePartDetail> implements ReversePartDetailService {

	@Autowired
	@Qualifier("reversePartDetailDao")
	private ReversePartDetailDao reversePartDetailDao;

	@Override
	public Dao<ReversePartDetail> getDao() {
		return this.reversePartDetailDao;
	}

}
