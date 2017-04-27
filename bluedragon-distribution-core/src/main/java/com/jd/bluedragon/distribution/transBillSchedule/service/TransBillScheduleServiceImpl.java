package com.jd.bluedragon.distribution.transBillSchedule.service;

import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.transBillSchedule.domain.TransBillScheduleRequest;
import com.jd.bluedragon.distribution.urban.domain.UrbanWaybill;
import com.jd.bluedragon.distribution.urban.service.UrbanWaybillService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by wuzuxiang on 2017/4/26.
 */
@Service("transBillScheduleService")
public class TransBillScheduleServiceImpl implements TransBillScheduleService {

    private static final Log logger = LogFactory.getLog(TransBillScheduleServiceImpl.class);

    private static final String TRANSBILL_PREFIX = "schedule_transBill_";

    private static final long expire_time = 7*24*60*60;//过期时间，以秒为单位

    @Autowired
    private Cluster redisClientCache;

    @Autowired
    private GoddessService goddessService;

    @Autowired
    private UrbanWaybillService urbanWaybillService;

    @Autowired
    private WaybillService waybillService;

    @Override
    public Boolean checkSameScheduleBill(TransBillScheduleRequest request) {
        Boolean bool = Boolean.FALSE;

        if(request == null || StringUtils.isBlank(request.getWaybillCode()) || StringUtils.isBlank(request.getBoxCode())){
            return bool;
        }
        if (this.existsKey(request.getBoxCode())){
            String oldScheduleBillCode = this.getKey(request.getBoxCode());
            String newScheduleBillCode = "";

            UrbanWaybill urbanWaybill = urbanWaybillService.getByWaybillCode(request.getWaybillCode());
            if(urbanWaybill != null && StringUtils.isNotBlank(urbanWaybill.getScheduleBillCode())){
                newScheduleBillCode = urbanWaybill.getScheduleBillCode();
            }
            this.logger.info("oldScheduleBillCode:" + oldScheduleBillCode + ",newScheduleBillCode:" + newScheduleBillCode);
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
    public String queryRoadCodeByWaybillCode(String waybillCode) {
        String result = "";
        if(StringUtils.isNotBlank(waybillCode)){
            try{
                BigWaybillDto waybillDto = waybillService.getWaybill(waybillCode);
                if(waybillDto != null){
                    result = waybillDto.getWaybill().getRoadCode();
                }
            }catch(Exception e){
                this.logger.info("派车单号{" + waybillCode + "}从运单接口获取roadCode失败",e);
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
        logger.info("箱号============================================================"+boxCode);
        this.logger.info("redis=========================================================="+result);
        return result;
    }

    /**
     * 设置派车单的redis记录，统一前缀
     * @param boxCode 箱号
     * @param waybillCode 包裹号运单号
     * @return
     */
    @Override
    public void setKey(String boxCode,String waybillCode) {
        String value = "";
        UrbanWaybill urbanWaybill = urbanWaybillService.getByWaybillCode(waybillCode);
        if(urbanWaybill != null && StringUtils.isNotBlank(urbanWaybill.getScheduleBillCode())){
            value = urbanWaybill.getScheduleBillCode();//获取运单的派车单号
        }
        redisClientCache.setEx(TRANSBILL_PREFIX + boxCode,value,expire_time, TimeUnit.SECONDS);
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
}
