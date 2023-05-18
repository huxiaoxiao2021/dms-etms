package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiFailDetailDto;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.arAbnormal.ArAbnormalService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.external.service.DmsExternalService;
import com.jd.bluedragon.distribution.send.service.DeliveryServiceImpl;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.distribution.wss.dto.SealBoxDto;
import com.jd.bluedragon.distribution.wss.dto.SealVehicleDto;
import com.jd.bluedragon.distribution.wss.service.PopAbnormalWssService;
import com.jd.bluedragon.distribution.wss.service.SealVehicleBoxService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("dmsExternalService")
public class DmsExternalServiceImpl implements DmsExternalService {

    private final Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);


    @Autowired
    private SealVehicleBoxService vehicleBoxService;

    @Autowired
    private PopAbnormalWssService popWssService;
    
    @Autowired
    @Qualifier("jsonCommandService")
    private JdCommandService jdCommandService;

    @Autowired
    private ArAbnormalService arAbnormalService;

    @Autowired
    private AllianceBusiDeliveryDetailService allianceBusiDeliveryDetailService;

    @Resource
    private SortingService sortingService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.updatePopPackNum",mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Boolean updatePopPackNum(String message) {
        return popWssService.updatePopPackNum(message);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.batchAddSealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BaseEntity<Map<String, Integer>> batchAddSealVehicle(List<SealVehicleDto> sealVehicleList) {
        return vehicleBoxService.batchAddSealVehicle(sealVehicleList);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.batchUpdateSealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BaseEntity<Map<String, Integer>> batchUpdateSealVehicle(List<SealVehicleDto> sealVehicleList) {
        return vehicleBoxService.batchUpdateSealVehicle(sealVehicleList);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.batchAddSealBox", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BaseEntity<Map<String, Integer>> batchAddSealBox(List<SealBoxDto> sealBoxList) {
        return vehicleBoxService.batchAddSealBox(sealBoxList);
    }
    /**
     * 执行json格式的的指令
     * @param jsonCommand
     * @return
     */
	@Override
	@JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.executeJsonCommand",mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public String executeJsonCommand(String jsonCommand) {
		return jdCommandService.execute(jsonCommand);
	}

    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.pushArAbnormal",mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public ArAbnormalResponse pushArAbnormal(ArAbnormalRequest arAbnormalRequest) {
        return arAbnormalService.pushArAbnormal(arAbnormalRequest);
    }

    /**
     * 加盟商交接
     * <p>
     * 优先判断交接池是否已校验过，如果校验过则不校验加盟商预付款余额
     * 未校验过则校验加盟商预付款余额是否充足，充足则直接记录称重信息和交接信息
     * 不充足则返回失败提示调用者
     * 如果校验池已校验过则直接记录称重信息和交接信息
     *
     * @param dto
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.allianceBusiDelivery",jAppName=Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public BaseEntity<List<AllianceBusiFailDetailDto>> allianceBusiDelivery(AllianceBusiDeliveryDto dto) {

        BaseEntity<List<AllianceBusiFailDetailDto>> result = new BaseEntity<>();
        try{
            result = allianceBusiDeliveryDetailService.allianceBusiDelivery(dto);
        }catch (Exception e){
            log.error("加盟商交接交接异常,入参:{}", JsonHelper.toJson(dto),e);
            result.setCode(BaseEntity.CODE_SERVICE_ERROR);
            result.setMessage(BaseEntity.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsExternalServiceImpl.getPagePackageNoByBoxCode",jAppName=Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public InvokeResult<List<String>> getPagePackageNoByBoxCode(SortingPageRequest request) {
        InvokeResult<List<String>> result = new InvokeResult<>();
        result.success();
        if (request == null || StringUtils.isBlank(request.getBoxCode())) {
            result.parameterError("缺少必要查询参数");
            return result;
        }
        if(request.getOffset() < 0 || request.getLimit() < 1){
            result.parameterError("分页参数不合法");
            return result;
        }
        if(request.getLimit() > 500){
            result.parameterError("数据条数不能大于500");
            return result;
        }
        result.setData(sortingService.getPagePackageNoByBoxCode(request));
        return result;
    }


}
