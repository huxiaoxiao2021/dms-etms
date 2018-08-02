package com.jd.bluedragon.distribution.siteRetake.service.impl;

import com.jd.bluedragon.core.base.BaseTraderManager;
import com.jd.bluedragon.distribution.siteRetake.service.SiteRetakeService;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 驻厂批量再取
 * @date 2018年08月02日 14时:47分
 */
public class SiteRetakeServiceImpl implements SiteRetakeService{
    @Autowired
    private BaseTraderManager baseTraderManager;

    /**
     * 查商家
     * @param key
     * @return
     */
    @Override
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(String key) {
        return baseTraderManager.queryBasicTraderInfoByKey(key);
    }
}
