package ld;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.consumer.reverse.PickWareConsumer;
import com.jd.bluedragon.distribution.consumer.reverse.ReversePopConsumer;
import com.jd.bluedragon.distribution.consumer.reverse.ReverseReceiveConsumer;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.AbstractTaskExecute;
import com.jd.bluedragon.distribution.reverse.domain.Product;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.distribution.worker.InspectionTask;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.domain.BaseDataDict;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context.xml")
public class ReverseTest {

    @Autowired
    private ReverseSendService reverseSendService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillService waybillService;

    @Test
    public void ECLPRejectReason(){

        Task tTask = new Task();

        String sendCode = "910-11252-20200211135630046";
        tTask.setBoxCode(sendCode);
        tTask.setBody(sendCode);
        tTask.setCreateSiteCode(910);
        tTask.setKeyword2("20");
        tTask.setReceiveSiteCode(11252);
        tTask.setType(Task.TASK_TYPE_SEND_DELIVERY);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_DELIVERY));
        tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_SEND));
        tTask.setOwnSign("DMS");
        tTask.setKeyword1("4");// 1 回传运单状态
        tTask.setFingerprint(sendCode + "_" + tTask.getKeyword1());
        try {
            reverseSendService.findSendwaybillMessage(tTask);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Test
    public void getExcessPicture(){

        weightAndVolumeCheckService.searchExcessPicture("JDVB00000516204",910);
    }


    @Test
    public void WaybillStatus(){

        Task tTask = new Task();

        tTask.setBody("{\n" +
                "  \"boxCode\" : \"BC1001190403130000000202\",\n" +
                "  \"waybillCode\" : \"JDVA00001832840\",\n" +
                "  \"packageCode\" : \"JDVA00001832840\",\n" +
                "  \"orgId\" : 6,\n" +
                "  \"orgName\" : \"总公司\",\n" +
                "  \"createSiteCode\" : 910,\n" +
                "  \"createSiteType\" : 64,\n" +
                "  \"createSiteName\" : \"北京马驹桥分拣中心\",\n" +
                "  \"receiveSiteCode\" : 39,\n" +
                "  \"receiveSiteType\" : 4,\n" +
                "  \"receiveSiteName\" : \"石景山营业部\",\n" +
                "  \"operatorId\" : 10053,\n" +
                "  \"operator\" : \"邢松\",\n" +
                "  \"operateType\" : 1,\n" +
                "  \"operateTime\" : 1554274662000\n" +
                "}");


        tTask.setOwnSign("LD");
        tTask.setKeyword1("JDVA00001832840");

        try {
            waybillService.doWaybillStatusTask(tTask);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Autowired
    private InspectionTask inspectionTask;

    @Qualifier("inspectionTaskExecute")
    @Autowired()
    private AbstractTaskExecute taskExecute;

    @Test
    public void testTask(){
        String s = "{\"type\":1130,\"siteCode\":910,\"keyword1\":\"910\",\"keyword2\":\"\",\"body\":\"[{\\\"sealBoxCode\\\":\\\"\\\",\\\"boxCode\\\":\\\"\\\",\\\"packageBarOrWaybillCode\\\":\\\"JDVA00003531968\\\",\\\"exceptionType\\\":\\\"\\\",\\\"operateType\\\":0,\\\"receiveSiteCode\\\":0,\\\"id\\\":3,\\\"businessType\\\":10,\\\"userCode\\\":10053,\\\"userName\\\":\\\"邢松\\\",\\\"siteCode\\\":910,\\\"siteName\\\":\\\"北京马驹桥分拣中心\\\",\\\"operateTime\\\":\\\"2019-05-16 11:13:46.467\\\"}]\",\"boxCode\":\"\",\"receiveSiteCode\":910}";

        Task task = JsonHelper.fromJsonUseGson(s,Task.class);


        List<InspectionRequest> middleRequests=JsonHelper.fromJsonUseGson(task.getBody(),new TypeToken<List<InspectionRequest>>(){}.getType());

        Task domain=new Task();

        for (InspectionRequest request:middleRequests){
            domain.setBody(JsonHelper.toJson(request));
            taskExecute.execute(domain);
        }

    }


    @Test
    public void test1() {


        BaseEntity<Waybill> oldWaybill = waybillQueryManager.getWaybillByReturnWaybillCode("VA00041973503");
        if (oldWaybill != null && oldWaybill.getData() != null && StringUtils.isNotBlank(oldWaybill.getData().getWaybillCode())) {

            BaseEntity<BigWaybillDto> bigWaybill = waybillQueryManager.getDataByChoice(oldWaybill.getData().getWaybillCode(), true, true, true, true, true, false, false);
            if (bigWaybill != null && bigWaybill.getData() != null && bigWaybill.getData().getWaybill() != null) {

                //send.setOrderId(bigWaybill.getData().getWaybill().getBusiOrderCode());
                System.out.println(bigWaybill.getData().getWaybill().getBusiOrderCode() + "===================================");
                if (bigWaybill.getData().getGoodsList() != null && bigWaybill.getData().getGoodsList().size() > 0) {
                    List<com.jd.bluedragon.distribution.reverse.domain.Product> proList = new ArrayList<Product>();
                    for (Goods good : bigWaybill.getData().getGoodsList()) {
                        com.jd.bluedragon.distribution.reverse.domain.Product product = new com.jd.bluedragon.distribution.reverse.domain.Product();
                        product.setProductId(good.getSku());
                        product.setProductName(good.getGoodName());
                        product.setProductNum(good.getGoodCount());
                        product.setProductPrice(good.getGoodPrice());
                        product.setProductLoss("0");
                        proList.add(product);
                    }
                    //send.setProList(proList);//存入原单的商品明细
                }
            }

            System.out.println("===================================");
        }
    }

    @Autowired
    @Qualifier("reverseReceiveConsumer")
    private ReverseReceiveConsumer reverseReceiveConsumer;

    @Autowired
    private ReversePopConsumer reversePopConsumer;

    @Autowired
    private DepartureService departureService;
    @Autowired
    private PickWareConsumer pickWareConsumer;

    @Test
    public void testMQ(){
        Message message = new Message();
        /*message.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ReceiveRequest>\n" +
                "  <sendCode>910-25016-20190518121903014-JDT000000072283</sendCode>\n" +
                "  <orderId>62341634627</orderId>\n" +
                "  <operateTime>2019-05-18 12:25:18</operateTime>\n" +
                "  <userCode>liuduo</userCode>\n" +
                "  <userName>liuduo|</userName>\n" +
                "  <receiveType>3</receiveType>\n" +
                "  <canReceive>1</canReceive>\n" +
                "</ReceiveRequest>");*/
        message.setText("{\"canReceive\":1,\"operateTime\":\"2019-08-26 09:32:19\",\"operateType\":1,\"operator\":\"冯硕|fengshuo9\",\"o" +
                "rderId\":101662618300,\"orgId\":611,\"packageCode\":\"WA1165522865029517316\"," +
                "\"pickwareCode\":\"Q566247436\"}");
        /*{"canReceive":1,"operateTime":"2019-08-26 09:32:19","operateType":1,"operator":"冯硕|fengshuo9","o
            rderId":101662618300,"orgId":611,"packageCode":"WA1165522865029517312",
            "pickwareCode":"Q566247436,Q566247437,Q566247439,Q566247440,Q566247441"}*/
        //reverseReceiveConsumer.consume(message);
        try {
            pickWareConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*String json = "{\"body\":\"1126534117837021184\",\"boxCode\":\"2226-553831-20190510000522015\",\"createSiteCode\":2226,\"createTime\":1557421638339,\"executeTime\":1557421638347,\"fingerprint\":\"2226-553831-20190510000522015_5\",\"keyword1\":\"5\",\"keyword2\":\"10\",\"ownSign\":\"DMS\",\"sequenceName\":\"SEQ_TASK_SORTING\",\"tableName\":\"task_send\",\"type\":1400}";
        Task t = JsonHelper.jsonToArray(json,Task.class);
        departureService.sendThirdDepartureInfoToTMS(t,false);*/
    }






    @Autowired
    private BaseMajorManager baseMajorManager;

    @Test
    public void testBase(){
        BaseDataDict refuseReason = baseMajorManager.getValidBaseDataDictListToMap(
                13,2,13).get(13);
        System.out.println(refuseReason.getTypeName());

    }

    @Test
    public void testReverseRecieve(){
        Message m = new Message();
        m.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReceiveRequest>\n" +
                "  <sendCode>910-14178-20190223165014011</sendCode>\n" +
                "  <orderId>JDVA00001588597</orderId>\n" +
                "  <operateTime>2019-03-15 14:11:54</operateTime>\n" +
                "  <userName>liuduo</userName>\n" +
                "  <receiveType>5</receiveType>\n" +
                "  <canReceive>1</canReceive>\n" +
                "</ReceiveRequest>");
        reverseReceiveConsumer.consume(m);

    }

}
