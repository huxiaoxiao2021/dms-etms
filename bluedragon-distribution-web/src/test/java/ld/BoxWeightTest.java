package ld;

import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/26 20:19
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-web-context.xml")
public class BoxWeightTest {

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Test
    public void test(){
        String jsonstr = "{\"barCode\":\"BC1001210125100000000202\",\"sourceCode\":\"DMS_CLIENT_WEIGHT_VOLUME\",\"businessType\":\"BY_BOX\",\"operateSiteCode\":10098,\"operateSiteName\":\"北京双树直送第一车队-测试\",\"operatorId\":10053,\"operatorCode\":\"ldtest21\",\"operatorName\":\"ldtest21\",\"weight\":120.0,\"volume\":0.0,\"length\":3.00,\"width\":3.00,\"height\":25.00,\"operateTime\":1611113868022,\"longPackage\":0}";
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

    String boxW = "{\n" +
            "  \"barCode\" : \"BC1001210125100000000202\",\n" +
            "  \"boxCode\" : \"BC1001210125100000000202\",\n" +
            "  \"weight\" : 120.0,\n" +
            "  \"length\" : 3.0,\n" +
            "  \"width\" : 3.0,\n" +
            "  \"height\" : 25.0,\n" +
            "  \"volume\" : 225.0,\n" +
            "  \"businessType\" : \"BY_BOX\",\n" +
            "  \"sourceCode\" : \"DMS_CLIENT_WEIGHT_VOLUME\",\n" +
            "  \"operateSiteCode\" : 10098,\n" +
            "  \"operateSiteName\" : \"北京双树直送第一车队-测试\",\n" +
            "  \"operatorCode\" : \"ldtest21\",\n" +
            "  \"operatorId\" : 10053,\n" +
            "  \"operatorName\" : \"ldtest21\",\n" +
            "  \"operateTime\" : 1611113868022,\n" +
            "  \"longPackage\" : 0\n" +
            "}";

}
