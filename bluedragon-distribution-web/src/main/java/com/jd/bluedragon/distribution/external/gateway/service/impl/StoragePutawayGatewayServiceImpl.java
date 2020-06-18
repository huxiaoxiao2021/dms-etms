package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.storageputaway.request.StoragePutawayRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.storage.StorageResource;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StorageCheckDto;
import com.jd.bluedragon.external.gateway.service.StoragePutawayGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * StoragePutawayGatewayServiceImpl
 * 暂存上架处理
 *
 * @author jiaowenqiang
 * @date 2019/7/11
 */
@Service("storagePutawayGatewayServiceImpl")
public class StoragePutawayGatewayServiceImpl implements StoragePutawayGatewayService {

    @Autowired
    private StorageResource storageResource;

    /**
     * 获取储位信息
     */
    @Override
    @JProfiler(jKey = "DMSWEB.StoragePutawayGatewayServiceImpl.getStorageInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<String>> getStorageInfo(Integer siteCode) {

        JdCResponse<List<String>> jdCResponse = new JdCResponse<>();

        InvokeResult<List<String>> invokeResult = storageResource.getStorageInfo(Long.valueOf(siteCode));

        jdCResponse.setCode(invokeResult.getCode());
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());

        return jdCResponse;
    }

    /**
     * 校验储位
     */
    @Override
    @JProfiler(jKey = "DMSWEB.StoragePutawayGatewayServiceImpl.checkStorage",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> checkStorage(Integer siteCode, String storageCode) {

        JdCResponse<Boolean> jdCResponse = new JdCResponse<>();

        InvokeResult<Boolean> invokeResult = storageResource.checkStorage(Long.valueOf(siteCode), storageCode);
        jdCResponse.setCode(invokeResult.getCode());
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());

        return jdCResponse;
    }

    /**
     * 校验是否需要暂存
     */
    @Override
    public JdCResponse<Boolean> checkIsNeedStorage(String barCode, Integer siteCode) {
        JdCResponse<Boolean> jdCResponse = new JdCResponse<>();
        InvokeResult<Boolean> result = storageResource.checkIsNeedStorage(barCode, siteCode);
        jdCResponse.setCode(result.getCode());
        jdCResponse.setMessage(result.getMessage());
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }

    /**
     * 暂存上架校验
     */
    @Override
    public JdCResponse<StorageCheckDto> storageTempCheck(String barCode, Integer siteCode) {
        JdCResponse<StorageCheckDto> jdCResponse = new JdCResponse<>();
        InvokeResult<StorageCheckDto> result = storageResource.storageTempCheck(barCode, siteCode);
        jdCResponse.setCode(result.getCode());
        jdCResponse.setMessage(result.getMessage());
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }

    /**
     * 暂存上架
     */
    @Override
    public JdCResponse<Boolean> putaway(StoragePutawayRequest request) {

        JdCResponse<Boolean> jdCResponse = new JdCResponse<>();
        if(request == null){
            jdCResponse.toFail("入参不能为空");
            return jdCResponse;
        }

        if (request.getUser().getUserCode()<=0 || StringUtils.isNotBlank(request.getUser().getUserName()) || request.getCurrentOperate().getSiteCode()<=0 || StringUtils.isNotBlank(request.getCurrentOperate().getSiteName())){
            jdCResponse.toFail("操作人信息和场地信息都不能为空");
            return jdCResponse;
        }

        InvokeResult<Boolean> invokeResult = storageResource.putaway(convert(request));

        jdCResponse.setCode(invokeResult.getCode());
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());

        return jdCResponse;
    }

    /**
     * 获取当前运单或包裹所属的履约单已经上架过的储位号
     * （如若上架过多个储位，则返回最近一次储位号）
     */
    @Override
    public JdCResponse<String> getExistStorageCode(String barCode) {

        JdCResponse<String> jdCResponse = new JdCResponse<>();

        InvokeResult<String> invokeResult = storageResource.getExistStorageCode(barCode);

        jdCResponse.setCode(invokeResult.getCode());
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());


        return jdCResponse;
    }


    private PutawayDTO convert(StoragePutawayRequest param) {
        PutawayDTO putawayDTO = new PutawayDTO();

        putawayDTO.setStorageCode(param.getStorageCode());
        putawayDTO.setBarCode(param.getBarCode());
        putawayDTO.setCreateSiteCode(param.getCurrentOperate().getSiteCode());
        putawayDTO.setOperateTime(new Date().getTime());
        putawayDTO.setOperatorErp(param.getErp());
        putawayDTO.setOperatorId(param.getUser().getUserCode());
        putawayDTO.setOperatorName(param.getUser().getUserName());
        putawayDTO.setStorageSource(param.getStorageSource());
        putawayDTO.setForceStorage(param.getForceStorage());

        return putawayDTO;
    }
}
