package ld;

import com.google.common.collect.Maps;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.*;
import com.jd.bluedragon.distribution.jy.dto.task.UnloadVehicleMqDto;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/8
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/ld/distribution-worker-context-test.xml")
public class TmsConsumerTest {
    @Autowired
    private TmsVehicleDetailStatusConsumer tmsVehicleDetailStatusConsumer;

    @Autowired
    private TmsTransWorkCarArriveConsumer tmsTransWorkCarArriveConsumer;

    @Autowired
    private TmsSealCarStatusConsumer tmsSealCarStatusConsumer;

    @Autowired
    private TmsCancelSealCarBatchConsumer tmsCancelSealCarBatchConsumer;


    //封车
    String tmsSealCarStatusJson = "{\"sealCarCode\":\"SC22041100014898\",\"status\":10,\"operateUserCode\":\"bjxings\",\"operateUserName\":\"邢松\",\"operateTime\":\"2022-04-11 15:57:41\",\"sealCarType\":10,\"batchCodes\":[\"910-39-01595202205127129\"],\"transBookCode\":null,\"volume\":22.3112,\"weight\":343.22,\"transWay\":3,\"vehicleNumber\":\"京AB0D87\",\"operateSiteId\":910,\"operateSiteCode\":\"010F002\",\"operateSiteName\":\"北京马驹桥分拣中心6\",\"warehouseCode\":null,\"largeCargoDetails\":null,\"pieceCount\":null,\"source\":1}";
    //解封车
    String tmsSealCarStatusJson2 ="{\"sealCarCode\":\"SC22041100014898\",\"status\":20,\"operateUserCode\":\"fang3\",\"operateUserName\":\"方斌\",\"operateTime\":\"2022-04-17 14:17:40\",\"sealCarType\":30,\"batchCodes\":null,\"transBookCode\":\"TB22041730847267\",\"volume\":null,\"weight\":null,\"transWay\":null,\"vehicleNumber\":\"鄂ADJ8702\",\"operateSiteId\":869056,\"operateSiteCode\":\"027Y432\",\"operateSiteName\":\"武汉汉口北揽收营业部\",\"warehouseCode\":null,\"largeCargoDetails\":null,\"pieceCount\":null,\"source\":1,\"sealCarInArea\":null}\n";
    //进围栏
    String tmsSealCarStatusJson3 = "{\"sealCarCode\":\"SC22041100014898\",\"status\":40,\"operateUserCode\":null,\"operateUserName\":null,\"operateTime\":\"2022-04-17 14:17:54\",\"sealCarType\":null,\"batchCodes\":null,\"transBookCode\":null,\"volume\":null,\"weight\":null,\"transWay\":null,\"vehicleNumber\":\"京AD13697\",\"operateSiteId\":null,\"operateSiteCode\":null,\"operateSiteName\":null,\"warehouseCode\":null,\"largeCargoDetails\":null,\"pieceCount\":null,\"source\":1,\"sealCarInArea\":null}\n";

    //司机到达 无任务
    String s1 = "{\"status\":20,\"vehicleDetailCode\":\"CBD22032933731038\",\"operateTime\":1648551913608,\"operatorUserId\":21926679,\"operatorCode\":\"wangzheng27\",\"operatorName\":\"王政\",\"operatorPhone\":\"15361435295\",\"sealCarCode\":\"SC22041100014898\",\"volume\":null,\"sendCarInArea\":1,\"arriveCarInArea\":null,\"specialTrans\":null,\"sameDevice\":null}\n";

    //司机到达
    String s2 = "{\"transWorkCode\":\"TW22033029178590\",\"transWorkItemCode\":\"TW22033029178590-001\",\"comeTime\":null,\"sendTime\":null,\"arriveTime\":\"2022-03-30 15:08:20\",\"businessType\":14,\"transType\":11,\"transWay\":2,\"vehicleNumber\":\"甘AX6A83\",\"operateType\":2}";



