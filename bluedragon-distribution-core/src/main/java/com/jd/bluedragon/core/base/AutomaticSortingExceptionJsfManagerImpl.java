package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.sorting.SortingExceptionJsfService;
import com.jd.bd.dms.automatic.sdk.modules.sorting.entity.WaybillInterceptTips;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sortingExceptionManager")
public class AutomaticSortingExceptionJsfManagerImpl implements AutomaticSortingExceptionJsfManager {

    private static final Logger log = LoggerFactory.getLogger(AutomaticSortingExceptionJsfManagerImpl.class);

    @Autowired
    private SortingExceptionJsfService sortingExceptionJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.AutomaticSortingExceptionJsfManagerImpl.getSortingExceptionTips", mState = {JProEnum.TP})
    public List<WaybillInterceptTips> getSortingExceptionTips(String barCode, Integer siteCode) {
        log.info("获取分拣机拦截信息开始，条码号：{}，操作站点：{}", barCode, siteCode);
        BaseDmsAutoJsfResponse<List<WaybillInterceptTips>> result = null;
        List<WaybillInterceptTips> list = null;

        try {
            result = sortingExceptionJsfService.getSortingExceptionTips(barCode, siteCode);
            if (result != null && BaseDmsAutoJsfResponse.SUCCESS_CODE == result.getStatusCode()) {
                list = result.getData();
            } else {
                log.error("获取分拣机拦截信息失败，请求返回：{}", JsonHelper.toJson(result));
            }
        } catch (Exception e) {
            log.error("获取分拣机拦截信息出错，请求返回：{}", JsonHelper.toJson(result), e);
        }

        return list;
    }
}
