package com.jd.bluedragon.distribution.web.sortingCenterQueryTool;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SortingCenterQueryRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.sortingCenterQueryTool.service.SortingCenterQueryService;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.common.util.StringUtils;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by wuzuxiang on 2016/10/14.
 */
@Controller
@RequestMapping("/sortingCenter")
public class SortingCenterQueryController {

    private final Logger log = LoggerFactory.getLogger(SortingCenterQueryController.class);

    private final static String HTTP = "http://";

    private final static String prefixKey = "localdmsIp$";


    @Autowired
    SortingCenterQueryService sortingCenterQueryService;

    @Authorization(Constants.DMS_WEB_TOOL_SORTINGCENTER_R)
    @RequestMapping(value = "/index" ,method = RequestMethod.GET)
    public String SortingCenterQueryIndex(){
        return "tools/sortingCenterTestTool/sortingCenterQuery";
    }

    @Authorization(Constants.DMS_WEB_TOOL_SORTINGCENTER_R)
    @RequestMapping(value = "/query" ,method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Long> queryDataFromThreeTables(@RequestBody SortingCenterQueryRequest<List<String>> request){
        InvokeResult<Long> result = new InvokeResult<Long>();
        try{
            if(StringUtils.isBlank(request.getSiteNo())){
                result.setCode(JdResponse.CODE_PARAM_ERROR);
                result.setMessage("分拣计划的ID为空");
                return result;
            }

            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if(StringUtils.isBlank(url)){
                result.setCode(JdResponse.CODE_PARAM_ERROR);
                result.setMessage("根据分拣中心ID，无法访问分拣中心地址，请检查properties配置!!!");
                return result;
            }

            InvokeResult<Long> remoteResult = sortingCenterQueryService.countNumFromThreeTables(request,HTTP + url + "/sortingCenter/query");
            if (remoteResult != null && remoteResult.getCode() == JdResponse.CODE_OK){
                result.setCode(JdResponse.CODE_OK);
                result.setMessage("区域数据查询成功");
                result.setData(remoteResult.getData());
            }
        }catch(Exception e){
            log.error("SortingCenterQueryTool--controller 查询区域分拣中心数据失败：",e);
            result.setCode(JdResponse.CODE_SERVICE_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            result.setData(null);
        }

        return result;
    }

    @Authorization(Constants.DMS_WEB_TOOL_SORTINGCENTER_R)
    @RequestMapping(value = "/queryDetail" ,method = RequestMethod.POST)
    @ResponseBody
    public Pager<Object[]> queryDetailFromThreeTables(@RequestBody Map<String ,Object> request){
        Pager<Object[]> result = new Pager<Object[]>();
        result.init();
        try{
            if(null == request || StringUtils.isBlank((String)request.get("siteNo")) ){
                return result;
            }

            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.get("siteNo"));
            if(StringUtils.isBlank(url)){
                return result;
            }

            if (null != request.get("pageNo")){
                result.setPageNo((Integer) request.get("pageNo"));
            }
            if (null != request.get("pageSize")){
                result.setPageSize((Integer) request.get("pageSize"));
            }

            List<Object> objects = sortingCenterQueryService.queryDetailsFromThreeTables(request,HTTP + url + "/sortingCenter/queryDetail");
            if (null != objects && objects.size() > 0){
                result.setData(objects.toArray());
            }
        }catch(Exception e){
            log.error("SortingCenterQueryTool--controller 查询区域分拣中心数据失败：",e);
        }

        return result;
    }
}
