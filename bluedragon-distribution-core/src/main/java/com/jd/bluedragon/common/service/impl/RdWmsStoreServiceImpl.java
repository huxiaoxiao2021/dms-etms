package com.jd.bluedragon.common.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.service.RdWmsStoreService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.rd.wms.jsf.bin.service.RdWmsStoreExtendService;
import com.jd.rd.wms.jsf.common.ServiceResult;


@Service("rdWmsStoreService")
public class RdWmsStoreServiceImpl implements RdWmsStoreService {
	
	private static final Log LOGGER = LogFactory.getLog(RdWmsStoreServiceImpl.class);
	
	@Autowired
	private RdWmsStoreExtendService rdWmsStoreExtendService;
	
	@Override
	public InvokeResult<String> getOrgStoreTag(Integer orgId, Integer storeId) {
		InvokeResult<String> rest = new InvokeResult<String>();
		ServiceResult<String> serviceResult = rdWmsStoreExtendService.getOrgStoreTag(orgId, storeId);
		rest.setData(serviceResult.getResultBody());
		return rest;
	}
	 
}
