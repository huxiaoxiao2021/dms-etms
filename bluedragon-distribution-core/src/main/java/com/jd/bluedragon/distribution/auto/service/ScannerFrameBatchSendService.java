package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.ScannerFrameBatchSend;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;

import java.util.*;
/**
 * 龙门架发货批量发货批次服务
 * Created by wangtingwei on 2016/12/8.
 */
public interface ScannerFrameBatchSendService {

    /**
     * 获取并生成发货批次对象(单个批次最长使用期限是24小时，超过后自动生成新批次)
     * @param operateTime       龙门架扫描时间
     * @param receiveSiteCode   发货接收站点
     * @param config            龙门驾状态配置
     * @return
     */
    ScannerFrameBatchSend getAndGenerate(Date operateTime,Integer receiveSiteCode,GantryDeviceConfig config);

    /**
     * 生成发货批次
     * @param domain    DOMAIN对象
     *
     * @return
     */
    boolean generateSend(ScannerFrameBatchSend domain);


}