    @Test
    public void testTmsMq(){

        Message message = new Message();
        try {
            message.setText(tmsSealCarStatusJson);
            //封车
            tmsSealCarStatusConsumer.consume(message);

            message.setText(tmsSealCarStatusJson3);
            //进围栏
            tmsSealCarStatusConsumer.consume(message);

            //司机到达
            message.setText(s2);
            //tmsTransWorkCarArriveConsumer.consume(message);

            //司机到达 无任务
            message.setText(s1);
            tmsVehicleDetailStatusConsumer.consume(message);

            message.setText(tmsSealCarStatusJson2);
            //解封车
            tmsSealCarStatusConsumer.consume(message);


        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    @Test
    public void testTmsMq2(){

        Message message = new Message();
        try {
            message.setText(tmsSealCarStatusJson);
            //封车
            tmsSealCarStatusConsumer.consume(message);

            message.setText(tmsSealCarStatusJson3);
            //进围栏
            tmsSealCarStatusConsumer.consume(message);

            //司机到达
            message.setText(s2);
            //tmsTransWorkCarArriveConsumer.consume(message);

            //司机到达 无任务
            message.setText(s1);
            tmsVehicleDetailStatusConsumer.consume(message);

            message.setText(tmsSealCarStatusJson2);
            //解封车
            tmsSealCarStatusConsumer.consume(message);


        } catch (Exception e) {
            e.printStackTrace();
        }




    }


    @Test
    public void testTmsMq3(){

        Message message = new Message();
        try {
            message.setText(tmsSealCarStatusJson);
            //封车
            tmsSealCarStatusConsumer.consume(message);

            message.setText(tmsSealCarStatusJson3);
            //进围栏
            tmsSealCarStatusConsumer.consume(message);

            //司机到达
            message.setText(s2);
            tmsTransWorkCarArriveConsumer.consume(message);

            //司机到达 无任务
            message.setText(s1);
            //tmsVehicleDetailStatusConsumer.consume(message);

            message.setText(tmsSealCarStatusJson2);
            //解封车
            tmsSealCarStatusConsumer.consume(message);


        } catch (Exception e) {
            e.printStackTrace();
        }




    }


    @Test
    public void testTmsMq4(){

        Message message = new Message();
        try {
            message.setText(tmsSealCarStatusJson);
            //封车
            tmsSealCarStatusConsumer.consume(message);

            message.setText(tmsSealCarStatusJson3);
            //进围栏
            //tmsSealCarStatusConsumer.consume(message);

            //司机到达
            message.setText(s2);
            tmsTransWorkCarArriveConsumer.consume(message);

            //司机到达 无任务
            message.setText(s1);
            //tmsVehicleDetailStatusConsumer.consume(message);

            message.setText(tmsSealCarStatusJson2);
            //解封车
            tmsSealCarStatusConsumer.consume(message);


        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    @Autowired
    private JyUnloadTaskCompleteConsumer unloadTaskCompleteConsumer;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private InitUnloadVehicleConsumer initUnloadVehicleConsumer;

    @Test
    public void JyUnloadTaskCompleteConsumerTest() throws Exception {
        String body = "{\n" +
                "  \"taskId\" : \"220413200000015\",\n" +
                "  \"bizId\" : \"SC22041300014948\",\n" +
                "  \"sealCarCode\" : \"SC22041300014948\",\n" +
                "  \"abnormalFlag\" : 1,\n" +
                "  \"shouldScanCount\" : 5,\n" +
                "  \"moreScanCount\" : 10,\n" +
                "  \"operateTime\" : 1649847283374,\n" +
                "  \"operateUserErp\" : \"bjxings\",\n" +
                "  \"operateUserName\" : \"邢松\"\n" +
                "}\n";

        Message message = new Message();
        message.setText(body);
        unloadTaskCompleteConsumer.consume(message);
    }

    @Test
    public void InitUnloadVehicleConsumerTest() throws Exception {
        String body = "{\n" +
                "    \"endOrgCode\": 4,\n" +
                "    \"endOrgName\": \"西南分公司\",\n" +
                "    \"endSiteCode\": \"028F020\",\n" +
                "    \"endSiteId\": 691538,\n" +
                "    \"endSiteName\": \"成都祥福分拣中心\",\n" +
                "    \"extendInfo\": {},\n" +
                "    \"sealCarCode\": \"BJ001\",\n" +
                "    \"sealCarStatus\": 10,\n" +
                "    \"sealCarTime\": 1649620363000,\n" +
                "    \"startOrgCode\": 10,\n" +
                "    \"startOrgName\": \"华南分公司\",\n" +
                "    \"startSiteCode\": \"663F001\",\n" +
                "    \"startSiteId\": 146054,\n" +
                "    \"startSiteName\": \"揭阳分拣中心\",\n" +
                "    \"totalScannedPackageCount\": 0,\n" +
                "    \"transportCode\": \"R1911088023065\",\n" +
                "    \"ts\": 1649620365106,\n" +
                "    \"vehicleNumber\": \"浙H27589\",\n" +
                "    \"vehicleNumberLastFour\": \"7589\",\n" +
                "    \"vehicleStatus\": 4,\n" +
                "    \"version\": 3,\n" +
                "    \"yn\": 1\n" +
                "}";

        UnloadVehicleMqDto mqDto = JsonHelper.fromJson(body, UnloadVehicleMqDto.class);
        Map<String, Object> extendMap = Maps.newHashMap();
        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_LOST_CNT, 1);
        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_SCAN_PROGRESS, 100);
        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_DAMAGE_CNT, 20);
        mqDto.setExtendInfo(extendMap);

        mqDto.setOrderTime(new Date());
        mqDto.setRanking(10);
        mqDto.setPredictionArriveTime(new Date());
        mqDto.setActualArriveTime(new Date());
        mqDto.setDesealCarTime(new Date());
        mqDto.setLineType(JyLineTypeEnum.TRUNK_LINE.getCode());
        mqDto.setLineTypeName(JyLineTypeEnum.TRUNK_LINE.getName());
        mqDto.setTotalCount(1000L);

        Message message = new Message();
        message.setText(JsonHelper.toJson(mqDto));

        initUnloadVehicleConsumer.consume(message);
    }


    @Autowired
    private TmsTransWorkItemOperateConsumer transWorkItemOperateConsumer;

    @Test
    public void TmsTransWorkItemOperateConsumerTest() throws Exception {
        String body = "{\n" +
                "    \"transWorkItemCode\": \"TW22060900790698-002\",\n" +
                "    \"transWorkCode\": \"TW22060900790698\",\n" +
                "    \"operateType\": 10,\n" +
                "    \"transType\": 17,\n" +
                "    \"transWay\": 2,\n" +
                "    \"beginNodeCode\": \"010F002\",\n" +
                "    \"endNodeCode\": \"731X079\",\n" +
                "    \"planDepartTime\": \"2022-06-10 00:00:00\"\n" +
                "}";
        Message message = new Message();
        message.setText(body);
        transWorkItemOperateConsumer.consume(message);
    }
}
