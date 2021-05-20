package com.jd.bluedragon.utils;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/5/19
 * @Description:
 */
@Service
public class BusiWaringUtil {

    private Logger log = LoggerFactory.getLogger(BusiWaringUtil.class);

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    public void bigWaybillWarning(String waybillCode,Integer packSize){
        try{
            if(uccPropertyConfiguration.getBigWaybillWaringSize().compareTo(packSize)<0){
                String msg = String.format("大运单来了！请做好准备！运单号%s,包裹总数%s",waybillCode,packSize);
                Profiler.businessAlarm("BusiWaringUtil.bigWaybillWaring", System.currentTimeMillis(), msg);
            }
        }catch (Exception e){
            log.warn("BusiWaringUtil.bigWaybillWaring! {} ,{}",waybillCode,packSize,e);
        }
    }
}
