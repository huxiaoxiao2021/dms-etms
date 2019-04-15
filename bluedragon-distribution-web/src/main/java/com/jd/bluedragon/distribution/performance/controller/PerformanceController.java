package com.jd.bluedragon.distribution.performance.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.performance.domain.Performance;
import com.jd.bluedragon.distribution.performance.domain.PerformanceCondition;
import com.jd.bluedragon.distribution.performance.service.PerformanceService;
import com.jd.bluedragon.distribution.performance.service.impl.PerformanceServiceImpl;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.QueryParam;


/**
 * @ClassName: PerformanceController
 * @Description: 打印加履
 * @author: hujiping
 * @date: 2018/8/17 10:26
 */
@Controller
@RequestMapping("jinpeng/performance")
public class PerformanceController {

    private static final Log logger = LogFactory.getLog(PerformanceController.class);

    @Autowired
    private PerformanceService performanceService;

    @Autowired
    private PerformanceServiceImpl performanceServiceImpl;


    /**
     * 返回主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_PERFORMANCE_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex(){
        return "/jinpeng/performance";
    }

    /**
     * 根据条件分页查询数据信息
     * @param performanceCondition
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_PERFORMANCE_R)
    @RequestMapping(value = "/query")
    @ResponseBody
    public PagerResult<Performance> query(@RequestBody PerformanceCondition performanceCondition){

        PagerResult<Performance> pagerResult = performanceService.listData(performanceCondition);
        return pagerResult;
    }

    @Authorization(Constants.DMS_WEB_EXPRESS_PERFORMANCE_R)
    @RequestMapping(value = "/isCanPrint")
    @ResponseBody
    public Integer isCanPrint(@RequestBody PerformanceCondition performanceCondition){

        return performanceService.getIsPrint(performanceCondition);
    }


    /**
     * 跳转到打印的履约单页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_PERFORMANCE_R)
    @RequestMapping(value = "/print")
    public ModelAndView print(@QueryParam("performanceCode")String performanceCode,
                        @QueryParam("waybillorPackCode")String waybillorPackCode){


        ModelAndView mav = new ModelAndView("/jinpeng/printInfo");
        mav.addObject("performanceCode",performanceCode);
        mav.addObject("waybillorPackCode",waybillorPackCode);
        return mav;
    }

    /**
     * 将履约单的详细数据传给前台
     * @return
     */
    @Authorization(Constants.DMS_WEB_EXPRESS_PERFORMANCE_R)
    @ResponseBody
    @RequestMapping(value = "/printInfo")
    public String printInfo(@QueryParam("performanceCode")String performanceCode,
                            @QueryParam("waybillorPackCode")String waybillorPackCode){

        return performanceService.print(performanceCode,waybillorPackCode);
    }
}
