package com.jd.bluedragon.core.jsf.dms.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;
import com.jd.bluedragon.distribution.jsf.service.CancelWaybillJsfService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * 
 * @ClassName: CancelWaybillJsfManager
 * @Description: 运单拦截jsfmanager
 * @author: wuyoude
 * @date: 2019年4月30日 下午2:37:26
 *
 */
@Service("cancelWaybillJsfManager")
public class CancelWaybillJsfManagerImpl implements CancelWaybillJsfManager{
	
    @Autowired
    @Qualifier("cancelWaybillJsfService")
    private CancelWaybillJsfService cancelWaybillJsfService;
    /**
     * 查询运单是否拦截完成
     * @param waybillCode
     * @param featureType
     * @return
     */
    @JProfiler(jKey = "dmsWeb.jsf.dmsver.cancelWaybillJsfService.checkWaybillBlock",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
    public BlockResponse checkWaybillBlock(String waybillCode, Integer featureType){
    	return cancelWaybillJsfService.checkWaybillBlock(waybillCode, featureType);
    }
    /**
     * 查询包裹是否拦截完成
     * @param packageCode
     * @param featureType
     * @return
     */
    @JProfiler(jKey = "dmsWeb.jsf.dmsver.cancelWaybillJsfService.checkPackageBlock",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
    public BlockResponse checkPackageBlock(String packageCode, Integer featureType){
    	return cancelWaybillJsfService.checkPackageBlock(packageCode, featureType);
    }
}
