package ld;

import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsCancelSealCarBatchConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsSealCarStatusConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsTransWorkCarArriveConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsVehicleDetailStatusConsumer;
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

        while (true){
            System.out.println();
        }

    }
}
