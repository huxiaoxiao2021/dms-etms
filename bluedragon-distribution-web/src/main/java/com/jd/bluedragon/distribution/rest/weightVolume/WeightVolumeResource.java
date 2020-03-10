package com.jd.bluedragon.distribution.rest.weightVolume;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

/**
 * <p>
 *     称重量方数据的上传接口
 *
 * @author wuzuxiang
 * @since 2020/1/15
 **/
@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class WeightVolumeResource {

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    /**
     * 单条上传接口
     * @param condition
     * @return
     */
    @POST
    @Path("/weightVolume/upload")
    public InvokeResult<Boolean> upload(WeightVolumeCondition condition) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        WeightVolumeEntity entity = new WeightVolumeEntity()
                .barCode(condition.getBarCode())
                .businessType(WeightVolumeBusinessTypeEnum.valueOf(condition.getBusinessType()))
                .sourceCode(FromSourceEnum.valueOf(condition.getSourceCode()))
                .height(condition.getHeight()).weight(condition.getWeight()).width(condition.getWidth()).length(condition.getLength()).volume(condition.getVolume())
                .operateSiteCode(condition.getOperateSiteCode()).operateSiteName(condition.getOperateSiteName())
                .operatorId(condition.getOperatorId()).operatorCode(condition.getOperatorCode()).operatorName(condition.getOperatorName())
                .operateTime(new Date(condition.getOperateTime())).longPackage(condition.getLongPackage());
        InvokeResult<Boolean> invokeResult = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.FALSE);
        result.setCode(invokeResult.getCode());
        result.setMessage(invokeResult.getMessage());
        result.setData(invokeResult.getData());
        return result;
    }

}
