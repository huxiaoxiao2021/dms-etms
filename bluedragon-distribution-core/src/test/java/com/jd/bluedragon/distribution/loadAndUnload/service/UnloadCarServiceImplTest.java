package com.jd.bluedragon.distribution.loadAndUnload.service;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.unloadCar.TaskHelpersReq;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskReq;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDistribution;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadCarDistributionDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.impl.UnloadCarServiceImpl;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.etms.vos.ws.VosQueryWS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2020/6/29 20:06
 */
@RunWith(MockitoJUnitRunner.class)
public class UnloadCarServiceImplTest {

    @InjectMocks
    private UnloadCarServiceImpl unloadCarService;

    @Mock
    private UnloadCarDao unloadCarDao;

    @Mock
    private SendDatailDao sendDatailDao;

    @Mock
    private UnloadCarDistributionDao unloadCarDistributionDao;

    @Mock
    private VosQueryWS vosQueryWS;

    private User user;

    private CurrentOperate currentOperate;

    private List<UnloadCar> unloadCars;

    private List<UnloadCarDistribution> unloadCarDistributions;

    private List<String> sealCarCodes;

    @Before
    public void before(){

        user = new User();
        user.setUserErp("bjxings");
        user.setUserName("邢松");

        currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        currentOperate.setSiteName("马驹桥分拣中心");

        UnloadCar unloadCar = new UnloadCar();
        unloadCar.setUnloadCarId(11111l);
        unloadCar.setSealCarCode("SC12345670");
        unloadCar.setStartSiteCode(910);
        unloadCar.setStartSiteName("马驹桥分拣中心");
        unloadCar.setEndSiteCode(364605);
        unloadCar.setEndSiteName("通州分拣中心");
        unloadCar.setSealTime(new Date());
        unloadCar.setSealCode("SC12345678");
        unloadCar.setBatchCode("121212-131313-123456789");
        unloadCar.setRailWayPlatForm("月-ANWS");
        unloadCar.setWaybillNum(123);
        unloadCar.setPackageNum(234);
        unloadCar.setUnloadUserErp("bjxings");
        unloadCar.setUnloadUserName("邢松");
        unloadCar.setDistributeTime(new Date());
        unloadCar.setCreateTime(new Date());
        unloadCar.setStatus(1);
        unloadCar.setYn(1);
        unloadCars = new ArrayList<>();
        unloadCars.add(unloadCar);

        UnloadCarDistribution unloadCarDistribution = new UnloadCarDistribution();
        unloadCarDistribution.setUnloadDistributeId(1111l);
        unloadCarDistribution.setSealCarCode("SC12345678");
        unloadCarDistribution.setUnloadUserErp("bjxings");
        unloadCarDistribution.setUnloadUserName("邢松");
        unloadCarDistribution.setUnloadUserType(1);
        unloadCarDistribution.setYn(1);
        unloadCarDistributions = new ArrayList<>();
        unloadCarDistributions.add(unloadCarDistribution);

        sealCarCodes = new ArrayList<>();
        sealCarCodes.add("SC12345670");
        sealCarCodes.add("SC12345678");
        sealCarCodes.add("SC12345671");


        when(unloadCarDao.getUnloadCarTaskByParams(ArgumentMatchers.<String, Object>anyMap())).thenReturn(unloadCars);
        when(unloadCarDao.updateUnloadCarTaskStatus(ArgumentMatchers.<String, Object>anyMap())).thenReturn(1);
        when(unloadCarDistributionDao.selectUnloadCarTaskHelpers(anyString())).thenReturn(unloadCarDistributions);
        when(unloadCarDistributionDao.deleteUnloadCarTaskHelpers(ArgumentMatchers.<String, Object>anyMap())).thenReturn(true);
        when(unloadCarDistributionDao.add(any(UnloadCarDistribution.class))).thenReturn(1);
        when(unloadCarDistributionDao.selectTasksByUser(anyString())).thenReturn(sealCarCodes);
        when(unloadCarDao.getUnloadCarTaskScan(ArgumentMatchers.<String>anyList())).thenReturn(unloadCars);
    }

    @Test
    public void testGetUnloadCarTask() {
        UnloadCarTaskReq unloadCarTaskReq = new UnloadCarTaskReq();
        unloadCarTaskReq.setUser(user);
        unloadCarTaskReq.setCurrentOperate(currentOperate);
        unloadCarService.getUnloadCarTask(unloadCarTaskReq);
    }

    @Test
    public void testUpdateUnloadCarTaskStatus() {
        UnloadCarTaskReq unloadCarTaskReq = new UnloadCarTaskReq();
        unloadCarTaskReq.setUser(user);
        unloadCarTaskReq.setCurrentOperate(currentOperate);
        unloadCarTaskReq.setTaskCode("SC12345678");
        unloadCarTaskReq.setOperateTime("2020-06-29 12-12-12");
        unloadCarTaskReq.setTaskStatus(1);
        unloadCarService.updateUnloadCarTaskStatus(unloadCarTaskReq);
    }

    @Test
    public void testGetUnloadCarTaskHelpers() {
        String sealCarCode = "SC12345678";
        unloadCarService.getUnloadCarTaskHelpers(sealCarCode);
    }

    @Test
    public void testUpdateUnloadCarTaskHelpers() {
        TaskHelpersReq taskHelpersReq = new TaskHelpersReq();
        taskHelpersReq.setUser(user);
        taskHelpersReq.setCurrentOperate(currentOperate);
        taskHelpersReq.setTaskCode("SC12345678");
        taskHelpersReq.setHelperERP("lijie357");
        taskHelpersReq.setHelperName("李杰");
        taskHelpersReq.setOperateTime("2020-06-29 12-12-12");
        taskHelpersReq.setOperateType(1);
        unloadCarService.updateUnloadCarTaskHelpers(taskHelpersReq);
    }
    @Test
    public void testGetUnloadCarTaskScan() {
        TaskHelpersReq taskHelpersReq = new TaskHelpersReq();
        taskHelpersReq.setUser(user);
        taskHelpersReq.setCurrentOperate(currentOperate);
        unloadCarService.getUnloadCarTaskScan(taskHelpersReq);
    }
}