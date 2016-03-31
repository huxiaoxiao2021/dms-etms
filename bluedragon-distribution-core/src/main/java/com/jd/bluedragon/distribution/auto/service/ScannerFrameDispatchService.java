package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;

/**
 * 龙门架数据分发器
 * Created by wangtingwei on 2016/3/10.
 */
public interface ScannerFrameDispatchService {

    /**
     * 数据分发
     * @param domain 龙门架数据
     * @return
     */
    boolean dispatch(UploadData domain);

}
