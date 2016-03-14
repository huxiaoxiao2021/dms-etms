package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;

/**
 * 龙门架发货消费器
 * Created by wangtingwei on 2016/3/14.
 */
public class ScannerFrameSendConsume implements ScannerFrameConsume {
    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {
        return false;
    }
}
