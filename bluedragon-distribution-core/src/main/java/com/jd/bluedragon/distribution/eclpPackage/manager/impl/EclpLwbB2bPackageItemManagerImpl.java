package com.jd.bluedragon.distribution.eclpPackage.manager.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.eclpPackage.manager.EclpLwbB2bPackageItemManager;
import com.jd.eclp.bbp.co.lwb.service.LwbB2bPackageItemService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
