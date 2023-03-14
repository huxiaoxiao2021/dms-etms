package com.jd.bluedragon.distribution.jy.service.collect.factory;

import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.strategy.CollectStatisticsDimensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhengchengfa
 * @Description //集齐维度工厂
 * @date
 **/
public class CollectStatisticsDimensionFactory {
    private static final Logger log = LoggerFactory.getLogger(CollectStatisticsDimensionFactory.class);


    private static Map<Integer, CollectStatisticsDimensionService> CollectStatisticsDimensionMap = new HashMap<>();

    private CollectStatisticsDimensionFactory() {
    }

//    private static final CollectStatisticsDimensionService EMPTY = new EmptyCollectStatisticsDimension();

    /**
     * 根据k获取对象
     * @param type
     * @return
     */
    public static CollectStatisticsDimensionService getCollectStatisticsDimensionService(Integer type) {
        CollectStatisticsDimensionService result = CollectStatisticsDimensionMap.get(type);
//        return result == null ? EMPTY : result;
        if(result == null) {
            log.error("未查到code={}的集齐类型", type);
            throw new JyBizException("获取集齐统计模型异常");
        }
        return result;
    }

    /**
     * 初始化集齐统计服务对象
     * @param type
     * @param collectStatisticsDimensionService
     */
    public static void registerCollectStatisticsDimension(Integer type, CollectStatisticsDimensionService collectStatisticsDimensionService) {
        CollectStatisticsDimensionMap.put(type, collectStatisticsDimensionService);
    }

//    /**
//     * 空对象
//     */
//    private static class EmptyCollectStatisticsDimension implements CollectStatisticsDimensionService {
//        @Override
//        public List<CollectReportStatisticsDto> collectStatistics(CollectQueryReqDto collectQueryReqDto) {
//            return null;
//        }
//
//        @Override
//        public List<CollectReportDto> queryCollectListPage(CollectQueryReqDto collectQueryReqDto) {
//            return null;
//        }
//
//        @Override
//        public List<CollectReportDetailPackageDto> queryCollectDetail(CollectQueryReqDto collectQueryReqDto) {
//            return null;
//        }
//    }

}
