package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.DWSCheckManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckCurrencyService;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * dws抽检消费处理
 *
 * @author hujiping
 * @date 2021/8/19 4:22 下午
 */
@Service("dmsSpotCheckDealConsumer")
public class DmsSpotCheckDealConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(DmsSpotCheckDealConsumer.class);

    @Autowired
    private SpotCheckCurrencyService spotCheckCurrencyService;

    @Qualifier("dwsCheckManager")
    @Autowired
    private DWSCheckManager dwsCheckManager;

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DmsSpotCheckDealConsumer.consume", Constants.UMP_APP_NAME_DMSWORKER,false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                log.warn("dws抽检消息dws_spot_check 非JSON格式，内容为【{}】", message.getText());
                return;
            }
            PackWeightVO packWeightVO = JsonHelper.fromJsonUseGson(message.getText(), PackWeightVO.class);
            if (packWeightVO == null) {
                log.warn("dws抽检消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            // 校验设备称重是否合规
            if(!spotCheckDealService.spotCheckIssueIsRelyOnMachineStatus(packWeightVO.getOperatorSiteCode())
                    && !checkEquipmentWeightIsAccurate(packWeightVO)){
                log.warn("设备:{}的称重不合规，此次称重单号:{}", packWeightVO.getMachineCode(), packWeightVO.getCodeStr());
                return;
            }
            spotCheckCurrencyService.spotCheckDeal(transferToSpotCheckDto(packWeightVO));
        }catch (SpotCheckSysException e){
            log.warn("抽检异常进行并MQ进行重试", e);
            throw e;
        }catch(Exception e){
            Profiler.functionError(info);
            log.error("处理dws抽检失败, 消息体:{}", message.getText(), e);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private boolean checkEquipmentWeightIsAccurate(PackWeightVO packWeightVO) {
        if(StringUtils.isEmpty(packWeightVO.getMachineCode()) || packWeightVO.getOperateTimeMillis() == 0){
            return true;
        }
        return dwsCheckManager.checkDWSMachineWeightIsAccurate(packWeightVO.getMachineCode(), new Date(packWeightVO.getOperateTimeMillis()));
    }

    private SpotCheckDto transferToSpotCheckDto(PackWeightVO packWeightVO) {
        SpotCheckDto spotCheckDto = new SpotCheckDto();
        spotCheckDto.setBarCode(packWeightVO.getCodeStr());
        spotCheckDto.setSpotCheckSourceFrom(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName());
        spotCheckDto.setWeight(packWeightVO.getWeight());
        spotCheckDto.setLength(packWeightVO.getLength());
        spotCheckDto.setWidth(packWeightVO.getWidth());
        spotCheckDto.setHeight(packWeightVO.getHigh());
        spotCheckDto.setVolume(packWeightVO.getVolume());
        spotCheckDto.setOrgId(packWeightVO.getOrganizationCode());
        spotCheckDto.setOrgName(packWeightVO.getOrganizationName());
        spotCheckDto.setSiteCode(packWeightVO.getOperatorSiteCode());
        spotCheckDto.setSiteName(packWeightVO.getOperatorSiteName());
        spotCheckDto.setOperateUserErp(packWeightVO.getErpCode());
        spotCheckDto.setOperateUserName(packWeightVO.getOperatorName());
        spotCheckDto.setMachineCode(packWeightVO.getMachineCode());
        spotCheckDto.setDimensionType(SpotCheckDimensionEnum.SPOT_CHECK_PACK.getCode());
        return spotCheckDto;
    }
}
