package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;

/**
 * 龙门架测量体积消费
 * Created by wangtingwei on 2016/3/14.
 */
public class ScannerFrameMeasureConsume implements ScannerFrameConsume {
    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {
        return false;
    }
}
