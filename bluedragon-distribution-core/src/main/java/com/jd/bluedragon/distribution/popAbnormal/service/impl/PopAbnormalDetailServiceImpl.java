package com.jd.bluedragon.distribution.popAbnormal.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.popAbnormal.dao.PopAbnormalDetailDao;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalDetailService;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:43:35
 * 
 *             POP收货差异明细Service
 */
@Service("popAbnormalDetailService")
public class PopAbnormalDetailServiceImpl implements PopAbnormalDetailService {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private PopAbnormalDetailDao popAbnormalDetailDao;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PopAbnormalDetail findByObj(PopAbnormalDetail popAbnormalDetail) {
		if (popAbnormalDetail == null) {
			this.logger.info("POP差异明细Service --> findByObj 传入验证参数为空");
			return null;
		}
		return this.popAbnormalDetailDao.findByObj(popAbnormalDetail);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<PopAbnormalDetail> findListByAbnormalId(Long abnormalId) {
		this.logger.info("POP差异明细Service --> findListByAbnormalId 传入验证参数为空");
		return this.popAbnormalDetailDao.findListByAbnormalId(abnormalId);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(PopAbnormalDetail popAbnormalDetail) {
		if (popAbnormalDetail == null) {
			this.logger.info("POP差异明细Service --> add 传入验证参数为空");
			return Constants.RESULT_FAIL;
		}
		this.popAbnormalDetailDao.add(PopAbnormalDetailDao.NAME_SPACE,
				popAbnormalDetail);
		return Constants.RESULT_SUCCESS;
	}
}
