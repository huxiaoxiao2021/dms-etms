package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.storageputaway.request.StoragePutawayRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.storage.StorageResource;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.external.gateway.service.StoragePutawayGatewayService;
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
    StorageResource storageResource;

    /**
     * 获取储位信息
     */
    @Override
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
    public JdCResponse<Boolean> checkStorage(Integer siteCode, String storageCode) {

        JdCResponse<Boolean> jdCResponse = new JdCResponse<>();

        InvokeResult<Boolean> invokeResult = storageResource.checkStorage(Long.valueOf(siteCode), storageCode);
        jdCResponse.setCode(invokeResult.getCode());
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());

        return jdCResponse;
    }

    /**
     * 暂存上架
     */
    @Override
    public JdCResponse<Boolean> putaway(StoragePutawayRequest request) {

        JdCResponse<Boolean> jdCResponse = new JdCResponse<>();

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

        return putawayDTO;
    }
}
