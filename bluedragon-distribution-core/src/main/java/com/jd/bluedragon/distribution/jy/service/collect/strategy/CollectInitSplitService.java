package com.jd.bluedragon.distribution.jy.service.collect.strategy;

import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public interface CollectInitSplitService {

    /**
     * 分批拆分
     * @param initCollectDto
     * @return
     */
    public boolean splitBeforeInit(InitCollectDto initCollectDto);

    /**
     * 拆分后初始化
     * @param initCollectSplitDto
     * @return
     */
    public boolean initAfterSplit(InitCollectSplitDto initCollectSplitDto);

}
