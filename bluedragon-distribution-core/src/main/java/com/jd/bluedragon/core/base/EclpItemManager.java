package com.jd.bluedragon.core.base;

import com.jd.kom.ext.service.domain.response.ItemInfo;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年05月11日 18时:34分
 */
public interface EclpItemManager {
    public List<ItemInfo> getltemBySoNo(String soNo);
    public String getDeptBySettlementOuId(String ouId);
}
