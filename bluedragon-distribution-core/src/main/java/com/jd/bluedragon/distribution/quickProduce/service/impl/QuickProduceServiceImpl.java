package com.jd.bluedragon.distribution.quickProduce.service.impl;

import com.jd.bluedragon.distribution.quickProduce.domain.QuickProduceWabill;
import com.jd.bluedragon.distribution.quickProduce.service.QuickProduceService;
import org.springframework.stereotype.Service;

/**
 * 快生项目获取运单信息
 */
@Service("quickProduceService")
public class QuickProduceServiceImpl implements QuickProduceService {
    /**
     * 快生项目获取运单信息
     * @param waybillCode
     * @return
     */
    @Override
    public QuickProduceWabill getQuickProduceWabill(String waybillCode) {
        return null;
    }
}
