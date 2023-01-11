package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.service.send.JySendAggsService;
import com.jd.bluedragon.distribution.jy.service.send.JySendProductAggsService;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadAggsService;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.external.gateway.service.JySendOrUnloadAggDataGatewayService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/1/9 11:33
 * @Description:
 */
public class JySendOrUnloadAggDataGatewayServiceImpl implements JySendOrUnloadAggDataGatewayService {
    private static final Logger log = LoggerFactory.getLogger(JySendOrUnloadAggDataGatewayServiceImpl.class);


    @Autowired
    private JySendAggsService jySendAggsService;
    @Autowired
    private JySendProductAggsService jySendProductAggsService;
    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;


    @Override
    public JdCResponse<Boolean> checkJySendAggsData(JySendAggsEntityQuery query) {
        JdCResponse<Boolean> response= JdCResponse.ok();
        response.setData(Boolean.TRUE);
        List<JySendAggsEntity> oldDataList = jySendAggsService.getSendAggsListByCondition(query);
        log.info("JySendAggs从老库获取的数据集合大小-{} ----{}", oldDataList.size(), JSON.toJSONString(oldDataList));
        if (CollectionUtils.isNotEmpty(oldDataList)) {
            for (int i = 1; i <= oldDataList.size(); i++) {
                List<JySendAggsEntity> sendAggMainData = jySendAggsService.getSendAggMainData(oldDataList.get(i));
                log.info("JySendAggs主库获取的第{}条数据为-{}", i, JSON.toJSONString(sendAggMainData));
                List<JySendAggsEntity> sendAggBakData = jySendAggsService.getSendAggBakData(oldDataList.get(i));
                log.info("JySendAggs从库获取的第{}条数据为-{}", i, JSON.toJSONString(sendAggMainData));
            }
        }
        return response;
    }

    @Override
    public JdCResponse<Boolean> checkJySendProductAggsData(JySendProductAggsEntityQuery query) {
        JdCResponse<Boolean> response= JdCResponse.ok();
        response.setData(Boolean.TRUE);
        List<JySendProductAggsEntity> oldDataList = jySendProductAggsService.getSendAggsListByCondition(query);
        log.info("JySendProductAggs从老库获取的数据集合大小-{} ----{}", oldDataList.size(), JSON.toJSONString(oldDataList));
        if(CollectionUtils.isNotEmpty(oldDataList)){
            for (int i = 1; i <= oldDataList.size(); i++) {
                List<JySendProductAggsEntity> sendProductAggMainData = jySendProductAggsService.getSendProductAggMainData(oldDataList.get(i));
                log.info("JySendProductAggs主库获取的第{}条数据为-{}", i, JSON.toJSONString(sendProductAggMainData));
                List<JySendProductAggsEntity> sendProductAggBakData = jySendProductAggsService.getSendProductAggBakData(oldDataList.get(i));
                log.info("JySendProductAggs从库获取的第{}条数据为-{}", i, JSON.toJSONString(sendProductAggBakData));
            }
        }
        return response;
    }


}
