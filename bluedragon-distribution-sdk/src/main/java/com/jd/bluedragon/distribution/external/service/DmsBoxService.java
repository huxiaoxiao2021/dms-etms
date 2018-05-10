package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.response.BoxResponse;

/**
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsBoxService {

    /**
     * 获取扫描的箱号详细信息(只有扫描箱号时才调用该接口)
     *
     * @param boxCode
     * @return
     */
    BoxResponse get(String boxCode);

}
