package com.jd.bluedragon.distribution.carSchedule.service;

import com.jd.bluedragon.distribution.carSchedule.domain.SendCodeToCarCode;

/**
 * Created by wuzuxiang on 2017/3/13.
 */
public interface SendCodeToCarNoService {

    Boolean addMapping(SendCodeToCarCode entity);

    Boolean cancelSendCar(SendCodeToCarCode entity);
}
