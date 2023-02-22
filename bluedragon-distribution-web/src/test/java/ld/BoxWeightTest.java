package ld;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.economic.domain.EconomicNetException;
import com.jd.bluedragon.distribution.economic.service.IEconomicNetService;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.handler.WeightVolumeHandlerStrategy;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.utils.AsynBufferDemotionUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/26 20:19
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class BoxWeightTest {

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Autowired
    private IEconomicNetService economicNetService;

    @Autowired
    private ReceiveService receiveService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AsynBufferDemotionUtil asynBufferDemotionUtil;
    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private WeightVolumeHandlerStrategy weightVolumeHandlerStrategy;

    @Test
    public void testOffline(){
        String s = "{\"type\":1800,\"siteCode\":910,\"keyword1\":\"910\",\"keyword2\":\"\",\"body\":\"[{\\\"taskType\\\":1301,\\\"packageCode\\\":\\\"JDV000516761514-5-5-\\\",\\\"waybillCode\\\":\\\"\\\",\\\"boxCode\\\":\\\"JDV000516761514-5-5-\\\",\\\"receiveSiteCode\\\":0,\\\"sealBoxCode\\\":\\\"\\\",\\\"shieldsCarCode\\\":\\\"\\\",\\\"carCode\\\":\\\"\\\",\\\"sendUserCode\\\":\\\"\\\",\\\"sendUser\\\":\\\"\\\",\\\"batchCode\\\":\\\"910-39-20210421184426455\\\",\\\"weight\\\":\\\"0\\\",\\\"volume\\\":\\\"0\\\",\\\"exceptionType\\\":\\\"\\\",\\\"turnoverBoxCode\\\":\\\"\\\",\\\"operateType\\\":0,\\\"goodsType\\\":\\\"\\\",\\\"airNo\\\":\\\"\\\",\\\"transName\\\":\\\"\\\",\\\"railwayNo\\\":\\\"\\\",\\\"num\\\":0,\\\"demo\\\":\\\"\\\",\\\"bizSource\\\":null,\\\"id\\\":34,\\\"businessType\\\":10,\\\"userCode\\\":17331,\\\"userName\\\":\\\"吴有德\\\",\\\"siteCode\\\":910,\\\"siteName\\\":\\\"北京马驹桥分拣中心\\\",\\\"operateTime\\\":\\\"2021-04-17 10:58:10.880\\\"}]\",\"boxCode\":\"\",\"receiveSiteCode\":910}";
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
    public void testReceive(){
        TaskRequest request = JsonHelper.fromJson(taskReqJson,TaskRequest.class);
        Object[] array = JsonHelper.jsonToArray(request.getBody(), Object[].class);

        for(Object element : array){
            String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                    + JsonHelper.toJson(element)
                    + Constants.PUNCTUATION_CLOSE_BRACKET;
            Task task = this.taskService.toTask(request, eachJson);
            Receive receive = receiveService.taskToRecieve(task);
            if(receive != null){
                receiveService.doReceiveing(receive);
            }
        }

    }

    @Test
    public void test(){
        String jsonstr = "{\"barCode\":\"BC1001210125100000000202\",\"sourceCode\":\"DMS_CLIENT_WEIGHT_VOLUME\",\"businessType\":\"BY_BOX\",\"operateSiteCode\":10098,\"operateSiteName\":\"北京双树直送第一车队-测试\",\"operatorId\":10053,\"operatorCode\":\"ldtest30\",\"operatorName\":\"ldtest30\",\"weight\":120.0,\"volume\":0.0,\"length\":3.00,\"width\":3.00,\"height\":25.00,\"operateTime\":1611113868022,\"longPackage\":0}";
        WeightVolumeCondition condition = JsonHelper.fromJson(jsonstr,WeightVolumeCondition.class);
        WeightVolumeEntity entity = new WeightVolumeEntity()
                .barCode(condition.getBarCode())
                .businessType(WeightVolumeBusinessTypeEnum.valueOf(condition.getBusinessType()))
                .sourceCode(FromSourceEnum.valueOf(condition.getSourceCode()))
                .height(condition.getHeight()).weight(condition.getWeight()).width(condition.getWidth()).length(condition.getLength()).volume(condition.getVolume())
                .operateSiteCode(condition.getOperateSiteCode()).operateSiteName(condition.getOperateSiteName())
                .operatorId(condition.getOperatorId()).operatorCode(condition.getOperatorCode()).operatorName(condition.getOperatorName())
                .operateTime(new Date(condition.getOperateTime())).longPackage(condition.getLongPackage());
        dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.TRUE);
    }

    @Test
    public void test2(){
        WeightVolumeEntity weightVolumeEntity = JsonHelper.fromJson(boxW,WeightVolumeEntity.class);
        economicNetService.boxWeightVolumeListener(weightVolumeEntity);

        WeightVolumeEntity waybillWE= JsonHelper.fromJson(waybillW,WeightVolumeEntity.class);
        //经济网单独称重需重新触发按箱称重均摊
        economicNetService.packageOrWaybillWeightVolumeListener(waybillWE);
    }


    @Test
    public void test3(){

        String json = "{\n" +
                "    \"barCode\":\"JDVF00003052300-1-1-\",\n" +
                "    \"weight\":1,\n" +
                "    \"length\":10000,\n" +
                "    \"width\":1,\n" +
                "    \"height\":0,\n" +
                "    \"volume\":10000,\n" +
                "    \"businessType\":\"BY_PACKAGE\",\n" +
                "    \"sourceCode\":\"DPRH\",\n" +
                "    \"operatorCode\":\"-1\",\n" +
                "    \"operatorId\":-1,\n" +
                "    \"operateSiteCode\":-1,\n" +
                "    \"operatorName\":\"任斌\",\n" +
                "    \"operateTime\":\"2022-12-21 13:40:38\"\n" +
                "}";
        WeightVolumeEntity weightVolumeEntity = JsonHelper.fromJson(json,WeightVolumeEntity.class);

        weightVolumeHandlerStrategy.doHandler(weightVolumeEntity);
    }

    @Test
    public void test4(){
        List<WeightVolumeEntity> splits = null;
        try{
            splits = JsonHelper.jsonToList(splitP, WeightVolumeEntity.class);
        }catch (Exception e){
        }

        economicNetService.dealBoxSplitWeightOfPage(splits);

    }

    String taskReqJson = "{\"type\":1110,\"siteCode\":910,\"keyword1\":\"910\",\"keyword2\":\"BC1001210127140000004211\",\"body\":\"[{\\\"shieldsCarCode\\\":\\\"\\\",\\\"carCode\\\":\\\"\\\",\\\"sealBoxCode\\\":\\\"\\\",\\\"packOrBox\\\":\\\"BC1001210127140000004211\\\",\\\"turnoverBoxCode\\\":\\\"\\\",\\\"queueNo\\\":\\\"\\\",\\\"departureCarId\\\":\\\"\\\",\\\"shieldsCarTime\\\":\\\"\\\",\\\"id\\\":6,\\\"businessType\\\":10,\\\"userCode\\\":17331,\\\"userName\\\":\\\"吴有德\\\",\\\"siteCode\\\":910,\\\"siteName\\\":\\\"北京马驹桥分拣中心\\\",\\\"operateTime\\\":\\\"2021-01-27 17:49:35.253\\\"}]\",\"boxCode\":\"\",\"receiveSiteCode\":910}";

    String splitP = "[ {\n" +
            "  \"barCode\" : \"ZYY000000000921-1-1-\",\n" +
            "  \"waybillCode\" : \"ZYY000000000921\",\n" +
            "  \"boxCode\" : \"BC1001210125100000000202\",\n" +
            "  \"weight\" : 30.0,\n" +
            "  \"length\" : 3.0,\n" +
            "  \"width\" : 3.0,\n" +
            "  \"height\" : 6.3,\n" +
            "  \"businessType\" : \"BY_PACKAGE\",\n" +
            "  \"sourceCode\" : \"ENET_BOX_SPLIT_PACKAGE\",\n" +
            "  \"operateSiteCode\" : 10098,\n" +
            "  \"operateSiteName\" : \"北京双树直送第一车队-测试\",\n" +
            "  \"operatorCode\" : \"ldtest30\",\n" +
            "  \"operatorId\" : 10053,\n" +
            "  \"operatorName\" : \"ldtest30\",\n" +
            "  \"operateTime\" : 1611113868022\n" +
            "} ]";

    String waybillW = "{\n" +
            "  \"barCode\" : \"ZYY000000000921\",\n" +
            "  \"waybillCode\" : \"ZYY000000000921\",\n" +
            "  \"weight\" : 99.0,\n" +
            "  \"volume\" : 112.0,\n" +
            "  \"businessType\" : \"BY_WAYBILL\",\n" +
            "  \"sourceCode\" : \"DMS_CLIENT_WEIGHT_VOLUME\",\n" +
            "  \"operateSiteCode\" : 10098,\n" +
            "  \"operateSiteName\" : \"北京双树直送第一车队-测试\",\n" +
            "  \"operatorCode\" : \"ldtest30\",\n" +
            "  \"operatorId\" : 10053,\n" +
            "  \"operatorName\" : \"ldtest30\",\n" +
            "  \"operateTime\" : 1611113868022,\n" +
            "  \"longPackage\" : 0\n" +
            "}";


    String boxW = "{\n" +
            "  \"barCode\" : \"BC1001210127110000003504\",\n" +
            "  \"boxCode\" : \"BC1001210127110000003504\",\n" +
            "  \"weight\" : 120.0,\n" +
            "  \"length\" : 3.0,\n" +
            "  \"width\" : 3.0,\n" +
            "  \"height\" : 25.0,\n" +
            "  \"volume\" : 225.0,\n" +
            "  \"businessType\" : \"BY_BOX\",\n" +
            "  \"sourceCode\" : \"DMS_CLIENT_WEIGHT_VOLUME\",\n" +
            "  \"operateSiteCode\" : 910,\n" +
            "  \"operateSiteName\" : \"北京双树直送第一车队-测试\",\n" +
            "  \"operatorCode\" : \"ldtest35\",\n" +
            "  \"operatorId\" : 10053,\n" +
            "  \"operatorName\" : \"ldtest35\",\n" +
            "  \"operateTime\" : 1611113868022,\n" +
            "  \"longPackage\" : 0\n" +
            "}";

}
