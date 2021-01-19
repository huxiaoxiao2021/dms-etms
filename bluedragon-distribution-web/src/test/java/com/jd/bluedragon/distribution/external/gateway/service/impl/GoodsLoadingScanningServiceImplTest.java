package com.jd.bluedragon.distribution.external.gateway.service.impl;

        import com.jd.bluedragon.Constants;
        import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
        import com.jd.bluedragon.common.dto.base.request.User;
        import com.jd.bluedragon.common.dto.base.response.JdCResponse;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsExceptionScanningReq;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadDeleteReq;
        import com.jd.bluedragon.common.dto.goodsLoadingScanning.response.GoodsExceptionScanningDto;
        import com.jd.bluedragon.common.dto.spotcheck.SpotCheckCheckReq;
        import com.jd.bluedragon.common.dto.spotcheck.SpotCheckSubmitReq;
        import com.jd.bluedragon.common.dto.unloadCar.CreateUnloadTaskReq;
        import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
        import com.jd.bluedragon.distribution.goodsLoadScan.dao.GoodsLoadScanRecordDao;
        import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
        import com.jd.bluedragon.distribution.goodsLoadScan.service.DmsDisSendService;
        import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
        import com.jd.bluedragon.distribution.goodsLoadScan.service.impl.LoadScanServiceImpl;
        import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
        import com.jd.bluedragon.external.gateway.service.GoodsLoadScanGatewayService;
        import com.jd.bluedragon.external.gateway.service.LoadAndUnloadCarGatewayService;
        import com.jd.bluedragon.external.gateway.service.LoadCarTaskGateWayService;
        import com.jd.bluedragon.utils.JsonHelper;
        import com.jd.ql.dms.report.domain.LoadScanDto;
        import org.apache.commons.collections4.ListUtils;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.springframework.test.context.ContextConfiguration;
        import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
        import javax.annotation.Resource;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Map;
        import com.jd.bluedragon.external.gateway.service.SpotCheckGateWayService;
        import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;

