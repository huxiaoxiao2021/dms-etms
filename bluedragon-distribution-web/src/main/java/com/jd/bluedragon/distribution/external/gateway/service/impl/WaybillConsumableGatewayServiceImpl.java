package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.consumable.response.WaybillConsumablePackConfirmRes;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumablePDAService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.WaybillConsumableGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Description // //运单耗材服务： 转运木质耗材打包确认  B网
 * @date 20210617
 **/
public class WaybillConsumableGatewayServiceImpl implements WaybillConsumableGatewayService {

    private static final Logger log = LoggerFactory.getLogger(WaybillConsumableGatewayServiceImpl.class);

    @Autowired
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    private WaybillConsumablePDAService waybillConsumablePDAService;

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillConsumableGatewayServiceImpl.getWaybillConsumableInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<WaybillConsumablePackConfirmRes>> getWaybillConsumableInfo(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq) {
        String methodDesc = "WaybillConsumableGatewayServiceImpl.getWaybillConsumableInfo--PDA操作耗材确认查询接口--";
        JdCResponse<List<WaybillConsumablePackConfirmRes>> res = new JdCResponse<>();

        if (waybillConsumablePackConfirmReq == null) {
            res.toFail("请求信息不能为空");
            return res;
        }
        if(log.isInfoEnabled()) {
            log.info(methodDesc + "begin--参数=【{}】", JsonHelper.toJson(waybillConsumablePackConfirmReq));
        }

        if (waybillConsumablePackConfirmReq.getUser() == null || waybillConsumablePackConfirmReq.getUser().getUserErp() == null) {
            res.toFail("操作人信息ERP不能为空");
            return res;
        }

        if (StringUtils.isBlank(waybillConsumablePackConfirmReq.getBusinessCode())) {
            res.toFail("单号不能为空");
            return res;
        }

        try {
            return waybillConsumablePDAService.getWaybillConsumableInfo(waybillConsumablePackConfirmReq);
        } catch (Exception e) {
            log.error(methodDesc + "耗材查询失败，操作异常，参数=【{}】", JsonHelper.toJson(waybillConsumablePackConfirmReq), e);
            res.toError("耗材查询失败，服务异常！");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillConsumableGatewayServiceImpl.doWaybillConsumablePackConfirm",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> doWaybillConsumablePackConfirm(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq) {
        String methodDesc = "WaybillConsumableGatewayServiceImpl.doWaybillConsumablePackConfirm--PDA操作耗材确认接口（绑定打包人+耗材确认）--";
        JdCResponse<Boolean> res = new JdCResponse<Boolean>();

        if (waybillConsumablePackConfirmReq == null) {
            res.toFail("请求信息不能为空");
            return res;
        }
        if(log.isInfoEnabled()) {
            log.info(methodDesc + "begin--参数=【{}】", JsonHelper.toJson(waybillConsumablePackConfirmReq));
        }
        if (waybillConsumablePackConfirmReq.getUser() == null || waybillConsumablePackConfirmReq.getUser().getUserErp() == null) {
            res.toFail("操作人信息ERP不能为空");
            return res;
        }
        if (StringUtils.isBlank(waybillConsumablePackConfirmReq.getBusinessCode())) {
            res.toFail("单号不能为空");
            return res;
        }
        if(CollectionUtils.isEmpty(waybillConsumablePackConfirmReq.getWaybillConsumableDtoList())) {
            res.toFail("耗材信息不能为空");
            return res;
        }
        try {
            return waybillConsumablePDAService.doWaybillConsumablePackConfirm(waybillConsumablePackConfirmReq);
        } catch (Exception e) {
            log.error(methodDesc + "耗材确认失败，操作异常，参数=【{}】", JsonHelper.toJson(waybillConsumablePackConfirmReq), e);
            res.toError(e.getMessage());
            return res;
        }
    }

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillConsumableGatewayServiceImpl.canModifyConsumableNum",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> canModifyConsumableNum(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq) {
        String methodDesc = "WaybillConsumableGatewayServiceImpl.canModifyConsumableNum--PDA校验运单耗材是否可变更耗材数量--";
        JdCResponse<Boolean> res = new JdCResponse<Boolean>();
        res.toSucceed();

        if(waybillConsumablePackConfirmReq == null) {
            res.toFail("请求信息不能为空");
            return res;
        }
        if(StringUtils.isBlank(waybillConsumablePackConfirmReq.getBusinessCode())) {
            res.toFail("单号不能为空");
            return res;
        }
        String businessCode = waybillConsumablePackConfirmReq.getBusinessCode().trim();
        String waybillCode = "";
        if(!(WaybillUtil.isPackageCode(businessCode) || WaybillUtil.isWaybillCode(businessCode))) {
            res.toFail("请输入正确的单号，支持运单号或包裹号");
            return res;
        } else  {
            waybillCode = WaybillUtil.getWaybillCode(businessCode);
        }
        try {
            res.setData(waybillConsumableRecordService.canModify(waybillCode));
            return res;
        } catch (Exception e) {
            log.error(methodDesc + "校验失败，操作异常，参数=【{}】", JsonHelper.toJson(waybillConsumablePackConfirmReq), e);
            res.toError("校验失败，服务异常！");
            return res;
        }
    }
}
