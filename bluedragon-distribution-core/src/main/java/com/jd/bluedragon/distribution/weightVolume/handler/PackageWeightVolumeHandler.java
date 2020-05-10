package com.jd.bluedragon.distribution.weightVolume.handler;

import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.weight.domain.OpeSendObject;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDetail;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDto;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     安包裹称重的处理类：
 *     调用运单的包裹称重的处理waybillPackageApi.uploadOpe()
 *
 * @author wuzuxiang
 * @since 2020/1/8
 **/
@Service("packageWeightVolumeHandler")
public class PackageWeightVolumeHandler extends AbstractWeightVolumeHandler {

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    @Qualifier("dmsWeightSendMQ")
    private DefaultJMQProducer dmsWeightSendMQ;

    @Autowired
    private SiteService siteService;

    @Autowired
    private WeightAndVolumeCheckService weightAndVolumeCheckService;

    @Autowired
    WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Override
    protected void handlerWeighVolume(WeightVolumeEntity entity) {
        /* 处理称重对象 */
        entity.setWaybillCode(WaybillUtil.getWaybillCode(entity.getBarCode()));
        entity.setPackageCode(entity.getBarCode());
        if (entity.getLength() != null && entity.getHeight() != null && entity.getVolume() != null && !NumberHelper.gt0(entity.getVolume())) {
            entity.setVolume(entity.getHeight() * entity.getLength() * entity.getWidth());
        }

        //自动化称重量方设备上传的运单/包裹，且为一单一件，且上游站点/分拣中心操作过称重，才进行抽检
        if(FromSourceEnum.DMS_AUTOMATIC_MEASURE.equals(entity.getSourceCode()) && getPackNum(entity.getWaybillCode()) == 1
                && !isFirstWeightVolume(entity)){
            PackWeightVO packWeightVO = convertToPackWeightVO(entity);
            WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
            weightVolumeCollectDto.setWaybillCode(entity.getWaybillCode());
            weightVolumeCollectDto.setPackageCode(entity.getPackageCode());
            weightVolumeCollectDto.setFromSource(FromSourceEnum.DMS_AUTOMATIC_MEASURE.name());
            InvokeResult<Boolean> result = weightAndVolumeCheckService.insertAndSendMq(packWeightVO,weightVolumeCollectDto,new InvokeResult<Boolean>());
            if(result != null && InvokeResult.RESULT_SUCCESS_CODE != result.getCode()){
                logger.warn("包裹自动化体积重量抽检失败：{}",result.getMessage());
            }
        }

        PackOpeDto packOpeDto = new PackOpeDto();
        packOpeDto.setWaybillCode(entity.getWaybillCode());
        packOpeDto.setOpeType(1);//分拣操作环节赋值：1

        PackOpeDetail packOpeDetail = new PackOpeDetail();
        packOpeDetail.setPackageCode(entity.getPackageCode());
        packOpeDetail.setOpeSiteId(entity.getOperateSiteCode());
        packOpeDetail.setOpeSiteName(entity.getOperateSiteName());
        packOpeDetail.setOpeUserId(entity.getOperatorId());
        packOpeDetail.setOpeUserName(entity.getOperatorName());
        packOpeDetail.setpHigh(entity.getHeight());
        packOpeDetail.setpLength(entity.getLength());
        packOpeDetail.setpWidth(entity.getWidth());
        packOpeDetail.setpWeight(entity.getWeight());
        packOpeDetail.setOpeTime(DateHelper.formatDateTime(entity.getOperateTime()));
        packOpeDetail.setLongPackage(entity.getLongPackage());
        packOpeDto.setOpeDetails(Collections.singletonList(packOpeDetail));
        try {
            Map<String, Object> resultMap = waybillPackageManager.uploadOpe(JsonHelper.toJson(packOpeDto));
            if (resultMap != null && resultMap.containsKey("code")
                    && WeightResponse.WEIGHT_TRACK_OK == Integer.parseInt(resultMap.get("code").toString())) {
                logger.info("向运单系统回传包裹称重信息成功：{}", entity.getPackageCode());
            } else {
                logger.warn("向运单系统回传包裹称重信息失败：{}，运单返回值：{}", entity.getPackageCode(), JsonHelper.toJson(resultMap));
            }

            /* 原始逻辑：发送MQ，在未来的日志里看看能否合并 */
            OpeSendObject opeSend = new OpeSendObject();
            opeSend.setPackage_code(entity.getPackageCode());
            opeSend.setDms_site_id(entity.getOperateSiteCode());
            opeSend.setThisUpdateTime(entity.getOperateTime().getTime());
            opeSend.setWeight(entity.getWeight() == null? 0f : (float)(double)entity.getWeight());//精度丢失问题
            opeSend.setLength(entity.getLength() == null? 0f :(float)(double)entity.getLength());//精度丢失问题
            opeSend.setWidth(entity.getWidth() == null? 0f :(float)(double)entity.getWidth());//精度丢失问题
            opeSend.setHigh(entity.getHeight() == null? 0f :(float)(double)entity.getHeight());//精度丢失问题
            opeSend.setOpeUserId(entity.getOperatorId());
            opeSend.setOpeUserName(entity.getOperatorName());
            if (opeSend.getHigh() != null && opeSend.getLength() != null && opeSend.getWidth() != null) {
                //计算体积
                opeSend.setVolume(opeSend.getHigh() * opeSend.getLength() * opeSend.getWidth());
            }
            dmsWeightSendMQ.send(entity.getPackageCode(),JsonHelper.toJson(opeSend));
        } catch (RuntimeException | JMQException e) {
            logger.warn("按包裹称重量方发生异常，处理失败：{}",JsonHelper.toJson(entity));
        }
    }

    //获取包裹数量
    public Integer getPackNum(String waybillCode){
        InvokeResult<Integer> result = waybillCommonService.getPackNum(waybillCode);

        return result != null ? result.getData():0;
    }

    //是否为首次称重量方，根据运单/包裹的全程跟踪状态值是否为“-160”
    public boolean isFirstWeightVolume(WeightVolumeEntity entity){
        String waybillCode = WaybillUtil.getWaybillCode(entity.getBarCode());
        String state = "-160";
        List<PackageStateDto> packageStateDtos = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode,state);
        if(CollectionUtils.isEmpty(packageStateDtos)){
            return true;
        }else {
            return false;
        }
    }

    public PackWeightVO convertToPackWeightVO(WeightVolumeEntity entity){
        PackWeightVO packWeightVO = new PackWeightVO();
        packWeightVO.setWeight(entity.getWeight());
        packWeightVO.setLength(entity.getLength());
        packWeightVO.setWidth(entity.getWidth());
        packWeightVO.setHigh(entity.getHeight());
        packWeightVO.setCodeStr(entity.getBarCode());
        packWeightVO.setOperatorId(entity.getOperatorId());
        packWeightVO.setOperatorName(entity.getOperatorName());
        packWeightVO.setErpCode(entity.getOperatorCode());
        BaseStaffSiteOrgDto site = siteService.getSite(entity.getOperateSiteCode());
        packWeightVO.setOrganizationName(site.getOrgName());
        packWeightVO.setOrganizationCode(site.getOrgId());
        packWeightVO.setOperatorSiteName(entity.getOperateSiteName());
        packWeightVO.setOperatorSiteCode(entity.getOperateSiteCode());
        return packWeightVO;
    }
}
