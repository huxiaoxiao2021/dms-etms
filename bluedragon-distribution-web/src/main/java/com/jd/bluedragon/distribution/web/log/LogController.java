package com.jd.bluedragon.distribution.web.log;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.domain.es.ESIndexAndTypeEnum;
import com.jd.dms.logger.dto.base.BusinessLogResult;
import com.jd.dms.logger.dto.base.TableResult;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.service.BusinessLogQueryService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/businesslog")
public class LogController {

    @Autowired
    private BusinessLogQueryService businessLogQueryService;

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Authorization(Constants.DMS_WEB_SORTING_OPERATELOG_R)
    @RequestMapping(value = "/goListPage", method = RequestMethod.GET)
    public String goListpage(Model model) {
        ErpUserClient.ErpUser erpUser = new ErpUserClient.ErpUser();
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(1);
        businessLogProfiler.setBizType(Constants.BIZTYPE_URL_CLICK);
        businessLogProfiler.setOperateType(Constants.NEW_LOG);
        JSONObject request=new JSONObject();
        request.put("operatorName",erpUser.getUserName());
        request.put("operatorCode",erpUser.getUserCode());
        businessLogProfiler.setOperateRequest(JSONObject.toJSONString(request));
        BusinessLogWriter.writeLog(businessLogProfiler);
        Map<String,String> orderByFiels = new HashMap<>(1);
        orderByFiels.put("time_stamp","响应时间");
        model.addAttribute("newLogPageTips",uccPropertyConfiguration.getNewLogPageTips());
        model.addAttribute("orderByFields",orderByFiels);
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

        HashMap<String, Integer> params = new HashMap<>();
        params.put("system_code", systemCode);
        try {
            HashMap<Integer, String> systemConfigMap = businessLogQueryService.getConfigByCondition(ESIndexAndTypeEnum.BizTypeConfig, params, null);
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

        HashMap<String, Integer> params = new HashMap<>();
        params.put("system_code", systemCode);
        params.put("biz_type_code",bizTypeCode);
        try {
            HashMap<Integer, String> operateTypeMap = businessLogQueryService.getConfigByCondition(ESIndexAndTypeEnum.OperateTypeConfig, params, null);
            response.setData(operateTypeMap);
        } catch (Exception e) {
            logger.error("根据系统编码和业务类型编码查询操作类型配置.失败原因:", e);
            response.setData(null);
            response.setCode(JdResponse.CODE_ERROR);
            response.setMessage("根据系统编码和业务类型编码查询操作类型配置异常.");
        }

        return response;
    }


    @Authorization(Constants.DMS_WEB_SORTING_OPERATELOG_R)
    @RequestMapping("/getBusinessLog")
    @ResponseBody
    public TableResult getBusinessLog(@RequestBody HashMap request) {
        request.put("sourceSys","112"); //只从实操日志里面查
        TableResult businessLogList=new TableResult();
        try {
            String orderByField = (String)request.remove("orderByField");
            String orderBy = (String)request.remove("orderBy");
            if(StringUtils.isNotEmpty(orderByField) && StringUtils.isNotEmpty(orderBy)){
                Map<String,String> sortFields = new HashMap<>();//数据结构为<time_stamp,asc>
                sortFields.put(orderByField,orderBy);
                request.put("sortFields",sortFields);//key是jsf服务端 指定的
            }
            BusinessLogResult businessLogReqResult = businessLogQueryService.getBusinessLogList(request);
            businessLogList.setRows(businessLogReqResult.getRows());
            businessLogList.setTotal(businessLogReqResult.getTotal());
            businessLogList.setStatusCode(TableResult.SUCCESS_CODE);
            businessLogList.setStatusMessage(TableResult.SUCCESS_MESSAGE);
        } catch (Exception e) {
            logger.error("BusinessLogController.getBusinessLogList-error!", e);
            businessLogList.setStatusCode(TableResult.SERVER_ERROR_CODE);
            businessLogList.setStatusMessage(TableResult.SERVER_ERROR_MESSAGE);
        }
        return businessLogList;
    }
}
