package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.print.WaybillPrintApi;
import com.jd.ldop.center.api.print.dto.PrintResultDTO;
import com.jd.ldop.center.api.print.dto.WaybillPrintDataDTO;
import com.jd.ldop.center.api.print.dto.WaybillPrintRequestDTO;
import com.jd.ldop.center.api.reverse.WaybillReturnSignatureApi;
import com.jd.ldop.center.api.reverse.WaybillReverseApi;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureResult;
import com.jd.ldop.center.api.reverse.dto.WaybillReturnSignatureDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResponseDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResult;
import com.jd.ldop.center.api.update.dto.WaybillAddress;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 外单 jsf接口 包装类
 */
@Service("ldopManager")
public class LDOPManagerImpl implements LDOPManager {

    @Autowired
    private WaybillReverseApi waybillReverseApi;

    @Autowired
    private WaybillPrintApi waybillPrintApi;

    @Autowired
    private WaybillReturnSignatureApi waybillReturnSignatureApi;

    private final Logger logger = Logger.getLogger(LDOPManagerImpl.class);
    /**
     * 触发外单逆向换单接口
     * @param waybillReverseDTO
     * @return
     */
    public boolean waybillReverse(WaybillReverseDTO waybillReverseDTO,JdResponse<Boolean> rest){

        CallerInfo info = null;
        try{
            info = Profiler.registerInfo( "DMSWEB.LDOPManagerImpl.waybillReverse",false, true);

            ResponseDTO responseDTO = waybillReverseApi.waybillReverse(waybillReverseDTO);
            if(!responseDTO.getStatusCode().equals(ResponseDTO.SUCCESS_CODE)){
                //失败
                rest.setMessage("外单自动换单接口失败 "+responseDTO.getStatusMessage());
                logger.error("触发逆向换单失败,入参："+ JsonHelper.toJson(waybillReverseDTO)+"  失败原因："+responseDTO.getStatusMessage());
                return false;
            }

            return true;
        }catch (Exception e){
            logger.error("触发逆向换单失败,入参："+ JsonHelper.toJson(waybillReverseDTO)+"  失败原因："+e.getMessage());
            Profiler.functionError(info);
            return false;
        }finally{
            Profiler.registerInfoEnd(info);
        }

    }

    @Override
    public WaybillReverseResult waybillReverse(WaybillReverseDTO waybillReverseDTO,StringBuilder errorMessage) {
        CallerInfo info = null;
        try{
            info = Profiler.registerInfo( "DMSWEB.LDOPManagerImpl.waybillReverse",false, true);

            ResponseDTO responseDTO = waybillReverseApi.waybillReverse(waybillReverseDTO);
            if(responseDTO.getStatusCode().equals(ResponseDTO.SUCCESS_CODE)){
                return (WaybillReverseResult) responseDTO.getData();
            }else {
                //失败
                errorMessage.append("外单自动换单接口失败 "+responseDTO.getStatusMessage());
                logger.error("触发逆向换单失败,入参："+ JsonHelper.toJson(waybillReverseDTO)+"  失败原因："+responseDTO.getStatusMessage());
            }
            return null;
        }catch (Exception e){
            logger.error("触发逆向换单失败,入参："+ JsonHelper.toJson(waybillReverseDTO)+"  失败原因："+e.getMessage());
            Profiler.functionError(info);
            return null;
        }finally{
            Profiler.registerInfoEnd(info);

        }
    }


    /**
     *
     * @param waybillCode 运单号
     * @param operatorId 操作人ID
     * @param operatorName 操作人
     * @param operateTime 操作时间
     * @param packageCount 拒收包裹数量
     * @param isTotal 是否是整单拒收
     * @return
     */
    public WaybillReverseDTO makeWaybillReverseDTO(String waybillCode, Integer operatorId, String operatorName, Date operateTime , Integer packageCount, Integer orgId, Integer createSiteCode, boolean isTotal){
        WaybillReverseDTO waybillReverseDTO = new WaybillReverseDTO();
        waybillReverseDTO.setSource(2); //分拣中心
        if(isTotal){
            waybillReverseDTO.setReverseType(1);// 整单拒收
        }else{
            waybillReverseDTO.setReverseType(2);// 包裹拒收
        }

        waybillReverseDTO.setWaybillCode(waybillCode);
        waybillReverseDTO.setOperateUserId(operatorId);
        waybillReverseDTO.setOperateUser(operatorName);
        waybillReverseDTO.setOrgId(orgId);
        waybillReverseDTO.setSortCenterId(createSiteCode);
        waybillReverseDTO.setOperateTime(operateTime);
        waybillReverseDTO.setReturnType(0);//默认
        if(!new Integer(0).equals(packageCount)){
            waybillReverseDTO.setPackageCount(packageCount);
        }

        return waybillReverseDTO;
    }


