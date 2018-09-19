package com.jd.bluedragon.distribution.web.waybill.rma;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.response.RmabillInfoResponse;
import com.jd.bluedragon.distribution.waybill.domain.RmaBillInfo;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by zhanghao141 on 2018/9/18.
 */
@Controller
@RequestMapping("/waybill/rma")
public class RmaHandOverController {
    private static final Logger logger = Logger.getLogger(RmaHandOverController.class);
    /**
     * 跳转到主界面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "waybill/rma/list";
    }
    /**
     * 查询方法
     * @return
     * @param rmaBillInfo
     * @param page
     */
    @RequestMapping("/query")
    @ResponseBody
    public RmabillInfoResponse<Pager<List<RmaBillInfo>>> query(RmaBillInfo rmaBillInfo, Pager<List<RmaBillInfo>> page) {
        RmabillInfoResponse<Pager<List<RmaBillInfo>>> rmabillInfoResponse = new RmabillInfoResponse<Pager<List<RmaBillInfo>>>();
//        try {
//            if(StringHelper.isEmpty(b2bRouterRequest.getOriginalSiteName())){
//                b2bRouterRequest.setOriginalSiteCode(null);
//            }
//            if(StringHelper.isEmpty(b2bRouterRequest.getDestinationSiteName())){
//                b2bRouterRequest.setDestinationSiteCode(null);
//            }
//
////            List<B2BRouter> resultList = b2bRouterService.queryByCondition(b2bRouterRequest, pager);
//
//            // 设置分页对象
//            if (pager == null) {
//                pager = new Pager<List<B2BRouter>>(Pager.DEFAULT_PAGE_NO);
//            }
//
//            pager.setData(resultList);
//            b2bRouterResponse.setData(pager);
//            b2bRouterResponse.setCode(B2BRouterResponse.CODE_NORMAL);
//
//        } catch (Exception e) {
//            logger.error("根据查询条件获取路由信息失败.",e);
//            b2bRouterResponse.setCode(B2BRouterResponse.CODE_EXCEPTION);
//            b2bRouterResponse.setData(null);
//            b2bRouterResponse.setMessage("根据查询条件获取路由信息失败："+e.getMessage());
//        }
        return rmabillInfoResponse;
    }
}
