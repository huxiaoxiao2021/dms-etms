package com.jd.bluedragon.distribution.sendCode.jsf;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.sendCode.DMSSendCodeJSFService;
import com.jd.bluedragon.distribution.sendCode.domain.HugeSendCodeEntity;
import com.jd.bluedragon.distribution.sendCode.domain.SendCodeDto;
import com.jd.bluedragon.distribution.sendCode.service.SendCodeService;
import com.jd.ql.dms.report.WeightVolSendCodeJSFService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolSendCodeSumVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     分拣批次号jsf服务实现类
 *
 * @author wuzuxiang
 * @since 2020/2/25
 **/
@Service("dmsSendCodeJSFService")
public class DMSSendCodeJSFServiceImpl implements DMSSendCodeJSFService {

    private static final Logger logger = LoggerFactory.getLogger(DMSSendCodeJSFServiceImpl.class);

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private WeightVolSendCodeJSFService weightVolSendCodeJSFService;

    @Override
    public InvokeResult<HugeSendCodeEntity> queryBigInfoBySendCode(String sendCode) {
        InvokeResult<HugeSendCodeEntity> result = new InvokeResult<>();
        result.success();

        HugeSendCodeEntity entity = new HugeSendCodeEntity();
        entity.setSendCode(sendCode);

        /* 1. 查询批次号重量体积信息 */
        BaseEntity<WeightVolSendCodeSumVo> weightVolSendCodeSumVoBaseEntity =
                weightVolSendCodeJSFService.sumWeightAndVolumeBySendCode(sendCode);
        if (weightVolSendCodeSumVoBaseEntity != null && weightVolSendCodeSumVoBaseEntity.isSuccess()
                && weightVolSendCodeSumVoBaseEntity.getData() != null) {
            entity.setVolume(weightVolSendCodeSumVoBaseEntity.getData().getPackageVolumeSum());
            entity.setWeight(weightVolSendCodeSumVoBaseEntity.getData().getPackageWeightSum());
        } else {
            logger.error("根据批次号查询批次下重量体积失败：{}", sendCode);
        }

        /* 2. 查询批次号的生鲜属性 */
        Boolean isFreshCode = sendCodeService.isFreshSendCode(sendCode);
        entity.setFreshCode(Boolean.TRUE.equals(isFreshCode));

        result.setData(entity);
        return result;
    }
}
