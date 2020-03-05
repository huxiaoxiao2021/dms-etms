package com.jd.bluedragon.distribution.web.sortscheme;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.response.CacheCleanResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.cacheClean.domain.CacheClean;
import com.jd.bluedragon.distribution.cacheClean.service.CacheCleanService;

import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeDetailService;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeService;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeSyncService;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.ExportByPOIUtil;
import com.jd.bluedragon.utils.IntegerHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSONObject;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.uim.annotation.Authorization;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

/**
 * Created by yangbo7 on 2016/6/22.
 */
@Controller
@RequestMapping("/autosorting/sortScheme")
public class SortSchemeController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String HTTP = "http://";

    private final static String prefixKey = "localdmsIp$";

    private static final int FILE_SIZE_LIMIT = 2 * 1024 * 1024;

    private static final int EXPORT_ROW_LIMIT = 50000;

    @Autowired
    private GoddessService goddessService;

    @Resource
    private SortSchemeService sortSchemeService;

    @Resource
    private SortSchemeDetailService sortSchemeDetailService;

    @Resource
    private SortSchemeSyncService sortSchemeSyncService;

    @Resource
    private BaseMajorManager baseMajorManager;

    @Resource
    private CacheCleanService cacheCleanService;

    @Autowired
    private LogEngine logEngine;



    // 页面跳转控制 增加参数跳转
    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Integer siteCode, String siteName, Model model) {
        return "sortscheme/sort-scheme-index";
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/cacheClean-index", method = RequestMethod.GET)
    public String cacheCleanindex(Integer siteCode,String siteName,Model model) {

        if(null == siteName || "".equals(siteName)){
            /** 该字段为空，需要从登陆用户的ERP信息中查找分拣中心的信息 **/
            log.info("开始获取当前登录用户的ERP信息......");
            try{
                ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();
                log.info("获取用户ERP：{}", user.getUserCode());
                BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
                if(bssod.getSiteType() == 64){/** 站点类型为64的时候为分拣中心 **/
                    siteCode = bssod.getSiteCode();
                    siteName = bssod.getSiteName();
                }
            }catch(Exception e){
                log.error("用户分拣中心初始化失败：",e);
            }
        }else{
            try{
                siteName = getSiteNameParam(URLDecoder.decode(siteName,"UTF-8"));//需要截取字段

            }catch(UnsupportedEncodingException e){
                log.error("分拣中心参数解码异常：",e);
            }
        }

        model.addAttribute("siteCode",siteCode);
        model.addAttribute("siteName",siteName);

        return "sortscheme/sort-scheme-cache-clean";
    }



    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/goDetail", method = RequestMethod.GET)
    public String goDetail(SortSchemeRequest request, Model model) {
        try {
            if (request == null || request.getSiteNo() == null || request.getId() == null) {
                return "sortscheme/sort-scheme-detail-index";
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getSiteNo());
            if (StringUtils.isBlank(url)) {
                return "sortscheme/sort-scheme-detail-index";
            }
            SortSchemeResponse<SortScheme> remoteResponse = sortSchemeService.findById2(request, HTTP + url + "/autosorting/sortScheme/find/id");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                model.addAttribute("sortScheme", remoteResponse.getData());
            }
        } catch (Exception e) {
            log.error("SortSchemeController.goDetail error!", e);
        }
        return "sortscheme/sort-scheme-detail-index";
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/goAdd", method = RequestMethod.GET)
    public String goAdd(Integer siteCode, String siteName, Model model) {

        try {
            siteName = getSiteNameParam(URLDecoder.decode(siteName, "UTF-8"));
            model.addAttribute("siteCode", siteCode);
            model.addAttribute("siteName", siteName);
        } catch (UnsupportedEncodingException e) {
            log.error("分拣中心参数解码异常：", e);
        }
        return "sortscheme/sort-scheme-add";
    }

    /**
     * 导入excel文件
     *
     * @param file
     * @param response
     */
    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public void doImportExcel(@RequestParam("importExcelFile") MultipartFile file, //
                              @RequestParam("id") Long id, //
                              @RequestParam("siteNo") Integer siteNo, //
                              HttpServletResponse response) {
        response.setContentType("text/json;charset=utf-8");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            if (siteNo == null || id == null) {
                throw new IOException("分拣中心ID或分拣计划ID为空,请刷新页面!!");
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + siteNo);
            if (StringUtils.isBlank(url)) {
                throw new IOException("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
            }
            if (file.getSize() > FILE_SIZE_LIMIT) {
                throw new IOException("文件大小超过限制(2M)");
            }
            Sheet sheet0 = ExportByPOIUtil.getSheetByIndex(file, 0);
            if (null == sheet0) {
                throw new DataFormatException("文件只能是Excel");
            }

            SortSchemeRequest request = new SortSchemeRequest();
            request.setId(id);
            request.setSortSchemeDetailJson(JsonHelper.toJson(sortSchemeDetailService.parseSortSchemeDetail2(sheet0)));
            SortSchemeResponse remoteResponse = sortSchemeService.importSortSchemeDetail2(request, HTTP + url + "/autosorting/sortScheme/import");
            if (remoteResponse == null || !IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                String msg = "导入分拣计划明细失败!";
                if(remoteResponse != null && StringUtils.isNotBlank(remoteResponse.getMessage())){
                    msg = remoteResponse.getMessage();
                }
                throw new DataFormatException(msg);
            }
            writeAndClose(pw, JsonHelper.toJson(new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK)));
        } catch (Exception e) {
            log.error("导入分拣计划明细失败", e);
            if(pw != null){
                if (e instanceof IOException) {
                    writeAndClose(pw, JsonHelper.toJson(new JdResponse(701, e.getMessage())));
                } else if (e instanceof DataFormatException) {
                    writeAndClose(pw, JsonHelper.toJson(new JdResponse(702, e.getMessage())));
                }else{
                    writeAndClose(pw, JsonHelper.toJson(new JdResponse(703, "导入分拣配置规则失败,系统异常")));
                }
            }
        }
    }

    /**
     * 写入PrintWriter并关闭
     *
     * @param pw
     * @param value
     */
    private void writeAndClose(PrintWriter pw, String value) {
        pw.write(value);
        pw.flush();
        pw.close();
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void doExportExcel(@RequestParam("id") Long id, @RequestParam("siteNo") String siteNo, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            if (id == null || siteNo == null) {
                PrintWriter pw = response.getWriter();
                writeAndClose(pw, "参数为空,无法导出明细数据!");
                return;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + siteNo);
            if (StringUtils.isBlank(url)) {
                PrintWriter pw = response.getWriter();
                writeAndClose(pw, "根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return;
            }
            SortSchemeDetailResponse<List<SortSchemeDetail>> remoteResponse = sortSchemeDetailService.findBySchemeId2(//
                    new SortSchemeDetailRequest(id.toString()), //
                    HTTP + url + "/autosorting/sortSchemeDetail/list/schemeId"//
            );
            if (remoteResponse == null || !IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK) //
                    || remoteResponse.getData() == null //
                    || remoteResponse.getData().size() < 1//
                    ) {
                PrintWriter pw = response.getWriter();
                writeAndClose(pw, MessageFormat.format("分拣计划[{0}]没有明细数据,不能导出!", id));
                return;
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("分拣计划明细.xls", "UTF-8"));
            OutputStream ouputStream = response.getOutputStream();
            HSSFWorkbook wb = sortSchemeDetailService.createWorkbook(remoteResponse.getData());
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception ex) {
            log.error("导出分拣计划明细异常", ex);
        }
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
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
            }else {
                if(remoteResponse == null){
                    log.warn("请求分拣中心本地获取分拣计划时remoteResponse为null,request:{}请求的url：{}{}/autosorting/sortScheme/list"
                    ,JsonHelper.toJson(request),HTTP , url);
                }else {
                    response.setCode(remoteResponse.getCode());
                    response.setMessage(remoteResponse.getMessage());
                    log.warn("请求分拣中心本地获取分拣计划失败request：{}remoteResponse:{}请求的url：{}{}/autosorting/sortScheme/list"
                            ,JsonHelper.toJson(request),JsonHelper.toJson(remoteResponse),HTTP , url);
                }
            }
        } catch (Exception e) {
            log.error("SortSchemeController.pageQuerySortScheme-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 查询已删缓存
     *author zhoutao on 2017/6/19
     */
    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/cacheClean", method = RequestMethod.POST)
    @ResponseBody
    public  CacheCleanResponse<Pager<List<CacheClean>>> cacheClean(@RequestBody CacheCleanRequest cacheCleanRequest) {
            CacheCleanResponse<Pager<List<CacheClean>>> response = new CacheCleanResponse<Pager<List<CacheClean>>> ();
        try {
            if (cacheCleanRequest == null || cacheCleanRequest.getSiteNo() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("分拣中心ID为空,请输入!!");
                return response;
            }
            if(cacheCleanRequest == null || cacheCleanRequest.getMachineCode()==null){
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("分拣机代码为空，请输入!!");
                return response;
            }
            if(cacheCleanRequest==null||cacheCleanRequest.getChuteCode1()==null){
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("滑槽号为空，请输入!!");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + cacheCleanRequest.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            CacheCleanResponse<Pager<List<CacheClean>>> remoteResponse = cacheCleanService.findPageCacheClean(cacheCleanRequest,HTTP + url + "services/smartDistribution/smartBoxes/query");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setData(remoteResponse.getData());
                response.setMessage("查找本次删除的缓存成功!");
            }
        } catch (Exception e) {
            log.error("findPageCacheClean-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 删除缓存
     *author zhoutao on 2017/6/19
     */
    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/excuteCacheClean", method = RequestMethod.POST)
    @ResponseBody
    public  CacheCleanResponse<Integer> excuteCacheClean(@RequestBody CacheCleanRequest cacheCleanRequest) {
        CacheCleanResponse<Integer> response = new CacheCleanResponse<Integer> ();
        try {
            if (cacheCleanRequest == null || cacheCleanRequest.getSiteNo() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("分拣中心ID为空,请输入!!");
                return response;
            }
            if(cacheCleanRequest == null || cacheCleanRequest.getMachineCode()==null){
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("分拣机代码为空，请输入!!");
                return response;
            }
            if(cacheCleanRequest==null||cacheCleanRequest.getChuteCode1()==null){
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("滑槽号为空，请输入!!");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + cacheCleanRequest.getSiteNo());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }
            CacheCleanResponse<Integer> remoteResponse = cacheCleanService.cacheClean(cacheCleanRequest,HTTP + url + "/services/smartDistribution/smartBoxes/clean");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setData(remoteResponse.getData());
                response.setMessage("删除缓存成功!");
            }
        } catch (Exception e) {
            log.error("cacheClean-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
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
            }else {
                if(remoteResponse == null){
                    log.warn("请求分拣中心本地添加分拣计划失败remoteResponse为null,request:{}", JsonHelper.toJson(request));
                }else {
                    response.setCode(remoteResponse.getCode());
                    response.setMessage(remoteResponse.getMessage());
                    log.warn("请求分拣中心本地添加分拣计划失败request：{}remoteResponse:{}", JsonHelper.toJson(request), JsonHelper.toJson(remoteResponse));
                }
            }
        } catch (Exception e) {
            log.error("SortSchemeResource.addSortScheme-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
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
            log.error("SortSchemeResource.deleteSortSchemeById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
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
            log.error("SortSchemeResource.deleteSortSchemeById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/update/able/id", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeResponse<String> ableSortSchemeById(@RequestBody SortSchemeRequest request) {
        SortSchemeResponse<String> response = new SortSchemeResponse<String>();
        String siteName = "";
        Integer siteCode = 0;
        try{
            ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();

            log.info("获取用户ERP：{}", user.getUserCode());
            BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
            if(bssod.getSiteType() == 64){/** 站点类型为64的时候为分拣中心 **/
                siteCode = bssod.getSiteCode();
                siteName = bssod.getSiteName();
            }
        }catch(Exception e){
            log.error("登录人没有维护基础资料信息");
        }
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
                boolean bool = sortSchemeSyncService.sendDtc(request,HTTP + url,siteCode);//添加方案同步到DTC的操作
                if(bool){
                    response.setCode(JdResponse.CODE_OK);
                    response.setMessage("分拣计划激活成功!");
                }else{
                    response.setCode(JdResponse.CODE_OK);
                    response.setMessage("分拣计划激活成功!仓库分拣方案同步失败，需要手动同步");
                }

            }
        } catch (Exception e) {
            log.error("SortSchemeResource.deleteSortSchemeById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 开启分拣机自动发货
     *
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/update/open/id", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeResponse<String> ableAutoSendById(@RequestBody SortSchemeRequest request) {
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
            SortSchemeResponse remoteResponse = sortSchemeService.ableAutoSendById(request, HTTP + url + "/autosorting/sortScheme/update/open/id");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage("分拣计划自动发货开启成功!");
            }
        } catch (Exception e) {
            log.error("SortSchemeResource.ableAutoSendById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 关闭分拣机自动发货
     *
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "/update/close/id", method = RequestMethod.POST)
    @ResponseBody
    public SortSchemeResponse<String> disableAutoSendById(@RequestBody SortSchemeRequest request) {
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
            SortSchemeResponse remoteResponse = sortSchemeService.disableAutoSendById(request, HTTP + url + "/autosorting/sortScheme/update/close/id");
            if (remoteResponse != null && IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage("分拣计划自动发货关闭成功!");
            }
        } catch (Exception e) {
            log.error("SortSchemeResource.disableAutoSendById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 分拣机箱号缓存清理功能
     *
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_SORTSCHEME_R)
    @RequestMapping(value = "cleanBoxCache", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse cleanBoxCache(@RequestBody CleanBoxCacheRequest request) {
        ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        log.info("获取用户ERP：{}", erpUser.getUserCode());

        JdResponse response = new JdResponse();
        try {
            if (request == null || request.getCreateSiteCode() == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数不能为空！");
                return response;
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + request.getCreateSiteCode());
            if (StringUtils.isBlank(url)) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
                return response;
            }

            if(StringHelper.isNotEmpty(request.getSiteCode())){
                response = sortSchemeService.cleanBoxCache(HTTP + url + "/services/smartDistribution/clearAllBoxCache/"+request.getSiteCode());
            }else{
                response = sortSchemeService.cleanBoxCache(HTTP + url + "/services/smartDistribution/clearAllBoxCache");
            }


            if (response != null && IntegerHelper.compare(response.getCode(), JdResponse.CODE_OK)) {
                response.setCode(JdResponse.CODE_OK);
                response.setMessage("箱号缓存清理成功!");
                addSystemLog(erpUser,request.getCreateSiteCode());
            }
        } catch (Exception e) {
            log.error("SortSchemeResource.disableAutoSendById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 去掉参数中的ID，保留中午分拣中心名
     **/
    private String getSiteNameParam(String str) {
        String siteName = "";
        String regEX = "[\\u4e00-\\u9fa5]+";
        Pattern pattern = Pattern.compile(regEX);
        Matcher matcher = pattern.matcher(str);
        log.info("分拣中心参数截取......");
        if (matcher.find()) {
            return matcher.group(0);
        }
        log.warn("getSiteNameParam()方法执行异常。。。");
        return str;
    }


    /**
     * 增加箱号缓存清理日志
     */
    private void addSystemLog(ErpUserClient.ErpUser user,String createSiteCode){
        long startTime=new Date().getTime();

        String erpUserCode = user.getUserCode();
        String erpUserName = user.getUserName();
        Goddess goddess = new Goddess();
        goddess.setHead(erpUserCode + ":" + erpUserName);
        goddess.setBody("用户["+erpUserCode + ":" + erpUserName+"],清理["+createSiteCode+"]分拣中心箱号缓存！");
        goddess.setDateTime(new Date());
        goddess.setKey("CleanBoxCache"+createSiteCode);

        long endTime = new Date().getTime();

        JSONObject request=new JSONObject();
        request.put("operatorCode",erpUserCode);
        request.put("siteCode",createSiteCode);

        JSONObject response=new JSONObject();
        response.put("info", goddess);

        BusinessLogProfiler businessLogProfiler=new BusinessLogProfilerBuilder()
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SORTING_BOXCACHECLEAR)
                .processTime(endTime,startTime)
                .build();

        logEngine.addLog(businessLogProfiler);


        goddessService.save(goddess);
    }
}
