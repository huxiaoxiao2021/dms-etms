package com.jd.bluedragon.distribution.web.areadest;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.api.response.AreaDestResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.areadest.service.AreaDestPlanService;
import com.jd.bluedragon.distribution.areadest.service.AreaDestService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.RouteType;
import com.jd.ql.basic.dto.SimpleBaseSite;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * 区域批次目的地
 * <p>
 * Created by lixin39 on 2016/12/9.
 */
@Controller
@RequestMapping("/areaDest")
public class AreaDestController {

    private final static Log logger = LogFactory.getLog(AreaDestController.class);

    @Autowired
    private AreaDestService areaDestService;

    @Autowired
    private AreaDestPlanService areaDestPlanService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    private static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10;

    private static final int EXPORT_ROW_LIMIT = 50000;

    /**
     * 获取区域批次目的地列表
     *
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse getList(AreaDestRequest request, Pager<List<AreaDest>> pager) {
        AreaDestResponse<Pager<List<AreaDest>>> response = new AreaDestResponse<Pager<List<AreaDest>>>();
        try {
            Integer planId = request.getPlanId();
            Integer routeType = request.getRouteType();
            if (planId == null) {
                response.setCode(JdResponse.CODE_PARAM_ERROR);
                response.setMessage("参数错误：获取编方案编号为null");
                return response;
            }

            // 设置分页对象
            if (pager == null) {
                pager = new Pager<List<AreaDest>>(Pager.DEFAULT_PAGE_NO);
            }

            List<AreaDest> result;
            if (routeType != null) {
                result = areaDestService.getList(planId, RouteType.getEnum(request.getRouteType()), pager);
            } else {
                result = areaDestService.getList(planId, null, pager);
            }

            pager.setData(result);
            response.setData(pager);
            response.setCode(JdResponse.CODE_OK);
            response.setMessage(JdResponse.MESSAGE_OK);
        } catch (Exception e) {
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            logger.error("获取区域批次目的地树形列表数据异常", e);
        }
        return response;
    }

    /**
     * 根据机构id获取对应分拣中心
     *
     * @param orgId
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/dmsList", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleBaseSite> queryDmsListByOrg(Integer orgId) {
        try {
            return baseMajorManager.getDmsListByOrgId(orgId);
        } catch (Exception e) {
            logger.error("获取机构下的所有分拣中心失败", e);
            return null;
        }
    }

    /**
     * 根据方案编号和路线类型获取关系列表
     *
     * @param planId
     * @param routeType
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/getSelected", method = RequestMethod.GET)
    @ResponseBody
    public List<AreaDest> getSelected(Integer planId, Integer routeType) {
        try {
            return areaDestService.getList(planId, RouteType.getEnum(routeType));
        } catch (Exception e) {
            logger.error("获取机构下的分拣中心失败", e);
        }
        return null;
    }

    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse save(AreaDestRequest request) {
        AreaDestResponse<String> response = new AreaDestResponse<String>();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                if (areaDestPlanService.isUsing(request.getPlanId())) {
                    response.setCode(JdResponse.CODE_SEE_OTHER);
                    response.setMessage("该方案正在处于启用状态，请释放后再操作！");
                } else {
                    Integer count = areaDestService.getCount(request.getPlanId(), request.getCreateSiteCode(), request.getReceiveSiteCode());
                    if (count != null) {
                        if (count > 0) {
                            response.setCode(JdResponse.CODE_SEE_OTHER);
                            response.setMessage("新增失败，始发站点与目的站点已经存在关系，请勿重复添加！");
                            return response;
                        } else {
                            if (areaDestService.add(buildAreaDestDomain(request, erpUser))) {
                                response.setCode(JdResponse.CODE_OK);
                                response.setMessage(JdResponse.MESSAGE_OK);
                                return response;
                            }
                        }
                    }
                    response.setCode(JdResponse.CODE_SERVICE_ERROR);
                    response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
                }
            } else {
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("获取Erp用户信息失败，结果为null，请重新登陆！");
            }
        } catch (Exception e) {
            logger.error("新增发货关系时发生异常", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    private AreaDest buildAreaDestDomain(AreaDestRequest request, ErpUserClient.ErpUser erpUser) {
        AreaDest areaDest = new AreaDest();
        areaDest.setPlanId(request.getPlanId());
        areaDest.setRouteType(request.getRouteType());
        areaDest.setCreateSiteCode(request.getCreateSiteCode());
        areaDest.setCreateSiteName(request.getCreateSiteName());
        Integer transferSiteCode = request.getTransferSiteCode();
        if (transferSiteCode != null) {
            areaDest.setTransferSiteCode(transferSiteCode);
            areaDest.setTransferSiteName(request.getTransferSiteName());
        } else {
            areaDest.setTransferSiteCode(0);
            areaDest.setTransferSiteName("");
        }
        areaDest.setReceiveSiteCode(request.getReceiveSiteCode());
        areaDest.setReceiveSiteName(request.getReceiveSiteName());
        areaDest.setCreateUser(erpUser.getUserCode());
        areaDest.setCreateUserCode(erpUser.getStaffNo());
        return areaDest;
    }

    /**
     * 批量保存
     *
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/saveBatch", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse saveBatch(@RequestBody AreaDestRequest request) {
        AreaDestResponse<String> response = new AreaDestResponse<String>();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                if (areaDestPlanService.isUsing(request.getPlanId())) {
                    response.setCode(JdResponse.CODE_SEE_OTHER);
                    response.setMessage("该方案正在处于启用状态，请释放后再操作！");
                } else {
                    if (areaDestService.addBatch(request, erpUser.getUserCode(), erpUser.getStaffNo()) > 0) {
                        response.setCode(JdResponse.CODE_OK);
                        response.setMessage(JdResponse.MESSAGE_OK);
                    } else {
                        response.setCode(JdResponse.CODE_SERVICE_ERROR);
                        response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
                    }
                }
            } else {
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("获取Erp用户信息失败，结果为null，请重新登陆！");
            }
        } catch (Exception e) {
            logger.error("批量保存目的分拣中心失败", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    /**
     * 批量移除
     *
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/delBatch", method = RequestMethod.POST)
    @ResponseBody
    public AreaDestResponse delBatch(@RequestBody AreaDestRequest request) {
        AreaDestResponse<String> response = new AreaDestResponse<String>();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                if (areaDestPlanService.isUsing(request.getPlanId())) {
                    response.setCode(JdResponse.CODE_SEE_OTHER);
                    response.setMessage("该方案正在处于启用状态，请释放后再操作！");
                } else {
                    if (areaDestService.disable(request, erpUser.getUserCode(), erpUser.getStaffNo())) {
                        response.setCode(JdResponse.CODE_OK);
                        response.setMessage(JdResponse.MESSAGE_OK);
                    } else {
                        response.setCode(JdResponse.CODE_SERVICE_ERROR);
                        response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
                    }
                }
            } else {
                response.setCode(JdResponse.CODE_SERVICE_ERROR);
                response.setMessage("获取Erp用户信息失败，结果为null，请重新登陆！");
            }
        } catch (Exception e) {
            logger.error("批量移除目的站点失败", e);
            response.setCode(JdResponse.CODE_SERVICE_ERROR);
            response.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return response;
    }

    /**
     * 导入excel文件
     *
     * @param file
     * @param response
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public void doImportExcel(@RequestParam("importExcelFile") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/json;charset=utf-8");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            if (file.getSize() > FILE_SIZE_LIMIT) throw new IOException("文件大小超过限制(10M)");
            Map<RouteType, Sheet> sheets = getSheets(file);
            if (null == sheets || sheets.size() == 0) throw new DataFormatException("导入失败，无效的Excel模板");
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (null == erpUser) throw new DataFormatException("未登录用户或没有权限，请重新登录");
            if (areaDestPlanService.isUsing(Integer.valueOf(request.getParameter("planId")))) {
                writeAndClose(pw, JsonHelper.toJson(new JdResponse(JdResponse.CODE_SEE_OTHER, "该方案正在处于启用状态，请释放后再操作！")));
            } else {
                Map<RouteType, Integer> result = areaDestService.importForExcel(sheets, getParameters(request), erpUser.getUserCode(), erpUser.getStaffNo());
                writeAndClose(pw, JsonHelper.toJson(new JdResponse(JdResponse.CODE_OK, getMessage(result))));
            }
        } catch (Exception e) {
            logger.error("导入方案配置关系失败", e);
            if(pw != null){
                if (e instanceof IOException) {
                    writeAndClose(pw, JsonHelper.toJson(new JdResponse(701, e.getMessage())));
                } else if (e instanceof DataFormatException) {
                    writeAndClose(pw, JsonHelper.toJson(new JdResponse(702, e.getMessage())));
                } else {
                    writeAndClose(pw, JsonHelper.toJson(new JdResponse(703, "导入方案配置关系失败，系统异常")));
                }
            }
        }
    }

    private String getMessage(Map<RouteType, Integer> result) {
        StringBuffer sb = new StringBuffer("导入配置完成，结果：");
        for (RouteType type : RouteType.values()) {
            Integer count = result.get(type);
            if (count != null) {
                sb.append("[" + type.getName());
                sb.append("成功导入" + count + "条]");
            }
        }
        return sb.toString();
    }

    private AreaDestRequest getParameters(HttpServletRequest request) {
        AreaDestRequest areaDestRequest = new AreaDestRequest();
        areaDestRequest.setPlanId(Integer.valueOf(request.getParameter("planId")));
        areaDestRequest.setCreateSiteCode(Integer.valueOf(request.getParameter("createSiteCode")));
        areaDestRequest.setCreateSiteName(request.getParameter("createSiteName"));
        return areaDestRequest;
    }

    /**
     * 从文件流获取Sheet
     *
     * @param file
     * @return
     * @throws Exception
     */
    private Map<RouteType, Sheet> getSheets(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        Workbook workbook;
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (fileName.toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        } else {
            throw new DataFormatException("文件只能是Excel");
        }
        Map<RouteType, Sheet> sheets = new HashMap<RouteType, Sheet>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (RouteType.MULTIPLE_DMS.getName().equals(sheet.getSheetName())) {
                sheets.put(RouteType.MULTIPLE_DMS, sheet);
            } else if (RouteType.DIRECT_DMS.getName().equals(sheet.getSheetName())) {
                sheets.put(RouteType.DIRECT_DMS, sheet);
            } else if (RouteType.DIRECT_SITE.getName().equals(sheet.getSheetName())) {
                sheets.put(RouteType.DIRECT_SITE, sheet);
            }
        }
        return sheets;
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

