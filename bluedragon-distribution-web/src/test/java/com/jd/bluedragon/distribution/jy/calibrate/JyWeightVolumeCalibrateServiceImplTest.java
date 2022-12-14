package com.jd.bluedragon.distribution.jy.calibrate;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateRequest;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateTaskResult;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.calibrate.JyWeightVolumeCalibrateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

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
            request.setMachineCode("WZ-HJ-JZBL-006");
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
    }

    @Test
    public void closeMachineCalibrateTask() {
    }

    @Test
    public void dealCalibrateTask() {
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