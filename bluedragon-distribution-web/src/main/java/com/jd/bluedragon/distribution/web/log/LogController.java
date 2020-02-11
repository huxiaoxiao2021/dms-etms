package com.jd.bluedragon.distribution.web.log;

import com.jd.dms.logger.domain.es.ESIndexAndTypeEnum;
import com.jd.dms.logger.dto.base.TableResult;
import com.jd.dms.logger.service.BusinessLogQueryService;
import com.jd.dms.logger.service.config.TypeService;
import com.jd.dms.logger.service.query.BusinessLogService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/businesslog")
public class LogController {

    @Autowired
    BusinessLogQueryService businessLogQueryService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //    @Authorization(Constants.DMS_WEB_SORTING_OFFLINELOG_R)
    @RequestMapping(value = "/goListPage", method = RequestMethod.GET)
    public String goListpage(Model model) {

        return "businesslog/log";
    }

    /**
     * 根据系统编码查询业务类型配置
     *
     * @param systemCode
     * @return
     */
    @RequestMapping(value = "/getBizTypeConfigBySystemCode")
    @ResponseBody
    public JdResponse<Map<Integer, String>> getBizTypeConfigBySystemCode(Integer systemCode) {
        logger.info("根据系统编码查询业务类型配置.systemCode:" + systemCode);
        JdResponse<Map<Integer, String>> response = new JdResponse<Map<Integer, String>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        Map<String, Integer> params = new HashMap<>();
        params.put("system_code", systemCode);
        try {
            Map<Integer, String> systemConfigMap = businessLogQueryService.getConfigByCondition(ESIndexAndTypeEnum.BizTypeConfig, params, null);
            response.setData(systemConfigMap);
        } catch (Exception e) {
            logger.error("根据系统编码查询业务类型配置.失败原因:", e);
            response.setData(null);
            response.setCode(JdResponse.CODE_ERROR);
            response.setMessage("根据系统编码查询业务类型配置异常.");
        }

        return response;
    }


    /**
     * 根据系统编码和业务编码查询操作类型配置
     *
     * @param systemCode
     * @return
     */
    @RequestMapping(value = "/getOpeBySysAndBiz")
    @ResponseBody
    public JdResponse<Map<Integer, String>> getOpeBySysAndBiz(Integer systemCode, Integer bizTypeCode) {
        logger.info("根据系统编码和业务类型编码查询操作类型配置.systemCode:" + systemCode + ",bizTypeCode:" + bizTypeCode);
        JdResponse<Map<Integer, String>> response = new JdResponse<Map<Integer, String>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        Map<String, Integer> params = new HashMap<>();
        params.put("system_code", systemCode);
        params.put("biz_type_code",bizTypeCode);
        try {
            Map<Integer, String> operateTypeMap = businessLogQueryService.getConfigByCondition(ESIndexAndTypeEnum.OperateTypeConfig, params, null);
            response.setData(operateTypeMap);
        } catch (Exception e) {
            logger.error("根据系统编码和业务类型编码查询操作类型配置.失败原因:", e);
            response.setData(null);
            response.setCode(JdResponse.CODE_ERROR);
            response.setMessage("根据系统编码和业务类型编码查询操作类型配置异常.");
        }

        return response;
    }


    @RequestMapping("/getBusinessLog")
    @ResponseBody
    public TableResult getBusinessLog(@RequestBody Map<String, Object> request) {
        request.put("sourceSys","112"); //只从实操日志里面查
        TableResult businessLogList=null;
        try {
            businessLogList = businessLogQueryService.getBusinessLogList(request);
        } catch (Exception e) {
            logger.error("BusinessLogController.getBusinessLogList-error!", e);
            businessLogList.setStatusCode(TableResult.SERVER_ERROR_CODE);
            businessLogList.setStatusMessage(TableResult.SERVER_ERROR_MESSAGE);
        }
        return businessLogList;
    }
}
