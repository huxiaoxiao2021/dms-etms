package com.jd.bluedragon.common.log;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationH2Test;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.LogEngine;
import com.jd.bluedragon.distribution.log.impl.JDQLogWriter;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSON;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class LogEngineImplTest extends AbstractDaoIntegrationH2Test {

    @Autowired
    private LogEngine logEngine;

    @Autowired
    private JDQLogWriter jdqLogWriter;

    @Autowired
    private OfflineLogService offlineLogService;

    @Autowired
    private OperationLogService operationLogService;

    @Test
    public void testConTentsLoads(){
        Assert.assertNotNull(logEngine);
        Assert.assertNotNull(jdqLogWriter);
        Assert.assertNotNull(offlineLogService);
        Assert.assertNotNull(operationLogService);
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void addLog() {
        BusinessLogProfiler businessLogProfiler=new BusinessLogProfiler();
        businessLogProfiler.setLogType("developer");

        businessLogProfiler.setTimeStamp(new Date().getTime());
        businessLogProfiler.setSourceSys(112);
        businessLogProfiler.setBizType(100);
        businessLogProfiler.setOperateType(1001);
        businessLogProfiler.setMethodName("methodname");
        businessLogProfiler.setOperateResponse("123");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("waybillCode","112345678");
        String operateRequest = JSON.toJSONString(jsonObject);

        businessLogProfiler.setOperateRequest(operateRequest);
        logEngine.addLog(businessLogProfiler);
    }

    @Test
    public void offlinelogTest(){
        OfflineLog offlineLog=new OfflineLog();
        offlineLog.setMethodName("methodname");
        offlineLog.setBoxCode("boxcodexxxxx");
        offlineLog.setPackageCode("packagecodexxxxxx");
        offlineLog.setWaybillCode("waybillcodexxxxx");
        offlineLog.setTaskType(Task.TASK_TYPE_RECEIVE);
        offlineLog.setOperateTime(new Date());
        offlineLog.setSendCode("sendcodexxxxxxxxxxx");

        offlineLogService.addOfflineLog(offlineLog);
    }

    @Test
    public void operateLogService(){
        OperationLog operationLog=new OperationLog();
        operationLog.setMethodName("methodname");
        operationLog.setLogType(OperationLog.LOG_TYPE_RECEIVE);
        operationLog.setWaybillCode("waybillcodexxxx");
        operationLog.setOperateTime(new Date());
        operationLog.setRemark("operateLogTest");

        operationLogService.add(operationLog);
    }

    @Test
    public void testBusineslogBuilder(){
        JSONObject request=new JSONObject();
        request.put("waybillcode",124234);

        JSONObject response = new JSONObject();
        response.put("k","v");

        BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
                .methodName("methodname")
                .url("url")
                .timeStamp(new Date().getTime())
                .reMark("remark")
                .operateResponse(response)
                .operateRequest(request)
                .operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SEND_REVERSE_SEND)
                .build();

        System.out.println(JSONObject.toJSONString(businessLogProfiler));
    }
}