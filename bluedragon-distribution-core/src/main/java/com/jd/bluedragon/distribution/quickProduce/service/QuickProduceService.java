package com.jd.bluedragon.distribution.quickProduce.service;

import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;

/**
 * Created by yanghongqiang on 2015/9/7.
 * 快生项目获取运单信息
 */
public interface QuickProduceService {
    /**
     * 快生项目获取运单信息
     * @param waybillCode
     * @return
     */
    public QuickProduceWabill getQuickProduceWabill(String waybillCode);
}
