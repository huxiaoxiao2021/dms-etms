package com.jd.bluedragon.distribution.rest.weightVolume;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.Objects;


/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/29 10:34 上午
 */
@ContextConfiguration( {"classpath:bak/distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class WeightVolumeResourceTest {

    @Autowired
    private WeightVolumeResource weightVolumeResource;

    private WeightVolumeRuleCheckDto condition;

    @Before
    public void setUp() throws Exception {
        condition = new WeightVolumeRuleCheckDto();
        condition.setBarCode("JDV000707119479");
        condition.setSourceCode(FromSourceEnum.DMS_CLIENT_BATCH_SORT_WEIGH_PRINT.name());
        condition.setCheckWeight(true);
        condition.setCheckLWH(false);
        condition.setCheckVolume(false);
        condition.setOperateSiteCode(910);
        condition.setOperateSiteName("马驹桥分拣中心");
        condition.setOperatorCode("bjxings");
        condition.setOperatorName("邢松");
    }

    /**
     * 包裹维度称重校验（有体积）
     */
    @Test
    public void packWeightVolumeRuleCheck() {
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE.name());
        condition.setWeight(1.3D);
        condition.setLength(10.0D);
        condition.setWidth(10.0D);
        condition.setHeight(10.0D);
        condition.setVolume(1000.0D);
        InvokeResult<Boolean> result = weightVolumeResource.weightVolumeRuleCheck(condition);
        Assert.isTrue(Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE));
    }
    /**
     * 包裹维度称重校验（无体积）
     */
    @Test
    public void packWeightVolumeRuleCheckWithNoVolume() {
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_PACKAGE.name());
        condition.setWeight(1.3D);
        condition.setLength(13.0D);
        condition.setWidth(13.0D);
        condition.setHeight(13.0D);
        InvokeResult<Boolean> result = weightVolumeResource.weightVolumeRuleCheck(condition);
        Assert.isTrue(Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE));
    }

    /**
     * 运单维度称重校验（有体积）
     */
    @Test
    public void waybillWeightVolumeRuleCheck() {
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name());
        condition.setWeight(1.3D);
        condition.setLength(10.0D);
        condition.setWidth(10.0D);
        condition.setHeight(10.0D);
        condition.setVolume(1000.0D);
        InvokeResult<Boolean> result = weightVolumeResource.weightVolumeRuleCheck(condition);
        Assert.isTrue(Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE));
    }
    /**
     * 运单维度称重校验（无体积）
     */
    @Test
    public void waybillWeightVolumeRuleCheckWithNoVolume() {
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL.name());
        condition.setWeight(1.3D);
        condition.setLength(13.0D);
        condition.setWidth(13.0D);
        condition.setHeight(13.0D);
        InvokeResult<Boolean> result = weightVolumeResource.weightVolumeRuleCheck(condition);
        Assert.isTrue(Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE));
    }

    /**
     * 箱号维度称重校验（有体积）
     */
    @Test
    public void boxWeightVolumeRuleCheck() {
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_BOX.name());
        condition.setWeight(1.3D);
        condition.setLength(10.0D);
        condition.setWidth(10.0D);
        condition.setHeight(10.0D);
        condition.setVolume(1000.0D);
        InvokeResult<Boolean> result = weightVolumeResource.weightVolumeRuleCheck(condition);
        Assert.isTrue(Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE));
    }
    /**
     * 箱号维度称重校验（无体积）
     */
    @Test
    public void boxWeightVolumeRuleCheckWithNoVolume() {
        condition.setBusinessType(WeightVolumeBusinessTypeEnum.BY_BOX.name());
        condition.setWeight(1.3D);
        condition.setLength(10.0D);
        condition.setWidth(10.0D);
        condition.setHeight(10.0D);
        InvokeResult<Boolean> result = weightVolumeResource.weightVolumeRuleCheck(condition);
        Assert.isTrue(Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE));
    }
}