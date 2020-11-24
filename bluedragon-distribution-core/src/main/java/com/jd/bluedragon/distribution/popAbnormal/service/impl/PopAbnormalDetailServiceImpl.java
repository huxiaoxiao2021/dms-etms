package com.jd.bluedragon.distribution.popAbnormal.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.popAbnormal.dao.PopAbnormalDetailDao;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:43:35
 * 
 *             POP收货差异明细Service
 */
@Service("popAbnormalDetailService")
public class PopAbnormalDetailServiceImpl implements PopAbnormalDetailService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PopAbnormalDetailDao popAbnormalDetailDao;

	@Override
	public PopAbnormalDetail findByObj(PopAbnormalDetail popAbnormalDetail) {
		if (popAbnormalDetail == null) {
			this.log.info("POP差异明细Service --> findByObj 传入验证参数为空");
			return null;
		}
		return this.popAbnormalDetailDao.findByObj(popAbnormalDetail);
	}

	@Override
	public List<PopAbnormalDetail> findListByAbnormalId(Long abnormalId) {
		this.log.info("POP差异明细Service --> findListByAbnormalId 传入验证参数为空");
		return this.popAbnormalDetailDao.findListByAbnormalId(abnormalId);
	}

	@Override
	public int add(PopAbnormalDetail popAbnormalDetail) {
		if (popAbnormalDetail == null) {
			this.log.info("POP差异明细Service --> add 传入验证参数为空");
			return Constants.RESULT_FAIL;
		}
		this.popAbnormalDetailDao.add(PopAbnormalDetailDao.NAME_SPACE,
				popAbnormalDetail);
		return Constants.RESULT_SUCCESS;
	}
}
