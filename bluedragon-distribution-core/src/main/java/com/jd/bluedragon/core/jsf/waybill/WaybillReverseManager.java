package com.jd.bluedragon.core.jsf.waybill;

import java.util.Date;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResponseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResult;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.ql.dms.common.domain.JdResponse;

/**
 * 外单jsf接口 包装类
 */
public interface WaybillReverseManager {

    /**
     * 外单自动换单接口
     * @param waybillReverseDTO
     * @return
     */
    boolean waybillReverse(DmsWaybillReverseDTO waybillReverseDTO,JdResponse<Boolean> rest);

    /**
     * 带返回结果的 外单自动换单接口
     * @param waybillReverseDTO
     * @param  errorMessage   错误信息
     * @return  返回结果展示数据用
     */
    DmsWaybillReverseResult waybillReverse(DmsWaybillReverseDTO waybillReverseDTO,StringBuilder errorMessage);
    
    /**
     * 获取换单前信息
     * @param waybillReverseDTO
     * @param errorMessage
     * @return
     */
    DmsWaybillReverseResponseDTO queryReverseWaybill(DmsWaybillReverseDTO waybillReverseDTO, StringBuilder errorMessage);
    
    /**
     * 外单逆向换单组装入参对象
     * @param waybillCode 运单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人
     * @param operateTime 操作时间
     * @param packageCount 拒收包裹数量
     * @param isTotal 是否是整单拒收
     * @return
     */
    DmsWaybillReverseDTO makeWaybillReverseDTO(String waybillCode, Integer operatorId, String operatorName, Date operateTime , Integer packageCount, Integer orgId, Integer createSiteCode, boolean isTotal);

    /**
     * 组装换单对象 支持二次换单
     * @param exchangeWaybillDto
     * @return
     */
    DmsWaybillReverseDTO makeWaybillReverseDTOCanTwiceExchange(ExchangeWaybillDto exchangeWaybillDto);
    /**
     * 获取运单信息
     * @param waybillCode
     * @return
     */
    Waybill getQuickProduceWabillFromDrec(String waybillCode);
    /**
     * 根据旧单号获取新单号
     * @param oldWaybillCode
     * @return
     */
    JdResult<String> queryWaybillCodeByOldWaybillCode(String oldWaybillCode);
}