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
                "JDX000234615130-1-5-",
                "JDX000234615130-2-5-",
                "JDX000234615130-3-5-",
                "JDX000234615130-4-5-",
                "JDX000234615130-5-5-");

        for(int i = 0; i < pcList.size(); i++) {


            try{
                String json1 = "{\n" +
                        "    \"bizId\": \"XCZJ23040400000023\",\n" +
                        "    \"currentOperate\": {\n" +
                        "        \"groupCode\": \"G00000052005\",\n" +
                        "        \"operateTime\": 1680600969956,\n" +
                        "        \"positionCode\": \"GW00108005\",\n" +
                        "        \"siteCode\": 10186,\n" +
                        "        \"siteName\": \"北京凉水河快运中心\"\n" +
                        "    },\n" +
                        "    \"goodNumber\": 5,\n" +
                        "    \"manualCreateTaskFlag\": true,\n" +
                        "    \"oldSiteId\": 39,\n" +
                        "    \"scanCode\": \"JDVA00257543655-3-5-\",\n" +
                        "    \"scanCodeType\": 101,\n" +
                        "    \"user\": {\n" +
                        "        \"userCode\": 18225,\n" +
                        "        \"userErp\": \"xumigen\",\n" +
                        "        \"userName\": \"徐迷根\"\n" +
                        "    }\n" +
                        "}";

                UnloadScanCollectDealDto param1 = JSONObject.parseObject(json1, UnloadScanCollectDealDto.class);
//                param1.setScanCode(pcList.get(i));
                param1.setNextSiteId(38);
                String json2 = "{\n" +
                        "        \"barCode\": \"JDVA00257543655-3-5-\",\n" +
                        "        \"bizId\": \"XCZJ23040400000023\",\n" +
                        "        \"collectDemoteSwitch\": false,\n" +
                        "        \"confirmMsg\": {},\n" +
                        "        \"endSiteId\": 39,\n" +
                        "        \"endSiteName\": \"石景山营业部\",\n" +
                        "        \"firstScan\": false,\n" +
                        "        \"prevSiteId\": 910,\n" +
                        "        \"prevSiteName\": \"北京马驹桥分拣中心\",\n" +
                        "        \"stageFirstScan\": false,\n" +
                        "        \"supplementary\": false,\n" +
                        "        \"warnMsg\": {}\n" +
                        "    }";
                ScanPackageRespDto scanPackageRespDto = JSONObject.parseObject(json2, ScanPackageRespDto.class);
//                scanPackageRespDto.setBarCode(pcList.get(i));
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
                String json  = "{\n" +
                        "    \"bizId\": \"XCZJ23040400000021\",\n" +
                        "    \"collectType\": 101,\n" +
                        "    \"currentOperate\": {\n" +
                        "        \"dmsCode\": \"010F002\",\n" +
                        "        \"groupCode\": \"G00000052005\",\n" +
                        "        \"operateTime\": 1680577144440,\n" +
                        "        \"orgId\": 6,\n" +
                        "        \"orgName\": \"华北\",\n" +
                        "        \"positionCode\": \"GW00108005\",\n" +
                        "        \"siteCode\": 10186,\n" +
                        "        \"siteName\": \"北京凉水河快运中心\"\n" +
                        "    },\n" +
                        "    \"manualCreateTaskFlag\": true,\n" +
                        "    \"pageNo\": 1,\n" +
                        "    \"pageSize\": 30,\n" +
                        "    \"user\": {\n" +
                        "        \"userCode\": 17331,\n" +
                        "        \"userErp\": \"wuyoude\",\n" +
                        "        \"userName\": \"吴有德\"\n" +
                        "    }\n" +
                        "}";
                CollectReportReqDto param2 = JSONObject.parseObject(json, CollectReportReqDto.class);
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
                String json = "{\n" +
                        "    \"bizId\": \"SC23040200030108\",\n" +
                        "    \"collectType\": 101,\n" +
                        "    \"collectionCode\": \"JQ23033122360300032\",\n" +
                        "    \"currentOperate\": {\n" +
                        "        \"dmsCode\": \"010F002\",\n" +
                        "        \"groupCode\": \"G00000052005\",\n" +
                        "        \"operateTime\": 1680437708513,\n" +
                        "        \"orgId\": 6,\n" +
                        "        \"orgName\": \"华北\",\n" +
                        "        \"positionCode\": \"GW00108005\",\n" +
                        "        \"siteCode\": 10186,\n" +
                        "        \"siteName\": \"北京凉水河快运中心\"\n" +
                        "    },\n" +
                        "    \"manualCreateTaskFlag\": false,\n" +
                        "    \"pageNo\": 3,\n" +
                        "    \"pageSize\": 30,\n" +
                        "    \"user\": {\n" +
                        "        \"userCode\": 17331,\n" +
                        "        \"userErp\": \"wuyoude\",\n" +
                        "        \"userName\": \"吴有德\"\n" +
                        "    },\n" +
                        "    \"waybillCode\": \"JD0003419580301\"\n" +
                        "}";
                CollectReportReqDto param1 = JSONObject.parseObject(json, CollectReportReqDto.class);
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
