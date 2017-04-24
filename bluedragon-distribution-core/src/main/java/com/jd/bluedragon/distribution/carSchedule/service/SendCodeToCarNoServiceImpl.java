package com.jd.bluedragon.distribution.carSchedule.service;

import com.jd.bluedragon.distribution.carSchedule.domain.SendCodeToCarCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 发车批次号与发车条码的对应关系
 * Created by wuzuxiang on 2017/3/13.
 */
public class SendCodeToCarNoServiceImpl implements SendCodeToCarNoService{

    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public Boolean addMapping(SendCodeToCarCode entity) {
        return null;
    }

    @Override
    public Boolean cancelSendCar(SendCodeToCarCode entity) {
        return null;
    }
}
