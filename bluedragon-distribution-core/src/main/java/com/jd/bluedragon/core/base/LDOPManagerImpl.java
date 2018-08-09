package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSON;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.reverse.WaybillReverseApi;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResponseDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResult;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 外单 jsf接口 包装类
 */
@Service("ldopManager")
public class LDOPManagerImpl implements LDOPManager {

    @Autowired
    private WaybillReverseApi waybillReverseApi;

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
}