    public WaybillReverseResponseDTO queryReverseWaybill(WaybillReverseDTO waybillReverseDTO,StringBuilder errorMessage) {

        CallerInfo info = null;
        try{
            info = Profiler.registerInfo( "DMSWEB.LDOPManagerImpl.queryReverseWaybill",false, true);

            ResponseDTO<WaybillReverseResponseDTO> responseDTO = waybillReverseApi.queryReverseWaybill(waybillReverseDTO);
            if(responseDTO.getStatusCode().equals(ResponseDTO.SUCCESS_CODE)){
                return responseDTO.getData();
            }else {
                //失败
                errorMessage.append("换单前获取外单信息接口失败 "+responseDTO.getStatusMessage());
                logger.error("换单前获取外单信息失败,入参："+ JsonHelper.toJson(waybillReverseDTO)+"  失败原因："+responseDTO.getStatusMessage());
            }
            return null;
        }catch (Exception e){
            logger.error("换单前获取外单信息失败,入参："+ JsonHelper.toJson(waybillReverseDTO)+"  失败原因："+e.getMessage());
            Profiler.functionError(info);
            return null;
        }finally{
            Profiler.registerInfoEnd(info);

        }
    }

    /**
     * 根据商家编码和运单号调用外单接口获取打印信息
     * @param customerCode
     * @param waybillCode
     * @return
     */
    public List<WaybillPrintDataDTO> getPrintDataForCityOrder(String customerCode, String waybillCode){
        WaybillPrintRequestDTO waybillPrintRequestDTO = new WaybillPrintRequestDTO();
        waybillPrintRequestDTO.setCustomerCode(customerCode);
        waybillPrintRequestDTO.setWaybillCode(waybillCode);

        CallerInfo info = null;
        try {
            info = Profiler.registerInfo("DMS.BASE.LDOPManagerImpl.getPrintDataForCityOrder",false,true);
            PrintResultDTO printResultDto= waybillPrintApi.getPrintDataForCityOrder(waybillPrintRequestDTO);

            if(printResultDto == null){
                logger.warn("根据商家编码和运单号调用外单接口获取打印信息为空.商家编码:" + customerCode + ", 运单号:" + waybillCode + ",返回值:" + printResultDto);
                return Collections.emptyList();
            }
            if(printResultDto.getData() == null){
                logger.warn("根据商家编码和运单号调用外单接口获取打印信息为空.商家编码:" + customerCode + ", 运单号:" + waybillCode + ",返回值:" + printResultDto.getStatusCode()+"-"+ printResultDto.getStatusMessage());
                return Collections.emptyList();
            }
            return printResultDto.getData();
        }catch (Exception e){
            logger.error("根据商家编码和运单号调用外单接口获取打印信息异常.",e);
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return Collections.emptyList();
    }

    /**
     * 组装换单对象支持二次换单
     * @param exchangeWaybillDto
     * @return
     */
    public WaybillReverseDTO makeWaybillReverseDTOCanTwiceExchange(ExchangeWaybillDto exchangeWaybillDto){
        WaybillReverseDTO waybillReverseDTO = new WaybillReverseDTO();
        waybillReverseDTO.setSource(2); //分拣中心
        if(exchangeWaybillDto.getIsTotalout()){
            waybillReverseDTO.setReverseType(1);// 整单拒收
        }else{
            waybillReverseDTO.setReverseType(2);// 包裹拒收
        }

        waybillReverseDTO.setWaybillCode(exchangeWaybillDto.getWaybillCode());
        waybillReverseDTO.setOperateUserId(exchangeWaybillDto.getOperatorId());
        waybillReverseDTO.setOperateUser(exchangeWaybillDto.getOperatorName());
        waybillReverseDTO.setOrgId(exchangeWaybillDto.getOrgId());
        waybillReverseDTO.setSortCenterId(exchangeWaybillDto.getCreateSiteCode());
        waybillReverseDTO.setOperateTime(DateHelper.parseDateTime(exchangeWaybillDto.getOperateTime()));
        waybillReverseDTO.setReturnType(0);//默认
        if(exchangeWaybillDto.getReturnType()!=null){
            waybillReverseDTO.setReturnType(exchangeWaybillDto.getReturnType());
        }
        if(!new Integer(0).equals(exchangeWaybillDto.getPackageCount())){
            waybillReverseDTO.setPackageCount(exchangeWaybillDto.getPackageCount());
        }

        //自定义地址
        if(exchangeWaybillDto.getAddress()!=null && !"".equals(exchangeWaybillDto.getAddress())){
            WaybillAddress waybillAddress = new WaybillAddress();

            waybillAddress.setAddress(exchangeWaybillDto.getAddress());
            waybillAddress.setContact(exchangeWaybillDto.getContact());
            waybillAddress.setPhone(exchangeWaybillDto.getPhone());
            waybillReverseDTO.setWaybillAddress(waybillAddress);
        }
        return waybillReverseDTO;
    }

    /**
     * 根据旧单号获取新单号
     * @param dto 旧单号对象
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.LDOPManagerImpl.waybillReturnSignature",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public ResponseDTO<ReturnSignatureResult> waybillReturnSignature(WaybillReturnSignatureDTO dto){
        return waybillReturnSignatureApi.waybillReturnSignature(dto);
    }

    /**
     * 根据运单号获得运单信息
     * @param waybillCode 运单号
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.LDOPManagerImpl.queryReturnSignatureMessage",
            mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public ResponseDTO<ReturnSignatureMessageDTO> queryReturnSignatureMessage(String waybillCode){
        return waybillReturnSignatureApi.queryReturnSignatureMessage(waybillCode);
    }

}
