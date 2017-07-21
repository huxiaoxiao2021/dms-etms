package com.jd.bluedragon.distribution.web.sortscheme;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CacheCleanRequest;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.request.SortSchemeRequest;
import com.jd.bluedragon.distribution.api.response.CacheCleanResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.cacheClean.domain.CacheClean;
import com.jd.bluedragon.distribution.cacheClean.service.CacheCleanService;
import com.jd.bluedragon.distribution.sortscheme.domain.SortScheme;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeDetailService;
import com.jd.bluedragon.distribution.sortscheme.service.SortSchemeService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.ExportByPOIUtil;
import com.jd.bluedragon.utils.IntegerHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.jd.bluedragon.distribution.cacheClean.service.CacheCleanService;
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

    private final Log logger = LogFactory.getLog(this.getClass());

    private final static String HTTP = "http://";

    private final static String prefixKey = "localdmsIp$";

    private static final int FILE_SIZE_LIMIT = 2 * 1024 * 1024;

    private static final int EXPORT_ROW_LIMIT = 50000;

    @Resource
    private SortSchemeService sortSchemeService;

    @Resource
    private SortSchemeDetailService sortSchemeDetailService;

    @Resource
    private BaseMajorManager baseMajorManager;

    @Resource
    private CacheCleanService cacheCleanService;

    // 页面跳转控制 增加参数跳转
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Integer siteCode, String siteName, Model model) {

        if (null == siteName || "".equals(siteName)) {
            /** 该字段为空，需要从登陆用户的ERP信息中查找分拣中心的信息 **/
            logger.info("开始获取当前登录用户的ERP信息......");
            try {
                ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();
                logger.info("获取用户ERP：" + user.getUserCode());
                BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
                if (bssod.getSiteType() == 64) {/** 站点类型为64的时候为分拣中心 **/
                    siteCode = bssod.getSiteCode();
                    siteName = bssod.getSiteName();
                }
            } catch (Exception e) {
                logger.error("用户分拣中心初始化失败：", e);
            }
        } else {
            try {
                siteName = getSiteNameParam(URLDecoder.decode(siteName, "UTF-8"));//需要截取字段

            } catch (UnsupportedEncodingException e) {
                logger.error("分拣中心参数解码异常：", e);
            }
        }

        model.addAttribute("siteCode", siteCode);
        model.addAttribute("siteName", siteName);

        return "sortscheme/sort-scheme-index";
    }

    @RequestMapping(value = "/cacheClean-index", method = RequestMethod.GET)
    public String cacheCleanindex(Integer siteCode,String siteName,Model model) {

        if(null == siteName || "".equals(siteName)){
            /** 该字段为空，需要从登陆用户的ERP信息中查找分拣中心的信息 **/
            logger.info("开始获取当前登录用户的ERP信息......");
            try{
                ErpUserClient.ErpUser user = ErpUserClient.getCurrUser();
                logger.info("获取用户ERP："+ user.getUserCode());
                BaseStaffSiteOrgDto bssod = baseMajorManager.getBaseStaffByErpNoCache(user.getUserCode());
                if(bssod.getSiteType() == 64){/** 站点类型为64的时候为分拣中心 **/
                    siteCode = bssod.getSiteCode();
                    siteName = bssod.getSiteName();
                }
            }catch(Exception e){
                logger.error("用户分拣中心初始化失败：",e);
            }
        }else{
            try{
                siteName = getSiteNameParam(URLDecoder.decode(siteName,"UTF-8"));//需要截取字段

            }catch(UnsupportedEncodingException e){
                logger.error("分拣中心参数解码异常：",e);
            }
        }

        model.addAttribute("siteCode",siteCode);
        model.addAttribute("siteName",siteName);

        return "sortscheme/sort-scheme-cache-clean";
    }




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
            logger.error("SortSchemeController.goDetail error!", e);
        }
        return "sortscheme/sort-scheme-detail-index";
    }

    @RequestMapping(value = "/goAdd", method = RequestMethod.GET)
    public String goAdd(Integer siteCode, String siteName, Model model) {

        try {
            siteName = getSiteNameParam(URLDecoder.decode(siteName, "UTF-8"));
            model.addAttribute("siteCode", siteCode);
            model.addAttribute("siteName", siteName);
        } catch (UnsupportedEncodingException e) {
            logger.error("分拣中心参数解码异常：", e);
        }
        return "sortscheme/sort-scheme-add";
    }

    /**
     * 导入excel文件
     *
     * @param file
     * @param response
     */
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
//            SortScheme sortScheme = null;
//            SortSchemeResponse<SortScheme> sortSchemeResponse = sortSchemeService.findById2(new SortSchemeRequest(id), HTTP + url + "/autosorting/sortScheme/find/id");
//            if (sortSchemeResponse != null && IntegerHelper.compare(sortSchemeResponse.getCode(), JdResponse.CODE_OK)) {
//                sortScheme = sortSchemeResponse.getData();
//            } else {
//                throw new DataFormatException("远程获取Sortscheme数据失败!!");
//            }
            SortSchemeRequest request = new SortSchemeRequest();
            request.setId(id);
            request.setSortSchemeDetailJson(JsonHelper.toJson(sortSchemeDetailService.parseSortSchemeDetail2(sheet0)));
            SortSchemeResponse remoteResponse = sortSchemeService.importSortSchemeDetail2(request, HTTP + url + "/autosorting/sortScheme/import");
            if (remoteResponse == null || !IntegerHelper.compare(remoteResponse.getCode(), JdResponse.CODE_OK)) {
                throw new DataFormatException(remoteResponse.getMessage());
            }
        } catch (Exception e) {
            if (e instanceof IOException) {
                logger.error("导入分拣计划明细失败", e);
                writeAndClose(pw, JsonHelper.toJson(new JdResponse(701, e.getMessage())));
            } else if (e instanceof DataFormatException) {
                logger.error("导入分拣计划明细失败", e);
                writeAndClose(pw, JsonHelper.toJson(new JdResponse(702, e.getMessage())));
            }
            logger.error("导入分拣配置规则失败", e);
            writeAndClose(pw, JsonHelper.toJson(new JdResponse(703, "导入分拣配置规则失败,系统异常")));
        }
        writeAndClose(pw, JsonHelper.toJson(new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK)));
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

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void doExportExcel(@RequestParam("id") Long id, @RequestParam("siteNo") String siteNo, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            if (id == null || siteNo == null) {
                PrintWriter pw = response.getWriter();
                writeAndClose(pw, "参数为空,无法导出明细数据!");
            }
            String url = PropertiesHelper.newInstance().getValue(prefixKey + siteNo);
            if (StringUtils.isBlank(url)) {
                PrintWriter pw = response.getWriter();
                writeAndClose(pw, "根据分拣中心ID,无法定位访问地址,请检查properties配置!!");
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
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("分拣计划明细.xls", "UTF-8"));
            OutputStream ouputStream = response.getOutputStream();
            HSSFWorkbook wb = sortSchemeDetailService.createWorkbook(remoteResponse.getData());
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception ex) {
            logger.error("导出分拣计划明细异常", ex);
        }
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

    /**
     * 查询已删缓存
     *author zhoutao on 2017/6/19
     */
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
            logger.error("findPageCacheClean-error!", e);
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
            logger.error("cacheClean-error!", e);
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

    /**
     * 开启分拣机自动发货
     *
     * @param request
     * @return
     */
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
            logger.error("SortSchemeResource.ableAutoSendById-error!", e);
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
            logger.error("SortSchemeResource.disableAutoSendById-error!", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setData(null);
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
        logger.info("分拣中心参数截取......");
        if (matcher.find()) {
            return matcher.group(0);
        }
        logger.error("getSiteNameParam()方法执行异常。。。");
        return str;
    }

}
