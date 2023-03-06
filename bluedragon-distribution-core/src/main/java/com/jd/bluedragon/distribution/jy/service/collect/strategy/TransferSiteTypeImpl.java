package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectDto;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectSiteTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectSiteTypeServiceFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @Author zhengchengfa
 * @Description //中转场地类型
 * @date
 **/
@Service("transferSiteTypeImpl")
public class TransferSiteTypeImpl implements CollectSiteTypeService, InitializingBean {


    @Override
    public void afterPropertiesSet() throws Exception {
        CollectSiteTypeServiceFactory.registerCollectSiteType(CollectSiteTypeEnum.HANDOVER.getCode(), this);
    }

    /**
     * 末端场地集齐初始化： 查运单组装
     * @param collectDto
     * @return
     */
    @Override
    public InvokeResult initCollect(CollectDto collectDto) {
        return null;
    }

    @Override
    public InvokeResult removeCollect(CollectDto collectDto) {
        return null;
    }
}
