package com.jd.bluedragon.distribution.eclpPackage.manager.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.ThirdWaybillNoRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.eclpPackage.manager.EclpLwbB2bPackageItemManager;
import com.jd.bluedragon.distribution.waybill.domain.ThirdWaybillNoResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.eclp.bbp.co.lwb.service.LwbB2bPackageItemService;
import com.jd.eclp.bbp.so.param.SoBindRelationParam;
import com.jd.eclp.bbp.so.param.SoBindRelationResponse;
import com.jd.eclp.bbp.so.service.SoBindRelationService;
import com.jd.eclp.core.ApiResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName EclpLwbB2bPackageItemManagerImpl
 * @Description
 * @Author wyh
 * @Date 2020/7/20 14:25
 **/
@Service("eclpLwbB2bPackageItemManager")
public class EclpLwbB2bPackageItemManagerImpl implements EclpLwbB2bPackageItemManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(EclpLwbB2bPackageItemManagerImpl.class);

    @Autowired
    private LwbB2bPackageItemService lwbB2bPackageItemService;

    @Autowired
    private SoBindRelationService sellerPackageNo;

    //调用系统来源
    public static String SOURCE="";

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.EclpLwbB2bPackageItemManagerImpl.findSellerPackageCode",
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public String findSellerPackageCode(String sellerPackageNo) {

        if (StringUtils.isBlank(sellerPackageNo)) {
            return StringUtils.EMPTY;
        }

        List<String> packageCodes = lwbB2bPackageItemService.getJdPackageNoBySellerPackageNo(sellerPackageNo);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("根据三方运单号查询京东包裹号, req:[{}], resp:[{}]", sellerPackageNo, packageCodes);
        }
        if (CollectionUtils.isNotEmpty(packageCodes)) {
            return packageCodes.get(0);
        }
        else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("根据三方运单号查询京东包裹号返回结果为空, 原始单号:[{}]", sellerPackageNo);
            }
        }

        return null;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.BASE.EclpLwbB2bPackageItemManagerImpl.searchPackageNoByThirdWaybillNo",
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ThirdWaybillNoResult> searchPackageNoByThirdWaybillNo(ThirdWaybillNoRequest request) {
        InvokeResult<ThirdWaybillNoResult> result = new InvokeResult<>();
        result.error("由三方运单号查询京东包裹号失败！");
        try {
            SoBindRelationParam param = new SoBindRelationParam();
            param.setSource(SOURCE);
            param.setThirdWaybill(request.getThirdWaybill());
            ApiResponse<SoBindRelationResponse> response = sellerPackageNo.queryThirdAndWaybillRelation(param);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("由三方运单号查询京东包裹号, request:[{}], response:[{}]", JsonHelper.toJson(request), JsonHelper.toJson(response));
            }

            if (response != null) {
                if (Objects.equals(1, response.getCode()) && response.getData() != null) {
                    SoBindRelationResponse dataSource = response.getData();
                    ThirdWaybillNoResult dataResult=new ThirdWaybillNoResult();
                    dataResult.setWaybillCode(dataSource.getWaybill());
                    result.success();
                    result.setData(dataResult);
                }
            }
        } catch (Exception e) {
            LOGGER.error("由三方运单号{}查询京东包裹号异常", request.getThirdWaybill(), e);
            result.error("由三方运单号查询京东包裹号异常");
            return result;
        }
        return result;
    }
}
