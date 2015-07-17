package com.jd.bluedragon.distribution.popPrint.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.popPrint.dao.PopPrintDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-14 下午07:50:33
 *
 * 类说明
 */
@Service("popPrintService")
public class PopPrintServiceImpl implements PopPrintService {
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private PopPrintDao popPrintDao;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PopPrint findByWaybillCode(String waybillCode) {
		if (StringUtils.isBlank(waybillCode)) {
			logger.info("传入运单号 waybillCode 为空");
		}
		return popPrintDao.findByWaybillCode(waybillCode);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<PopPrint> findSitePrintDetail(Map<String,Object> map){
		return  popPrintDao.findSitePrintDetail(map);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Integer findSitePrintDetailCount(Map<String,Object> map){
		return  popPrintDao.findSitePrintDetailCount(map);
	}



	

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<PopPrint> findAllByWaybillCode(String waybillCode) {
		if (StringUtils.isBlank(waybillCode)) {
			logger.info("传入运单号 waybillCode 为空");
		}
		return this.popPrintDao.findAllByWaybillCode(waybillCode);
	}

	@Override
	@Profiled(tag = "PopPrintServiceImpl.add")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(PopPrint popPrint) {
		if (popPrint == null) {
			logger.info("传入popPrint 为空");
			return 0;
		}
		return popPrintDao.add(popPrint);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateByWaybillCode(PopPrint popPrint) {
		return popPrintDao.updateByWaybillCode(popPrint);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int updateByWaybillOrPack(PopPrint popPrint) {
		return this.popPrintDao.updateByWaybillOrPack(popPrint);
	}

	@Override
	@Profiled(tag = "PopPrintServiceImpl.findLimitListNoReceive")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<PopPrint> findLimitListNoReceive(Map<String, Object> paramMap) {
		return popPrintDao.findLimitListNoReceive(paramMap);
	}

}
