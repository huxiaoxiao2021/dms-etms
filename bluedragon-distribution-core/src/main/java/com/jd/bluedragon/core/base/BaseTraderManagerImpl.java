package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ldop.middle.api.BaseResult;
import com.jd.ldop.middle.api.basic.BasicTraderInfoQueryApi;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 查询商家
 * @date 2018年08月02日 14时:24分
 */
public class BaseTraderManagerImpl implements BaseTraderManager {
    private static final Logger logger = LoggerFactory.getLogger(BaseTraderManagerImpl.class);
    @Autowired
    private BasicTraderInfoQueryApi basicTraderInfoQueryApi;

    @Override
    @JProfiler(jKey = "com.jd.bluedragon.core.base.BaseTraderManagerImpl", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(String key) {
        if (StringHelper.isEmpty(key)) {
            return new ArrayList();
        }
        try {
            BaseResult<List<BasicTraderQueryDTO>> baseResult = basicTraderInfoQueryApi.queryBasicTraderInfoByKey(key.trim());
            if (baseResult != null && baseResult.getStatusCode() == 0 && baseResult.getData() != null && baseResult.getData().size() > 0) {
                return baseResult.getData();
            } else if (baseResult != null && baseResult.getStatusCode() == 1) {
                logger.warn("BaseTraderManagerImpl-queryBasicTraderInfoByKey 查询失败，key:" + key + ",返回结果：" + baseResult.getStatusMessage());
                return new ArrayList();
            } else {
                logger.warn("BaseTraderManagerImpl-queryBasicTraderInfoByKey 查询失败，key:" + key);
                return new ArrayList();
            }
        } catch (Exception e) {
            logger.error("BaseTraderManagerImpl-queryBasicTraderInfoByKey 查询失败，key:" + key, e);
            return new ArrayList();
        }
    }
}
