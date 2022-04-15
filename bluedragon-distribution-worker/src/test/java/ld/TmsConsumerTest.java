package ld;

import com.google.common.collect.Maps;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleTaskRequest;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.*;
import com.jd.bluedragon.distribution.jy.dto.task.UnloadVehicleMqDto;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
                "    \"version\": 1,\n" +
                "    \"yn\": 1\n" +
                "}";

        UnloadVehicleMqDto mqDto = JsonHelper.fromJson(body, UnloadVehicleMqDto.class);
        Map<String, Object> extendMap = Maps.newHashMap();
        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_LOST_CNT, 1);
        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_SCAN_PROGRESS, 100);
        extendMap.put(UnloadVehicleMqDto.EXTEND_KEY_DAMAGE_CNT, 10);
        mqDto.setExtendInfo(extendMap);

        Message message = new Message();
        message.setText(JsonHelper.toJson(mqDto));

        initUnloadVehicleConsumer.consume(message);
    }


}
