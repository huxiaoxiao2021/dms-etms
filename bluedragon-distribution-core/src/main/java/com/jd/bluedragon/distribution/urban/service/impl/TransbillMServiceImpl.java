package com.jd.bluedragon.distribution.urban.service.impl;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.urban.dao.TransbillMDao;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.dto.WaybillScheduleDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 城配运单M表--Service接口实现
 *
 * @author wuyoude
 * @ClassName: TransbillMServiceImpl
 * @Description: TODO
 * @date 2017年04月28日 13:30:01
 */
@Service("transbillMService")
@SuppressWarnings("all")
public class TransbillMServiceImpl implements TransbillMService {

    private static final Logger log = LoggerFactory.getLogger(TransbillMServiceImpl.class);

    @Autowired
    private TransbillMDao transbillMDao;

    @Autowired
    WaybillQueryManager waybillQueryManager;


    /**
     * create by: mmzs
     * description:以后不再使用
     * create time:
     *
     * @Param: transbillM
     * @return
     */
    @Deprecated
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean saveOrUpdate(TransbillM transbillM) {
        Integer rs = 0;
        if (transbillM != null && transbillM.getMId() != null) {
            TransbillM oldData = transbillMDao.findById(transbillM.getMId());
            if (oldData != null) {
                if (transbillM.getTsM() >= oldData.getTsM()) {
                    rs = transbillMDao.updateBySelective(transbillM);
                } else {
                    log.warn("本次数据ts_m小于数据库当前ts_m，抛弃本次数据！transbillM:{}" , JsonHelper.toJson(transbillM));
                }
            } else {
                rs = transbillMDao.insert(transbillM);
            }
            return rs == 1;
        } else {
            log.warn("城配运单transbillM保存失败！transbillM:{}" , JsonHelper.toJson(transbillM));
        }
        return false;
    }

    /**
     * create by: mmzs
     * description:关于派车单号和运单号集齐验证不再使用
     * create time:
     *
     * @Param: scheduleBillCode
     * @return
     */
    @Deprecated
    @Override
    public List<String> getEffectWaybillCodesByScheduleBillCode(String scheduleBillCode) {

        if (StringHelper.isNotEmpty(scheduleBillCode)) {
            return transbillMDao.findEffectWaybillCodesByScheduleBillCode(scheduleBillCode);
        }
        return null;
    }

    @Override
    public TransbillM getByWaybillCode(String waybillCode) {
        if (StringUtils.isNotBlank(waybillCode)) {
            TransbillM transbillM=new TransbillM();

            WaybillScheduleDto waybillScheduleDto = null;
            String waybillSign = null;
            try {
                WChoice wChoice = new WChoice();
                wChoice.setQueryWaybillS(true);
                wChoice.setQueryWaybillC(true);
                BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(
                        waybillCode, wChoice);
                if (baseEntity != null && baseEntity.getData() != null) {
                    waybillScheduleDto = baseEntity.getData().getWaybillSchedule();
                    if (null != baseEntity.getData().getWaybill()) {
                        waybillSign = baseEntity.getData().getWaybill().getWaybillSign();
                    } else {
                        log.error("运单号【{}】调度信息查询中没有查到waybillSign标志.", waybillCode);
                    }
                }
                this.log.info("运单号【{}】调用调度信息成功", waybillCode);
            } catch (Exception e) {
                this.log.error("运单号【{}】调用调度信息异常：",waybillCode, e);
            }

            Integer requireTransMode= StringHelper.stringToInteger(waybillSign,74);
            transbillM.setRequireTransMode(null == requireTransMode? 0 : requireTransMode);

            if (waybillScheduleDto == null) {
                // 无数据
                this.log.warn("运单号【{}】的调用调度信息为空",waybillCode);
            } else {
                String truckSpot=waybillScheduleDto.getTruckSpot();
                transbillM.setScheduleBillCode(null);
                transbillM.setTruckSpot(truckSpot);
            }

            return transbillM;
        }
        return null;
    }
    private  WaybillScheduleDto getWaybillScheduleDto(String waybillCode){
        WaybillScheduleDto waybillScheduleDto = null;
        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillS(true);
            wChoice.setQueryWaybillC(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybillScheduleDto = baseEntity.getData().getWaybillSchedule();
            }
            this.log.info("运单号【{}】调用调度信息成功", waybillCode);
        } catch (Exception e) {
            this.log.error("运单号【{}】调用调度信息异常：",waybillCode, e);
        }
        if (waybillScheduleDto == null) {
            // 无数据
            this.log.warn("运单号【{}】的调用调度信息为空",waybillCode);
        }
        return waybillScheduleDto;

    }
    private  Waybill getWaybill(String waybillCode){
        Waybill waybill = null;
        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillS(true);
            wChoice.setQueryWaybillC(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = baseEntity.getData().getWaybill();
            }
            if(log.isInfoEnabled()){
                this.log.info("运单号【{}】调用运单数据成功，运单【{}】",waybillCode,waybill);
            }
        } catch (Exception e) {
            this.log.error("运单号【{}】调用运单异常：",waybillCode, e);
        }
        if (waybill == null) {
            // 无数据
            log.warn("{}对应的运单信息为空！",waybillCode);
        }
        return waybill;
    }

}
