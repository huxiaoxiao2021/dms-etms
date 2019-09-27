package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.eclp.isv.domain.GoodsInfo;
import com.jd.eclp.isv.service.EclpOpenService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * ECLP主数据服务封装
 */
@Service("eclpOpenManager")
public class EclpOpenManagerImpl implements EclpOpenManager {

    private Logger logger = LoggerFactory.getLogger(EclpOpenManagerImpl.class);

    @Value("${eclp.open.service.pin:jddaojia}")
    private String PIN ;

    @Autowired
    private EclpOpenService eclpOpenService;

    /**
     * 创建商品主数据
     * @param goodsInfo
     * @return
     */
    @Override
    public String transportGoodsInfo(GoodsInfo goodsInfo) {
        try {

            String result = eclpOpenService.transportGoodsInfo(goodsInfo,PIN);
            if(StringUtils.isNotBlank(result)){
                return result;
            }
        }catch (Exception e){
            logger.error("创建商品主数据失败"+ JsonHelper.toJson(goodsInfo)+"|"+PIN,e);
        }
        return null;
    }
}
