package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reverse.domain.BackAddressDTOExt;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResponseDTO;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResult;
import com.jd.ldop.business.api.dto.request.BackAddressDTO;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.print.dto.WaybillPrintDataDTO;
import com.jd.ldop.center.api.refund.dto.RefundApplyDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureResult;
import com.jd.ldop.center.api.reverse.dto.WaybillReturnSignatureDTO;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.List;

/**
 * 外单jsf接口 包装类
 */
public interface LDOPManager {

    /**
     * 外单自动换单接口
     * @param dmsWaybillReverseDTO
     * @return
     */
    boolean waybillReverse(DmsWaybillReverseDTO dmsWaybillReverseDTO,JdResponse<Boolean> rest);

    /**
     * 带返回结果的 外单自动换单接口
     * @param dmsWaybillReverseDTO
     * @param  errorMessage   错误信息
     * @return  返回结果展示数据用
     */
    DmsWaybillReverseResult waybillReverse(DmsWaybillReverseDTO dmsWaybillReverseDTO,StringBuilder errorMessage);

    /**
     * 获取换单前信息
     * @param dmsWaybillReverseDTO
     * @param errorMessage
     * @return
     */
    DmsWaybillReverseResponseDTO queryReverseWaybill(DmsWaybillReverseDTO dmsWaybillReverseDTO, StringBuilder errorMessage);

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
	/**
	 * 扩展backAddressDTO，获取全地址
	 * @param backAddressDTO
	 * @return
	 */
	BackAddressDTOExt getBackAddressDTOExt(BackAddressDTO backAddressDTO);
	/**
	 * 退款接口 https://cf.jd.com/pages/viewpage.action?pageId=490844510
	 * @param refundApplyDTO
	 * @return
	 */
	JdResult<String> refundApply(RefundApplyDTO refundApplyDTO);
}
