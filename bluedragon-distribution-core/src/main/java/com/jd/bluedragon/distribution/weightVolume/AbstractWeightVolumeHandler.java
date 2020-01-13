package com.jd.bluedragon.distribution.weightVolume;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.report.weightVolumeFlow.WeightVolumeFlowJSFService;
import com.jd.ql.dms.report.weightVolumeFlow.domain.WeightVolumeFlowEntity;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
        /* 校验参数 */
        if (!checkWeightVolumeParam(entity)) {
            logger.warn("称重量方处理失败，参数有误：{}", JsonHelper.toJson(entity));
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, InvokeResult.PARAM_ERROR);
            result.setData(Boolean.FALSE);
            return result;
        }
        /* 处理称重量方任务 */
        handlerWeighVolume(entity);

        /* 处理之后记录流水 */
        postHandler(entity);

        return result;
    }

    protected boolean checkWeightVolumeParam(WeightVolumeEntity entity) {
        /* 校验基础参数 */
        if (null == entity || StringHelper.isEmpty(entity.getBarCode())
                || StringHelper.isEmpty(entity.getOperateSiteName()) || entity.getOperateSiteCode() == null
                || StringHelper.isEmpty(entity.getOperatorCode()) || StringHelper.isEmpty(entity.getOperatorName())
                || entity.getSourceCode() == null || entity.getBusinessType() == null || entity.getOperateTime() == null) {
            logger.warn("分拣称重量方处理的消息实体部分数据不存在，参数错误：{}",JsonHelper.toJson(entity));
            return Boolean.FALSE;
        }

        /* 校验称重量方信息的存在性 */
        if (entity.getHeight() == null && entity.getLength() == null && entity.getWidth() == null
                && entity.getWeight() == null && entity.getVolume() == null) {
            logger.warn("分拣称重量方处理的消息实体无称重量方数据，参数错误：{}",JsonHelper.toJson(entity));
            return Boolean.FALSE;
        }

        /* 校验称重量方信息的有效性，可以不传但是不能为空 */
        if (entity.getWeight() != null && entity.getWeight() < 0) {
            logger.warn("分拣称重量方处理的消息重量信息小于0，参数错误：{}", JsonHelper.toJson(entity));
            return Boolean.FALSE;
        }
        if (entity.getLength() != null && entity.getLength() < 0) {
            logger.warn("分拣称重量方处理的消息长度信息小于0，参数错误：{}", JsonHelper.toJson(entity));
            return Boolean.FALSE;
        }
        if (entity.getWidth() != null && entity.getWidth() < 0) {
            logger.warn("分拣称重量方处理的消息宽信息小于0，参数错误：{}", JsonHelper.toJson(entity));
            return Boolean.FALSE;
        }
        if (entity.getHeight() != null && entity.getHeight() < 0) {
            logger.warn("分拣称重量方处理的消息高度信息小于0，参数错误：{}", JsonHelper.toJson(entity));
            return Boolean.FALSE;
        }
        if (entity.getVolume() != null && entity.getVolume() < 0) {
            logger.warn("分拣称重量方处理的消息体积信息小于0，参数错误：{}", JsonHelper.toJson(entity));
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    protected abstract void handlerWeighVolume(WeightVolumeEntity entity);

    protected void postHandler(WeightVolumeEntity entity) {
        WeightVolumeFlowEntity weightVolumeEntity = new WeightVolumeFlowEntity();
        BeanHelper.copyProperties(weightVolumeEntity, entity);
        weightVolumeFlowJSFService.recordWeightVolumeFlow(weightVolumeEntity);
    }
}
