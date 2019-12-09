package com.jd.bluedragon.distribution.transBillSchedule.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.response.TransBillScheduleResponse;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.transBillSchedule.domain.TransBillScheduleRequest;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by wuzuxiang on 2017/4/26.
 */
@Service("transBillScheduleService")
public class TransBillScheduleServiceImpl implements TransBillScheduleService {

    private static final Logger log = LoggerFactory.getLogger(TransBillScheduleServiceImpl.class);

    private static final String TRANSBILL_PREFIX = "schedule_transBill_";

    private static final long expire_time = 7*24*60*60;//过期时间，以秒为单位

    @Autowired
    private Cluster redisClientCache;

    @Autowired
    private GoddessService goddessService;

    @Autowired
    private TransbillMService transbillMService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Override
    public Boolean checkSameScheduleBill(TransBillScheduleRequest request) {
        Boolean bool = Boolean.FALSE;

        if(request == null || StringUtils.isBlank(request.getWaybillCode()) || StringUtils.isBlank(request.getBoxCode())){
            return bool;
        }
        if (this.existsKey(request.getBoxCode())){
            String oldScheduleBillCode = this.getKey(request.getBoxCode());
            String newScheduleBillCode = this.queryScheduleCode(request.getWaybillCode());

            this.log.info("oldScheduleBillCode:{},newScheduleBillCode:{}",oldScheduleBillCode, newScheduleBillCode);
            if (oldScheduleBillCode.equals(newScheduleBillCode)){
                bool = Boolean.TRUE;
            }
        }else {
            this.setKey(request.getBoxCode(),request.getWaybillCode());
            bool = Boolean.TRUE;
        }
        return bool;
    }

    @Override
    public String queryScheduleCode(String waybillCode) {
        String scheduleCode = Constants.SCHEDULE_CODE_DEFAULT;
        if(StringUtils.isNotBlank(waybillCode)){
            TransbillM transbillM = transbillMService.getByWaybillCode(waybillCode);
            if(transbillM != null && StringUtils.isNotBlank(transbillM.getScheduleBillCode())){
                scheduleCode = transbillM.getScheduleBillCode();
            }
        }
        return scheduleCode;
    }

    @Override
    public String queryTruckSpotByWaybillCode(String waybillCode) {
        String result = "";
        if(StringUtils.isNotBlank(waybillCode)){
            try{
                TransbillM transbillM = transbillMService.getByWaybillCode(waybillCode);
                if(transbillM != null && StringHelper.isNotEmpty(transbillM.getTruckSpot())){
                	result = "卡位号:"+transbillM.getTruckSpot();
                }
            }catch(Exception e){
                this.log.error("运单号{}获取roadCode失败",waybillCode,e);
            }
        }
        return result;
    }

    /**
     * 获取箱号对应的派车单号
     * @param boxCode
     * @return
     */
    @Override
    public String getKey(String boxCode) {
        String result = this.redisClientCache.get(TRANSBILL_PREFIX + boxCode);
        return result;
    }

    /**
     * 设置派车单的redis记录，统一前缀,value 的默认值为“-1”
     * @param boxCode 箱号
     * @param waybillCode 包裹号运单号
     * @return
     */
    @Override
    public void setKey(String boxCode,String waybillCode) {
        String value = Constants.SCHEDULE_CODE_DEFAULT;
        TransbillM transbillM = transbillMService.getByWaybillCode(waybillCode);
        if(transbillM != null && StringUtils.isNotBlank(transbillM.getScheduleBillCode())){
            value = transbillM.getScheduleBillCode();//获取运单的派车单号
        }
        redisClientCache.setEx(TRANSBILL_PREFIX + StringUtils.trim(boxCode),value,expire_time, TimeUnit.SECONDS);
    }

    /**
     * 检查该箱子的派车单信息是不是存在
     * @param boxCode
     * @return
     */
    @Override
    public Boolean existsKey(String boxCode){
        Boolean bool = redisClientCache.exists(TRANSBILL_PREFIX + boxCode);
        return bool;
    }

    /**
     * 删除该箱号的派车单信息
     * @param boxCode
     * @return
     */
    @Override
    public boolean delete(String boxCode) {
        return redisClientCache.del(TRANSBILL_PREFIX + boxCode) > 0;
    }

    /**
     * 获取派车单信息
     * @param request
     * @return
     */
    public TransBillScheduleResponse checkScheduleBill(TransBillScheduleRequest request){
        TransBillScheduleResponse response = new TransBillScheduleResponse();
        response.setBoxCode(request.getBoxCode());
        response.setWaybillCode(request.getWaybillCode());
        response.setScheduleCode(queryScheduleCode(request.getWaybillCode()));
        response.setSameScheduleBill(checkSameScheduleBill(request));
        response.setRoadCode(queryTruckSpotByWaybillCode(request.getWaybillCode()));

        return response;
    }
}
