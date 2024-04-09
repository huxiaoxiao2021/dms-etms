package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.cmp.jsf.SwDeviceJsfService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("miniStoreJsfManger")
@Slf4j
public class MiniStoreJsfMangerImpl implements MiniStoreJsfManger{
    @Autowired
    SwDeviceJsfService swDeviceJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.MiniStoreJsfMangerImpl.isDeviceUse", mState = {JProEnum.TP})
    public Integer isDeviceUse(String code) {
        try {
            Integer availableStatus = swDeviceJsfService.isDeviceUse(code);
            log.info("调用保温箱系统校验微仓设备可用性接口，设备码:{}，返回结果:{}",code,availableStatus);
            return availableStatus;
        } catch (Exception e) {
            log.error("调用保温箱系统isDeviceUse接口异常,设备码:{}", code,e);
        }
        return -1;
    }
}
