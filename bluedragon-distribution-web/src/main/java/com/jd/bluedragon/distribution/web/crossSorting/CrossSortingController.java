package com.jd.bluedragon.distribution.web.crossSorting;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.CrossSortingRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.cross.domain.CrossSorting;
import com.jd.bluedragon.distribution.cross.service.CrossSortingService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.ErpUserClient.ErpUser;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.erp.service.dto.CommonDto;
import com.jd.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.DataFormatException;

@Controller
@RequestMapping("/crossSorting")
public class CrossSortingController {

    private final Log logger = LogFactory.getLog(this.getClass());
    private static final int FILE_SIZE_LIMIT = 1024 * 1024;
    private static final int EXPORT_ROW_LIMIT = 50000;

    @Autowired
    private CrossSortingService crossSortingService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        try {
            ErpUser erpUser = ErpUserClient.getCurrUser();
            model.addAttribute("erpUser", erpUser);
        } catch (Exception e) {
            logger.error("index error!", e);
        }
        return "cross-sorting/cross-sorting-index";
    }

    @RequestMapping(value = "/goAddBatch", method = RequestMethod.GET)
    public String goAddBatch(Model model) {
        return "cross-sorting/cross-sorting-add";
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<Pager<List<CrossSorting>>> doQueryCrossSorting(CrossSortingRequest request, Pager<List<CrossSorting>> pager, Model model) {
        CommonDto<Pager<List<CrossSorting>>> cdto = new CommonDto<Pager<List<CrossSorting>>>();
        try {
            logger.info("CrossSortingController doQueryCrossSorting begin...");
            if (null == request || ((null == request.getCreateDmsCode() || request.getCreateDmsCode() < 1)
                    && (null == request.getDestinationDmsCode() || request.getDestinationDmsCode() < 1)
                    && StringUtils.isBlank(request.getCreateUserName())
                    && (null == request.getOrgId() || request.getOrgId() < 1))) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数不能为空！");
                return cdto;
            }
            Map<String, Object> params = this.getParamsFromRequest(request);
            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<CrossSorting>>(Pager.DEFAULT_PAGE_NO);
            } else {
                pager = new Pager<List<CrossSorting>>(pager.getPageNo(), pager.getPageSize());
            }
            params.putAll(ObjectMapHelper.makeObject2Map(pager));

            List<CrossSorting> CrossSortingList = crossSortingService.findPageCrossSorting(params);
            Integer totalSize = crossSortingService.findCountCrossSorting(params);
            pager.setTotalSize(totalSize);
            pager.setData(CrossSortingList);
            logger.info("查询符合条件的规则数量：" + totalSize);

            cdto.setData(pager);
            cdto.setCode(CommonDto.CODE_NORMAL);
        } catch (Exception e) {
            logger.error("doQueryCrossSorting-error!", e);
            cdto.setCode(CommonDto.CODE_EXCEPTION);
            cdto.setData(null);
            cdto.setMessage(e.getMessage());
        }
        return cdto;
    }

    private Map<String, Object> getParamsFromRequest(CrossSortingRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (request.getCreateDmsCode() != null && request.getCreateDmsCode() > 0) {
            params.put("createDmsCode", request.getCreateDmsCode());
        }
        if (request.getDestinationDmsCode() != null && request.getDestinationDmsCode() > 0) {
            params.put("destinationDmsCode", request.getDestinationDmsCode());
        }
        if (StringUtils.isNotBlank(request.getCreateUserName())) {
            params.put("createUserName", request.getCreateUserName());
        }
        if (request.getOrgId() != null && request.getOrgId() > 0) {
            params.put("orgId", request.getOrgId());
        }
        return params;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<String> doDeleteCrossSorting(CrossSortingRequest request) {
        CommonDto<String> cdto = new CommonDto<String>();
        logger.info("CrossSortingController doDeleteCrossSorting begin...");
        try {
            if (null == request || (null == request.getId() || request.getId() < 1)) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数不能为空！");
                return cdto;
            }
            CrossSorting cs = new CrossSorting();
            ErpUser erpUser = ErpUserClient.getCurrUser();
            cs.setId(request.getId());
            if (erpUser != null) {
                cs.setDeleteUserCode(erpUser.getUserId());
                cs.setDeleteUserName(erpUser.getUserName());
            }
            cs.setDeleteTime(new Date());
            int result = crossSortingService.updateCrossSortingForDelete(cs);
            if (result == 1) {
                cdto.setCode(CommonDto.CODE_SUCCESS);
            } else {
                cdto.setCode(CommonDto.CODE_FAIL);
            }
        } catch (Exception e) {
            logger.error("doQueryCrossSorting-error!", e);
            cdto.setCode(CommonDto.CODE_EXCEPTION);
            cdto.setData(null);
            cdto.setMessage(e.getMessage());
        }
        return cdto;
    }

    @RequestMapping(value = "/addBatch", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<String> doAddBatchCrossSorting(CrossSortingRequest request) {
        CommonDto<String> cdto = new CommonDto<String>();
        logger.info("CrossSortingController doAddCrossSorting begin...");
        try {
            //request.setData("[{\"mixDmsCode\":123,\"mixDmsName\":\"zhangsan\"},{\"mixDmsCode\":456,\"mixDmsName\":\"zhangsan\"}]");
            if (checkData(request)) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数不能为空！");
                return cdto;
            }
            List<CrossSorting> csList = JSON.parseArray(request.getData(), CrossSorting.class);
            if (checkJsonObj(csList)) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数异常！");
                return cdto;
            }
            csList = parseCrossSorting(request, csList, ErpUserClient.getCurrUser());
            // 从数据库获取混装分拣中心,然后过滤掉重复,再做添加
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("createDmsCode", request.getCreateDmsCode());
            params.put("destinationDmsCode", request.getDestinationDmsCode());
            List<CrossSorting> dmsDBList = crossSortingService.findMixDms(params);
            csList = filterCrossSorting(csList, dmsDBList);
            if (csList.size() < 1) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("没有新增数据！");
                return cdto;
            }
            int result = crossSortingService.addBatchCrossSorting(csList);
            if (result == csList.size()) {
                cdto.setCode(CommonDto.CODE_SUCCESS);
            } else {
                cdto.setCode(CommonDto.CODE_FAIL);
            }
        } catch (Exception e) {
            logger.error("doAddCrossSorting-error!", e);
            cdto.setCode(CommonDto.CODE_EXCEPTION);
            cdto.setData(null);
            cdto.setMessage(e.getMessage());
        }
        return cdto;
    }

    // 将包含数据库中的数据过滤
    private List<CrossSorting> filterCrossSorting(List<CrossSorting> csList, List<CrossSorting> dmsDBList) {
        if (dmsDBList == null || dmsDBList.size() < 1) {
            return csList;
        }
        List<CrossSorting> newList = new ArrayList<CrossSorting>();
        Set<Integer> dmsCodeSet = new HashSet<Integer>();
        for (CrossSorting cs : dmsDBList) {
            if (cs.getMixDmsCode() != null && cs.getMixDmsCode() > 0) {
                dmsCodeSet.add(cs.getMixDmsCode());
            }
        }
        for (CrossSorting cs : csList) {
            if (!dmsCodeSet.contains(cs.getMixDmsCode())) {
                newList.add(cs);
            }
        }
        return newList;
    }

    // 检查json序列化的对象中的数据
    private boolean checkJsonObj(List<CrossSorting> csrList) {
        if (csrList == null || csrList.size() < 1 || csrList.size() > 10) {
            return true;
        }
        for (CrossSorting cs : csrList) {
            if (cs.getMixDmsCode() == null || cs.getMixDmsCode() < 1
                    || cs.getMixDmsName() == null
                    || StringUtils.isBlank(cs.getMixDmsName())) {
                return true;
            }
        }
        return false;
    }

    private List<CrossSorting> parseCrossSorting(CrossSortingRequest request, List<CrossSorting> csrList, ErpUser user) {
        for (CrossSorting cs : csrList) {
            cs.setOrgId(request.getOrgId());
            cs.setCreateDmsCode(request.getCreateDmsCode());
            cs.setCreateDmsName(request.getCreateDmsName());
            cs.setDestinationDmsCode(request.getDestinationDmsCode());
            cs.setDestinationDmsName(request.getDestinationDmsName());
            if (user != null) {
                cs.setCreateUserCode(user.getUserId()); // 维护的是用户ID,不是ERP帐号
                cs.setCreateUserName(user.getUserName());
            }
            cs.setCreateTime(new Date());
            cs.setDeleteUserCode(null);
            cs.setDeleteUserName(null);
            cs.setDeleteTime(null);
            cs.setYn(Integer.valueOf(1));
        }
        return csrList;
    }

    private boolean checkData(CrossSortingRequest request) {
        if (request == null || request.getOrgId() == null
                || request.getOrgId() < 1
                || request.getCreateDmsCode() == null
                || request.getCreateDmsCode() < 1
                || request.getCreateDmsName() == null
                || StringUtils.isBlank(request.getCreateDmsName())
                || request.getDestinationDmsCode() == null
                || request.getDestinationDmsCode() < 1
                || request.getDestinationDmsName() == null
                || StringUtils.isBlank(request.getDestinationDmsName())
                || request.getData() == null
                || StringUtils.isBlank(request.getData())) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/mixDms", method = RequestMethod.POST)
    @ResponseBody
    public CommonDto<List<CrossSorting>> doQueryMixDms(CrossSortingRequest request) {
        CommonDto<List<CrossSorting>> cdto = new CommonDto<List<CrossSorting>>();
        logger.info("CrossSortingController doQueryMixDms begin...");
        try {
            if (null == request || null == request.getCreateDmsCode()
                    || request.getCreateDmsCode() < 1
                    || null == request.getDestinationDmsCode()
                    || request.getDestinationDmsCode() < 1) {
                cdto.setCode(CommonDto.CODE_WARN);
                cdto.setMessage("参数不能为空！");
                return cdto;
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("createDmsCode", request.getCreateDmsCode());
            params.put("destinationDmsCode", request.getDestinationDmsCode());
            List<CrossSorting> mixDmsList = crossSortingService.findMixDms(params);
            if (null != mixDmsList && mixDmsList.size() > 0) {
                cdto.setCode(CommonDto.CODE_SUCCESS);
                cdto.setData(mixDmsList);
            } else {
                cdto.setCode(CommonDto.CODE_FAIL);
            }
        } catch (Exception e) {
            logger.error("doAddCrossSorting-error!", e);
            cdto.setCode(CommonDto.CODE_EXCEPTION);
            cdto.setData(null);
            cdto.setMessage(e.getMessage());
        }
        return cdto;
    }

    /**
     * 导入excel文件
     * @param file
     * @param response
     * 前台已经注释掉了
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @Deprecated
    public void doImportExcel(@RequestParam("importExcelFile") MultipartFile file, HttpServletResponse response){
        response.setContentType("text/json;charset=utf-8");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            if (file.getSize() > FILE_SIZE_LIMIT) throw new IOException("文件大小超过限制(1M)");
            Sheet sheet0 = getSheet0FromFile(file);
            if (null == sheet0) throw new DataFormatException("文件只能是Excel");
            ErpUser erpUser = ErpUserClient.getCurrUser();
            if (null == erpUser) throw new DataFormatException("未登录用户，没有权限");
            crossSortingService.importCrossSortingRules(sheet0, erpUser.getUserName(), String.valueOf(erpUser.getUserId()));
            writeAndClose(pw, JsonHelper.toJson(new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK)));
        } catch (Exception e) {
            if(pw != null){
                if (e instanceof IOException) {
                    logger.error("导入分拣配置规则失败", e);
                    writeAndClose(pw,JsonHelper.toJson(new JdResponse(701, e.getMessage())));
                } else if (e instanceof DataFormatException) {
                    logger.error("导入分拣配置规则失败", e);
                    writeAndClose(pw,JsonHelper.toJson(new JdResponse(702, e.getMessage())));
                }else{
                    writeAndClose(pw,JsonHelper.toJson(new JdResponse(703, "导入分拣配置规则失败,系统异常")));
                }
            }
        }
    }

    /**
     * 写入PrintWriter并关闭
     * @param pw
     * @param value
     */
    private void writeAndClose(PrintWriter pw, String value){
        pw.write(value);
        pw.flush();
        pw.close();
    }

    /**
     * 解析请求参数
     * @param request
     * @return
     */
    private CrossSortingRequest getParameter(HttpServletRequest request) {
        CrossSortingRequest request1 = new CrossSortingRequest();
        String orgID = request.getParameter("orgId");
        String createDmsCode = request.getParameter("createDmsCode");
        String destinationDmsCode = request.getParameter("destinationDmsCode");
        String createUserName = request.getParameter("createUserName");
        String type = request.getParameter("type");
        if (!StringHelper.isEmpty(orgID)) {
            request1.setOrgId(Integer.valueOf(orgID));
        }
        if (!StringHelper.isEmpty(createDmsCode)) {
            request1.setCreateDmsCode(Integer.valueOf(createDmsCode));
        }
        if (!StringHelper.isEmpty(destinationDmsCode)) {
            request1.setDestinationDmsCode(Integer.valueOf(destinationDmsCode));
        }
        if (!StringHelper.isEmpty(createUserName)) {
            request1.setCreateUserName(createUserName);
        }
        if (!StringHelper.isEmpty(type)) {
            request1.setType(Integer.valueOf(type));
        }
        return request1;
    }

    /**
     * 导出
     * @param request
     * @param response
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void doExportExcel(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> params = this.getParamsFromRequest(getParameter(request));
            List<CrossSorting> crossSortings = crossSortingService.findPageCrossSorting(params);
            if (crossSortings.size() > EXPORT_ROW_LIMIT) {
                response.setHeader("Content-type", "text/html;charset=UTF-8");
                PrintWriter pw = response.getWriter();
                writeAndClose(pw, "要导出的数据太多，建议用条件过滤");
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("跨分拣配置规则.xls", "UTF-8"));
            OutputStream ouputStream = response.getOutputStream();
            HSSFWorkbook wb = createWorkbook(crossSortings);
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception ex) {
            logger.error("导出分拣配置规则异常", ex);
        }
    }

    /**
     * 根据给定的跨分拣规则创建工作薄
     * @param crossSortings 跨分拣规则
     * @return 创建的工作薄
     */
    private HSSFWorkbook createWorkbook(List<CrossSorting> crossSortings){
        HSSFWorkbook wb = new HSSFWorkbook();

        // create sheet
        HSSFSheet sheet = wb.createSheet("Sheet1");
        HSSFRow row = sheet.createRow(0);

        // create two colors
        HSSFPalette customPalette = wb.getCustomPalette();
        customPalette.setColorAtIndex((short) 9, (byte) 220, (byte) 230, (byte) 241);
        customPalette.setColorAtIndex((short) 10, (byte) 217, (byte) 217, (byte) 217);

        // create cell style with font and colors
        HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor((short) 9);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor((short) 10);
//        createCellOfRow(row, 0, "规则类型", style);
        createCellOfRow(row, 0, "始发分拣中心编码", style);
        createCellOfRow(row, 1, "始发分拣中心名称", style);
        createCellOfRow(row, 2, "目的分拣中心编码", style);
        createCellOfRow(row, 3, "目的分拣中心名称", style);
        createCellOfRow(row, 4, "可混装分拣中心编码", style);
        createCellOfRow(row, 5, "可混装分拣中心名称", style);

        // create cell style(border with border color)
        HSSFCellStyle styleContent = wb.createCellStyle();
        styleContent.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleContent.setRightBorderColor((short) 10);

        for (int i = 0; i < crossSortings.size(); i++) {
            CrossSorting crossSorting = crossSortings.get(i);
            HSSFRow row1 = sheet.createRow(i + 1);
//            createCellOfRow(row1, 0, CrossSortingImpl.CREATE_PACKAGE_CODE.equals(crossSorting.getType())
//                    ? CrossSortingImpl.CREATE_PACKAGE
//                    : CrossSortingImpl.CREATE_SEND, styleContent);
            createCellOfRow(row1, 0, String.valueOf(crossSorting.getCreateDmsCode()), styleContent);
            createCellOfRow(row1, 1, crossSorting.getCreateDmsName(),styleContent);
            createCellOfRow(row1, 2, String.valueOf(crossSorting.getDestinationDmsCode()), styleContent);
            createCellOfRow(row1, 3, crossSorting.getDestinationDmsName(), styleContent);
            createCellOfRow(row1, 4, String.valueOf(crossSorting.getMixDmsCode()), styleContent);
            createCellOfRow(row1, 5, crossSorting.getMixDmsName(), styleContent);
        }

        // set auto size column
        sheet.autoSizeColumn((short) 0);
        sheet.autoSizeColumn((short) 1);
        sheet.autoSizeColumn((short) 2);
        sheet.autoSizeColumn((short) 3);
        sheet.autoSizeColumn((short) 4);
        sheet.autoSizeColumn((short) 5);
        return wb;
    }

    /**
     * 根据具体格式和内容创建单元格
     * @param row 单元格所在行
     * @param colIndex 单元格的index
     * @param cellValue 单元格内容
     * @param cellStyle 单元格样式
     */
    private void createCellOfRow(HSSFRow row, int colIndex, String cellValue, HSSFCellStyle cellStyle) {
        HSSFCell cell = row.createCell(colIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cellValue);
    }

    /**
     * 从文件流获取Sheet
     * @param file 上传的文件
     * @return sheet
     * @throws Exception
     */
    private Sheet getSheet0FromFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName.toLowerCase().endsWith("xlsx")) {
            return new XSSFWorkbook(file.getInputStream()).getSheetAt(0);
        } else if (fileName.toLowerCase().endsWith("xls")) {
            return new HSSFWorkbook(file.getInputStream()).getSheetAt(0);
        } else {
            throw new DataFormatException("文件只能是Excel");
        }
    }

    public static void main(String[] args) {
        String json = "[{\"mixDmsCode\":123,\"mixDmsName\":\"zhangsa12\"},{\"mixDmsCode\":456,\"mixDmsName\":\"zhangsan\"}]";
        //json = null;
        List<CrossSorting> csList = JSON.parseArray(json, CrossSorting.class);
        for (CrossSorting cs : csList) {
            System.out.println(cs.getMixDmsName());
        }
    }
}


