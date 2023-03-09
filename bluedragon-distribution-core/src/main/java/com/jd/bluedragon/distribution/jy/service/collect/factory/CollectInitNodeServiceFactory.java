//package com.jd.bluedragon.distribution.jy.service.collect.factory;
//
//import com.jd.bluedragon.distribution.jy.exception.JyBizException;
//import com.jd.bluedragon.distribution.jy.service.collect.bridge.CollectInitNodeService;
//import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectSiteTypeService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Author zhengchengfa
// * @Description //集齐初始化节点
// * @date
// **/
//public class CollectInitNodeServiceFactory {
//    private static final Logger log = LoggerFactory.getLogger(CollectInitNodeServiceFactory.class);
//
//    private static Map<Integer, CollectInitNodeService> CollectInitNodeMap = new HashMap<>();
//
//    private CollectInitNodeServiceFactory() {
//    }
//
//    /**
//     * 根据k获取对象
//     * @param type
//     * @return
//     */
//    public static CollectInitNodeService getCollectSiteTypeService(Integer type) {
//        CollectInitNodeService result = CollectInitNodeMap.get(type);
//        if(result == null) {
//            log.error("未查到code={}的集齐场地类型", type);
//            throw new JyBizException("获取集齐场地类型异常");
//        }
//        return result;
//    }
//
//    /**
//     * 初始化集齐统计服务对象
//     * @param type
//     * @param collectInitNodeService
//     */
//    public static void registerCollectSiteType(Integer type, CollectInitNodeService collectInitNodeService) {
//        CollectInitNodeMap.put(type, collectInitNodeService);
//    }
//
//}
