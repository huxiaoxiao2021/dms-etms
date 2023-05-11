package com.jd.bluedragon.distribution.rest.weightVolume;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeUploadResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeCondition;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleCheckDto;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.dms.logger.annotation.BusinessLog;
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
     * 称重量方数据校验
     * @param condition
     * @return
     */
    @POST
    @Path("/weightVolume/weightVolumeRuleCheck")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1018, operateType = 101801)
    public InvokeResult<Boolean> weightVolumeRuleCheck(WeightVolumeRuleCheckDto condition) {
        return dmsWeightVolumeService.weightVolumeRuleCheck(condition);
    }
    /**
     * 称重上传校验接口
     * @param condition
     * @return
     */
    @POST
    @Path("/weightVolume/checkBeforeUpload")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1018, operateType = 101803)
    public JdResult<WeightVolumeUploadResult> checkBeforeUpload(WeightVolumeCondition condition) {
    	return dmsWeightVolumeService.checkBeforeUpload(condition);
    }    
    /**
     * 称重上传校验并上传接口
     * @param condition
     * @return
     */
    @POST
    @Path("/weightVolume/checkAndUpload")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1018, operateType = 101804)
    public JdResult<WeightVolumeUploadResult> checkAndUpload(WeightVolumeCondition condition) {
    	JdResult<WeightVolumeUploadResult> result = dmsWeightVolumeService.checkBeforeUpload(condition);
    	//校验成功，上传处理
    	if(result != null 
    			&& result.isSucceed()
    			&& result.getData() != null
    			&& Boolean.TRUE.equals(result.getData().getCheckResult())) {
    		InvokeResult<Boolean> uploadResult = upload(condition);
    		if(uploadResult != null 
    				&& uploadResult.getCode() == InvokeResult.RESULT_SUCCESS_CODE) {
    			result.toSuccess("上传成功！");
    		}else if(uploadResult != null){
    			result.toFail(uploadResult.getMessage());
    		} else {
    			result.toFail("上传失败！");
    		}
    	}
    	return result;
    }
    /**
     * 单条上传接口
     * @param condition
     * @return
     */
    @POST
    @Path("/weightVolume/upload")
    @BusinessLog(sourceSys = Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB, bizType = 1018, operateType = 101802)
    public InvokeResult<Boolean> upload(WeightVolumeCondition condition) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        // 称重数据超额处理
        String remark = dmsWeightVolumeService.weightVolumeExcessDeal(condition);
        WeightVolumeEntity entity = new WeightVolumeEntity()
                .barCode(condition.getBarCode())
                .businessType(WeightVolumeBusinessTypeEnum.valueOf(condition.getBusinessType()))
                .sourceCode(FromSourceEnum.valueOf(condition.getSourceCode()))
                .height(condition.getHeight()).weight(condition.getWeight()).width(condition.getWidth()).length(condition.getLength()).volume(condition.getVolume())
                .operateSiteCode(condition.getOperateSiteCode()).operateSiteName(condition.getOperateSiteName())
                .operatorId(condition.getOperatorId()).operatorCode(condition.getOperatorCode()).operatorName(condition.getOperatorName())
                .operateTime(new Date(condition.getOperateTime())).longPackage(condition.getLongPackage())
                .machineCode(condition.getMachineCode()).remark(remark);
        entity.setOverLengthAndWeightEnable(condition.getOverLengthAndWeightEnable());
        entity.setOverLengthAndWeightTypes(condition.getOverLengthAndWeightTypes());
        InvokeResult<Boolean> invokeResult = dmsWeightVolumeService.dealWeightAndVolume(entity, Boolean.FALSE);
        result.setCode(invokeResult.getCode());
        result.setMessage(invokeResult.getMessage());
        result.setData(invokeResult.getData());
        return result;
    }

}
