package com.jd.bluedragon.distribution.weightVolume.service;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.DMSWeightVolumeJSFService;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeJSFEntity;
import com.jd.bluedragon.utils.BeanHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     分拣称重量方处理JSF实现
 *
 * @author wuzuxiang
 * @since 2020/1/10
 **/
@Service("dmsWeightVolumeJSFService")
public class DMSWeightVolumeJSFServiceImpl implements DMSWeightVolumeJSFService {

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Override
    public InvokeResult<Boolean> dealSyncWeightVolume(WeightVolumeJSFEntity entity) {
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.success();
        invokeResult.setData(Boolean.TRUE);

        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity();
        BeanHelper.copyProperties(weightVolumeEntity, entity);
        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> result =
                dmsWeightVolumeService.dealWeightAndVolume(weightVolumeEntity);
        if (null != result) {
            invokeResult.setData(result.getData());
            invokeResult.setCode(result.getCode());
            invokeResult.setMessage(result.getMessage());
            return invokeResult;
        }
        return invokeResult;
    }

    @Override
    public InvokeResult<Boolean> dealAsyncWeightVolume(WeightVolumeJSFEntity entity) {
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        invokeResult.success();
        invokeResult.setData(Boolean.TRUE);

        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity();
        BeanHelper.copyProperties(weightVolumeEntity, entity);
        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> result =
                dmsWeightVolumeService.dealWeightAndVolume(weightVolumeEntity, Boolean.FALSE);
        if (null != result) {
            invokeResult.setData(result.getData());
            invokeResult.setCode(result.getCode());
            invokeResult.setMessage(result.getMessage());
            return invokeResult;
        }
        return invokeResult;
    }
}
