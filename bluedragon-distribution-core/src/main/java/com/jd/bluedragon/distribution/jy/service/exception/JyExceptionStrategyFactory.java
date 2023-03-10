package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.distribution.jy.service.exception.impl.JySanwuExceptionServiceImpl;
import com.jd.bluedragon.distribution.jy.service.exception.impl.JyScrappedExceptionServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 21:35
 * @Description:
 */
public class JyExceptionStrategyFactory {

    //使用Map集合存储策略信息,彻底消除if...else
    private static Map<Integer,JyExceptionServiceStrategy> strategyMap;

    //初始化具体策略,保存到map集合 新增一个具体策略就要put一个新实现
    static {
        strategyMap = new HashMap<>();
        strategyMap.put(0,new JySanwuExceptionServiceImpl());
        strategyMap.put(1,new JyScrappedExceptionServiceImpl());
    }
    //根据异常类型获取对应策略类对象
    public static JyExceptionServiceStrategy getJyExceptionServiceStrategy(Integer type){
        return strategyMap.get(type);
    }


}
