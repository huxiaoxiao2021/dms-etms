package ld;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.consumer.reverse.ReverseReceiveConsumer;
import com.jd.bluedragon.distribution.reverse.domain.Product;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
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

        String sendCode = "910-14178-20190223165014011";
        tTask.setBoxCode(sendCode);
        tTask.setBody(sendCode);
        tTask.setCreateSiteCode(910);
        tTask.setKeyword2("20");
        tTask.setReceiveSiteCode(14178);
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


    @Test
    public void WaybillStatus(){

        Task tTask = new Task();

        tTask.setBody("{\n" +
                "  \"sendCode\" : \"910-11252-20190328135641090\",\n" +
                "  \"waybillCode\" : \"JDVA00001757689\",\n" +
                "  \"packageCode\" : \"JDVA00001757689\",\n" +
                "  \"createSiteCode\" : 11252,\n" +
                "  \"createSiteType\" : 903,\n" +
                "  \"createSiteName\" : \"北京服装备件库\",\n" +
                "  \"receiveSiteCode\" : 11252,\n" +
                "  \"receiveSiteType\" : 903,\n" +
                "  \"receiveSiteName\" : \"北京服装备件库\",\n" +
                "  \"operatorId\" : -1,\n" +
                "  \"operator\" : \"bjtc\",\n" +
                "  \"operateType\" : 900,\n" +
                "  \"operateTime\" : 1553753239000\n" +
                "}");


        tTask.setOwnSign("LD");
        tTask.setKeyword1("JDVA00001757689");

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

    @Test
    public void testMQ(){
        Message message = new Message();
        message.setText("{\"waybillCode\":\"JDVA00001757689\",\"sendCode\":\"910-11252-20190328135641090\",\"receiveType\":8,\"canReceive\":2,\"operaterName\":\"bjtc\",\"operateTime\":\"2019-03-29 09:09:53\",\"detailList\":[{\"goodsNo\":\"EMG4398059875755\",\"goodsName\":\"商品1\",\"batchNo\":\"ISV-C-20190327112208-1508-1\",\"quantity\":0},{\"goodsNo\":\"EMG4398059875756\",\"goodsName\":\"商品1\",\"batchNo\":\"ISV-C-20190327111908-2532-1\",\"quantity\":1}]}\n");
        reverseReceiveConsumer.consume(message);
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
