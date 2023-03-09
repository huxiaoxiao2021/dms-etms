package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectDto;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectSiteTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectSiteTypeServiceFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @Author zhengchengfa
 * @Description //末级场地类型
 * @date
 **/
@Service("endSiteTypeImpl")
public class EndSiteTypeImpl implements CollectSiteTypeService, InitializingBean {


    @Override
    public void afterPropertiesSet() throws Exception {
        CollectSiteTypeServiceFactory.registerCollectSiteType(CollectSiteTypeEnum.WAYBILL.getCode(), this);
    }

    /**
     * 中转场地集齐初始化：直接处理封车数据
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
