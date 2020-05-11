package com.jd.bluedragon.distribution.log.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.log.BusinessLogDto;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.log.BusinessLogConstans.OperateTypeEnum;
import com.jd.dms.logger.dto.base.BusinessLogResult;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.jd.dms.logger.service.BusinessLogQueryService;

/**
 * 日志管理
 * 
 * @author wuyoude
 *
 */
@Service("businessLogManager")
public class BusinessLogManagerImpl implements BusinessLogManager{
	
    private static final Logger logger = LoggerFactory.getLogger(BusinessLogManagerImpl.class);
    
    @Autowired
    private LogEngine logEngine;
    
    @Autowired
    private BusinessLogQueryService businessLogQueryService;
    
	@Override
	public boolean addLog(BusinessLogDto log) {
	 if(log == null){
     	return false;
     }
	 if(log.getOperateType() == null){
		 logger.warn("addLog fail! log.operateType is empty!");
		 return false;
	 }
        long currentTime = System.currentTimeMillis();
        JSONObject response = new JSONObject();
        OperateTypeEnum operateType = OperateTypeEnum.toEnum(log.getOperateType());
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                .operateTypeEnum(operateType)
                .processTime(currentTime,currentTime)
                .timeStamp(System.currentTimeMillis())
                .operateRequest(log)
                .operateResponse(response)
//                .methodName("PackingConsumableConsumer#consume")
                .build();
        logEngine.addLog(businessLogProfiler);
		return true;
	}
	@Override
	public List<BusinessLogDto> queryLogs(String businessKey) {
		return this.queryLogs(businessKey, null);
	}
	@Override
	public List<BusinessLogDto> queryLogs(String businessKey,Integer operateType) {
		List<BusinessLogDto> logs = new ArrayList<BusinessLogDto>();
        Map<String,String> sortFields = new HashMap<>();//数据结构为<time_stamp,asc>
        sortFields.put("time_stamp","desc");
        Date endTime = new Date();
        Date startTime = DateHelper.addDate(endTime, -30);
		HashMap request = new HashMap();
		request.put("startTime",DateHelper.formatDateTime(startTime));
		request.put("endTime",DateHelper.formatDateTime(endTime));
		request.put("offset","0");
		request.put("limit","100");
		request.put("sortFields",sortFields);//key是jsf服务端 指定的
		
		request.put("waybillCode",businessKey);
		if(operateType != null){
			OperateTypeEnum operateTypeEnum = OperateTypeEnum.toEnum(operateType);
			if(operateTypeEnum != null){
				request.put("bizType",operateTypeEnum.getBizTypeCode());
				request.put("operateType",operateTypeEnum.getCode());
			}
		}
        try {
            logger.info("request[{}]", JsonHelper.toJson(request));
            BusinessLogResult businessLogReqResult = businessLogQueryService.getBusinessLogList(request);
            List<HashMap<String,String>> rows = businessLogReqResult.getRows();
            if(rows != null){
            	for(HashMap<String,String> map : businessLogReqResult.getRows()){
            		BusinessLogDto log = new BusinessLogDto();
            		log.setBusinessKey(map.get("waybillCode"));
            		log.setOperateUser(map.get("operatorName"));
            		log.setOperateTime(map.get("timeStamp"));
            		log.setOperateContent(map.get("operateResponse"));
            		logs.add(log);
                }
            }
        } catch (Exception e) {
            logger.error("BusinessLogController.getBusinessLogList-error!request[{}]",JsonHelper.toJson(request), e);
        }
		return logs;
	}
}
