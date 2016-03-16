package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;

/**
 * 自动分拣数据消费
 * Created by wangtingwei on 2016/3/14.
 */
public interface ScannerFrameConsume {
    /**
     * 消费接口
     * @param uploadData 上传数据
     * @param config     配置数据
     * @return
     */
    boolean onMessage(UploadData uploadData,GantryDeviceConfig config);
}
