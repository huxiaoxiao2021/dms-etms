package com.jd.bluedragon.core.jsf.merchant;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.merchant.sdk.order.dto.UpdateOrderRequest;

/**
 * 
 * @ClassName: ExpressOrderServiceWsManager
 * @Description: 快运订单修改接口-服务管理-jsf接口定义
 * @author: wuyoude
 * @date: 2023年05月05日 下午2:37:26
 *
 */
public interface ExpressOrderServiceWsManager {
    /**
     * 渠道订单修改接口
     * @param dto
     * @return
     */
	JdResult<Boolean> updateOrderSelective(UpdateOrderRequest dto);
}
