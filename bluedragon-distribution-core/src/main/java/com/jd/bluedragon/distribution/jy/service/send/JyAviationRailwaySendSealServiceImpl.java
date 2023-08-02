package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskAviationSendService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskBindService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:55
 * @Description
 */
public class JyAviationRailwaySendSealServiceImpl extends JySendVehicleServiceImpl implements JyAviationRailwaySendSealService{

    @Autowired
    private JyAviationRailwaySendSealCacheService jyAviationRailwaySendSealCacheService;
    @Autowired
    private JyBizTaskAviationSendService jyBizTaskAviationSendService;
    @Autowired
    private JyBizTaskBindService jyBizTaskBindService;
}
