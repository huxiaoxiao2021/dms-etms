package com.jd.bluedragon.distribution.jy.service.transfer;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.config.JYTransferSiteEntity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.jy.service.transfer
 * @ClassName: JyTransferConfigService
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/18 21:23
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface JyTransferConfigService {

    InvokeResult<Boolean> isTransferSite(JYTransferSiteEntity entity);
}
