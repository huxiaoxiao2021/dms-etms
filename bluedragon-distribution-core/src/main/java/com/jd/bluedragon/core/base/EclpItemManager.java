package com.jd.bluedragon.core.base;

import com.jd.eclp.spare.ext.api.inbound.OrderResponse;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrder;
import com.jd.kom.ext.service.domain.response.ItemInfo;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年05月11日 18时:34分
 */
public interface EclpItemManager {

    /**
     * ECLP入库单创建接口成功状态码
     */
    int ORDER_RESPONSE_SUCCESS = 200;
    /**
     * 也是牛逼 ECLP那边相同接口 同一业务场景返回两种response 接口里也不提供成功码
     */
    int STRINGAPIRESPONE_SUCCESS = 200;

    public List<ItemInfo> getltemBySoNo(String soNo);
    public String getDeptBySettlementOuId(String ouId);
    public OrderResponse createInboundOrder(InboundOrder inboundOrder);

    boolean cancelInboundOrder(String deptNo,String isvInboundOrderNo,Byte inboundSource);
}
