package com.jd.bluedragon.distribution;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateStatusEnum;
import com.jd.bluedragon.distribution.consumer.debon.DebonReturnScheduleConsumer;
import com.jd.bluedragon.distribution.consumer.spotCheck.DmsSpotCheckDealConsumer;
import com.jd.bluedragon.distribution.consumer.spotCheck.DwsCalibrateDealSpotCheckConsumer;
import com.jd.bluedragon.distribution.consumer.weight.DwsWeightVolumeCalibrateConsumer;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.UUID;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/9/7 6:18 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class DmsSpotCheckDealConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(DmsSpotCheckDealConsumerTest.class);

    @Autowired
    private DmsSpotCheckDealConsumer dmsSpotCheckDealConsumer;

    @Test
    public void consumer() {
        try {
            PackWeightVO packWeightVO = new PackWeightVO();
            packWeightVO.setCodeStr("JDX000221717365-1-3-");
            packWeightVO.setOrganizationCode(6);
            packWeightVO.setOrganizationName("总公司");
            packWeightVO.setLength(10.0D);
            packWeightVO.setWidth(10.0D);
            packWeightVO.setHigh(10.0D);
            packWeightVO.setWeight(1.3D);
            packWeightVO.setVolume(1000.0D);
            packWeightVO.setErpCode("wuyoude");
            packWeightVO.setOperatorId(17331);
            packWeightVO.setOperatorName("吴有德");
            packWeightVO.setOperatorSiteCode(910);
            packWeightVO.setOperatorSiteName("北京马驹桥分拣中心");
            packWeightVO.setOperateTimeMillis(System.currentTimeMillis());

            Message message = new Message();
            message.setText(JsonHelper.toJson(packWeightVO));

            dmsSpotCheckDealConsumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("dws抽检mq处理异常!", e);
            Assert.fail();
        }
    }

    @Autowired
    private DwsWeightVolumeCalibrateConsumer dwsWeightVolumeCalibrateConsumer;

    @Test
    public void consumer1() {
        try {
            DwsMachineCalibrateMQ dwsMachineCalibrateMQ = new DwsMachineCalibrateMQ();
            dwsMachineCalibrateMQ.setBusinessId(UUID.randomUUID().toString());
            dwsMachineCalibrateMQ.setCalibrateStatus(JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode());
            dwsMachineCalibrateMQ.setMachineCode("ylq06261-DWS001");
            dwsMachineCalibrateMQ.setMachineStatus(JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode());
            dwsMachineCalibrateMQ.setCalibrateTime(1664444491388L);
            dwsMachineCalibrateMQ.setPreviousCalibrateTime(1664434800000L);

            Message message = new Message();
            message.setText(JsonHelper.toJson(dwsMachineCalibrateMQ));

            dwsWeightVolumeCalibrateConsumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("设备校准mq处理异常!", e);
            Assert.fail();
        }
    }

    @Autowired
    private DwsCalibrateDealSpotCheckConsumer dwsCalibrateDealSpotCheckConsumer;

    @Test
    public void consumer2() {
        try {

            WeightVolumeSpotCheckDto weightVolumeSpotCheckDto = new WeightVolumeSpotCheckDto();

            Message message = new Message();
            message.setText(JsonHelper.toJson(weightVolumeSpotCheckDto));

            dwsCalibrateDealSpotCheckConsumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("设备校准mq处理异常!", e);
            Assert.fail();
        }
    }

    @Autowired
    private DebonReturnScheduleConsumer debonReturnScheduleConsumer;

    @Test
    public void  debonReturnScheduleConsumeTest() throws Exception{

        ReassignWaybill reassignWaybill = new ReassignWaybill();
        reassignWaybill.setPackageBarcode("JD0003420119555-1-1-");
        reassignWaybill.setAddress("北京朝阳区三环以内未访问");
        reassignWaybill.setReceiveSiteCode(910);
        reassignWaybill.setReceiveSiteName("北京马驹桥分拣中心");
        reassignWaybill.setChangeSiteCode(14508);
        reassignWaybill.setChangeSiteName("城配揽派调度车队");
        reassignWaybill.setOperateTime(new Date());
        reassignWaybill.setUserCode(1);
        reassignWaybill.setUserName("德邦运营人员");
        reassignWaybill.setSiteCode(39);
        reassignWaybill.setSiteName("石景山营业部");
        reassignWaybill.setWaybillCode("JD0003420119555");
        reassignWaybill.setInterfaceType(100111);

        Message message = new Message();
        message.setText(JsonHelper.toJson(reassignWaybill));
        debonReturnScheduleConsumer.consume(message);
    }
}
