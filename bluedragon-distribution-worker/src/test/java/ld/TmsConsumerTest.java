package ld;

import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsCancelSealCarBatchConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsSealCarStatusConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsTransWorkCarArriveConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsVehicleDetailStatusConsumer;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

    @Test
    public void testTmsMq(){
        String tmsSealCarStatusJson = "{\"sealCarCode\":\"SC22040419643118\",\"status\":10,\"operateUserCode\":\"chenyifei6\",\"operateUserName\":\"陈毅飞\",\"operateTime\":\"2022-04-04 19:10:54\",\"sealCarType\":30,\"batchCodes\":[\"R202204041106221342\",\"R1510931730171867136\",\"R202204041145461342\"],\"transBookCode\":\"TB22040430009175\",\"volume\":null,\"weight\":null,\"transWay\":2,\"vehicleNumber\":\"京AAJ7385\",\"operateSiteId\":1342,\"operateSiteCode\":\"010Y059\",\"operateSiteName\":\"北京上庄营业部\",\"warehouseCode\":null,\"largeCargoDetails\":null,\"pieceCount\":null,\"source\":2,\"sealCarInArea\":null}\n";

        Message message = new Message();
        message.setText(tmsSealCarStatusJson);
        try {
            tmsSealCarStatusConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
