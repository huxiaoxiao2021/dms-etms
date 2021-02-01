package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.box.BoxDto;

import javax.swing.*;

/**
 * 发往物流网关的接口不要在此类中加方法
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


    /**
     * 根据箱号查询箱信息
     * @param boxCode
     * @return
     */
    BoxDto getBoxByBoxCode(String boxCode);

    /**
     * 更新箱状态；状态有：可用，不可用
     * @param boxReq
     * @return
     */
    Boolean updateBoxStatus(BoxReq boxReq);
}
