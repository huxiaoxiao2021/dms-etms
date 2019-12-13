package com.jd.bluedragon.distribution.popReveice.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.popPrint.dao.PopPrintDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popReveice.dao.PopReceiveDao;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;
import com.jd.bluedragon.distribution.popReveice.service.PopReceiveService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-21 下午09:01:51
 *
 * POP收货处理服务实现
 */
@Service("popReceiveService")
public class PopReceiveServiceImpl implements PopReceiveService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PopPrintDao popPrintDao;
	
	@Autowired
	private InspectionService inspectionService;
	
	@Autowired
	private PopReceiveDao popReceiveDao;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public int saveRecevie(Inspection inspection) {
		this.log.info("补全订单收货信息，inspection:{}" , inspection);
		return inspectionService.addInspectionPop(inspection);
	}

	@Override
	public PopPrint findPopPrint(String waybillCode) {
		this.log.info("根据运单号查询打印信息，waybillCode:{}" , waybillCode);
		return popPrintDao.findByWaybillCode(waybillCode);
	}
	

	@Override
	public List<PopReceive> findListByParamMap(Map<String, Object> paramMap) {
		this.log.info("findListByParamMap --> paramMap:{}" , paramMap);
		if (paramMap == null || paramMap.isEmpty()) {
			return null;
		}
		return this.popReceiveDao.findListByPopReceive(paramMap);
	}
	
	@Override
	public PopReceive findByFingerPrint(String fingerPrint) {
		this.log.info("findByFingerPrint --> fingerPrint:{}" , fingerPrint);
		if (StringUtils.isBlank(fingerPrint)) {
			return null;
		}
		return this.popReceiveDao.findByFingerPrint(fingerPrint);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public int addReceive(PopReceive popReceive) {
		this.log.info("addReceive --> popReceive:{}" , popReceive);
		this.popReceiveDao.add(PopReceiveDao.namespace, popReceive);
		return Constants.RESULT_SUCCESS;
	}

	@Override
	public List<PopReceive> findPopReceiveList(Map<String, Object> map) {
		 return this.popReceiveDao.findPopReceiveList(map);
	}

	@Override
	public int count(Map<String, Object> map) {
		return this.popReceiveDao.count(map);
	}
}
