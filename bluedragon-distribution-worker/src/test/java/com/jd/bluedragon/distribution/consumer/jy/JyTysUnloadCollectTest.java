package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.collect.JyCollectBatchUpdateStatusConsumer;
import com.jd.bluedragon.distribution.consumer.jy.collect.JyCollectDataInitSplitConsumer;
import com.jd.bluedragon.distribution.consumer.jy.collect.JyCollectDataSplitBatchInitConsumer;
import com.jd.bluedragon.distribution.consumer.jy.collect.JyCollectStatusBatchUpdateWaybillSplitConsumer;
import com.jd.bluedragon.distribution.jy.dto.CurrentOperate;
import com.jd.bluedragon.distribution.jy.dto.collect.BatchUpdateCollectStatusDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.UnloadScanCollectDealDto;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectCacheService;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectBatchUpdateTypeEnum;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.jsf.gd.util.JsonUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/13 14:06
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")

public class JyTysUnloadCollectTest {

    private static final Logger logger = LoggerFactory.getLogger(JyTysUnloadCollectTest.class);

    @Autowired
    private JyCollectBatchUpdateStatusConsumer jyCollectBatchUpdateStatusConsumer;
    @Autowired
    private JyCollectDataInitSplitConsumer jyCollectDataInitSplitConsumer;
    @Autowired
    private JyCollectDataSplitBatchInitConsumer jyCollectDataSplitBatchInitConsumer;
    @Autowired
    private JyCollectStatusBatchUpdateWaybillSplitConsumer jyCollectStatusBatchUpdateWaybillSplitConsumer;
    @Autowired
    private JyCollectCacheService jyCollectCacheService;
    @Resource
    private Cluster redisClientCache;

    @Autowired
    private JyCollectService jyCollectService;


    @Test
    public void consume2() {
        while(true) {
            try {
                //空任务扫描初始化
                taskNullScanInitCollect();
                //封车集齐初始化
//                sealCarInitCollect();
            } catch (Exception e) {
                logger.error("服务异常!", e);
            }
        }
    }


    private void taskNullScanInitCollect() throws Exception {
        //空任务扫描初始化
        String taskNullScanInitJson = "{\n" +
                "\"bizId\" : \"XCZJ23040100000001\",\n" +
                "\"operateTime\" : 1680331698039,\n" +
                "\"operateNode\" : 103,\n" +
                "\"taskNullScanCodeType\" : 101,\n" +
                "\"taskNullScanCode\" : \"JDX000234572048-1-5-\",\n" +
                "\"taskNullScanSiteCode\" : 10186,\n" +
                "\"operatorErp\" : \"xumigen\"\n" +
                "}";
        //空任务扫描初始化
        Message message1 = new Message();
        message1.setText(taskNullScanInitJson);
        InitCollectDto mqBody = JsonHelper.fromJson(taskNullScanInitJson,InitCollectDto.class);
        mqBody.setWaybillCode(WaybillUtil.getWaybillCode(mqBody.getTaskNullScanCode()));
        //防重缓存去除
        String key = jyCollectCacheService.getCacheKeyWaybillCollectSplitBeforeInit(mqBody, CollectInitNodeEnum.NULL_TASK_INIT.getCode());
        redisClientCache.del(key);
        jyCollectDataInitSplitConsumer.consume(message1);
    }
    //封车集齐初始化
    private void sealCarInitCollect() throws Exception {
        InitCollectDto initCollectDto = new InitCollectDto();
        initCollectDto.setBizId("SC23032400029387");
        initCollectDto.setOperateTime(System.currentTimeMillis());
        initCollectDto.setOperateNode(CollectInitNodeEnum.SEAL_INIT.getCode());
        Message message1 = new Message();
        message1.setText(JsonUtils.toJSONString(initCollectDto));

        String key = jyCollectCacheService.getCacheKeySealCarCollectSplitBeforeInit(initCollectDto);
        redisClientCache.del(key);
        jyCollectDataInitSplitConsumer.consume(message1);

    }

    @Test
    public void consume3() {
        while (true) {

            try {
                //空任务节点
//                taskNullScanSplitInitCollect();
                //封车节点
                sealCarSplitInitCollect();
            } catch (Exception e) {
                logger.error("服务异常!", e);
            }
        }
    }

