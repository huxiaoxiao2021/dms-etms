package com.jd.bluedragon.distribution.performance.service;

import com.jd.bluedragon.distribution.performance.domain.Performance;
import com.jd.bluedragon.distribution.performance.domain.PerformanceCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * @ClassName: PerformanceService
 * @Description: 123
 * @author: hujiping
 * @date: 2018/8/20 13:44
 */
public interface PerformanceService {

    /**
     * 查询加履系统获得信息
     * @param performanceCondition
     * @return
     */
    PagerResult<Performance> listData(PerformanceCondition performanceCondition);

    /**
     * 打印
     * @param performanceCode
     * @return
     */
    String print(String performanceCode,String waybillorPackCode);


    /**
     * 判断是否打印
     * @param performanceCondition
     * @return
     * */
    Integer getIsPrint(PerformanceCondition performanceCondition);
}
