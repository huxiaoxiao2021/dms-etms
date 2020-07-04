package com.jd.bluedragon.distribution.exportLog.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLogCondition;
import com.jd.bluedragon.distribution.exportlog.service.ExportLogService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.common.util.StringUtils;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: 刘春和
 * @date: 2020/6/23 13:41
 * @description:
 */
@Controller
@RequestMapping("exportLog")
public class ExportLogController  extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(ExportLogController.class);

    @Autowired
    private ExportLogService exportLogService;

    @Autowired
    private JssService jssService;

    @Value("${jss.waybillcheck.export.zip.bucket}")
    private String bucket;
    /**
     * 获取导出任务列表
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping("/exportLogList")
    @ResponseBody
    public PagerResult<ExportLog> listData(@RequestBody ExportLogCondition condition) {
        LoginUser loginUser = getLoginUser();
        condition.setCreateUser(loginUser.getUserErp());
        condition.setType(1);
        return exportLogService.listData(condition);
    }
    /**
     * 下载文件
     *
     * @param fileName
     * @param response
     * @param request
     */
    @RequestMapping(value = "/downLoadFile")
    public void downLoad(@RequestParam("fileName") String fileName, HttpServletResponse response, HttpServletRequest request) {
        if(!StringUtils.isBlank(fileName)){
            fileName=fileName+".zip";
        }
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        InputStream inputStream=null;
        try {
            inputStream = jssService.downloadFile(bucket, fileName);
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载运单号校验导出记录失败", e);
        }finally {
            try {
                if(inputStream!=null) {
                    inputStream.close();
                }
            }catch (IOException ex){
                log.error("下载运单号校验导出记录失败,fileName:{}", fileName,ex);
            }
        }
    }

    /**
     * 删除文件
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_WAYBILLCODECHECK_R)
    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult deleteFile(Long id) {
        InvokeResult invokeResult = new InvokeResult();
        try {
            exportLogService.delete(id);
            invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
            invokeResult.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        } catch (Exception ex) {
            invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
            invokeResult.setMessage("删除失败");
            log.error("删除文件失败", ex);
        }
        return invokeResult;
    }
}
