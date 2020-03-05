package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.report.weightVolumeFlow.WeightVolumeFlowJSFService;
import com.jd.ql.dms.report.weightVolumeFlow.domain.WeightVolumeFlowEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
public abstract class AbstractWeightVolumeHandler implements IWeightVolumeHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected WeightVolumeFlowJSFService weightVolumeFlowJSFService;

    @Override
    public InvokeResult<Boolean> handlerOperateWeightVolume(WeightVolumeEntity entity) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        result.setData(Boolean.TRUE);
        /* 处理称重量方任务 */
        handlerWeighVolume(entity);

        /* 处理之后记录流水 */
        postHandler(entity);

        return result;
    }

    protected abstract void handlerWeighVolume(WeightVolumeEntity entity);

    protected void postHandler(WeightVolumeEntity entity) {
        WeightVolumeFlowEntity weightVolumeEntity = new WeightVolumeFlowEntity();
        BeanHelper.copyProperties(weightVolumeEntity, entity);
        weightVolumeFlowJSFService.recordWeightVolumeFlow(weightVolumeEntity);
    }
}
