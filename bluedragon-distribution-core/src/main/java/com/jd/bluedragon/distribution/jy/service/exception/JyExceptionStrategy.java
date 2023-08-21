package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpSourceEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.service.exception.impl.JyDamageExceptionServiceImpl;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExt;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 21:18
 * @Description: 异常策略类
 */
public abstract class JyExceptionStrategy {


    private final Logger logger = LoggerFactory.getLogger(JyExceptionStrategy.class);

    public abstract Integer getExceptionType();

    /**
     * PDA选择不同的异常类型、破损类型、修复类型进行判断
     * @param req
     * @return
     */
    public abstract JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req,Waybill waybill);



    /**
     * 港澳单校验逻辑
     * @param waybillCode
     * @param waybill
     * @return
     */
    public boolean isHKorMOWaybill(String waybillCode,Waybill waybill){
        if(waybill != null &&  waybill.getWaybillExt() != null){
            WaybillExt waybillExt = waybill.getWaybillExt();
            if((org.apache.commons.lang3.StringUtils.isNotBlank(waybillExt.getStartFlowDirection()) && (Objects.equals("HK",waybillExt.getStartFlowDirection()) || Objects.equals("MO",waybillExt.getStartFlowDirection())))
                    || (org.apache.commons.lang3.StringUtils.isNotBlank(waybillExt.getEndFlowDirection()) && (Objects.equals("HK",waybillExt.getEndFlowDirection()) || Objects.equals("MO",waybillExt.getEndFlowDirection())))){
                logger.info("港澳单-{}",waybillCode);
                return true;
            }
        }
        return false;
    }
}
