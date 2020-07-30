package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.ldop.business.api.dto.request.BackAddressDTO;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.print.dto.WaybillPrintDataDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureResult;
import com.jd.ldop.center.api.reverse.dto.WaybillReturnSignatureDTO;
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

    /**
     * 根据商家编码和运单号调用外单接口获取打印信息
     * @param customerCode
     * @param waybillCode
     * @return
     */
    List<WaybillPrintDataDTO> getPrintDataForCityOrder(String customerCode, String waybillCode);

    /**
     * 根据旧单号获取新单号
     * @param dto 旧单号对象
     * @return
     */
    ResponseDTO<ReturnSignatureResult> waybillReturnSignature(WaybillReturnSignatureDTO dto);

    /**
     * 根据运单号获得运单信息
     * @param waybillCode 运单号
     * @return
     */
    ResponseDTO<ReturnSignatureMessageDTO> queryReturnSignatureMessage(String waybillCode);

    /**
     * 组装换单对象 支持二次换单
     * @param exchangeWaybillDto
     * @return
     */
    WaybillReverseDTO makeWaybillReverseDTOCanTwiceExchange(ExchangeWaybillDto exchangeWaybillDto);

    /**
     * 根据商家ID和商家单号获取运单号
     * @param busiId 商家ID
     * @param busiCode 商家单号
     * @return
     */
    @Deprecated
    InvokeResult<String> findWaybillCodeByBusiIdAndBusiCode(String busiId,String busiCode);

    /**
     * 根据商家ID和商家单号获取运单号
     * @param busiId 商家ID
     * @param busiCode 商家单号
     * @return
     */
    String queryWaybillCodeByOrderIdAndCustomerCode(Integer busiId,String busiCode);
    /**
     * 根据退货类型和商家编码获取退货地址信息
     * @param backType 退货类型
     * @param busiCode 商家编码
     * @return
     */
	JdResult<List<BackAddressDTO>> queryBackAddressByType(Integer backType,String busiCode);
}
