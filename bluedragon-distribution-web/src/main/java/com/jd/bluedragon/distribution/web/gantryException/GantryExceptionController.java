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
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.util.DateUtil;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


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

    /**
     * 初始化
     *
     */
    @Authorization(Constants.DMS_WEB_SORTING_GANTRYAUTOSEND_R)
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

    @Authorization(Constants.DMS_WEB_SORTING_SORTMACHINEAUTOSEND_R)
    @RequestMapping(value = "/autoMachineExceptionList", method = RequestMethod.GET)
    public String autoMachineExceptionList(GantryExceptionRequest request, Model model) {

        Date nowTime = new Date();
        String endTime = request.getEndTime();
        String startTime = request.getStartTime();
        endTime = com.jd.jsf.gd.util.StringUtils.isBlank(endTime) ? DateUtil.format(nowTime, DateUtil.FORMAT_DATE_TIME) : endTime;
        if(com.jd.jsf.gd.util.StringUtils.isBlank(startTime)){
            Date startDateTime = DateHelper.add(nowTime, Calendar.HOUR,-24);
            startTime = DateUtil.format(startDateTime, DateUtil.FORMAT_DATE_TIME);
        }
        request.setEndTime(endTime);
        request.setStartTime(startTime);
        model.addAttribute("queryParam", request);
        return "gantryException/sortMachineExceptionList";
    }

    /**
     * 查询符合条件的异常数据
     *
     */
    @Authorization(Constants.DMS_WEB_SORTING_MACHINE_EXCEPTION)
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
                result.setMessage("查询异常信息成功");
            } catch (Exception e) {
                result.setCode(10000);
                result.setMessage("查询异常信息失败");
                logger.error("查询异常信息失败", e);
            }
        } else {
            result.setCode(10000);
            result.setMessage("查询参数不能为空且时间间隔不能超过24小时");
            this.logger
                    .error("GantryExceptionController gantryExceptionPageList PARAM ERROR --> 传入参数非法");
        }
        return result;
    }

    /**
     * 查询符合条件的数量
     *
     */
    @Authorization(Constants.DMS_WEB_SORTING_MACHINE_EXCEPTION)
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
            result.setCode(10000);
            result.setMessage("查询参数不能为空且时间间隔不能超过24小时");
            this.logger
                    .error("GantryExceptionController queryGantryExceptionCountByParam PARAM ERROR --> 传入参数非法");
        }
        return result;
    }

    /**
     * 导出异常信息数据
     *
     */
    @Authorization(Constants.DMS_WEB_SORTING_MACHINE_EXCEPTION)
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
                    String machineName = "龙门架";
                    if(request.getBusiType() != null && request.getBusiType() == 2){
                        machineName = "分拣机";
                    }
                    String filename = machineName + "编号" + request.getMachineId() + "的异常信息"
                            + DateHelper.formatDate(new Date(), Constants.DATE_TIME_MS_STRING) + ".xls";
                    response.setContentType("application/vnd.ms-excel");
                    response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
                    outputStream = response.getOutputStream();
                    HSSFWorkbook wb = createWorkbook(gantryExceptionExports, request.getBusiType());
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

    /**
     * 检查参数，防止跳过js直接发送访问请求
     *
     */
    private Boolean checkAddParam(GantryExceptionRequest request) {
        if(StringUtils.isBlank(request.getMachineId())||
                ! StringHelper.isNotEmpty(request.getStartTime()) ||
                ! StringHelper.isNotEmpty(request.getEndTime())) {
            return false;
        }
        return true;
    }

    /**
     * 检查开始和截止时间
     *
     */
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
     * 创建excel
     *
     */
    private HSSFWorkbook createWorkbook(List<GantryException> gantryExceptions, Integer busiType){
        String machineName = "龙门架编号";
        String pkgNumberName = "条码号";
        if(busiType != null && busiType == 2){
            machineName = "物理滑槽";
            pkgNumberName = "包裹号";
        }
        HSSFWorkbook wb = new HSSFWorkbook();

        // create sheet
        HSSFSheet sheet = wb.createSheet("Sheet1");
        HSSFRow row = sheet.createRow(0);
        //create two colors
//        HSSFPalette customPalette = wb.getCustomPalette();
//        customPalette.setColorAtIndex((short) 9, (byte) 220, (byte) 230, (byte) 241);
//        customPalette.setColorAtIndex((short) 10, (byte) 217, (byte) 217, (byte) 217);

        //create cell style with font and colors
        HSSFCellStyle style = wb.createCellStyle();
        style.setFillForegroundColor((short) 9);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor((short) 10);

//        createCellOfRow(row, 0, "规则类型", style);
        createCellOfRow(row, 0, machineName, style);
        createCellOfRow(row, 1, pkgNumberName, style);
        createCellOfRow(row, 2, "运单号", style);
        createCellOfRow(row, 3, "批次号", style);
        createCellOfRow(row, 4, "体积", style);
        createCellOfRow(row, 5, "异常类型", style);
        createCellOfRow(row, 6, "操作时间", style);
        createCellOfRow(row, 7, "发货状态", style);

        // create cell style(border with border color)
        HSSFCellStyle styleContent = wb.createCellStyle();
        styleContent.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleContent.setRightBorderColor((short) 10);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < gantryExceptions.size(); i++) {
            GantryException gantryException = gantryExceptions.get(i);
            HSSFRow row1 = sheet.createRow(i + 1);
            if(busiType != null && busiType == 2){
                createCellOfRow(row1, 0, gantryException.getChuteCode(), styleContent);
                createCellOfRow(row1, 1, gantryException.getPackageCode(), styleContent);
            }else {
                createCellOfRow(row1, 0, gantryException.getMachineId(), styleContent);
                createCellOfRow(row1, 1, gantryException.getBarCode(), styleContent);
            }

            createCellOfRow(row1, 2, gantryException.getWaybillCode(),styleContent);
            createCellOfRow(row1, 3, gantryException.getSendCode(),styleContent);
            createCellOfRow(row1, 4, String.valueOf(gantryException.getVolume()), styleContent);
            createCellOfRow(row1, 5, replaceExceptionType(gantryException.getType()), styleContent);
            createCellOfRow(row1, 6, format.format(gantryException.getOperateTime()), styleContent);
            createCellOfRow(row1, 7, (gantryException.getSendStatus() == 1 ? "已发货" : "未发货"), styleContent);
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
     * 替换异常类型为异常原因
     *
     */
    private String replaceExceptionType(Integer type) {
        String exceptionReasonStr = "";
        if (type == 1) {
            exceptionReasonStr = "无预分拣站点";
        } else if (type == 2) {
            exceptionReasonStr = "无运单信息";
        } else if (type == 3) {
            exceptionReasonStr = "无箱号信息";
        } else if (type == 4) {
            exceptionReasonStr = "拦截订单";
        } else if (type == 5) {
            exceptionReasonStr = "龙门架未绑该站点";
        }else if (type == 21){
            exceptionReasonStr = "发货始发地站点无效";
        }else if (type == 22){
            exceptionReasonStr = "无发货目的站点";
        }else if (type == 23){
            exceptionReasonStr = "订单拦截";
        }else if (type == 24){
            exceptionReasonStr = "无落格时间";
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

    /**
     * 构造参数
     *
     */
    private Map<String, Object> buildParam(GantryExceptionRequest request){
        Map<String, Object> param = new HashMap<String, Object>();
        if (null == request) {
            return param;
        }
        param.put("machineId", request.getMachineId());
        param.put("startTime", request.getStartTime());
        param.put("endTime", request.getEndTime());
        param.put("sendStatus", request.getSendStatus());
        param.put("siteCode", request.getSiteCode());
        param.put("type", request.getType());
        return param;
    }
}
