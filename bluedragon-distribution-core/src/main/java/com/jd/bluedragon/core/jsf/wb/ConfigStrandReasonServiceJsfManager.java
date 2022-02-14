package com.jd.bluedragon.core.jsf.wb;

import com.jd.dms.wb.sdk.model.config.ConfigStrandReason;
import com.jd.dms.wb.sdk.query.config.ConfigStrandReasonQuery;
import com.jd.dms.wb.sdk.vo.config.ConfigStrandReasonVo;
import com.jd.dms.workbench.utils.sdk.base.PageData;
import com.jd.dms.workbench.utils.sdk.base.Result;

/**
 * 
 * @ClassName: ConfigStrandReasonServiceJsfManager
 * @Description: 滞留原因配置jsfmanager
 * @author: wuyoude
 * @date: 2022年1月27日 下午2:37:26
 *
 */
public interface ConfigStrandReasonServiceJsfManager {
    /**
     * 根据原因编码查询
     *
     * @param reasonCode
     * @return
     */
	public Result<ConfigStrandReason> queryByReasonCode(Integer reasonCode);	
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	public Result<PageData<ConfigStrandReasonVo>> queryPageList(ConfigStrandReasonQuery query);    
}
