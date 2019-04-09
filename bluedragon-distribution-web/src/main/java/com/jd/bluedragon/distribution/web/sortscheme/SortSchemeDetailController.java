package com.jd.bluedragon.distribution.web.sortscheme;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeDetailService;
import com.jd.bluedragon.utils.IntegerHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yangbo7 on 2016/6/22.
 */
@Controller
@RequestMapping("/autosorting/sortSchemeDetail")
public class SortSchemeDetailController {

    private final Log logger = LogFactory.getLog(this.getClass());

    private final static String HTTP = "http://";

    private final static String prefixKey = "localdmsIp$";

    @Resource
    private SortSchemeDetailService sortSchemeDetailService;

    // 页面跳转控制
    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView index(SortSchemeRequest request){

        HashMap schemeParamMap = new HashMap();
        schemeParamMap.put("schemeId",request.getId());
        schemeParamMap.put("siteNo",request.getSiteNo());
        schemeParamMap.put("siteName",request.getSiteName());
        schemeParamMap.put("machineCode",request.getMachineCode());
        schemeParamMap.put("sortMode",request.getSortMode());
        schemeParamMap.put("name",request.getName());

        return new ModelAndView("sortscheme/sort-scheme-detail-index").addObject("schemeParam",schemeParamMap);
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> pageQuerySortSchemeDetails(@RequestBody SortSchemeDetailRequest request) {
        SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> response = new SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>>();
        try {
            if (request == null || request.getSiteNo() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("分拣中心ID为空,请输入!!");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> remoteResponse = sortSchemeDetailService.pageQuerySortSchemeDetail(request, HTTP + url + "/autosorting/sortSchemeDetail/list");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setData(remoteResponse.getData());
            }
        } catch (Exception e) {
            logger.error("SortSchemeController.pageQuerySortScheme-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/list/mixsite", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeDetailResponse<List<String>> mixsite(@RequestBody SortSchemeDetailRequest request) {
        SortSchemeDetailResponse<List<String>> response = new SortSchemeDetailResponse<List<String>>();
        try {
            if (request == null || request.getSiteNo() == null || request.getSchemeId() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("分拣中心ID或分拣计划ID为空,请输入!!");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            SortSchemeDetailResponse<List<String>> remoteResponse = sortSchemeDetailService.findMixSiteBySchemeId2(request, HTTP + url + "/autosorting/sortSchemeDetail/list/mixsite");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setData(remoteResponse.getData());
            }
        } catch (Exception e) {
            logger.error("SortSchemeController.pageQuerySortScheme-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/list/chutecode", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeDetailResponse<List<String>> chuteCode(@RequestBody SortSchemeDetailRequest request) {
        SortSchemeDetailResponse<List<String>> response = new SortSchemeDetailResponse<List<String>>();
        try {
            if (request == null || request.getSiteNo() == null || request.getSchemeId() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("分拣中心ID或分拣计划ID为空,请输入!!");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            SortSchemeDetailResponse<List<String>> remoteResponse = sortSchemeDetailService.findChuteCodeBySchemeId2(request, HTTP + url + "/autosorting/sortSchemeDetail/list/chutecode");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setData(remoteResponse.getData());
            }
        } catch (Exception e) {
            logger.error("SortSchemeController.pageQuerySortScheme-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
