package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;

import java.util.Date;

/**
 * Created by wangtingwei on 2016/12/8.
 */
public class ScannerFrameBatchSendServiceImpl implements ScannerFrameBatchSendService {

    @Override
    public ScannerFrameBatchSend getAndGenerate(Date operateTime, Integer receiveSiteCode, GantryDeviceConfig config) {
        return null;
    }

    @Override
    public boolean generateSend(ScannerFrameBatchSend domain) {
        return false;
    }
}
