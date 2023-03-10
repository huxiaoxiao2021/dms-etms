package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.factory.CollectInitSplitServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * @Author zhengchengfa
 * @Description //集齐初始化： 无任务扫描，按运单号分页拆分
 * @date
 **/
public class CollectWaybillInitSplitServiceImpl implements CollectInitSplitService, InitializingBean {
    private Logger logger = LoggerFactory.getLogger(CollectWaybillInitSplitServiceImpl.class);

//    @Autowired
//    private CargoDetailServiceManager cargoDetailServiceManager;
//    @Autowired
//    private JyCollectService jyCollectService;
//    @Autowired
//    private VosManager vosManager;
//    @Autowired
//    private BaseMajorManager baseMajorManager;


    @Override
    public void afterPropertiesSet() throws Exception {
        CollectInitSplitServiceFactory.registerCollectInitSplitService(CollectInitNodeEnum.NULL_TASK_INIT.getCode(), this);
    }

    @Override
    public boolean splitBeforeInit(InitCollectDto initCollectDto) {
        //todo zcf 空任务处理运单拆分逻辑
        return true;
    }

    @Override
    public boolean initAfterSplit(InitCollectSplitDto initCollectSplitDto) {
        //todo zcf 空任务处理运单拆分逻辑
        return true;
    }

}
