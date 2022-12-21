package com.jd.bluedragon.distribution.jy.service.transfer;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JYTransferConfigJsfService;
import com.jd.bluedragon.distribution.jy.config.JYTransferSiteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.jy.service.transfer
 * @ClassName: JYTransferConfigJsfServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/18 21:25
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
public class JYTransferConfigJsfServiceImpl implements JYTransferConfigJsfService {

    @Autowired
    private JyTransferConfigService jyTransferConfigService;

    @Override
    public InvokeResult<Boolean> isTransferSite(JYTransferSiteEntity entity) {
        return jyTransferConfigService.isTransferSite(entity);
    }
}
