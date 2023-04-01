package ld;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.User;
import com.jd.bluedragon.distribution.jy.dto.collect.*;
import com.jd.bluedragon.distribution.jy.dto.unload.CollectStatisticsQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanCollectStatisticsDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanPackageRespDto;
import com.jd.bluedragon.distribution.jy.enums.ScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectCacheService;
import com.jd.bluedragon.distribution.jy.service.collect.constant.CollectCacheConstant;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadVehicleCheckTysService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/12
 * @Description:
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyTysUnloadTest {

    @Autowired
    private JyUnloadVehicleCheckTysService jyUnloadVehicleCheckTysService;
    @Autowired
    private JyUnloadVehicleTysService jyUnloadVehicleTysService;
    @Autowired
    @Qualifier(value = "jyCollectDataInitSplitProducer")
    private DefaultJMQProducer jyCollectDataInitSplitProducer;
    @Autowired
    private JyCollectCacheService jyCollectCacheService;
    @Resource
    private Cluster redisClientCache;

    @Test
    public void test(){
        while (true) {
            System.out.println(System.currentTimeMillis());
        }
    }

    @Test
    public void collectDealTest(){
        List<String> pcList = Arrays.asList(
                "JDX000234596031-1-5-",
                "JDX000234596040-1-5-",
                "JDX000234596059-1-5-",
                "JDX000234596067-1-5-",
                "JDX000234596076-1-5-",
                "JDX000234596085-1-5-",
                "JDX000234596093-1-5-",
                "JDX000234596104-1-5-",
                "JDX000234596112-1-5-",
                "JDX000234596121-1-5-");

        for(int i = 0; i < pcList.size(); i++) {


            try{
                String json1 = "{\n" +
                        "    \"bizId\": \"XCZJ23040100000001\",\n" +
                        "    \"currentOperate\": {\n" +
                        "        \"groupCode\": \"G00000052005\",\n" +
                        "        \"operateTime\": 1680331698368,\n" +
                        "        \"positionCode\": \"GW00108005\",\n" +
                        "        \"siteCode\": 10186,\n" +
                        "        \"siteName\": \"北京凉水河快运中心\"\n" +
                        "    },\n" +
                        "    \"goodNumber\": 5,\n" +
                        "    \"manualCreateTaskFlag\": true,\n" +
                        "    \"oldSiteId\": 39,\n" +
                        "    \"scanCode\": \"JDX000234586760-1-5-\",\n" +
                        "    \"scanCodeType\": 101,\n" +
                        "    \"user\": {\n" +
                        "        \"userCode\": 18225,\n" +
                        "        \"userErp\": \"xumigen\",\n" +
                        "        \"userName\": \"徐迷根\"\n" +
                        "    }\n" +
                        "}";

                UnloadScanCollectDealDto param1 = JSONObject.parseObject(json1, UnloadScanCollectDealDto.class);
                param1.setScanCode(pcList.get(i));

                String json2 = "{\n" +
                        "        \"barCode\": \"JDX000234586760-1-5-\",\n" +
                        "        \"bizId\": \"XCZJ23040100000001\",\n" +
                        "        \"collectDemoteSwitch\": false,\n" +
                        "        \"confirmMsg\": {},\n" +
                        "        \"firstScan\": false,\n" +
                        "        \"prevSiteId\": 910,\n" +
                        "        \"prevSiteName\": \"北京马驹桥分拣中心\",\n" +
                        "        \"stageFirstScan\": false,\n" +
                        "        \"supplementary\": false,\n" +
                        "        \"warnMsg\": {}\n" +
                        "    }";
                ScanPackageRespDto scanPackageRespDto = JSONObject.parseObject(json2, ScanPackageRespDto.class);
                scanPackageRespDto.setBarCode(pcList.get(i));
                InvokeResult<ScanPackageRespDto> param2 = new InvokeResult<>();
                param2.success();
                param2.setData(scanPackageRespDto);

                jyUnloadVehicleCheckTysService.collectDeal(param1, param2);
                log.info(JsonHelper.toJson(param2));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void queryCollectStatisticsByDiffDimensionTest(){
        while(true) {
            try {
                CollectStatisticsQueryDto param = new CollectStatisticsQueryDto();
                param.setBizId("SC23032400029409");
                User user = new User();
                user.setUserErp("xumigen");
                param.setUser(user);
                CurrentOperate currentOperate = new CurrentOperate();
                currentOperate.setSiteCode(10186);
                param.setCurrentOperate(currentOperate);
                InvokeResult<ScanCollectStatisticsDto> res = jyUnloadVehicleTysService.queryCollectStatisticsByDiffDimension(param);
                System.out.println(JsonHelper.toJson(res));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void findCollectReportPageTest(){
        while(true) {
            try {
                CollectReportReqDto param1 = new CollectReportReqDto();
                param1.setPageNo(1);
                param1.setPageSize(100);
                param1.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
                param1.setBizId("XCZJ23031600000013");

                User user = new User();
                user.setUserErp("xumigen");
                param1.setUser(user);
                CurrentOperate currentOperate = new CurrentOperate();
                currentOperate.setSiteCode(10186);
                param1.setCurrentOperate(currentOperate);

                String jsonSite = "{\n" +
                        "            \"dmsCode\": \"010K001\",\n" +
                        "            \"groupCode\": \"G00000052005\",\n" +
                        "            \"operateTime\": 1679365075203,\n" +
                        "            \"orgId\": 6,\n" +
                        "            \"orgName\": \"华北\",\n" +
                        "            \"positionCode\": \"GW00108005\",\n" +
                        "            \"siteCode\": 10186,\n" +
                        "            \"siteName\": \"北京凉水河快运中心\"\n" +
                        "        }";
                CurrentOperate siteInfo = JSONObject.parseObject(jsonSite, CurrentOperate.class);


                String jsonUser = "{\n" +
                        "            \"userCode\": 18225,\n" +
                        "            \"userErp\": \"xumigen\",\n" +
                        "            \"userName\": \"徐迷根\"\n" +
                        "        }";
                User userInfo = JSONObject.parseObject(jsonUser, User.class);

                String json = "{\"bizId\":\"XCZJ23032900000049\",\"combinationTransfer\":false,\"currentOperate\":{\"dmsCode\":\"010F002\",\"groupCode\":\"G00000048001\",\"operateTime\":1680075658515,\"orgId\":6,\"orgName\":\"华北\",\"positionCode\":\"GW00002001\",\"siteCode\":10186,\"siteName\":\"北京凉水河快运中心\"},\"forceCombination\":false,\"groupCode\":\"G00000048001\",\"isForceCombination\":false,\"scanCode\":\"JDX000234142433-69-1000-\",\"taskId\":\"230329200000082\",\"type\":0,\"user\":{\"userCode\":17331,\"userErp\":\"wuyoude\",\"userName\":\"吴有德\"},\"vehicleNumber\":\"晋CDGGGDG\",\"workType\":0}";
                CollectReportReqDto param2 = JSONObject.parseObject(json, CollectReportReqDto.class);
//                param2.setUser(user);
//                param2.setCurrentOperate(currentOperate);
                InvokeResult<CollectReportResDto>  res = jyUnloadVehicleTysService.findCollectReportPage(param2);
                System.out.println("end");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void findCollectReportDetailPageTest(){
        while(true) {
            try {
                CollectReportReqDto param1 = new CollectReportReqDto();
                param1.setPageNo(1);
                param1.setPageSize(100);
                param1.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
                param1.setBizId("SC23032900029737");
                param1.setWaybillCode("JD0003419552536");
                param1.setCollectionCode("JQ23032919450000032");

                User user = new User();
                user.setUserErp("xumigen");
                param1.setUser(user);
                CurrentOperate currentOperate = new CurrentOperate();
                currentOperate.setSiteCode(10186);
                param1.setCurrentOperate(currentOperate);
                InvokeResult<CollectReportDetailResDto> res = jyUnloadVehicleTysService.findCollectReportDetailPage(param1);
                System.out.println("end");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void findCollectReportByScanCodeTest(){
        while(true) {
            try {
                String json = "";
                CollectReportQueryParamReqDto param1 = new CollectReportQueryParamReqDto();
                param1.setCollectType(CollectTypeEnum.WAYBILL_BUQI.getCode());
                param1.setBizId("SC23032400029409");
                param1.setScanCode("JD0003419520704-1-5-");

                User user = new User();
                user.setUserErp("xumigen");
                param1.setUser(user);
                CurrentOperate currentOperate = new CurrentOperate();
                currentOperate.setSiteCode(10186);
                param1.setCurrentOperate(currentOperate);
                InvokeResult<CollectReportResDto>  res = jyUnloadVehicleTysService.findCollectReportByScanCode(param1);
                System.out.println("end");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testSendJmq(){
        while(true) {
            try {
                InitCollectDto initCollectDto = new InitCollectDto();
                initCollectDto.setBizId("SC23032000029149");
                initCollectDto.setOperateTime(System.currentTimeMillis());
                initCollectDto.setOperateNode(CollectInitNodeEnum.SEAL_INIT.getCode());
                String msgText = JsonHelper.toJson(initCollectDto);
                StringBuilder sb = new StringBuilder();
                sb.append(CollectCacheConstant.CACHE_SEAL_COLLECT_SPLIT_BEFORE_INIT)
                        .append(initCollectDto.getBizId());
                redisClientCache.del(sb.toString());

                StringBuilder sb1 = new StringBuilder();
                sb1.append(CollectCacheConstant.CACHE_SEAL_COLLECT_INIT_AFTER_SPLIT)
                        .append("SC23032000029149")
                        .append(Constants.SEPARATOR_COLON)
                        .append("40240-10186-20230320043403193")
                        .append(Constants.SEPARATOR_COLON)
                        .append(1)
                        .append(Constants.SEPARATOR_COLON)
                        .append(100);
                redisClientCache.del(sb.toString());
//                jyCollectCacheService.cacheSaveSealCarCollectSplitBeforeInit(initCollectDto);
//                if (jyCollectCacheService.cacheExistSealCarCollectSplitBeforeInit(initCollectDto)) {
//                    log.info("end");
//                }
                jyCollectDataInitSplitProducer.sendOnFailPersistent(initCollectDto.getBizId(),msgText);
                System.out.println("end");

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
