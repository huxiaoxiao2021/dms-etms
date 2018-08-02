package com.jd.bluedragon.core.base;

import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 查询商家
 * @date 2018年08月02日 14时:23分
 */
public interface BaseTraderManager {
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(String key);
}
