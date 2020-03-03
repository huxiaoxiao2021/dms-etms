package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.ver.domain.JsfResponse;
import com.jd.dms.ver.domain.WaybillInterceptTips;
import com.jd.dms.ver.service.VerCommonJsfApi;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("verCommonJsfApiManager")
public class VerCommonJsfApiManagerImpl implements  VerCommonJsfApiManager {

    private static final Logger log = LoggerFactory.getLogger(VerCommonJsfApiManagerImpl.class);

    @Autowired
    private VerCommonJsfApi verCommonJsfApi;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.VerCommonJsfApiManagerImpl.getSortMachineInterceptTips", mState = {JProEnum.TP})
    public List<WaybillInterceptTips> getSortMachineInterceptTips(String barCode) {
        JsfResponse<List<WaybillInterceptTips>> result = null;


        log.info("获取分拣机业务拦截信息开始，条码号：{}", barCode);
        List<WaybillInterceptTips> list = null;
        try {
            result = verCommonJsfApi.getSortMachineInterceptTips(barCode);
            if (result != null && JsfResponse.SUCCESS_CODE.equals(result.getCode())) {
                list = result.getData();
            } else {
                log.error("获取分拣机业务信息失败，请求返回：{}", JsonHelper.toJson(result));
            }
        } catch (Exception e) {
            log.error("获取分拣机业务信息出错，请求返回：{}", JsonHelper.toJson(result), e);
        }

        return list;
    }
}
