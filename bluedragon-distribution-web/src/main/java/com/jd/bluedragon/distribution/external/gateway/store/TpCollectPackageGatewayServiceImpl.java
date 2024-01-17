package com.jd.bluedragon.distribution.external.gateway.store;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.constants.BoxTypeV2Enum;
import com.jd.bluedragon.distribution.box.domain.GenerateBoxReq;
import com.jd.bluedragon.distribution.box.domain.GenerateBoxResp;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxReq;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxResp;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.external.gateway.store.TpCollectPackageGatewayService;
import com.jd.bluedragon.utils.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class TpCollectPackageGatewayServiceImpl implements TpCollectPackageGatewayService {

    @Autowired
    private BoxService boxService;
    @Override
    public JdResponse<GenerateBoxResp> generateBoxCode(GenerateBoxReq request) {
        checkGenerateBoxReq(request);
        BoxRequest boxRequest =assembleBoxRequest(request);
        BoxResponse boxResponse = boxService.genBoxWithoutSiteInfo(boxRequest);
        JdResponse jdResponse = converJdResponse(boxResponse);
        assembleBoxData(boxResponse, jdResponse);
        return jdResponse;
    }

    private static void assembleBoxData(BoxResponse boxResponse, JdResponse jdResponse) {
        if (ObjectHelper.isNotNull(boxResponse.getBoxCodes())){
            GenerateBoxResp generateBoxResp =new GenerateBoxResp();
            generateBoxResp.setBoxCodes(Arrays.asList(boxResponse.getBoxCodes().split(",")));
            jdResponse.setData(generateBoxResp);
        }
    }

    @Override
    public JdResponse<UpdateBoxResp> updateBox(UpdateBoxReq request) {
        checkUpdateBoxReq(request);
        BoxResponse boxResponse = boxService.updateBox(request);
        return converJdResponse(boxResponse);
    }

    private void checkUpdateBoxReq(UpdateBoxReq request) {
        if (ObjectHelper.isEmpty(request.getBoxCode())){
            throw new IllegalArgumentException("参数错误：缺失箱号！");
        }
        if (ObjectHelper.isEmpty(request.getCreateSiteCode()) && ObjectHelper.isEmpty(request.getStoreInfo())){
            throw new IllegalArgumentException("参数错误：缺失始发场地信息！");
        }
        if (ObjectHelper.isNotNull(request.getStoreInfo())){
            if (ObjectHelper.isEmpty(request.getStoreInfo().getStoreType())){
                throw new IllegalArgumentException("参数错误：缺失仓储类型！");
            }
            if (ObjectHelper.isEmpty(request.getStoreInfo().getCky2())){
                throw new IllegalArgumentException("参数错误：缺失配送中心信息！");
            }
            if (ObjectHelper.isEmpty(request.getStoreInfo().getStoreId())){
                throw new IllegalArgumentException("参数错误：缺失库房id信息！");
            }
        }
        if (ObjectHelper.isEmpty(request.getReceiveSiteCode())){
            throw new IllegalArgumentException("参数错误：缺失目的场地信息！");
        }
        if (ObjectHelper.isEmpty(request.getUserCode())){
            throw new IllegalArgumentException("参数错误：缺失操作人编码信息！");
        }
        if (ObjectHelper.isEmpty(request.getUserName())){
            throw new IllegalArgumentException("参数错误：缺失操作人姓名信息！");
        }
        if (ObjectHelper.isEmpty(request.getOpeateTime())){
            throw new IllegalArgumentException("参数错误：缺失操作时间信息！");
        }
        if (ObjectHelper.isEmpty(request.getMixBoxType())){
            throw new IllegalArgumentException("参数错误：缺失箱号混装类型信息！");
        }
        if (ObjectHelper.isEmpty(request.getTransportType())){
            throw new IllegalArgumentException("参数错误：缺失箱号运输类型信息！");
        }
    }

    private JdResponse converJdResponse(BoxResponse boxResponse) {
        JdResponse jdResponse =new JdResponse();
        if (ObjectHelper.isNotNull(boxResponse)){
            jdResponse.setCode(boxResponse.getCode());
            jdResponse.setMessage(boxResponse.getMessage());
        }
        return jdResponse;
    }

    private BoxRequest assembleBoxRequest(GenerateBoxReq request) {
        BoxRequest boxRequest =new BoxRequest();
        boxRequest.setSystemType(request.getSource());
        boxRequest.setType(request.getBoxType());
        boxRequest.setSubType(request.getBoxSubType());
        boxRequest.setQuantity(request.getCount());
        return boxRequest;
    }

    private void checkGenerateBoxReq(GenerateBoxReq request) {
        if (ObjectHelper.isEmpty(request.getBoxType())){
            throw new IllegalArgumentException("参数错误：缺失箱号类型！");
        }
        if (ObjectHelper.isEmpty(request.getBoxSubType())){
            throw new IllegalArgumentException("参数错误：缺失箱号子类型！");
        }
        if (ObjectHelper.isEmpty(request.getSource())){
            throw new IllegalArgumentException("参数错误：缺失系统调用来源！");
        }
        if (ObjectHelper.isEmpty(request.getCount())){
            throw new IllegalArgumentException("参数错误：缺失生成箱号数量！");
        }
        if (ObjectHelper.isEmpty(BoxTypeV2Enum.getFromCode(request.getBoxType()))){
            throw new IllegalArgumentException("参数错误：暂不支持该类型箱号！");
        }
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(),
                invokeResult.getData());
    }

    public static void main(String[] args) {
        String a ="";
        List<String> aa =Arrays.asList(a.split(","));
        System.out.println(aa);
    }
}
