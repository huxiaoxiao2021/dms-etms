package com.jd.bluedragon.distribution.urban.service.impl;

import java.util.List;

import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.framework.asynBuffer.producer.jmq.BusinessIdGen;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.urban.dao.TransbillMDao;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;

import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.WaybillScheduleDto;

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

    private static final Log logger = LogFactory.getLog(TransbillMServiceImpl.class);

    @Autowired
    private WaybillQueryApi waybillQueryApi;

    @Autowired
    private TransbillMDao transbillMDao;


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
                    logger.warn("本次数据ts_m小于数据库当前ts_m，抛弃本次数据！transbillM:" + JsonHelper.toJson(transbillM));
                }
            } else {
                rs = transbillMDao.insert(transbillM);
            }
            return rs == 1;
        } else {
            logger.warn("城配运单transbillM保存失败！transbillM:" + (transbillM == null ? "对象为空" : JsonHelper.toJson(transbillM)));
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
            WaybillScheduleDto waybillScheduleDto = this.getWaybillScheduleDto(waybillCode);
            Waybill waybill=this.getWaybill(waybillCode);
            if(waybill!=null){
                String waybillSign=waybill.getWaybillSign();
                if(waybillSign.length()>=75){
                    Integer requireTransMode= StringHelper.stringToInteger(waybillSign,74);
                    transbillM.setRequireTransMode(requireTransMode);
                }
               else{
                    transbillM.setRequireTransMode(0);//和运单沟通，waybillsign长度不足75，默认设0
                }
            }
            if(waybillScheduleDto!=null){
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
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybillScheduleDto = baseEntity.getData().getWaybillSchedule();
            }
            this.logger.info("运单号【 " + waybillCode + "】调用调度信息成功");
        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用调度信息异常：", e);
        }
        if (waybillScheduleDto == null) {
            // 无数据
            this.logger.warn("运单号【 " + waybillCode + "】的调用调度信息为空");
        }
        return waybillScheduleDto;

    }
    private  Waybill getWaybill(String waybillCode){
        Waybill waybill = null;
        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillS(true);
            wChoice.setQueryWaybillC(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = baseEntity.getData().getWaybill();
            }
            this.logger.info("运单号【 " + waybillCode + "】调用运单数据成功，运单【" + waybill + "】");
        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单异常：", e);
        }
        if (waybill == null) {
            // 无数据
            logger.warn(waybillCode + "对应的运单信息为空！");
        }
        return waybill;
    }

}