    /**
     * Excel导出
     *
     * @param planId
     * @param response
     */
    @Authorization(Constants.DMS_WEB_TOOL_AREADESTPLAN_R)
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ResponseBody
    public void doExportExcel(Integer planId, HttpServletResponse response) {
        try {
            if (planId != null && planId > 0) {
                List<AreaDest> areaDests = areaDestService.getList(planId, null);
                if (areaDests.size() > EXPORT_ROW_LIMIT) {
                    response.setHeader("Content-type", "text/html;charset=UTF-8");
                    PrintWriter pw = response.getWriter();
                    writeAndClose(pw, "要导出的数据太大");
                }
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("发货方案配置.xls", "UTF-8"));
                OutputStream outputStream = response.getOutputStream();
                HSSFWorkbook wb = createWorkbook(areaDests);
                wb.write(outputStream);
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception ex) {
            logger.error("导出发货方案配置异常", ex);
        }
    }

    /**
     * 根据方案关系创建工作薄
     *
     * @param areaDests 方案关系
     * @return 创建的工作薄
     */
    private HSSFWorkbook createWorkbook(List<AreaDest> areaDests) {
        HSSFWorkbook wb = new HSSFWorkbook();
        this.createColorIndex(wb);
        for (RouteType type : RouteType.values()) {
            HSSFSheet sheet = wb.createSheet(type.getName());
            HSSFRow row = sheet.createRow(0);
            HSSFCellStyle style = getHeadCellStyle(wb);
            switch (type) {
                case DIRECT_SITE:
                    createCellOfRow(row, 0, "预分拣站点编号*", style);
                    createCellOfRow(row, 1, "预分拣站点名称", style);
                    sheet.setColumnWidth(0, 5000);
                    sheet.setColumnWidth(1, 10000);
                    break;
                case DIRECT_DMS:
                    createCellOfRow(row, 0, "下级分拣中心编号*", style);
                    createCellOfRow(row, 1, "下级分拣中心名称", style);
                    createCellOfRow(row, 2, "预分拣站点编号*", style);
                    createCellOfRow(row, 3, "预分拣站点名称", style);
                    sheet.setColumnWidth(0, 5000);
                    sheet.setColumnWidth(1, 10000);
                    sheet.setColumnWidth(2, 5000);
                    sheet.setColumnWidth(3, 10000);
                    break;
                case MULTIPLE_DMS:
                    createCellOfRow(row, 0, "下级分拣中心编号", style);
                    createCellOfRow(row, 1, "下级分拣中心名称", style);
                    createCellOfRow(row, 2, "末级分拣中心编号*", style);
                    createCellOfRow(row, 3, "末级分拣中心名称", style);
                    sheet.setColumnWidth(0, 5000);
                    sheet.setColumnWidth(1, 10000);
                    sheet.setColumnWidth(2, 5000);
                    sheet.setColumnWidth(3, 10000);
                    break;
            }
            /*sheet.autoSizeColumn((short) 0);
            sheet.autoSizeColumn((short) 1);
            sheet.autoSizeColumn((short) 2);
            sheet.autoSizeColumn((short) 3);*/
        }

        // create cell style(border with border color)
        HSSFCellStyle styleContent = getCellStyle(wb);
        for (int i = 0; i < areaDests.size(); i++) {
            AreaDest areaDest = areaDests.get(i);
            RouteType type = RouteType.getEnum(areaDest.getRouteType());
            switch (type) {
                case DIRECT_SITE:
                    HSSFSheet sheet = wb.getSheet(type.getName());
                    HSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
                    createCellOfRow(row, 0, areaDest.getReceiveSiteCode(), styleContent);
                    createCellOfRow(row, 1, areaDest.getReceiveSiteName(), styleContent);
                    break;
                case DIRECT_DMS:
                    HSSFSheet sheet1 = wb.getSheet(type.getName());
                    HSSFRow row1 = sheet1.createRow(sheet1.getLastRowNum() + 1);
                    createCellOfRow(row1, 0, areaDest.getTransferSiteCode(), styleContent);
                    createCellOfRow(row1, 1, areaDest.getTransferSiteName(), styleContent);
                    createCellOfRow(row1, 2, areaDest.getReceiveSiteCode(), styleContent);
                    createCellOfRow(row1, 3, areaDest.getReceiveSiteName(), styleContent);
                    break;
                case MULTIPLE_DMS:
                    HSSFSheet sheet2 = wb.getSheet(type.getName());
                    HSSFRow row2 = sheet2.createRow(sheet2.getLastRowNum() + 1);
                    createCellOfRow(row2, 0, areaDest.getTransferSiteCode(), styleContent);
                    createCellOfRow(row2, 1, areaDest.getTransferSiteName(), styleContent);
                    createCellOfRow(row2, 2, areaDest.getReceiveSiteCode(), styleContent);
                    createCellOfRow(row2, 3, areaDest.getReceiveSiteName(), styleContent);
                    break;
            }
        }
        return wb;
    }

