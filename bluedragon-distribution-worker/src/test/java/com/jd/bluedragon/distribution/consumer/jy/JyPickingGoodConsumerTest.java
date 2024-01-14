package com.jd.bluedragon.distribution.consumer.jy;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.consumer.jy.task.aviation.*;
import com.jd.bluedragon.distribution.consumer.jy.task.dto.AirTransportBillDto;
import com.jd.bluedragon.distribution.consumer.jy.task.dto.TmsAviationPickingGoodMqBody;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.service.common.CommonService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 提货岗相关mq自测
 * @Author zhengchengfa
 * @Date 2023/8/23 16:45
 * @Description
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class JyPickingGoodConsumerTest  {
    private static final CurrentOperate SITE_910 = new CurrentOperate(910,"马驹桥分拣中心",new Date());
    public static final CurrentOperate SITE_40240 = new CurrentOperate(40240, "北京通州分拣中心", new Date());

    public static final User USER_wuyoude = new User(65396,"吴有德");

    public static final String GROUP_CODE = "G00000130001";
    public static final String POST = JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode();

    static {
        USER_wuyoude.setUserErp("wuyoude");
    }


    @Autowired
    private TmsAviationPickingGoodConsumer tmsAviationPickingGoodConsumer;
    @Autowired
    private JyPickingGoodScanConsumer jyPickingGoodScanConsumer;
    @Autowired
    private JyPickingGoodDetailInitConsumer jyPickingGoodDetailInitConsumer;
    @Autowired
    private CommonService commonService;
    @Autowired
    private JyPickingGoodWaitScanItemNumUpdateConsumer jyPickingGoodWaitScanItemNumUpdateConsumer;
    @Autowired
    private JyPickingGoodDetailInitSplitConsumer jyPickingGoodDetailInitSplitConsumer;
    @Test
    public void test() {
        String boxCode = "BC1001210816140000000505";
        //2000个箱号103s, 5000个耗时251s
        int boxNumMax = 5000;
        String waybillCode = "JD0003423499306";
        // 5000个运单  125s,   1W个耗时271s
        int waybillNumMax = 10000;
        while (true) {

            long startTime1 = System.currentTimeMillis();
            for (int i = 0; i < waybillNumMax; i++) {
                commonService.getRouteNextSiteByWaybillCode(40240, waybillCode);
            }
            Long temp1 = (System.currentTimeMillis() - startTime1) / 1000;

            long startTime2 = System.currentTimeMillis();
            for (int i = 0; i < boxNumMax; i++) {
                commonService.getRouteNextSiteByBox(40240, boxCode);
            }
            Long temp2 = (System.currentTimeMillis() - startTime2) / 1000;

            System.out.println("end");
        }
    }


    @Test
    public void tmsAviationPickingGoodConsumer() {
        List<TmsAviationPickingGoodMqBody> list = new ArrayList<>();
        for (int i = 10; i < 30; i ++) {

            TmsAviationPickingGoodMqBody mqBody = new TmsAviationPickingGoodMqBody();
            mqBody.setTplBillCode(String.format("%s000%s", DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyyyMMdd), i));
            mqBody.setFlightNumber(String.format("FN00%s", i/4 + 1));
            mqBody.setBeginNodeCode("START");
            mqBody.setBeginNodeName("始发机场测试");
            mqBody.setEndNodeCode("END");
            mqBody.setEndNodeName("目的机场测试");
            mqBody.setPlanTakeOffTime(DateHelper.addHours(DateHelper.getCurrentDayWithOutTimes(), -12));
            mqBody.setRealTakeOffTime(DateHelper.addHours(DateHelper.getCurrentDayWithOutTimes(), -10));
            mqBody.setPlanTouchDownTime(DateHelper.getCurrentDayWithOutTimes());
            mqBody.setRealTouchDownTime(null);//
            mqBody.setOperateType(10);
            mqBody.setDepartCargoAmount(5000);
            mqBody.setDepartCargoRealWeight(50d * 1000d);
            mqBody.setOperateTime(new Date());
            mqBody.setTransbillList(this.getAirTransportBillDto());
            if(i%2 == 1) {
                mqBody.setRealTouchDownTime(new Date());
            }


            list.add(mqBody);
        }


        while (true) {
            for (TmsAviationPickingGoodMqBody body : list) {

                Message message = new Message();
                message.setBusinessId(body.getTplBillCode());
                message.setText(JsonHelper.toJson(body));
                try {
                    tmsAviationPickingGoodConsumer.consume(message);
                } catch (Exception e) {
                    System.out.println("纳尼，发现error");
                    e.printStackTrace();
                }
            }
        }
    }


    private List<AirTransportBillDto> getAirTransportBillDto() {
        List<AirTransportBillDto> transbillList = new ArrayList<>();

        AirTransportBillDto dto1 = new AirTransportBillDto();
        dto1.setBatchCode("910-40240-20220621176414243");
        dto1.setSealCarCode("SC22062100017955");
        dto1.setBeginNodeCode("010F002");//910
        dto1.setBeginNodeName("北京马驹桥分拣中心");
        dto1.setEndNodeCode("010F016");//40240
        dto1.setEndNodeName("北京通州分拣中心");
        dto1.setTransportCode("T180929000517");
        transbillList.add(dto1);

        AirTransportBillDto dto2 = new AirTransportBillDto();
        dto2.setBatchCode("910-40240-20220801098213455");
        dto2.setSealCarCode("SC22062100017955");
        dto2.setBeginNodeCode("010F002");//910
        dto2.setBeginNodeName("北京马驹桥分拣中心");
        dto2.setEndNodeCode("010F016");//40240
        dto2.setEndNodeName("北京通州分拣中心");
        dto2.setTransportCode("T180929000517");
        transbillList.add(dto2);

        AirTransportBillDto dto3 = new AirTransportBillDto();
        dto3.setBatchCode("910-10186-20211214182344566");
        dto3.setSealCarCode("SC21121400011833");
        dto3.setBeginNodeCode("010F002");//910
        dto3.setBeginNodeName("北京马驹桥分拣中心");
        dto3.setEndNodeCode("010K001");//10186
        dto3.setEndNodeName("北京凉水河快运中心");
        dto3.setTransportCode("T211108001405");
        transbillList.add(dto3);

        return transbillList;
    }





    @Test
    public void jyPickingGoodScanConsumerTest() {
        String barCodeBox = "BC1001210816140000000505";
        String barCodePackage = "JD0003423499306-13-50-";


        JyPickingGoodScanDto scanDto = new JyPickingGoodScanDto();
        scanDto.setBizId("NPGT24011300000001");
        scanDto.setSiteId(40240l);
        scanDto.setOperatorTime(System.currentTimeMillis());
        scanDto.setGroupCode("G00000130001");
//        scanDto.setMoreScanFlag(!BarCodeFetchPickingTaskRuleEnum.WAIT_PICKING_TASK.getCode().equals(4));
        scanDto.setMoreScanFlag(false);
        scanDto.setUser(USER_wuyoude);

        while (true) {
            try {
                Message pickingPackage = new Message();
                scanDto.setBarCode("JD0003423499306-27-50-");
                scanDto.setSendGoodFlag(false);
                pickingPackage.setBusinessId(scanDto.getBarCode());
                pickingPackage.setText(JsonHelper.toJson(scanDto));
                jyPickingGoodScanConsumer.consume(pickingPackage);

                Message pickingBox = new Message();
                scanDto.setBarCode("BC1001210816140000000517");
                scanDto.setSendGoodFlag(false);
                pickingBox.setBusinessId(scanDto.getBarCode());
                pickingBox.setText(JsonHelper.toJson(scanDto));
                jyPickingGoodScanConsumer.consume(pickingBox);

                Message sendPackage = new Message();
                scanDto.setBarCode("JD0003423499306-28-50-");
                scanDto.setSendGoodFlag(true);
                scanDto.setNextSiteId(910l);
                sendPackage.setBusinessId(scanDto.getBarCode());
                sendPackage.setText(JsonHelper.toJson(scanDto));
                jyPickingGoodScanConsumer.consume(sendPackage);

                Message sendBox = new Message();
                scanDto.setBarCode("BC1001210816140000000518");
                scanDto.setSendGoodFlag(true);
                scanDto.setNextSiteId(910l);
                sendBox.setBusinessId(scanDto.getBarCode());
                sendBox.setText(JsonHelper.toJson(scanDto));
                jyPickingGoodScanConsumer.consume(sendBox);
            } catch (Exception e) {
                System.out.println("纳尼，发现error");
                e.printStackTrace();
            }
        }
    }




    @Test
    public void jyPickingGoodDetailInitConsumerTest(){

        String json1 = "{\n" +
                "    \"batchCode\": \"910-10186-20211214182344566\",\n" +
                "    \"bizId\": \"PGT24011400000042\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 10186,\n" +
                "    \"sealCarCode\": \"SC21121400011833\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";
        String json2 = "{\n" +
                "    \"batchCode\": \"910-40240-20220801098213455\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"sealCarCode\": \"SC22062100017955\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";
        String json3 = "{\n" +
                "    \"batchCode\": \"910-40240-20220621176414243\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"sealCarCode\": \"SC22062100017955\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";
        //非分拣中心
        String json4 = "{\n" +
                "    \"batchCode\": \"910-40240-20230830198504864\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"sealCarCode\": \"SC22062100017955\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 39,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";
        PickingGoodTaskDetailInitDto dto1 = JSONObject.parseObject(json1, PickingGoodTaskDetailInitDto.class);
        PickingGoodTaskDetailInitDto dto2 = JSONObject.parseObject(json2, PickingGoodTaskDetailInitDto.class);
        PickingGoodTaskDetailInitDto dto3 = JSONObject.parseObject(json3, PickingGoodTaskDetailInitDto.class);
        PickingGoodTaskDetailInitDto dto4 = JSONObject.parseObject(json4, PickingGoodTaskDetailInitDto.class);

        while (true) {

            Message msg1 = new Message();
            msg1.setBusinessId(dto1.getBatchCode());
            msg1.setText(json1);

            Message msg2 = new Message();
            msg2.setBusinessId(dto2.getBatchCode());
            msg2.setText(json2);

            Message msg3 = new Message();
            msg3.setBusinessId(dto3.getBatchCode());
            msg3.setText(json3);


            Message msg4 = new Message();
            msg4.setBusinessId(dto4.getBatchCode());
            msg4.setText(json4);

            List<Message> list = Arrays.asList(msg1, msg2, msg3);
            for (Message message : list) {
                try {
                    jyPickingGoodDetailInitConsumer.consume(message);
                } catch (Exception e) {
                    System.out.println("纳尼，发现error");
                    e.printStackTrace();
                }
            }
            System.out.println("end");

        }
    }



    @Test
    public void jyPickingGoodWaitScanItemNumUpdateConsumerTest() {

        String json1 = "{\n" +
                "    \"batchCode\": \"910-40240-20220801098213455\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"calculateNextSiteAggFlag\": false,\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"waitPickingItemNum\": 4\n" +
                "}";
        String json2 = "{\n" +
                "    \"batchCode\": \"910-40240-20220801098213455\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"calculateNextSiteAggFlag\": true,\n" +
                "    \"nextSiteId\": 10186,\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"waitPickingItemNum\": 3\n" +
                "}";
        String json3 = "{\n" +
                "    \"batchCode\": \"910-40240-20220801098213455\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"calculateNextSiteAggFlag\": true,\n" +
                "    \"nextSiteId\": 910,\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"waitPickingItemNum\": 1\n" +
                "}";




        while (true) {
            List<String> list = Arrays.asList(json1, json2, json3);
            for (String json : list) {
                try {
                    Message message = new Message();
                    message.setText(json);

                    jyPickingGoodWaitScanItemNumUpdateConsumer.consume(message);
                } catch (Exception e) {
                    System.out.println("纳尼，发现error");
                    e.printStackTrace();
                }
            }
            System.out.println("end");
        }
    }


    @Test
    public void jyPickingGoodDetailInitSplitConsumerTest(){
        String json1 = "{\n" +
                "    \"batchCode\": \"910-10186-20211214182344566\",\n" +
                "    \"bizId\": \"PGT24011400000042\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"packageCode\": \"JDV000883197313-1-1-\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 10186,\n" +
                "    \"sealCarCode\": \"SC21121400011833\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";
        String json2 = "{\n" +
                "    \"batchCode\": \"910-40240-20220801098213455\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"packageCode\": \"JD0003363152191-1-9-\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"sealCarCode\": \"SC22062100017955\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";
        String json3 = "{\n" +
                "    \"batchCode\": \"910-40240-20220801098213455\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"boxCode\": \"BC1001220803270000200117\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"packageCode\": \"JDVF00002061400-1-1-\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"scanIsBoxType\": true,\n" +
                "    \"sealCarCode\": \"SC22062100017955\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";
        String json4 = "{\n" +
                "    \"batchCode\": \"910-40240-20220801098213455\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"boxCode\": \"BC1001220803230000200218\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"packageCode\": \"JDVF00002064702-1-1-\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"scanIsBoxType\": true,\n" +
                "    \"sealCarCode\": \"SC22062100017955\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";

        String json5 = "{\n" +
                "    \"batchCode\": \"910-40240-20220801098213455\",\n" +
                "    \"bizId\": \"PGT24011400000041\",\n" +
                "    \"boxCode\": \"BC1001220803230000200521\",\n" +
                "    \"businessNumber\": \"2024011400013\",\n" +
                "    \"packageCode\": \"JDVF00002066125-1-1-\",\n" +
                "    \"pickingNodeCode\": \"END\",\n" +
                "    \"pickingSiteId\": 40240,\n" +
                "    \"scanIsBoxType\": true,\n" +
                "    \"sealCarCode\": \"SC22062100017955\",\n" +
                "    \"serviceNumber\": \"FN004\",\n" +
                "    \"startSiteId\": 910,\n" +
                "    \"startSiteType\": 64,\n" +
                "    \"taskType\": 1\n" +
                "}";

        List<String> list = Arrays.asList(json1, json2, json3, json4, json5);
        while (true) {
            for (String json : list) {
                Message message = new Message();
                message.setText(json);
                try {
                    jyPickingGoodDetailInitSplitConsumer.consume(message);
                    System.out.println("end");

                } catch (Exception e) {
                    System.out.println("纳尼，发现error");
                    e.printStackTrace();
                }
            }
            System.out.println("end");
        }
    }
}


