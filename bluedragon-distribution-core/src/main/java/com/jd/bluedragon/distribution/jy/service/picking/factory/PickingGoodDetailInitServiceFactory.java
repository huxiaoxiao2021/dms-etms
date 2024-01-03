package com.jd.bluedragon.distribution.jy.service.picking.factory;

import com.jd.bluedragon.distribution.jy.service.picking.strategy.PickingGoodDetailInitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Description //待提货明细初始化服务工厂
 * @Date 15:03 2020/4/17

 * @date
 **/
public class PickingGoodDetailInitServiceFactory {
    private static final Logger log = LoggerFactory.getLogger(PickingGoodDetailInitServiceFactory.class);

    private static Map<Integer, PickingGoodDetailInitService> detailInitServiceMap = new HashMap<>();

    private PickingGoodDetailInitServiceFactory() {
    }

    /**
     * 根据k获取对象
     * 该方法可能返回null， 调用方自己处理null
     * @param code
     * @return
     */
    public static PickingGoodDetailInitService getPickingGoodDetailInitService(Integer code) {

        PickingGoodDetailInitService result = detailInitServiceMap.get(code);
        if(Objects.isNull(result)) {
            log.warn("未查到code={}的待提货明细加工服务", code);
        }
        return result;
    }

    /**
     * 待提货明细加工服务注册
     * @param code
     * @param detailInitService
     */
    public static void registerPickingGoodDetailInitService(Integer code, PickingGoodDetailInitService detailInitService) {
        detailInitServiceMap.put(code, detailInitService);
    }

}