    private final static short colorIndex_1 = 9;

    private final static short colorIndex_2 = 10;

    private void createColorIndex(HSSFWorkbook wb) {
        // create two colors
        HSSFPalette customPalette = wb.getCustomPalette();
        customPalette.setColorAtIndex(colorIndex_1, (byte) 220, (byte) 230, (byte) 241);
        customPalette.setColorAtIndex(colorIndex_2, (byte) 217, (byte) 217, (byte) 217);
    }

    private HSSFCellStyle getHeadCellStyle(HSSFWorkbook wb) {
        // create cell style with font and colors
        HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(colorIndex_1);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(colorIndex_2);
        HSSFFont font = wb.createFont();
        font.setFontName("微软雅黑");
        style.setFont(font);
        return style;
    }

    private HSSFCellStyle getCellStyle(HSSFWorkbook wb) {
        // create cell style with font and colors
        HSSFCellStyle style = wb.createCellStyle();
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(colorIndex_2);
        return style;
    }

    /**
     * 根据具体格式和内容创建单元格
     *
     * @param row       单元格所在行
     * @param colIndex  单元格的index
     * @param cellValue 单元格内容
     * @param cellStyle 单元格样式
     */
    private void createCellOfRow(HSSFRow row, int colIndex, String cellValue, HSSFCellStyle cellStyle) {
        HSSFCell cell = row.createCell(colIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cellValue);
    }

    private void createCellOfRow(HSSFRow row, int colIndex, double cellValue, HSSFCellStyle cellStyle) {
        HSSFCell cell = row.createCell(colIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(cellValue);
    }

}
