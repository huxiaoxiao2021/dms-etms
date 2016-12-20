package com.jd.bluedragon.distribution.web.gantryException;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.GantryExceptionRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hanjiaxing
 * @version 1.0
 * @date 2016/12/14
 */
@Controller
@RequestMapping("/gantryException")
public class GantryExceptionController {
    private static final Log logger = LogFactory.getLog(GantryExceptionController.class);

    @Autowired
    private GantryDeviceService gantryDeviceService;
    @Autowired
    private GantryExceptionService gantryExceptionService;

    @RequestMapping(value = "/gantryExceptionList", method = RequestMethod.GET)
    public String gantryExceptionPageList(GantryExceptionRequest request, Model model) {

        try {
            if (request.getSiteCode() != null) {
                List<GantryDevice> allDevice = gantryDeviceService.getGantryByDmsCode(request.getSiteCode());
                model.addAttribute("allDevice", allDevice);
            }
        } catch (Exception e){
            logger.error("获取分拣中心编号为" +request.getSiteCode() + "的龙门架失败",e);
        }
        if (request.getEndTime() == null || request.getEndTime() == "") {
            Date date = new Date();
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            request.setEndTime(sdf.format(date));
        }

        model.addAttribute("queryParam", request);

        return "gantryException/gantryExceptionList";
    }

