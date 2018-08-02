package com.jd.bluedragon.distribution.siteRetake.service;

import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年08月02日 14时:46分
 */
public interface SiteRetakeService {
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(String key);
}
