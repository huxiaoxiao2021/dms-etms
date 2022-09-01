package com.jd.bluedragon.core.jsf.boxlimit.impl;

import com.jd.bluedragon.core.jsf.boxlimit.BoxLimitConfigManager;
import com.jdl.basic.api.service.boxLimit.BoxlimitConfigJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/2 14:27
 * @Description:
 */
@Slf4j
@Service("boxLimitConfigManager")
public class BoxLimitConfigManagerImpl implements BoxLimitConfigManager {

    @Autowired
    private BoxlimitConfigJsfService basicBoxlimitConfigJsfService;

    @Override
    public Integer getLimitNums(Integer createSiteCode, String type) {
        log.info("调用拣运基础服务集箱包裹配置信息 入参 {}-{}",createSiteCode,type);
        try{
            Result<Integer> response = basicBoxlimitConfigJsfService.getLimitNums(createSiteCode, type);
            if(response != null && response.getData() != null){
                return response.getData();
            }
        }catch (Exception e){
            log.info("调用拣运基础服务集箱包裹配置信息异常 {}",e.getMessage(),e);
        }
        return null;
    }
}