    @RequestMapping(value = "/doQuery", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Pager<List<GantryException>>> queryGantryExceptionByParam(GantryExceptionRequest request
                                            , Pager<List<GantryException>> pager){
        InvokeResult<Pager<List<GantryException>>> result = new InvokeResult<Pager<List<GantryException>>>();
        if (checkAddParam(request) && checkDateGap(request)) {
            try {
                Map<String, Object> params = this.buildParam(request);

                if (null == pager) {
                    pager = new Pager<List<GantryException>>(Pager.DEFAULT_PAGE_NO);
                } else {
                    pager = new Pager<List<GantryException>>(pager.getPageNo(), pager.getPageSize());
                }
                params.putAll(ObjectMapHelper.makeObject2Map(pager));

                List<GantryException> gantryExceptionList = gantryExceptionService.getGantryExceptionPage(params);
                Integer totalSize = gantryExceptionService.getGantryExceptionCount(params);
                pager.setTotalSize(totalSize);
                pager.setData(gantryExceptionList);
                result.setData(pager);
                result.setCode(200);
                result.setMessage("查询龙门架异常信息成功");
            } catch (Exception e) {
                result.setCode(10000);
                result.setMessage("查询龙门架异常信息失败");
                logger.error("查询龙门架异常信息失败", e);
            }
        } else {
            this.logger
                    .error("GantryExceptionController gantryExceptionPageList PARAM ERROR --> 传入参数非法");
        }
        return result;
    }

    @RequestMapping(value = "/doQueryCount", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<Integer> queryGantryExceptionCountByParam(GantryExceptionRequest request){
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        if (checkAddParam(request) && checkDateGap(request)) {
            try{
                Map<String, Object> params = buildParam(request);
                Integer totalSize = gantryExceptionService.getGantryExceptionCount(params);
                result.setData(totalSize);
                result.setCode(200);
                result.setMessage("查询龙门架异常信息成功");
            } catch (Exception e){
                result.setCode(10000);
                result.setMessage("查询龙门架异常信息失败");
                logger.error("查询龙门架异常信息失败", e);
            }
        } else {
            this.logger
                    .error("GantryExceptionController queryGantryExceptionCountByParam PARAM ERROR --> 传入参数非法");
        }
            return result;
    }

    /**
     * 导出异常信息数据
     *
     */
    @RequestMapping(value = "/doExport", method = RequestMethod.GET)
    public void exportGantryException(GantryExceptionRequest request, HttpServletResponse response) {
        this.logger.info("导出发货异常信息数据");

        if (checkAddParam(request) && checkDateGap(request)) {
        Map<String, Object> params = buildParam(request);

        OutputStream outputStream = null;
            try {
                List<GantryException> gantryExceptions = null;
                try {
                    if (!params.isEmpty()) {
                        gantryExceptions = this.gantryExceptionService.getGantryException(params);
                    }
                } catch (Exception e) {
                    this.logger.error("根据条件查询发货异常信息数据异常：", e);
                }

                if (gantryExceptions != null && !gantryExceptions.isEmpty()) {

                    int maxCount = 5000;

                    List<GantryException> gantryExceptionExports = new ArrayList<GantryException>();

                    if (gantryExceptions.size() > maxCount) {
                        for (int i = 0; i < maxCount; i++) {
                            gantryExceptionExports.add(gantryExceptions
                                    .get(i));
                        }
                    } else {
                        gantryExceptionExports = gantryExceptions;
                    }
                    String filename = "龙门架编号" + request.getSiteCode() + "的异常信息"
                            + DateHelper.formatDate(new Date(), Constants.DATE_TIME_MS_STRING) + ".xls";
                    response.setContentType("application/vnd.ms-excel");
                    response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
                    outputStream = response.getOutputStream();
                    HSSFWorkbook wb = createWorkbook(gantryExceptionExports);
                    wb.write(outputStream);
                    outputStream.flush();
                    outputStream.close();
                }

            } catch (IOException e) {
                this.logger.error("根据条件查询龙门架自动发货导出数据异常：", e);
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    this.logger.error("关闭数据流 ERROR", e);
                }
            }
        } else {
            this.logger
                    .error("GantryExceptionController exportGantryException PARAM ERROR --> 传入参数非法");
        }

    }

    private Boolean checkAddParam(GantryExceptionRequest request) {
        if(request.getSiteCode() == null ||
                ! StringHelper.isNotEmpty(request.getStartTime()) ||
                ! StringHelper.isNotEmpty(request.getEndTime())) {
            return false;
        }
        return true;
    }

    private Boolean checkDateGap(GantryExceptionRequest request) {
        int gap = 1000 * 60 * 60 * 25;
        if(! StringHelper.isNotEmpty(request.getStartTime()) || ! StringHelper.isNotEmpty(request.getEndTime())) {
            return false;
        }
        Date startDate = getFormatDate(request.getStartTime(), "yyyy-MM-dd HH:mm:ss");
        Date endDate = getFormatDate(request.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        if (endDate.getTime() - startDate.getTime() > gap) {
            return false;
        }
        return true;
    }

    public Date getFormatDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date d = new Date();
        try {
            d = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            this.logger.error("时间转换失败");
        }
        return d;
    }

    /**
     * 根据给定的跨分拣规则创建工作薄
     * @param
     * @return
     */
    private HSSFWorkbook createWorkbook(List<GantryException> gantryExceptions){
        HSSFWorkbook wb = new HSSFWorkbook();

        // create sheet
        HSSFSheet sheet = wb.createSheet("Sheet1");
        HSSFRow row = sheet.createRow(0);

        // create two colors
//        HSSFPalette customPalette = wb.getCustomPalette();
//        customPalette.setColorAtIndex((short) 9, (byte) 220, (byte) 230, (byte) 241);
//        customPalette.setColorAtIndex((short) 10, (byte) 217, (byte) 217, (byte) 217);

        // create cell style with font and colors
        HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor((short) 9);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor((short) 10);

//        createCellOfRow(row, 0, "规则类型", style);

        createCellOfRow(row, 0, "包裹号", style);
        createCellOfRow(row, 1, "运单号", style);
        createCellOfRow(row, 2, "体积", style);
        createCellOfRow(row, 3, "异常原因", style);
        createCellOfRow(row, 4, "操作时间", style);
        createCellOfRow(row, 5, "发货状态", style);

        // create cell style(border with border color)
        HSSFCellStyle styleContent = wb.createCellStyle();
        styleContent.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleContent.setRightBorderColor((short) 10);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < gantryExceptions.size(); i++) {
            GantryException gantryException = gantryExceptions.get(i);
            HSSFRow row1 = sheet.createRow(i + 1);

            createCellOfRow(row1, 0, gantryException.getPackageCode(), styleContent);
            createCellOfRow(row1, 1, gantryException.getWaybillCode(),styleContent);
            createCellOfRow(row1, 2, String.valueOf(gantryException.getVolume()), styleContent);
            createCellOfRow(row1, 3, replaceExceptionType(gantryException.getType()), styleContent);
            createCellOfRow(row1, 4, format.format(gantryException.getOperateTime()), styleContent);
            createCellOfRow(row1, 5, (gantryException.getYn() == 1 ? "未发货" : "已发货"), styleContent);
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

    private String replaceExceptionType(Integer type) {
        String exceptionReasonStr = "";
        if (type == 1) {
            exceptionReasonStr = "没有预分拣站点";
        } else if (type == 2) {
            exceptionReasonStr = "没有运单信息";
        } else if (type == 3) {
            exceptionReasonStr = "没有箱号信息";
        }
        return exceptionReasonStr;
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
//        cell.setCellStyle(cellStyle);
        cell.setCellValue(cellValue);
    }

    private Map<String, Object> buildParam(GantryExceptionRequest request){
        Map<String, Object> param = new HashMap<String, Object>();
        if (null == request) {
            return param;
        }
        param.put("machineId", request.getMachineId());
        param.put("startTime", request.getStartTime());
        param.put("endTime", request.getEndTime());
        param.put("isSend", request.getIsSend());
        param.put("siteCode", request.getSiteCode());
        return param;
    }
}
