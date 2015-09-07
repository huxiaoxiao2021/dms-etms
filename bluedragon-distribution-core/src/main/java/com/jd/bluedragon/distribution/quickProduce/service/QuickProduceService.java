package com.jd.bluedragon.distribution.quickProduce.service;

import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;

/**
 * Created by yanghongqiang on 2015/9/7.
 */
public interface QuickProduceService {
    public QuickProduceWabill getQuickProduceWabill(String waybillCode);
}
