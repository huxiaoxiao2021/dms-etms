package com.jd.bluedragon.distribution.jy.calibrate;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetailResult;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateRequest;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateTaskResult;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateTypeEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import com.jd.bluedragon.distribution.jy.service.calibrate.JyWeightVolumeCalibrateService;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/12/20 2:27 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bak/distribution-web-context-test.xml")
@Slf4j
public class JyWeightVolumeCalibrateServiceImplTest {

    @Autowired
    private JyWeightVolumeCalibrateService jyWeightVolumeCalibrateService;

    @Test
    public void machineCalibrateScan() {
        try {
            DwsWeightVolumeCalibrateRequest request = new DwsWeightVolumeCalibrateRequest();
            request.setMachineCode("WZ-HJ-JZBL-009");
            User user = new User();
            user.setUserErp("bjxings");
            request.setUser(user);
            InvokeResult<DwsWeightVolumeCalibrateTaskResult> result = jyWeightVolumeCalibrateService.machineCalibrateScan(request);

            DwsWeightVolumeCalibrateTaskResult data = result.getData();
            data.getDoneTaskList();
            data.getTodoTaskList();
            data.getOvertimeTaskList();
            Assert.assertTrue(true);
        }catch (Exception e){
            log.error("服务异常!", e);
            Assert.fail();
        }
    }

    @Test
    public void getMachineCalibrateDetail() {
        try {
            DwsWeightVolumeCalibrateRequest request = new DwsWeightVolumeCalibrateRequest();
            request.setMachineCode("ylq06261-DWS001");
            request.setCalibrateTaskStartTime(1664452080000L);
            request.setCalibrateTaskEndTime(1664452200000L);
            InvokeResult<DwsWeightVolumeCalibrateDetailResult> invokeResult = jyWeightVolumeCalibrateService.getMachineCalibrateDetail(request);
            log.info("result:{}", JsonHelper.toJson(invokeResult));
        }catch (Exception e) {
            log.error("服务异常!", e);
            Assert.fail();
        }
    }

    @Test
    public void closeMachineCalibrateTask() {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            User user = new User();
            user.setUserErp("bjxings");
            DwsWeightVolumeCalibrateRequest request = new DwsWeightVolumeCalibrateRequest();
            request.setMachineCode("WZ-HJ-JZBL-008");
            request.setCalibrateTaskStartTime(sdf.parse("2023-02-07 23:59:59").getTime());
            request.setUser(user);
            request.setMachineTaskId(17L);
            jyWeightVolumeCalibrateService.closeMachineCalibrateTask(request);
        }catch (Exception e){
            log.error("服务异常!", e);
            Assert.fail();
        }
    }

    @Test
    public void dealCalibrateTask() {
        try {
            DwsMachineCalibrateMQ mq1 = new DwsMachineCalibrateMQ();
            mq1.setMachineCode("WZ-HJ-JZBL-007");
            mq1.setCalibrateTime(new Date().getTime());
            mq1.setCalibrateType(JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_W.getCode());
            mq1.setWeightCalibrateStatus(1);

            DwsMachineCalibrateMQ mq2 = new DwsMachineCalibrateMQ();
            mq2.setMachineCode("WZ-HJ-JZBL-007");
            mq2.setCalibrateTime(new Date().getTime());
            mq2.setCalibrateType(JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_V.getCode());
            mq2.setVolumeCalibrateStatus(1);
            mq2.setMachineStatus(JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode());

            InvokeResult<Boolean> result1 = jyWeightVolumeCalibrateService.dealCalibrateTask(mq1);
            log.info("result1:{}", JsonHelper.toJson(result1));
            InvokeResult<Boolean> result2 = jyWeightVolumeCalibrateService.dealCalibrateTask(mq2);
            log.info("result2:{}", JsonHelper.toJson(result2));
            Assert.assertTrue(true);
        }catch (Exception e){
            log.error("服务异常!", e);
            Assert.fail();
        }
    }

    @Test
    public void regularScanCalibrateTask() {

        try {
            InvokeResult<Boolean> result = jyWeightVolumeCalibrateService.regularScanCalibrateTask();

            Assert.assertTrue(true);
        }catch (Exception e){
            log.error("服务异常!", e);
            Assert.fail();
        }
    }
}