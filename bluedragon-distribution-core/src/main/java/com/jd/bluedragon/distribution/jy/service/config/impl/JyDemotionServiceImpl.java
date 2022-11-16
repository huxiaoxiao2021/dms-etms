package com.jd.bluedragon.distribution.jy.service.config.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.bluedragon.sdk.modules.client.dto.JyDemotionConfigInfo;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 拣运降级实现
 *
 * @author hujiping
 * @date 2022/10/10 4:17 PM
 */
@Service("jyDemotionService")
public class JyDemotionServiceImpl implements JyDemotionService {

    private static final Logger logger = LoggerFactory.getLogger(JyDemotionServiceImpl.class);

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    public boolean checkIsDemotion(String key) {
        try {
            String globalJyDemotionConfig = uccPropertyConfiguration.getJyDemotionConfig();
            List<JyDemotionConfigInfo> list = JsonHelper.jsonToList(globalJyDemotionConfig, JyDemotionConfigInfo.class);
            if(CollectionUtils.isNotEmpty(list)) {
                for (JyDemotionConfigInfo jyDemotionConfigInfo : list) {
                    if(Objects.equals(jyDemotionConfigInfo.getDemotionFuncKey(), key)){
                        return Objects.equals(jyDemotionConfigInfo.getDemotionSwitch(), true);
                    }
                }
            }
        }catch (Exception e){
            logger.error("获取拣运全局降级配置异常!", e);
        }
        return false;
    }

    @Override
    public List<JyDemotionConfigInfo> obtainJyDemotionConfig() {
        try {
            String globalJyDemotionConfig = uccPropertyConfiguration.getJyDemotionConfig();
            return JsonHelper.jsonToList(globalJyDemotionConfig, JyDemotionConfigInfo.class);
        }catch (Exception e){
            logger.error("获取拣运全局降级配置异常!", e);
        }
        return Lists.newArrayList();
    }
}
