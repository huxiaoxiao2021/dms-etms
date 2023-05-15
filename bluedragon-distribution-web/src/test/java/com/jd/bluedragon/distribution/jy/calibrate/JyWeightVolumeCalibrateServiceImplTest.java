package com.jd.bluedragon.distribution.jy.calibrate;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetailResult;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateRequest;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateTaskResult;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateTypeEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import com.jd.bluedragon.distribution.jy.dto.comboard.BoardCountReq;
import com.jd.bluedragon.distribution.jy.service.calibrate.JyWeightVolumeCalibrateService;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@ContextConfiguration(locations = "classpath:spring/distribution-web-context.xml")
@Slf4j
public class JyWeightVolumeCalibrateServiceImplTest {

    @Autowired
    private JyWeightVolumeCalibrateService jyWeightVolumeCalibrateService;

    @Autowired
    private SpotCheckDealService spotCheckDealService;
    
    @Test
    public void executeIssueOfCompensate() {
        try {
            spotCheckDealService.executeIssueOfCompensate("抽检异常数据结果.xlsx");
            Assert.assertTrue(true);
        }catch (Exception e){
            log.error("服务异常!", e);
            Assert.fail();
        }
    }
    
    

    @Test
    public void machineCalibrateScan() {
        try {
            DwsWeightVolumeCalibrateRequest request = new DwsWeightVolumeCalibrateRequest();
            request.setMachineCode("BJL-FF-SORT-002");
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
            request.setCalibrateTaskStartTime(sdf.parse("2022-12-26 11:06:15").getTime());
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
            mq1.setCalibrateStatus(1);

            DwsMachineCalibrateMQ mq2 = new DwsMachineCalibrateMQ();
            mq2.setMachineCode("WZ-HJ-JZBL-007");
            mq2.setCalibrateTime(new Date().getTime());
            mq2.setCalibrateType(JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_V.getCode());
            mq2.setCalibrateStatus(1);
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
    
    @Autowired
    private JyBizTaskComboardService jyBizTaskComboardService;

    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;

    @Test
    public void cacheMethodTest() {

        try {
            BoardCountReq boardCountReq = new BoardCountReq();
            boardCountReq.setStartSiteId(910L);
            boardCountReq.setEndSiteIdList(Lists.newArrayList(23,39,630105,1437));
            boardCountReq.setCreateTime(new Date(1672502400000L));
            boardCountReq.setStatusList(Lists.newArrayList(1,2,3,4));
            boardCountReq.setComboardSourceList(Lists.newArrayList(1,2));
            boardCountReq.setTemplateCode("templateCode1");
            long start = System.currentTimeMillis();
            jyBizTaskComboardService.boardCountTaskBySendFlowListWithCache(boardCountReq);
            System.out.println("无缓存耗时：" + (System.currentTimeMillis() - start));
            start = System.currentTimeMillis();
            jyBizTaskComboardService.boardCountTaskBySendFlowListWithCache(boardCountReq);
            System.out.println("有缓存耗时：" + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            jyBizTaskComboardService.boardCountTaskBySendFlowListWithCache(boardCountReq);
            System.out.println("有缓存耗时：" + (System.currentTimeMillis() - start));

            start = System.currentTimeMillis();
            jyBizTaskComboardService.boardCountTaskBySendFlowListWithCache(boardCountReq);
            System.out.println("有缓存耗时：" + (System.currentTimeMillis() - start));
            
            String s = redisClientCache.get("JyBizTaskComboardServiceImpl.boardCountTaskBySendFlowListWithCache-templateCode1");

            jyBizTaskComboardService.removeBoardCountCache("templateCode1");
            
            start = System.currentTimeMillis();
            jyBizTaskComboardService.boardCountTaskBySendFlowListWithCache(boardCountReq);
            System.out.println("无缓存耗时：" + (System.currentTimeMillis() - start));
            boardCountReq.setTemplateCode("TemplateCode123");
            start = System.currentTimeMillis();
            jyBizTaskComboardService.boardCountTaskBySendFlowListWithCache(boardCountReq);
            System.out.println("无缓存耗时：" + (System.currentTimeMillis() - start));
            boardCountReq.setTemplateCode("TemplateCode321");
            start = System.currentTimeMillis();
            jyBizTaskComboardService.boardCountTaskBySendFlowListWithCache(boardCountReq);
            System.out.println("无缓存耗时：" + (System.currentTimeMillis() - start));

            

            jyBizTaskComboardService.boardCountTaskBySendFlowListWithCache(boardCountReq);

            s = redisClientCache.get("JyBizTaskComboardServiceImpl.boardCountTaskBySendFlowListWithCache-templateCode1");

            Assert.assertTrue(true);
        }catch (Exception e){
            log.error("服务异常!", e);
            Assert.fail();
        }
    }
}