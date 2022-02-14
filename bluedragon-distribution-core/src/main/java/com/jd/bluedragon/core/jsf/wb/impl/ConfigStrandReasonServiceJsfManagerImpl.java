package com.jd.bluedragon.core.jsf.wb.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.wb.ConfigStrandReasonServiceJsfManager;
import com.jd.dms.wb.sdk.api.config.IConfigStrandReasonJsfService;
import com.jd.dms.wb.sdk.model.config.ConfigStrandReason;
import com.jd.dms.wb.sdk.query.config.ConfigStrandReasonQuery;
import com.jd.dms.wb.sdk.vo.config.ConfigStrandReasonVo;
import com.jd.dms.workbench.utils.sdk.base.PageData;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * 
 * @ClassName: ConfigStrandReasonServiceJsfManagerImpl
 * @Description: 滞留原因配置jsfmanager
 * @author: wuyoude
 * @date: 2022年1月27日 下午2:37:26
 *
 */
@Service("cancelWaybillJsfManager")
public class ConfigStrandReasonServiceJsfManagerImpl implements ConfigStrandReasonServiceJsfManager{
	
    @Autowired
    @Qualifier("configStrandReasonJsfService")
    private IConfigStrandReasonJsfService configStrandReasonJsfService;
    
    /**
     * 根据原因编码查询
     *
     * @param reasonCode
     * @return
     */
    @JProfiler(jKey = "dmsWeb.jsf.wb.configStrandReasonJsfService.queryByReasonCode",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})    
	public Result<ConfigStrandReason> queryByReasonCode(Integer reasonCode) {
		return configStrandReasonJsfService.queryByReasonCode(reasonCode);
	}	
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
    @JProfiler(jKey = "dmsWeb.jsf.wb.configStrandReasonJsfService.queryPageList",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})    
	public Result<PageData<ConfigStrandReasonVo>> queryPageList(ConfigStrandReasonQuery query){
		return configStrandReasonJsfService.queryPageList(query);
	 }
}
