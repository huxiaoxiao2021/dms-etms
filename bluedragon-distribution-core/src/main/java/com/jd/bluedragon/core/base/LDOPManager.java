package com.jd.bluedragon.core.base;

import com.jd.ldop.center.api.reverse.dto.WaybillReverseDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResponseDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResult;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.Date;
import java.util.List;

/**
 * 外单jsf接口 包装类
 */
public interface LDOPManager {

    /**
     * 外单自动换单接口
     * @param waybillReverseDTO
     * @return
     */
    boolean waybillReverse(WaybillReverseDTO waybillReverseDTO,JdResponse<Boolean> rest);

    /**
     * 带返回结果的 外单自动换单接口
     * @param waybillReverseDTO
     * @param  errorMessage   错误信息
     * @return  返回结果展示数据用
     */
    WaybillReverseResult waybillReverse(WaybillReverseDTO waybillReverseDTO,StringBuilder errorMessage);

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
    WaybillReverseDTO makeWaybillReverseDTO(String waybillCode, Integer operatorId, String operatorName, Date operateTime , Integer packageCount, Integer orgId, Integer createSiteCode, boolean isTotal);

    /**
     * 获取换单前信息
     * @param waybillReverseDTO
     * @param errorMessage
     * @return
     */
    WaybillReverseResponseDTO queryReverseWaybill(WaybillReverseDTO waybillReverseDTO, StringBuilder errorMessage);

    }
