package com.jd.bluedragon.core.base;

import cn.jdl.batrix.sdk.base.BusinessIdentity;
import cn.jdl.batrix.spec.RequestProfile;
import cn.jdl.oms.api.ModifyExpressOrderService;
import cn.jdl.oms.core.model.ChannelInfo;
import cn.jdl.oms.core.model.ProductInfo;
import cn.jdl.oms.express.model.ModifyExpressOrderRequest;
import cn.jdl.oms.express.model.ModifyExpressOrderResponse;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.domain.CancelFeatherLetterRequest;
import com.jd.bluedragon.dms.utils.ProductTypeConstants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.OmsReqUtils;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName OmsManagerImpl
 * @Description
 * @Author wyh
 * @Date 2021/3/26 10:59
 **/
@Service
public class OmsManagerImpl implements OmsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OmsManagerImpl.class);

    private static final String CODE_SUCCESS = "1";

    public static final String BUSINESS_UNIT = "cn_jdl_c2c";
    public static final String BUSINESS_TYPE = "express";

    @Autowired
    private ModifyExpressOrderService modifyExpressOrderService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public ModifyExpressOrderRequest makeCancelLetterRequest(CancelFeatherLetterRequest request, String omcOrderCode) {
        ModifyExpressOrderRequest omsRequest = new ModifyExpressOrderRequest();

        BusinessIdentity identity = new BusinessIdentity();
        identity.setBusinessUnit(BUSINESS_UNIT);
        identity.setBusinessType(BUSINESS_TYPE);

        omsRequest.setBusinessIdentity(identity);

        omsRequest.setOrderNo(omcOrderCode);

        ChannelInfo channelInfo = new ChannelInfo();
        channelInfo.setChannelOperateTime(new Date());
        channelInfo.setSystemCaller(OmsReqUtils.SYSTEM_CALLER);
        omsRequest.setChannelInfo(channelInfo);

        omsRequest.setInitiatorType(OmsReqUtils.INITIATOR_TYPE_4);
        omsRequest.setOperator(request.getUserErp());

        // 设置字段要修改的类型
        Map<String, String> modifyFields = new HashMap<>();
        modifyFields.put("productInfos", OmsReqUtils.MODIFY_FILEDS_TYPE_ADD);
        omsRequest.setModifiedFields(modifyFields);

        // 设置要修改的字段的值
        List<ProductInfo> productInfos = new ArrayList<>();
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductNo(ProductTypeConstants.JI_MAO_XIN_TYPE);

        Map<String, String> extendProps = new HashMap<>();
        extendProps.put("operateType", OmsReqUtils.OPERATE_TYPE_DEL);
        productInfo.setExtendProps(extendProps);

        productInfos.add(productInfo);

        omsRequest.setProductInfos(productInfos);

        return omsRequest;
    }

    @Override
    public InvokeResult<String> cancelFeatherLetterByWaybillCode(final String waybillCode, final ModifyExpressOrderRequest request) {
        final InvokeResult<String> result = new InvokeResult<>();

        String umpKey = "Dms.Etms.OmsManagerImpl.cancelFeatherLetterByWaybillCode";
        CallerInfo callerInfo = Profiler.registerInfo(umpKey, Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {

            RequestProfile profile = OmsReqUtils.genProfile();
            profile.setTraceId(waybillCode + Constants.UNDER_LINE + UUID.randomUUID());

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("调用OMS取消服务. profile:{}, request:{}", JsonHelper.toJson(profile), JsonHelper.toJson(request));
            }

            // https://cf.jd.com/pages/viewpage.action?pageId=440443537#id-%E4%BA%8C%E3%80%81%E4%BF%AE%E6%94%B9%E6%9C%8D%E5%8A%A1%EF%BC%88C2C%EF%BC%89-2.3%E8%AF%B7%E6%B1%82%E5%8F%82%E6%95%B0
            ModifyExpressOrderResponse response = modifyExpressOrderService.modifyOrder(profile, request);
            if (response == null) {
                LOGGER.warn("取消鸡毛信服务失败-接口返回空. waybillCode[{}]", waybillCode);
                result.setCode(com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_CODE);
                result.setMessage("取消鸡毛信失败, 请求返回空！");
                return result;
            }

            if (!CODE_SUCCESS.equals(response.getCode())) {
                LOGGER.warn("取消鸡毛信服务失败. waybillCode[{}], responseDTO[{}]", waybillCode, JsonHelper.toJson(response));
                result.setCode(com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_CODE);
                result.setMessage(response.getMessage());
                return result;
            }
        }
        catch (Exception ex) {
            LOGGER.error("取消鸡毛信失败. request:{}", JsonHelper.toJson(request));
            result.error(ex);
            Profiler.functionError(callerInfo);
        }
        finally {
            Profiler.registerInfoEnd(callerInfo);
        }

        return result;
    }

}

