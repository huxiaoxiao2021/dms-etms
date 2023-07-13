package com.jd.bluedragon.distribution.external.gateway.service.impl;



import com.jd.bluedragon.distribution.debon.WaybillReturnScheduleService;
import com.jd.bluedragon.distribution.debon.WaybillReturnScheduleServiceImpl;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleRequest;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleResult;
import com.jd.bluedragon.distribution.jsf.service.WaybillReturnScheduleGateWayService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/22 16:15
 * @Description:
 */
@Service("waybillReturnScheduleGateWayService")
public class WaybillReturnScheduleGateWayServiceImpl implements WaybillReturnScheduleGateWayService {

    private static final Logger log = LoggerFactory.getLogger(WaybillReturnScheduleGateWayServiceImpl.class);


    @Autowired
    private WaybillReturnScheduleService debonReturnScheduleService;
    @Override
    public JdResponse<ReturnScheduleResult> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request) {
        try{
            return debonReturnScheduleService.returnScheduleSiteInfoByWaybill(request);
        }catch (Exception e){
            log.error("返调度获取站点信息异常!");
            return new JdResponse<>(JdResponse.CODE_ERROR,JdResponse.MESSAGE_ERROR);
        }
    }
}
