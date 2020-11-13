package com.jd.bluedragon.distribution.external.gateway.service.impl;

        import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
        import com.jd.bluedragon.common.dto.base.request.User;
        import com.jd.bluedragon.common.dto.base.response.JdCResponse;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
        import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
        import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
        import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
        import com.jd.bluedragon.external.gateway.service.GoodsLoadScanGatewayService;
        import com.jd.bluedragon.external.gateway.service.LoadCarTaskGateWayService;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.springframework.test.context.ContextConfiguration;
        import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

        import javax.annotation.Resource;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;

/**
 * 装车发货相关功能测试
 * @Author zhengchengfa
 * @Date 2020年10月22日
 */
@ContextConfiguration( locations = {"classpath:distribution-web-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class GoodsLoadingScanningServiceImplTest {

    @Resource
    private GoodsLoadScanGatewayService goodsLoadingScanningService;

    @Resource
    private GoodsLoadScanRecordDao goodsLoadScanRecordDao;

    @Resource
    private LoadCarTaskGateWayService loadCarTaskGateWayService;


    @Resource
    private LoadScanCacheService loadScanCacheService;




    @Test //不齐异常数据查询测试
    public void testFindExceptionGoodsLoading() {

        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();
        param.setTaskId(1L);

        User user = new User();
        user.setUserName("admin");
        user.setUserCode(2020001);
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(1241136);
        currentOperate.setSiteName("这是哪里啊");
        param.setCurrentOperate(currentOperate);

        loadScanCacheService.delTaskLoadScan(param.getTaskId());

        JdCResponse<List<GoodsExceptionScanningDto>> res = null;
        for(int i=0; i< 3; i++) {
            res = goodsLoadingScanningService.findExceptionGoodsLoading(param);
        }
        System.out.println(res.getCode() + "----" + res.getMessage());
        for(GoodsExceptionScanningDto r : res.getData()) {
            System.out.println(r.getTaskId());
            System.out.println("运单：" + r.getWaybillCode());
            System.out.println("已装：" + r.getLoadAmount());
            System.out.println("未装：" + r.getUnloadAmount());
        }

        System.out.println("------------------");
    }

    @Test //强制下发测试
    public void goodsCompulsoryDeliver() {
        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();

        User user = new User();
        user.setUserName("admin");
        user.setUserCode(2020001);
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(1241136);
        currentOperate.setSiteName("这是哪里啊");
        param.setCurrentOperate(currentOperate);

        param.setTaskId(1L);

        List<String> list = new ArrayList<>();
        list.add("JD0026827277756");
        list.add("JD0026829398865");
        param.setWaybillCode(list);


        JdCResponse res = goodsLoadingScanningService.goodsCompulsoryDeliver(param);
        System.out.println(res.getCode() + "----" + res.getMessage());
    }

    @Test //完成发货测试
    public void testGoodsLoadingDeliver() {
        GoodsLoadingReq param = new GoodsLoadingReq();

        User user = new User();
        user.setUserName("bjxings");
        user.setUserCode(10053);
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        currentOperate.setSiteName("北京马驹桥分拣中心");
        param.setCurrentOperate(currentOperate);

        param.setTaskId(114L);
        param.setSendCode("910-483-20201029015536423");
        param.setReceiveSiteCode(483);

        loadScanCacheService.unLock(param.getTaskId().toString());//测试代码
        loadScanCacheService.delTaskLoadScan(param.getTaskId());
        JdCResponse res = goodsLoadingScanningService.goodsLoadingDeliver(param);

        System.out.println(res.getCode() + "----" + res.getMessage());
    }

    @Test //取消发货测试
    public void testGoodsRemoveScanning() {
        GoodsExceptionScanningReq param = new GoodsExceptionScanningReq();

        User user = new User();
        user.setUserName("admin");
        user.setUserCode(2020001);
        user.setUserErp("bjxings");
        param.setUser(user);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        currentOperate.setSiteName("这是哪里啊");
        param.setCurrentOperate(currentOperate);

        param.setTaskId(95L);
        loadScanCacheService.delTaskLoadScan(param.getTaskId());

        param.setPackageCode("B001");//多个改数
        JdCResponse res = goodsLoadingScanningService.goodsRemoveScanning(param);

//        param.setPackageCode("PA001");//多个改数
//        res = goodsLoadingScanningService.goodsRemoveScanning(param);
//        System.out.println(res.getCode() + "----" + res.getMessage());

        System.out.println("-----------------end-----------------");

    }

    @Test //测试批量插入包裹信息
    public void testBatchInsert() {
        List<GoodsLoadScanRecord> list = new ArrayList<>();

        GoodsLoadScanRecord param = new GoodsLoadScanRecord();

        param.setTaskId(10L);
        param.setWayBillCode("test1");
        param.setEndSiteCode(123L);
        param.setEndSiteName("目的地");
        param.setCreateSiteCode(321L);
        param.setCreateSiteName("出发地");
        param.setPackageCode("afda");
        param.setBoardCode("afsdfa");
        param.setScanAction(-1);
        param.setTransfer(1);
        param.setCreateUserCode(123);
        param.setCreateUserName("zcf");
        param.setUpdateUserName("zcf");
        param.setUpdateUserCode(23);
        param.setUpdateTime(new Date());
        param.setCreateTime(new Date());
        param.setForceStatus(1);
        param.setFlowDisaccord(1);
        list.add(param);

        GoodsLoadScanRecord param1 = new GoodsLoadScanRecord();

        param1.setTaskId(10L);
        param.setWayBillCode("test2");
        param1.setEndSiteCode(123L);
        param1.setEndSiteName("目的地");
        param1.setCreateSiteCode(321L);
        param1.setCreateSiteName("出发地");
        param1.setPackageCode("afda");
        param1.setBoardCode("afsdfa");
        param1.setScanAction(-1);
        param1.setTransfer(1);
        param1.setCreateUserCode(123);
        param1.setCreateUserName("zcf");
        param1.setUpdateUserName("zcf");
        param1.setUpdateUserCode(23);
        param1.setUpdateTime(new Date());
        param1.setCreateTime(new Date());
        param1.setForceStatus(1);
        param1.setFlowDisaccord(1);
        list.add(param1);

        boolean res =  goodsLoadScanRecordDao.batchInsert(list);
        System.out.println(res);

    }

    @Test
    public void testCheckLicenseNumber() {

        JdCResponse<String> res = loadCarTaskGateWayService.checkLicenseNumber("123456789");
        System.out.println(res.getCode() + res.getMessage() + res.getData());

        JdCResponse<String> res1 = loadCarTaskGateWayService.checkLicenseNumber("010PDA123");
        System.out.println(res1.getCode() + res1.getMessage() + res1.getData());
        System.out.println("-------");
    }

    @Test
    public void testDeleteLoadCarTask() {
//        LoadDeleteReq param = new LoadDeleteReq();
//        param.setId(1L);
//        JdCResponse res = loadCarTaskGateWayService.deleteLoadCarTask(param);
//        System.out.println(res.getCode() + "----" + res.getMessage());

        LoadDeleteReq loadDeleteReq = new LoadDeleteReq();
        loadDeleteReq.setId(97L);
        loadDeleteReq.setOperateUserErp("jiaowenqiang");
        loadDeleteReq.setOperateUserName("焦文强");
        JdCResponse res1 = loadCarTaskGateWayService.deleteLoadCarTask(loadDeleteReq);

    }


    @Test
    public void testLicenseNumber(){
    String licenseNumber="010A01077";
    loadCarTaskGateWayService.checkLicenseNumber(licenseNumber);
    }
}
