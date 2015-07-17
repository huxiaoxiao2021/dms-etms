package com.jd.bluedragon.distribution.reverse.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.jd.bluedragon.distribution.reverse.dao.PickWareDao;
import com.jd.bluedragon.distribution.reverse.domain.PickWare;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.StringHelper;
@Service("pickWareService")
public class PickWareServiceImpl implements PickWareService {

	@Autowired
	private PickWareDao pickWareDao;
	private final Log logger = LogFactory.getLog(this.getClass());
	 
	@Profiled(tag = "PickWareService.add")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(PickWare pickWare) {
		this.initFingerPrint(pickWare);
		if (!this.has(pickWare)) {
			return this.pickWareDao.add(PickWareDao.namespace, pickWare);
		} else {
			this.logger.error(" Duplicate pickWare: " + pickWare.toString());
		}
		return 0;
	}
	
	private void initFingerPrint(PickWare pickWare){
		StringBuilder fingerprint = new StringBuilder("");
		fingerprint.append(pickWare.getPackageCode()).append("_")
		.append(pickWare.getOrgId()).append("_").append(pickWare.getCanReceive())
		.append("_").append(pickWare.getOperateType()).append("_").append(pickWare.getOperateTime())
		.append("_").append(pickWare.getOperator());
		if (StringUtils.isNotBlank(fingerprint.toString())) {
			pickWare.setFingerprint(Md5Helper.encode(fingerprint.toString()));
		}
	}
	
	private Boolean has(PickWare pickWare) {
		if (StringHelper.isEmpty(pickWare.getFingerprint())) {
			return Boolean.FALSE;
		}
		if (this.findByFingerprint(pickWare) > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	@Profiled(tag = "PickWareService.findByFingerprint")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Integer findByFingerprint(PickWare pickWare) {
		Assert.notNull(pickWare.getFingerprint(), "fingerprint must not be null");
		return this.pickWareDao.findByFingerprint(pickWare);
	}
	
}
