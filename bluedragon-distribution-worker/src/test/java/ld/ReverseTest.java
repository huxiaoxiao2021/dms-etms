package ld;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.asynbuffer.service.AsynBufferService;
import com.jd.bluedragon.distribution.asynbuffer.service.AsynBufferServiceImpl;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.consumer.reverse.PickWareConsumer;
import com.jd.bluedragon.distribution.consumer.reverse.ReversePopConsumer;
import com.jd.bluedragon.distribution.consumer.reverse.ReverseReceiveConsumer;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.flow.handler.PrintHandoverApprovePostHandlerTest;
import com.jd.bluedragon.distribution.framework.AbstractTaskExecute;
import com.jd.bluedragon.distribution.reverse.domain.Product;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.distribution.worker.InspectionTask;
import com.jd.bluedragon.utils.AsynBufferDemotionUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.domain.BaseDataDict;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.formula.functions.T;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class ReverseTest {

    private static final Logger logger = LoggerFactory.getLogger(ReverseTest.class);

    @Autowired
    private ReverseSendService reverseSendService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillService waybillService;
    @Autowired
    private AsynBufferDemotionUtil asynBufferDemotionUtil;
    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Test
    public void testOffline(){
        String s = "\"type\":1800,\"siteCode\":910,\"keyword1\":\"910\",\"keyword2\":\"\",\"body\":\"[{\\\"taskType\\\":1301,\\\"packageCode\\\":\\\"JDV000516761514-5-5-\\\",\\\"waybillCode\\\":\\\"\\\",\\\"boxCode\\\":\\\"JDV000516761514-5-5-\\\",\\\"receiveSiteCode\\\":0,\\\"sealBoxCode\\\":\\\"\\\",\\\"shieldsCarCode\\\":\\\"\\\",\\\"carCode\\\":\\\"\\\",\\\"sendUserCode\\\":\\\"\\\",\\\"sendUser\\\":\\\"\\\",\\\"batchCode\\\":\\\"910-39-20210421184426455\\\",\\\"weight\\\":\\\"0\\\",\\\"volume\\\":\\\"0\\\",\\\"exceptionType\\\":\\\"\\\",\\\"turnoverBoxCode\\\":\\\"\\\",\\\"operateType\\\":0,\\\"goodsType\\\":\\\"\\\",\\\"airNo\\\":\\\"\\\",\\\"transName\\\":\\\"\\\",\\\"railwayNo\\\":\\\"\\\",\\\"num\\\":0,\\\"demo\\\":\\\"\\\",\\\"bizSource\\\":null,\\\"id\\\":34,\\\"businessType\\\":10,\\\"userCode\\\":17331,\\\"userName\\\":\\\"吴有德\\\",\\\"siteCode\\\":910,\\\"siteName\\\":\\\"北京马驹桥分拣中心\\\",\\\"operateTime\\\":\\\"2021-04-17 10:58:10.880\\\"}]\",\"boxCode\":\"\",\"receiveSiteCode\":910}";
        TaskRequest task = JsonHelper.fromJson(s,TaskRequest.class);

        try {
            int index = 0;
            while (index++ <= 10){
                uccPropertyConfiguration.setOfflineCurrentLimitingCount(3);
                List<Boolean> r = new ArrayList<>();
                for(int i = 0 ; i< 10 ; i++){
                    r.add(asynBufferDemotionUtil.isDemotionOfSite(task.getSiteCode(),task.getBody()));
                }

                System.out.println(JsonHelper.toJson(r));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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
        try {
            InvokeResult<String> pictureUrlC = weightAndVolumeCheckService.searchExcessPicture("JDK000000055923", 910);
            System.out.println(pictureUrlC.getData());
            InvokeResult<List<String>> pictureUrlBList = weightAndVolumeCheckService.searchExcessPictureOfB2b("JDK000000055923", 910);
            for (String datum : pictureUrlBList.getData()) {
                System.out.println(datum);
            }
            Assert.assertTrue(true);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
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
        message.setText("{\"waybillCode\":\"JDX000155555052\",\"sendCode\":\"910-11252-20200212100902013,JDX000155555052\",\"receiveType\":10,\"canReceive\":1,\"operaterName\":\"bjtc\",\"operateTime\":\"2020-02-12 11:10:51\",\"detailList\":[{\"goodsNo\":\"EMG4398060033898\",\"goodsName\":\"测试商品\",\"batchNo\":\"JDX000155555052-1-2-\",\"quantity\":1},{\"goodsNo\":\"EMG4398060033899\",\"goodsName\":\"测试商品\",\"batchNo\":\"JDX000155555052-2-2-\",\"quantity\":1}]}");
        /*{"canReceive":1,"operateTime":"2019-08-26 09:32:19","operateType":1,"operator":"冯硕|fengshuo9","o
            rderId":101662618300,"orgId":611,"packageCode":"WA1165522865029517312",
            "pickwareCode":"Q566247436,Q566247437,Q566247439,Q566247440,Q566247441"}*/
        reverseReceiveConsumer.consume(message);
        try {
            //pickWareConsumer.consume(message);
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
