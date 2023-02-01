package com.jd.bluedragon.distribution.jy.service.send;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleCommonRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleToScanPackageDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProductTypeAgg;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanPackage;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanPackageDetailResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JySendVehicleTysJsfService;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleToScanPackage;
import com.jd.bluedragon.distribution.jy.send.SendVehicleCommonReq;
import com.jd.bluedragon.distribution.jy.send.SendVehicleToScanPackageDetailDto;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/7 15:59
 * @Description:
 */
@Service("jySendVehicleTysJsfService")
public class JySendVehicleTysJsfServiceImpl implements JySendVehicleTysJsfService {


    private static final Logger log = LoggerFactory.getLogger(JySendVehicleTysJsfServiceImpl.class);


    @Autowired
    private IJySendVehicleService jySendVehicleService;

    @Override
    public InvokeResult<List<JySendVehicleProductType>> getSendVehicleToScanProductType(SendVehicleCommonReq request) {
        InvokeResult<List<JySendVehicleProductType>> invokeResult = new InvokeResult<>();
        SendVehicleCommonRequest req = new SendVehicleCommonRequest();
        BeanUtils.copyProperties(request, req);
        log.info("tys拣运发车岗获取待扫产品类型 入参-{}", JSON.toJSONString(req));
        try {
            InvokeResult<List<SendVehicleProductTypeAgg>> result = jySendVehicleService.sendVehicleToScanAggByProduct(req);
            invokeResult.setMessage(result.getMessage());
            invokeResult.setCode(result.getCode());
            List<JySendVehicleProductType> productTypeList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(result.getData())) {
                for (SendVehicleProductTypeAgg typeAgg : result.getData()) {
                    JySendVehicleProductType type = new JySendVehicleProductType();
                    BeanUtils.copyProperties(typeAgg, type);
                    productTypeList.add(type);
                }
            }
            log.info("tys拣运发车岗获取待扫产品类型 结果-{}", JSON.toJSONString(productTypeList));
            invokeResult.setData(productTypeList);
        } catch (Exception e) {
            log.error("tys拣运发车岗获取待扫产品类型异常！-{}", e.getMessage(), e);
            invokeResult.error("拣运发车岗获取待扫产品类型异常！");
        }
        return invokeResult;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleTysJsfServiceImpl.getSendVehicleToScanPackages",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendVehicleToScanPackageDetailDto> getSendVehicleToScanPackages(SendVehicleCommonReq request) {
        InvokeResult<SendVehicleToScanPackageDetailDto> invokeResult = new InvokeResult<>();
        SendVehicleToScanPackageDetailRequest req = new SendVehicleToScanPackageDetailRequest();
        BeanUtils.copyProperties(request, req);
        log.info("拣运发车岗获取待扫包裹列表 入参-{}", JSON.toJSONString(req));
        try {
            InvokeResult<SendVehicleToScanPackageDetailResponse> result = jySendVehicleService.sendVehicleToScanPackageDetail(req);
            invokeResult.setCode(result.getCode());
            invokeResult.setMessage(result.getMessage());
            if (Objects.nonNull(result.getData())) {
                SendVehicleToScanPackageDetailDto dto = new SendVehicleToScanPackageDetailDto();
                dto.setPackageCount(result.getData().getPackageCount());
                dto.setProductType(request.getProductType());
                dto.setProductTypeName(result.getData().getProductTypeName());
                List<JySendVehicleToScanPackage> packageCodeList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(result.getData().getPackageCodeList())) {
                    for (SendVehicleToScanPackage toScanPackage : result.getData().getPackageCodeList()) {
                        JySendVehicleToScanPackage p = new JySendVehicleToScanPackage();
                        BeanUtils.copyProperties(toScanPackage, p);
                        packageCodeList.add(p);
                    }
                }
                dto.setPackageCodeList(packageCodeList);
                invokeResult.setData(dto);
            }
        } catch (Exception e) {
            log.error("拣运发车岗获取待扫包裹列表异常！-{}", e.getMessage(), e);
            invokeResult.error("拣运发车岗获取待扫包裹列表异常！");
        }
        return invokeResult;
    }
}