/**
 * 装车发货相关功能测试
 *
 * @Author zhengchengfa
 * @Date 2020年10月22日
 */
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
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

    @Resource
    private LoadScanServiceImpl loadScanService;

    @Resource
    private DmsDisSendService dmsDisSendService;
    @Resource
    private SpotCheckGateWayService spotCheckGateWayService;

    @Resource
    private LoadAndUnloadCarGatewayService loadAndUnloadCarGatewayService;
    @Resource
    private WeightAndVolumeCheckService weightAndVolumeCheckService;


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
        for (int i = 0; i < 3; i++) {
            res = goodsLoadingScanningService.findExceptionGoodsLoading(param);
        }
        System.out.println(res.getCode() + "----" + res.getMessage());
        for (GoodsExceptionScanningDto r : res.getData()) {
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

        boolean res = goodsLoadScanRecordDao.batchInsert(list);
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
    public void testLicenseNumber() {
        String licenseNumber = "010A01077";
        loadCarTaskGateWayService.checkLicenseNumber(licenseNumber);
    }

    @Test
    public void testCreateTask() {
        LoadCarTaskCreateReq loadCarTaskCreateReq = new LoadCarTaskCreateReq();
        loadCarTaskCreateReq.setCreateSiteCode(910L);
        loadCarTaskCreateReq.setEndSiteCode(698L);
        loadCarTaskCreateReq.setEndSiteName("杭州配送中心");
        loadCarTaskCreateReq.setCreateSiteName("北京马驹桥分拣中心");
        loadCarTaskCreateReq.setEndSiteName("杭州配送中心");
        loadCarTaskCreateReq.setCreateUserErp("ssssssss");
        loadCarTaskCreateReq.setCreateUserName("焦文强");
        loadCarTaskCreateReq.setLicenseNumber("藏A2637B");

        loadCarTaskGateWayService.loadCarTaskCreate(loadCarTaskCreateReq);

    }

    @Test
    public void testUpdateTaskStatus() {
        LoadCar loadCar = new LoadCar();
        loadCar.setId(150L);
        User user = new User();
        user.setUserName("管理员");
        user.setUserCode(100001);
        loadScanService.updateTaskStatus(loadCar, user);
    }

    @Test
    public void testGetLoadScanByWaybillAndPackageCode() {
        LoadScanDto loadScanDto = new LoadScanDto();
        loadScanDto.setWayBillCode("JDV000488510739");
        loadScanDto.setPackageCode("JDV000488510739-4-5-");
        loadScanDto.setCreateSiteId(364605);
        LoadScanDto scanDto = dmsDisSendService.getLoadScanByWaybillAndPackageCode(loadScanDto);
        System.out.println(JsonHelper.toJson(scanDto));
    }


    @Test
    public void testFindRecordsByWaybillCode() {
        Map<String, GoodsLoadScanRecord> map = goodsLoadScanRecordDao.findRecordsByWaybillCode(910L,
                "JDV000488800736");

        List<String> list = new ArrayList<>();

        list.add("JDV000488800736-6-5-");
        list.add("JDV000488800736-7-5-");
        list.add("JDV000488800736-8-5-");
        list.add("JDV000488800736-9-5-");
        list.add("JDV000488800736-10-5-");


        GoodsLoadScanRecord loadScanRecord = new GoodsLoadScanRecord();
        loadScanRecord.setTaskId(169L);
        loadScanRecord.setWayBillCode("JDV000488800736");
        // 装车动作
        loadScanRecord.setScanAction(GoodsLoadScanConstants.GOODS_SCAN_LOAD);
        // 包裹号转板号标识
        loadScanRecord.setTransfer(2);
        // 多扫标识
        loadScanRecord.setFlowDisaccord(0);
        // 强发标识
        loadScanRecord.setForceStatus(GoodsLoadScanConstants.GOODS_LOAD_SCAN_FORCE_STATUS_N);

        loadScanRecord.setCreateUserCode(10053);
        loadScanRecord.setCreateUserName("刑松");
        loadScanRecord.setUpdateUserCode(10053);
        loadScanRecord.setUpdateUserName("刑松");

        loadScanRecord.setCreateTime(new Date());
        loadScanRecord.setUpdateTime(new Date());
        loadScanRecord.setYn(Constants.YN_YES);


        //2020 10-23为了装车名词报表增加始发和目的场地id以及各自名称
        loadScanRecord.setCreateSiteCode(364605L);
        loadScanRecord.setCreateSiteName("北京通州分拣中心");
        loadScanRecord.setEndSiteCode(910L);
        loadScanRecord.setEndSiteName("北京马驹桥分拣中心");
        loadScanRecord.setLicenseNumber("京A12345");

        List<List<String>> subPackageCodes = ListUtils.partition(list, 2);
        // 分批批量插入，每次1000个
        for (List<String> packageCodes : subPackageCodes) {
            loadScanRecord.setPackageCodeList(packageCodes);
            goodsLoadScanRecordDao.batchInsertByWaybill(loadScanRecord);
        }

        List<Long> idList = new ArrayList<>();
        idList.add(86170L);
        idList.add(86171L);
        idList.add(86172L);
        loadScanRecord.setIdList(idList);
        int a = goodsLoadScanRecordDao.batchUpdateGoodsScanRecordByIds(loadScanRecord);

        System.out.println(JsonHelper.toJson(map));
    }
    @Test
    public void testSpotCheckTest() {
        SpotCheckCheckReq req = new SpotCheckCheckReq();
        req.setWaybillCode("JDK000000055176");
        req.setLoginErp("wuyoude");
        req.setCreateSiteCode(364605);
        req.setWeight(10D);
        req.setVolume(10D);
        spotCheckGateWayService.checkIsExcess(req);
    }

    @Test
    public void testSpotCheckSubmit() {
        SpotCheckSubmitReq req = new SpotCheckSubmitReq();
        req.setWaybillCode("JDK000000055167");
        req.setLoginErp("bjxings");
        req.setCreateSiteCode(910);
        req.setWeight(5D);
        req.setVolume(5D);
        req.setSource(1);
        List<String>list=new ArrayList<>();
        list.add("http://test.storage.jd.com/dms-feedback/31babd13-ebea-478b-824e-b93a28af3bae.png?Expires=1925111176&AccessKey=a7ogJNbj3Ee9YM1O&Signature=g77Mk2WV0R%2BOqVXcB%2BPoSXhAA58%3D");
        list.add("http://test.storage.jd.com/dms-feedback/31babd13-ebea-478b-824e-b93a28af3bae.png?Expires=1925111176&AccessKey=a7ogJNbj3Ee9YM1O&Signature=g77Mk2WV0R%2BOqVXcB%2BPoSXhAA58%3D");
        list.add("http://test.storage.jd.com/dms-feedback/31babd13-ebea-478b-824e-b93a28af3bae.png?Expires=1925111176&AccessKey=a7ogJNbj3Ee9YM1O&Signature=g77Mk2WV0R%2BOqVXcB%2BPoSXhAA58%3D");
        spotCheckGateWayService.spotCheckSubmit(req);
    }


        weightAndVolumeCheckService.searchPicture("JDK000000055832",364605,1,"SPOT_CHECK_ANDROID");
    }

    @Test
    public void testCreateUnloadTask(){
        CreateUnloadTaskReq req=new CreateUnloadTaskReq();
        req.setCreateSiteCode(910L);
        req.setOperateUserErp("bjxingsong");
        req.setVehicleNumber("京A00001");
        req.setOperateUserName("刑松");
        loadAndUnloadCarGatewayService.createUnloadTask(req);
    }
    @Test
    public void testSpotCheckPictures(){

        weightAndVolumeCheckService.searchPicture("JDK000000055832",364605,1,"SPOT_CHECK_ANDROID");
    }
}
