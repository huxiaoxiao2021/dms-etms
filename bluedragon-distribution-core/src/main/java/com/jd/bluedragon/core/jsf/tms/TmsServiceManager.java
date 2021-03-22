package com.jd.bluedragon.core.jsf.tms;

import java.util.List;

import com.jd.bluedragon.distribution.command.JdResult;

/**
 * 
 * @ClassName: TmsServiceManager
 * @Description: 调用运输jsf接口定义
 * @author: wuyoude
 * @date: 2021年01月14日 下午2:37:26
 *
 */
public interface TmsServiceManager {
    /**
     * 调用eclp通用站内信创建接口
     * @param dto
     * @return
     */
	JdResult<TransportResource> getTransportResourceByTransCode(String transCode);
    /**
     * 调用运输系统获取运力资源
     * @param createSiteCode        始发站点
     * @param receiveSiteCode       结束站点
     * @param filterTransTypes      过滤运力类型
     * @return
     */
	JdResult<List<TransportResource>> loadTransportResources(String startNodeCode, String endNodeCode, List<Integer> filterTransTypes);
}
