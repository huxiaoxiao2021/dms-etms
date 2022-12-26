package ld;

import com.google.common.collect.Maps;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.*;
import com.jd.bluedragon.distribution.jy.dto.task.UnloadVehicleMqDto;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.TmsLineTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.tms.jdi.dto.BigTransWorkDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import org.apache.commons.collections4.CollectionUtils;
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

    //解封车
    String testRealRankingJson ="{\"sealCarCode\":\"SC22120800024423\",\"status\":20,\"operateUserCode\":\"fang3\",\"operateUserName\":\"方斌\",\"operateTime\":\"2022-12-10 17:17:40\",\"sealCarType\":30,\"batchCodes\":null,\"transBookCode\":\"TB22041730847267\",\"volume\":null,\"weight\":null,\"transWay\":null,\"vehicleNumber\":\"鄂ADJ8702\",\"operateSiteId\":869056,\"operateSiteCode\":\"027Y432\",\"operateSiteName\":\"武汉汉口北揽收营业部\",\"warehouseCode\":null,\"largeCargoDetails\":null,\"pieceCount\":null,\"source\":1,\"sealCarInArea\":null}\n";
    @Test
    public void testRealRanking(){
        //解封车
        Message message = new Message();
        message.setText(testRealRankingJson);
        try {
            tmsSealCarStatusConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
                "    \"billCode\": \"TJ22041943964444\",\n" +
                "    \"endOrgCode\": 6,\n" +
                "    \"endOrgName\": \"华北分公司\",\n" +
                "    \"endSiteCode\": \"530F001\",\n" +
                "    \"endSiteId\": 831908,\n" +
                "    \"endSiteName\": \"菏泽分拣中心\",\n" +
                "    \"extendInfo\": {\n" +
                "        \"lostCnt\": \"499\",\n" +
                "        \"damageCnt\": \"23\",\n" +
                "        \"totalScannedPackageProgress\": \"90.01\"\n" +
                "    },\n" +
                "    \"sealCarCode\": \"XCZJ22041900000009\",\n" +
                "    \"sealCarStatus\": 10,\n" +
                "    \"sealCarTime\": 1650373338000,\n" +
                "    \"startOrgCode\": 6,\n" +
                "    \"startOrgName\": \"华北分公司\",\n" +
                "    \"startSiteCode\": \"530Y006\",\n" +
                "    \"startSiteId\": 221357,\n" +
                "    \"startSiteName\": \"菏泽巨野营业部\",\n" +
                "    \"totalScannedPackageCount\": 0,\n" +
                "    \"transportCode\": \"R2111177231019\",\n" +
                "    \"ts\": 1650373334591,\n" +
                "    \"vehicleNumber\": \"鲁R5SU05\",\n" +
                "    \"vehicleNumberLastFour\": \"SU05\",\n" +
                "    \"vehicleStatus\": 4,\n" +
                "    \"version\": 1,\n" +
                "    \"yn\": 1\n" +
                "}";

        UnloadVehicleMqDto mqDto = JsonHelper.fromJson(body, UnloadVehicleMqDto.class);
//        Map<String, Object> extendMap = Maps.newHashMap();
//        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_LOST_CNT, 1);
//        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_SCAN_PROGRESS, 100);
//        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_DAMAGE_CNT, 20);
//        mqDto.setExtendInfo(extendMap);
//
//        mqDto.setOrderTime(new Date());
//        mqDto.setRanking(10);
//        mqDto.setPredictionArriveTime(new Date());
//        mqDto.setActualArriveTime(new Date());
//        mqDto.setDesealCarTime(new Date());
//        mqDto.setLineType(JyLineTypeEnum.TRUNK_LINE.getCode());
//        mqDto.setLineTypeName(JyLineTypeEnum.TRUNK_LINE.getName());
//        mqDto.setTotalCount(1000L);

        Message message = new Message();
        message.setText(JsonHelper.toJson(mqDto));

        initUnloadVehicleConsumer.consume(message);
    }


    @Autowired
    private TmsTransWorkItemOperateConsumer transWorkItemOperateConsumer;

    @Test
    public void TmsTransWorkItemOperateConsumerTest() throws Exception {
        String body = "{\"transWorkItemCode\":\"TW22090800811121-003\",\"transWorkCode\":\"TW22090800811121\",\"operateType\":10,\"transType\":1,\"transWay\":1,\"beginNodeCode\":\"010F016\",\"endNodeCode\":\"592F002\",\"planDepartTime\":\"2022-09-09 06:15:00\"}";
        Message message = new Message();
        message.setText(body);
        transWorkItemOperateConsumer.consume(message);
       /* body = "{\"transWorkItemCode\":\"TW22090800811121-002\",\"transWorkCode\":\"TW22090800811121\",\"operateType\":10,\"transType\":1,\"transWay\":1,\"beginNodeCode\":\"010F016\",\"endNodeCode\":\"010F002\",\"planDepartTime\":\"2022-09-09 07:30:00\"}";
        message.setText(body);
        transWorkItemOperateConsumer.consume(message);*/
    }

    public static void main(String[] args) {

        BigTransWorkDto bigTransWorkDto  = JsonHelper.fromJson("{\n" +
                "        \"transWorkBillDto\":{\n" +
                "            \"beginProvinceId\":22,\n" +
                "            \"planArriveTime\":\"2022-09-13 02:30:00\",\n" +
                "            \"createUserName\":\"jd_VzpZQrYuHHHQ\",\n" +
                "            \"realDepartTime\":\"2022-09-12 18:41:04\",\n" +
                "            \"cancelFlag\":false,\n" +
                "            \"endOrgName\":\"西南分公司\",\n" +
                "            \"arriveOnTime\":false,\n" +
                "            \"carrierName\":\"南京福佑在线电子商务有限公司淮安分公司\",\n" +
                "            \"yn\":1,\n" +
                "            \"vehicleNumber\":\"川ADD391\",\n" +
                "            \"id\":43123620,\n" +
                "            \"carrierDriverCode\":\"18781284007\",\n" +
                "            \"vehicleType\":10,\n" +
                "            \"requireDeliveryTime\":\"2022-09-13 02:30:00\",\n" +
                "            \"endOrgCode\":\"4\",\n" +
                "            \"endCityId\":1930,\n" +
                "            \"palletCount\":0,\n" +
                "            \"planMileage\":420,\n" +
                "            \"beginOrgName\":\"西南分公司\",\n" +
                "            \"updateUserName\":\"system\",\n" +
                "            \"weight\":901.25,\n" +
                "            \"beginCountyName\":\"利州区\",\n" +
                "            \"checkCode\":\"298645\",\n" +
                "            \"endCityName\":\"成都市\",\n" +
                "            \"volume\":7.233,\n" +
                "            \"endCountyId\":49316,\n" +
                "            \"updateUserCode\":\"system\",\n" +
                "            \"routeLineCode\":\"\",\n" +
                "            \"transWorkCode\":\"TW22091243347194\",\n" +
                "            \"transType\":34,\n" +
                "            \"boxCount\":20,\n" +
                "            \"carrierCode\":\"C191011001193\",\n" +
                "            \"businessLine\":1,\n" +
                "            \"transJobCode\":\"TJ22091264469628\",\n" +
                "            \"beginCountyId\":27499,\n" +
                "            \"beginCityName\":\"广元市\",\n" +
                "            \"dataSource\":1,\n" +
                "            \"carrierDriverName\":\"冉路\",\n" +
                "            \"status\":40,\n" +
                "            \"carrierClass\":1,\n" +
                "            \"beginCityId\":1977,\n" +
                "            \"realVehicleType\":10,\n" +
                "            \"sysSource\":36,\n" +
                "            \"orderQuoteType\":1,\n" +
                "            \"endNodeCode\":\"028F010\",\n" +
                "            \"endProvinceId\":22,\n" +
                "            \"planDepartTime\":\"2022-09-12 20:00:00\",\n" +
                "            \"beginNodeCode\":\"839C500\",\n" +
                "            \"orgCode\":\"1\",\n" +
                "            \"class\":\"com.jd.tms.jdi.dto.TransWorkBillDto\",\n" +
                "            \"workStatus\":20,\n" +
                "            \"lineMode\":0,\n" +
                "            \"endProvinceName\":\"四川\",\n" +
                "            \"endNodeName\":\"成都转运中心\",\n" +
                "            \"createUserCode\":\"jd_VzpZQrYuHHHQ\",\n" +
                "            \"requirePickupTime\":\"2022-09-12 20:00:00\",\n" +
                "            \"beginNodeName\":\"广元上西集配站\",\n" +
                "            \"carrierType\":2,\n" +
                "            \"endCountyName\":\"龙泉驿区\",\n" +
                "            \"updateTime\":\"2022-09-12 20:27:00\",\n" +
                "            \"beginProvinceName\":\"四川\",\n" +
                "            \"carrierDriverPhone\":\"18781284007\",\n" +
                "            \"beginOrgCode\":\"4\",\n" +
                "            \"createTime\":\"2022-09-12 15:22:55\",\n" +
                "            \"workType\":1,\n" +
                "            \"businessDomain\":2,\n" +
                "            \"assignTime\":\"2022-09-12 15:22:55\",\n" +
                "            \"businessType\":11,\n" +
                "            \"cooperateMode\":0,\n" +
                "            \"transWay\":2\n" +
                "        },\n" +
                "        \"class\":\"com.jd.tms.jdi.dto.BigTransWorkDto\",\n" +
                "        \"transWorkItemDtoList\":[\n" +
                "            {\n" +
                "                \"endMobile\":\"028-63800620\",\n" +
                "                \"transBookCode\":\"TB22091244050904\",\n" +
                "                \"beginProvinceId\":22,\n" +
                "                \"planArriveTime\":\"2022-09-13 02:30:00\",\n" +
                "                \"itemOpeStatus\":2,\n" +
                "                \"beginNodeSubTypeName\":\"三级中转场\",\n" +
                "                \"transWorkItemCode\":\"TW22091243347194-001\",\n" +
                "                \"endNodeTypeName\":\"分拣中心\",\n" +
                "                \"endOrgName\":\"西南分公司\",\n" +
                "                \"currentVehicleNumber\":\"川ADD391\",\n" +
                "                \"yn\":1,\n" +
                "                \"endNodeType\":2,\n" +
                "                \"vehicleNumber\":\"川ADD391\",\n" +
                "                \"simpleCode\":\"22091252318864\",\n" +
                "                \"id\":141651294,\n" +
                "                \"currentDriverName\":\"冉路\",\n" +
                "                \"carrierDriverCode\":\"19979847149\",\n" +
                "                \"requireDeliveryTime\":\"2022-09-13 02:30:00\",\n" +
                "                \"endOrgCode\":\"4\",\n" +
                "                \"endCityId\":1930,\n" +
                "                \"travelTime\":18900000,\n" +
                "                \"palletCount\":0,\n" +
                "                \"beginOrgName\":\"西南分公司\",\n" +
                "                \"weight\":0,\n" +
                "                \"beginCountyName\":\"利州区\",\n" +
                "                \"transJobItemCode\":\"TJ22091264469628-001\",\n" +
                "                \"beginMobile\":\"18181026677\",\n" +
                "                \"endCityName\":\"成都市\",\n" +
                "                \"volume\":0,\n" +
                "                \"beginNodeType\":2,\n" +
                "                \"endCountyId\":49316,\n" +
                "                \"routeLineCode\":\"R2007289212356\",\n" +
                "                \"transWorkCode\":\"TW22091243347194\",\n" +
                "                \"transType\":2,\n" +
                "                \"boxCount\":0,\n" +
                "                \"businessLine\":1,\n" +
                "                \"transJobCode\":\"TJ22091264469628\",\n" +
                "                \"beginCountyId\":27499,\n" +
                "                \"beginCityName\":\"广元市\",\n" +
                "                \"roundTripType\":1,\n" +
                "                \"currentDriverCode\":\"18781284007\",\n" +
                "                \"endAddress\":\"成都市龙泉驿区经开区南6路669号（成工工程机械股份有限公司2号门入）\",\n" +
                "                \"carrierDriverName\":\"119****7149\",\n" +
                "                \"status\":40,\n" +
                "                \"whiteListFlag\":1,\n" +
                "                \"beginCityId\":1977,\n" +
                "                \"endNodeSubTypeName\":\"快运中心\",\n" +
                "                \"endNodeCode\":\"028F010\",\n" +
                "                \"endProvinceId\":22,\n" +
                "                \"planDepartTime\":\"2022-09-12 21:15:00\",\n" +
                "                \"beginNodeCode\":\"839F001\",\n" +
                "                \"milage\":420,\n" +
                "                \"class\":\"com.jd.tms.jdi.dto.TransWorkItemDto\",\n" +
                "                \"beginAddress\":\"四川广运集团股份有限公司广运现代物流中心一期8号库\",\n" +
                "                \"lineMode\":0,\n" +
                "                \"endProvinceName\":\"四川\",\n" +
                "                \"endNodeName\":\"成都转运中心\",\n" +
                "                \"transportCode\":\"R2007289212356\",\n" +
                "                \"requirePickupTime\":\"2022-09-12 21:15:00\",\n" +
                "                \"beginNodeSubType\":208,\n" +
                "                \"beginNodeName\":\"广元分拣中心\",\n" +
                "                \"endPerson\":\"张涛\",\n" +
                "                \"endCountyName\":\"龙泉驿区\",\n" +
                "                \"updateTime\":\"2022-09-12 20:20:51\",\n" +
                "                \"beginProvinceName\":\"四川\",\n" +
                "                \"beginNodeTypeName\":\"分拣中心\",\n" +
                "                \"beginOrgCode\":\"4\",\n" +
                "                \"itemWorkStatus\":10,\n" +
                "                \"createTime\":\"2022-09-12 15:22:55\",\n" +
                "                \"transbillSign\":\"00000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "                \"businessDomain\":2,\n" +
                "                \"businessType\":11,\n" +
                "                \"endNodeSubType\":206,\n" +
                "                \"beginPerson\":\"须永林\",\n" +
                "                \"transWay\":2\n" +
                "            },\n" +
                "            {\n" +
                "                \"endMobile\":\"028-63800620\",\n" +
                "                \"transBookCode\":\"TB22091244036658\",\n" +
                "                \"beginProvinceId\":22,\n" +
                "                \"planArriveTime\":\"2022-09-13 02:30:00\",\n" +
                "                \"itemOpeStatus\":2,\n" +
                "                \"beginNodeSubTypeName\":\"集配站\",\n" +
                "                \"transWorkItemCode\":\"TW22091243347194-002\",\n" +
                "                \"endNodeTypeName\":\"分拣中心\",\n" +
                "                \"endOrgName\":\"西南分公司\",\n" +
                "                \"currentVehicleNumber\":\"川ADD391\",\n" +
                "                \"yn\":1,\n" +
                "                \"endNodeType\":2,\n" +
                "                \"vehicleNumber\":\"川ADD391\",\n" +
                "                \"simpleCode\":\"22091252318865\",\n" +
                "                \"id\":141651295,\n" +
                "                \"currentDriverName\":\"冉路\",\n" +
                "                \"carrierDriverCode\":\"19979847149\",\n" +
                "                \"requireDeliveryTime\":\"2022-09-13 02:30:00\",\n" +
                "                \"endOrgCode\":\"4\",\n" +
                "                \"endCityId\":1930,\n" +
                "                \"travelTime\":23400000,\n" +
                "                \"palletCount\":0,\n" +
                "                \"beginOrgName\":\"西南分公司\",\n" +
                "                \"weight\":0,\n" +
                "                \"beginCountyName\":\"利州区\",\n" +
                "                \"transJobItemCode\":\"TJ22091264469628-002\",\n" +
                "                \"beginMobile\":\"15700580114\",\n" +
                "                \"endCityName\":\"成都市\",\n" +
                "                \"volume\":0,\n" +
                "                \"beginNodeType\":10,\n" +
                "                \"endCountyId\":49316,\n" +
                "                \"routeLineCode\":\"R2010222327802\",\n" +
                "                \"transWorkCode\":\"TW22091243347194\",\n" +
                "                \"beginPhone\":\"15700580114\",\n" +
                "                \"transType\":34,\n" +
                "                \"boxCount\":0,\n" +
                "                \"businessLine\":1,\n" +
                "                \"transJobCode\":\"TJ22091264469628\",\n" +
                "                \"beginCountyId\":27499,\n" +
                "                \"beginCityName\":\"广元市\",\n" +
                "                \"roundTripType\":1,\n" +
                "                \"currentDriverCode\":\"18781284007\",\n" +
                "                \"endAddress\":\"成都市龙泉驿区经开区南6路669号（成工工程机械股份有限公司2号门入）\",\n" +
                "                \"carrierDriverName\":\"119****7149\",\n" +
                "                \"status\":40,\n" +
                "                \"whiteListFlag\":1,\n" +
                "                \"beginCityId\":1977,\n" +
                "                \"beginTownName\":\"河西街道\",\n" +
                "                \"endNodeSubTypeName\":\"快运中心\",\n" +
                "                \"endNodeCode\":\"028F010\",\n" +
                "                \"endProvinceId\":22,\n" +
                "                \"planDepartTime\":\"2022-09-12 20:00:00\",\n" +
                "                \"beginTownId\":57150,\n" +
                "                \"beginNodeCode\":\"839C500\",\n" +
                "                \"milage\":300.01,\n" +
                "                \"class\":\"com.jd.tms.jdi.dto.TransWorkItemDto\",\n" +
                "                \"beginAddress\":\"四川省广元市利州区河西街道江北社区浩口村6组广元交通物流2A\",\n" +
                "                \"lineMode\":0,\n" +
                "                \"endProvinceName\":\"四川\",\n" +
                "                \"endNodeName\":\"成都转运中心\",\n" +
                "                \"transportCode\":\"R2010222327802\",\n" +
                "                \"requirePickupTime\":\"2022-09-12 20:00:00\",\n" +
                "                \"beginNodeSubType\":1008,\n" +
                "                \"beginNodeName\":\"广元上西集配站\",\n" +
                "                \"endPerson\":\"张涛\",\n" +
                "                \"endCountyName\":\"龙泉驿区\",\n" +
                "                \"updateTime\":\"2022-09-12 18:33:43\",\n" +
                "                \"beginProvinceName\":\"四川\",\n" +
                "                \"beginNodeTypeName\":\"车队虚拟站点\",\n" +
                "                \"beginOrgCode\":\"4\",\n" +
                "                \"itemWorkStatus\":10,\n" +
                "                \"createTime\":\"2022-09-12 15:22:55\",\n" +
                "                \"transbillSign\":\"00000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\n" +
                "                \"businessDomain\":2,\n" +
                "                \"businessType\":11,\n" +
                "                \"endNodeSubType\":206,\n" +
                "                \"beginPerson\":\"华永恒\",\n" +
                "                \"transWay\":2\n" +
                "            }\n" +
                "        ]\n" +
                "    }",BigTransWorkDto.class);
        if(bigTransWorkDto != null && bigTransWorkDto.getTransWorkBillDto() != null ){
            TransWorkBillDto transWorkBillDto = bigTransWorkDto.getTransWorkBillDto();
            if(!CollectionUtils.isEmpty(bigTransWorkDto.getTransWorkItemDtoList())){
                Integer lineTypeCode = transWorkBillDto.getTransType();
                JyLineTypeEnum lineType = TmsLineTypeEnum.getLineType(transWorkBillDto.getTransType());
                //派车明细中的线路类型 干支传摆 从大到小处理 （ 以 JyLineTypeEnum order 排名顺序）
                for(com.jd.tms.jdi.dto.TransWorkItemDto item : bigTransWorkDto.getTransWorkItemDtoList()){
                    JyLineTypeEnum itemLineType = TmsLineTypeEnum.getLineType(item.getTransType());
                    if(itemLineType.getOrder() < lineType.getOrder()){
                        lineType = itemLineType;
                        lineTypeCode = item.getTransType();
                    }
                }
                //替换线路类型
                transWorkBillDto.setTransType(lineTypeCode);
            }
        }
    }

}
