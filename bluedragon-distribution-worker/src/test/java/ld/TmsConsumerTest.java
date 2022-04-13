package ld;

import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleTaskRequest;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsCancelSealCarBatchConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsSealCarStatusConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsTransWorkCarArriveConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsVehicleDetailStatusConsumer;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
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
        //封车
        String tmsSealCarStatusJson = "{\"sealCarCode\":\"SC22041100014898\",\"status\":10,\"operateUserCode\":\"bjxings\",\"operateUserName\":\"邢松\",\"operateTime\":\"2022-04-11 15:57:41\",\"sealCarType\":10,\"batchCodes\":[\"910-39-01595202205127129\"],\"transBookCode\":null,\"volume\":22.3112,\"weight\":343.22,\"transWay\":3,\"vehicleNumber\":\"京AB0D87\",\"operateSiteId\":910,\"operateSiteCode\":\"010F002\",\"operateSiteName\":\"北京马驹桥分拣中心6\",\"warehouseCode\":null,\"largeCargoDetails\":null,\"pieceCount\":null,\"source\":1}";
        //解封车
        String tmsSealCarStatusJson2 = "{\"sealCarCode\":\"SC22041100014898\",\"status\":20,\"operateUserCode\":\"bjxings\",\"operateUserName\":\"邢松\",\"operateTime\":\"2022-04-11 15:57:41\",\"sealCarType\":10,\"batchCodes\":[\"910-39-01595202205127129\"],\"transBookCode\":null,\"volume\":22.3112,\"weight\":343.22,\"transWay\":3,\"vehicleNumber\":\"京AB0D87\",\"operateSiteId\":910,\"operateSiteCode\":\"010F002\",\"operateSiteName\":\"北京马驹桥分拣中心6\",\"warehouseCode\":null,\"largeCargoDetails\":null,\"pieceCount\":null,\"source\":1}";


        Message message = new Message();
        message.setText(tmsSealCarStatusJson2);
        try {
            tmsSealCarStatusConsumer.consume(message);
        } catch (Exception e) {
            e.printStackTrace();
        }





    }


}
