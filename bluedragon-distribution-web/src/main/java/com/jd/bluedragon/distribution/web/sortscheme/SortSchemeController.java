package com.jd.bluedragon.distribution.web.sortscheme;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeDetailService;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeService;
import com.jd.bluedragon.utils.IntegerHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.jsf.gd.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by yangbo7 on 2016/6/22.
 */
@Controller
@RequestMapping("/autosorting/sortScheme")
public class SortSchemeController {

    private final Log logger = LogFactory.getLog(this.getClass());

    private final static String HTTP = "http://";

    private final static String prefixKey = "localdmsIp$";

    @Resource
    private SortSchemeService sortSchemeService;

    @Resource
    private SortSchemeDetailService sortSchemeDetailService;

    // 页面跳转控制
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "sortscheme/sort-scheme-index";
    }

    @RequestMapping(value = "/goAdd", method = RequestMethod.GET)
    public String goAdd() {
        return "sortscheme/sort-scheme-add";
    }


    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeResponse<Pager<List<SortScheme>>> pageQuerySortScheme(@RequestBody SortSchemeRequest request) {
        SortSchemeResponse<Pager<List<SortScheme>>> response = new SortSchemeResponse<Pager<List<SortScheme>>>();
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
            SortSchemeResponse<Pager<List<SortScheme>>> remoteResponse = sortSchemeService.pageQuerySortScheme(request, HTTP + url + "/autosorting/sortScheme/list");
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


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeResponse<String> addSortScheme(@RequestBody SortSchemeRequest request) {
        SortSchemeResponse<String> response = new SortSchemeResponse<String>();
        try {
            if (request == null || StringUtils.isBlank(request.getSiteNo()) || //
                    StringUtils.isBlank(request.getMachineCode()) || //
                    !SortSchemeRequest.validateSortMode(request.getSortMode())) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数不能为空！");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            SortSchemeResponse remoteResponse = sortSchemeService.addSortScheme2(request, HTTP + url + "/autosorting/sortScheme/add");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage("分拣计划添加成功!");
            }
        } catch (Exception e) {
            logger.error("SortSchemeResource.addSortScheme-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/delete/id", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeResponse<String> deleteSortSchemeById(@RequestBody SortSchemeRequest request) {
        SortSchemeResponse<String> response = new SortSchemeResponse<String>();
        try {
            if (request == null || request.getId() == null || request.getId() < 1 || request.getSiteNo() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数不能为空！");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            SortSchemeResponse remoteResponse = sortSchemeService.deleteById2(request, HTTP + url + "/autosorting/sortScheme/delete/id");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage("分拣计划删除成功!");
            }
        } catch (Exception e) {
            logger.error("SortSchemeResource.deleteSortSchemeById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/update/disable/id", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeResponse<String> disableSortSchemeById(@RequestBody SortSchemeRequest request) {
        SortSchemeResponse<String> response = new SortSchemeResponse<String>();
        try {
            if (request == null || request.getId() == null || request.getId() < 1 || request.getSiteNo() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数不能为空！");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            SortSchemeResponse remoteResponse = sortSchemeService.disableById2(request, HTTP + url + "/autosorting/sortScheme/update/disable/id");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage("分拣计划禁用成功!");
            }
        } catch (Exception e) {
            logger.error("SortSchemeResource.deleteSortSchemeById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @RequestMapping(value = "/update/able/id", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeResponse<String> ableSortSchemeById(@RequestBody SortSchemeRequest request) {
        SortSchemeResponse<String> response = new SortSchemeResponse<String>();
        try {
            if (request == null || request.getId() == null || request.getId() < 1 || request.getSiteNo() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数不能为空！");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            SortSchemeResponse remoteResponse = sortSchemeService.ableById2(request, HTTP + url + "/autosorting/sortScheme/update/able/id");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage("分拣计划激活成功!");
            }
        } catch (Exception e) {
            logger.error("SortSchemeResource.deleteSortSchemeById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

//    @RequestMapping(value = "/sortSchemeDetail/list", method = RequestMethod.POST)
//    @ResponseBody
//    public SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> pageQuerySortSchemeDetail(@RequestBody SortSchemeDetailRequest request) {
//
//        SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> response = new SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>>();
//        try {
//            if (request == null || request.getSiteNo() == null) {
//                response.setCode(JdResponse.CODE_PARAM_ERROR);
//                response.setMessage("参数不能为空！");
//                return response;
//            }
//            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
//            if (StringUtils.isBlank(url)) {
//                response.setCode(JdResponse.CODE_PARAM_ERROR);
//                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
//                return response;
//            }
//            SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> remoteResponse = sortSchemeDetailService.pageQuerySortSchemeDetail(request, HTTP + url + "/autosorting/sortSchemeDetail/list");
//            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
//                response.setCode(JdResponse.CODE_OK);
//                response.setData(remoteResponse.getData());
//            }
//        } catch (Exception e) {
//            logger.error("SortSchemeResource.pageQuerySortSchemeDetail-error!", e);
//            response.setCode(JdResponse.CODE_SERVICE_ERROR);
//            response.setData(null);
//            response.setMessage(e.getMessage());
//        }
//        return response;
//    }

}






























