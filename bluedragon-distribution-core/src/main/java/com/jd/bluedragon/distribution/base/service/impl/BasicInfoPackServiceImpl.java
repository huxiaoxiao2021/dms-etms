package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.service.BasicInfoPackService;
import com.jd.bluedragon.utils.JsonUtil;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.fastjson.JSON;
import com.jd.ldop.basic.api.BasicTraderAPI;
import com.jd.ldop.basic.dto.BasicTraderNeccesaryInfoDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/11/16 19:45
 */
public class BasicInfoPackServiceImpl implements BasicInfoPackService {

    private final Logger logger = LoggerFactory.getLogger(BasicInfoPackServiceImpl.class);

    @Autowired
    private WaybillQueryApi waybillQueryApi;

    @Autowired
    @Qualifier("basicTraderAPI")
    private BasicTraderAPI basicTraderAPI;

    /**
     * 通过运单号获取运单基本信息
     * @param waybillCode
     * @return
     */
    public Waybill packBasicInfo(String waybillCode) {
        BaseEntity<Waybill> waybillBaseEntity = null;
        try {
            //调用运单接口-分装运单对象
            waybillBaseEntity = waybillQueryApi.getWaybillByWaybillCode(waybillCode);
            if(waybillBaseEntity !=null && waybillBaseEntity.getData()!=null ){
                return waybillBaseEntity.getData();
            }
        }catch (Exception e){
            logger.error("通过运单获取青龙业主基本信息error,入参waybillCode:{},出参waybillBaseEntity{}",waybillCode,JSON.toJSONString(waybillBaseEntity),e);
        }
        return  null;
    }


    /**
     * 通过青龙业主号-获取商家基本信息
     * @param traderCode
     * @return
     */
    public  BasicTraderNeccesaryInfoDTO  getBaseTraderNeccesaryInfo(String traderCode){
        //封装商家的基本信息
        ResponseDTO<BasicTraderNeccesaryInfoDTO> responseDTO = null;
        try {
            responseDTO =  basicTraderAPI.getBaseTraderNeccesaryInfoByCode(traderCode);
            if(responseDTO != null && responseDTO.isSuccess() && responseDTO.getResult() != null ){
               return  responseDTO.getResult();
            }
        }catch (Exception e){
            logger.error("通过青龙业主号获取业主基本信息error,入参traderCode:{},出参responseDTO{}:",traderCode, JSON.toJSONString(responseDTO),e);
        }
        return null;
    }
}
    