    private void sealCarSplitInitCollect() throws Exception {
        String body1 = "{\n" +
                "    \"bizId\": \"SC23032400029387\",\n" +
                "    \"operateNode\": 101,\n" +
                "    \"operateTime\": 1679055682628,\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 100,\n" +
                "    \"sealBatchCode\": \"40240-10186-20230324103443653\",\n" +
                "    \"sealSiteCode\": 40240,\n" +
                "    \"shouldUnSealSiteCode\": 10186\n" +
                "}";
        Message message1 = new Message();
        message1.setText(body1);
        jyCollectDataSplitBatchInitConsumer.consume(message1);
        String body2 = "{\n" +
                "    \"bizId\": \"SC23031700029108\",\n" +
                "    \"operateNode\": 101,\n" +
                "    \"operateTime\": 1679055682628,\n" +
                "    \"pageNo\": 2,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"sealBatchCode\": \"40240-10186-20230317173374331\",\n" +
                "    \"sealSiteCode\": 40240,\n" +
                "    \"shouldUnSealSiteCode\": 10186\n" +
                "}";
        Message message2 = new Message();
        message2.setText(body2);
        jyCollectDataSplitBatchInitConsumer.consume(message2);
    }

    private void taskNullScanSplitInitCollect() throws Exception {
        String body1 = "{\n" +
                "    \"bizId\": \"XCZJ23031600000013\",\n" +
                "    \"operateNode\": 103,\n" +
                "    \"operateTime\": 1679035677055,\n" +
                "    \"operatorErp\": \"xumigen\",\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 30,\n" +
                "    \"taskNullScanCode\": \"JD0003419483749-1-50-\",\n" +
                "    \"taskNullScanCodeType\": 101,\n" +
                "    \"taskNullScanSiteCode\": 10186,\n" +
                "    \"waybillCode\": \"JD0003419483749\"\n" +
                "}";
        String body2 = "{\n" +
                "    \"bizId\": \"XCZJ23031600000013\",\n" +
                "    \"operateNode\": 103,\n" +
                "    \"operateTime\": 1679035677055,\n" +
                "    \"operatorErp\": \"xumigen\",\n" +
                "    \"pageNo\": 2,\n" +
                "    \"pageSize\": 30,\n" +
                "    \"taskNullScanCode\": \"JD0003419483749-1-50-\",\n" +
                "    \"taskNullScanCodeType\": 101,\n" +
                "    \"taskNullScanSiteCode\": 10186,\n" +
                "    \"waybillCode\": \"JD0003419483749\"\n" +
                "}";


        Message message1 = new Message();
        message1.setText(body1);
        jyCollectDataSplitBatchInitConsumer.consume(message1);

        Message message2 = new Message();
        message2.setText(body2);
        jyCollectDataSplitBatchInitConsumer.consume(message2);
    }

    @Test
    public void consume4() {
        while(true) {
            try {
                BatchUpdateCollectStatusDto mqDto = new BatchUpdateCollectStatusDto();
                mqDto.setBizId("XCZJ23031600000013");
                mqDto.setOperateTime(System.currentTimeMillis());
                mqDto.setBatchType(CollectBatchUpdateTypeEnum.WAYBILL_BATCH.getCode());
                mqDto.setScanCode("JD0003419483749-1-50-");
                mqDto.setScanSiteCode(10186);
                String msg = com.jd.bluedragon.utils.JsonHelper.toJson(mqDto);

                Message message1 = new Message();
                message1.setText(msg);
                jyCollectStatusBatchUpdateWaybillSplitConsumer.consume(message1);
            } catch (Exception e) {
                logger.error("服务异常!", e);
            }
        }
    }

    @Test
    public void consume1() {
        while(true) {

            try {
                String body1 = "{\n" +
                        "    \"batchType\": 101,\n" +
                        "    \"bizId\": \"XCZJ23031600000013\",\n" +
                        "    \"operateTime\": 1679059290434,\n" +
                        "    \"pageNo\": 3,\n" +
                        "    \"pageSize\": 10,\n" +
                        "    \"scanCode\": \"JD0003419483749-1-50-\",\n" +
                        "    \"scanSiteCode\": 10186\n" +
                        "}";

                Message message1 = new Message();
                message1.setText(body1);
                jyCollectBatchUpdateStatusConsumer.consume(message1);
            } catch (Exception e) {
                logger.error("服务异常!", e);
            }
        }
    }

    @Test
    public void test1() {
        UnloadScanCollectDealDto re = new UnloadScanCollectDealDto();
        re.setBizId("JQ23032410452400032");
        re.setScanCode("JDX000233472480-4-4-");
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(10186);
        re.setCurrentOperate(currentOperate);
        re.setGoodNumber(4);
        System.out.println(JsonHelper.toJson(jyCollectService.scanQueryCollectTypeStatistics(re)));

        UnloadScanCollectDealDto re1 = new UnloadScanCollectDealDto();
        re1.setBizId("XCZJ23032300000019");
        re1.setScanCode("JDX000233366667-10-10-");
        re1.setCurrentOperate(currentOperate);
        re1.setGoodNumber(10);
        System.out.println(JsonHelper.toJson(jyCollectService.scanQueryCollectTypeStatistics(re1)));
    }

    @Test
    public void test2() {

    }

}
