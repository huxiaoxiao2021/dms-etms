package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarStatusEnum;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScan;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadScanRecord;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanDao;
import com.jd.bluedragon.distribution.loadAndUnload.dao.UnloadScanRecordDao;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;


/**
 * 卸车相关功能测试
 * @author lvyuan21
 * @date 2020-12-29 16:14
 */
@ContextConfiguration( locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class UnloadCarServiceImplTest {

    @Resource
    private UnloadCarService unloadCarService;

    @Resource
    private UnloadScanDao unloadScanDao;
    @Resource
    private UnloadScanRecordDao unloadScanRecordDao;



    @Test
    public void testStartUnloadTask() {
        UnloadCarTaskReq req = new UnloadCarTaskReq();
        req.setTaskCode("SC20050800003129");
        req.setTaskStatus(UnloadCarStatusEnum.UNLOAD_CAR_STARTED.getType());
        User user = new User();
        user.setUserName("吴有德");
        user.setUserErp("wuyoude");
        req.setUser(user);
        req.setOperateTime("2020-12-29");
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(364605);
        req.setCurrentOperate(currentOperate);
        InvokeResult<Void> result = unloadCarService.startUnloadTask(req);
        System.out.println(result);

    }

    @Test
    public void testFindUnloadScan() {
        List<UnloadScan> unloadScan = unloadScanDao.findUnloadScanByBySealCarCode("SC20050800003129");
        System.out.println(unloadScan);
    }

    @Test
    public void testFindUnloadScanRecord() {
        List<UnloadScanRecord> unloadScan = unloadScanRecordDao.findRecordBySealCarCode("SC20050800003129");
        System.out.println(unloadScan);
    }

}
