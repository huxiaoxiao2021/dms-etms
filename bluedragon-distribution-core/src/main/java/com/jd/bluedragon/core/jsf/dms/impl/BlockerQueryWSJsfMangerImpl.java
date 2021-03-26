package com.jd.bluedragon.core.jsf.dms.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.jsf.dms.BlockerQueryWSJsfManager;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.JsonUtil;
import com.jd.etms.blocker.constant.BlockerFlag;
import com.jd.etms.blocker.dto.CommonDto;
import com.jd.etms.blocker.dto.ExceptionOrderDto;
import com.jd.etms.blocker.dto.ExceptionOrderQueryDto;
import com.jd.etms.blocker.webservice.BlockerQueryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liming522
 * @Description: 运单拦截信息接口
 * @Date: create in 2021/3/26 10:16
 */
@Service("blockerQueryWSJsfManager")
public class BlockerQueryWSJsfMangerImpl implements BlockerQueryWSJsfManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // 运单拦截标识
    private final static  Integer blockerFlag = 1;


    @Autowired
    @Qualifier("blockerQueryWSJsfService")
    private BlockerQueryWS blockerQueryWS;

    /**
     * 运单拦截信息接口
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "dmsWeb.jsf.blockerQueryWSJsfManager.queryExceptionOrders",jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse queryExceptionOrders(String waybillCode) {
        JdCResponse jdCResponse = new JdCResponse<>();
        jdCResponse.toSucceed();
        //校验参数
        if(StringUtils.isEmpty(waybillCode)){
            jdCResponse.toError("调用运单拦截信息接口-运单参数为空");
            return jdCResponse;
        }

        ExceptionOrderQueryDto exceptionOrderQuery = new ExceptionOrderQueryDto();
        List<String> waybillCodes = new ArrayList<>();
        waybillCodes.add(waybillCode);
        exceptionOrderQuery.setWaybillCodes(waybillCodes);
        exceptionOrderQuery.setBlockerFlag(BlockerFlag.PROMISED);

        try {
            CommonDto<List<ExceptionOrderDto>>  ordersResult =   blockerQueryWS.queryExceptionOrders(exceptionOrderQuery);
            if(ordersResult == null || ordersResult.getCode() != CommonDto.CODE_SUCCESS){
                log.error("运单拦截接口异常,运单号{},返回结果{}",waybillCode, JsonHelper.toJsonMs(ordersResult));
                jdCResponse.toError("运单拦截接口异常");
                return jdCResponse;
            }

            //集合是空就不需要拦截
            if(CollectionUtils.isNotEmpty(ordersResult.getData())){
                jdCResponse.toError("此单已退款，请直接操作退回");
                return jdCResponse;
            }

        }catch (Exception e){
            log.error("调用运单异常订单拦截接口error,运单号{}",waybillCode,e);
        }
        return jdCResponse;
    }
}
    
