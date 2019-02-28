package com.jd.bluedragon.common.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.service.RdWmsStoreService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.rd.wms.jsf.bin.service.RdWmsStoreExtendService;
import com.jd.rd.wms.jsf.common.ServiceResult;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;


@Service("rdWmsStoreService")
public class RdWmsStoreServiceImpl implements RdWmsStoreService {
	
	private static final Log LOGGER = LogFactory.getLog(RdWmsStoreServiceImpl.class);
	
	@Autowired
	private RdWmsStoreExtendService rdWmsStoreExtendService;
	@Autowired
	private BaseMajorManager baseMajorManager;
	/**
	 * 根据机构号、备件库id获取备件条码前缀信息
	 */
	@Override
	public InvokeResult<String> getOrgStoreTag(Integer orgId,Integer storeSiteId, Integer storeId) {
		InvokeResult<String> rest = new InvokeResult<String>();
		try {
			//库房号为null/0，去基础资料查询storeId
			if(storeId==null || storeId==0 || orgId==null || orgId==0){
				BaseStaffSiteOrgDto storeInfo = baseMajorManager.getBaseSiteBySiteId(storeSiteId);
				if(storeInfo != null){
					orgId = storeInfo.getOrgId();
					storeId = SerialRuleUtil.getStoreIdFromStoreCode(storeInfo.getStoreCode());
				}
				LOGGER.warn("传入库房号为null/0，去基础资料查询库房号");
			}
			//调用备件库接口获取备件条码前缀
			ServiceResult<String> serviceResult = getOrgStoreTag(orgId, storeId);
			if(serviceResult != null 
					&& Boolean.TRUE.equals(serviceResult.getSuccess())
					&& StringHelper.isNotEmpty(serviceResult.getResultBody())){
				rest.setData(serviceResult.getResultBody());
			}else{
				LOGGER.error("获取备件条码前缀异常,返回信息：" + (serviceResult==null?"":JsonHelper.toJson(serviceResult)));
				String errorMsg = "调用备件库接口获取前缀结果为空";
				if(serviceResult != null && StringUtils.isNotEmpty(serviceResult.getResultMsg())){
                    errorMsg = serviceResult.getResultMsg();
                }
				rest.customMessage(InvokeResult.RESULT_THIRD_ERROR_CODE,errorMsg);
			}
		} catch (Exception e) {
			LOGGER.error("获取备件条码前缀异常", e);
			rest.error("获取备件条码前缀异常");
		}
		return rest;
	}
	/**
	 * 调用备件库接口获取前缀
	 * @param orgId
	 * @param storeId
	 * @return
	 */
	private ServiceResult<String> getOrgStoreTag(Integer orgId,Integer storeId){
		CallerInfo monitor = Profiler.registerInfo("dmsWeb.jsf.rdWmsStoreExtendService.getOrgStoreTag", false, true);
		ServiceResult<String> serviceResult = null;
		try {
			serviceResult = rdWmsStoreExtendService.getOrgStoreTag(orgId, storeId);
        }catch (Throwable throwable){
        	serviceResult = new ServiceResult<String>();
        	serviceResult.setSuccess(Boolean.FALSE);
        	serviceResult.setResultMsg(throwable.getMessage());
        	LOGGER.error("调用JSF-RdWmsStoreExtendService.getOrgStoreTag获取备件条码前缀异常", throwable);
			Profiler.functionError(monitor);
		}finally{
			Profiler.registerInfoEnd(monitor);
		}
		return serviceResult;
	}
}
