package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.inspection.exception.WayBillCodeIllegalException;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.distribution.material.dto.RecycleMaterialOperateRecordPublicDto;
import com.jd.bluedragon.distribution.material.enums.MaterialFlowActionDetailV2Enum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.common.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @ClassName InspectionWaybillTaskExecutor
 * @Description
 * @Author wyh
 * @Date 2020/9/25 9:32
 **/
@Service("inspectionWaybillTaskExecutor")
public class InspectionWaybillTaskExecutor extends InspectionTaskCommonExecutor {

    @Autowired
    private InspectionNotifyService inspectionNotifyService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("recycleMaterialOperateRecordProducer")
    private DefaultJMQProducer recycleMaterialOperateRecordProducer;

    @Override
    protected InspectionTaskExecuteContext prepare(InspectionRequest request) {
        InspectionTaskExecuteContext context = new InspectionTaskExecuteContext();
        context.setInspectionRequest(request);
        context.setPassCheck(true);
        if (null == request) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("验货JSON解析后对象为空");
            }
            context.setPassCheck(false);
            return context;
        }

        builderSite(request,context);

        String code = request.getPackageBarOrWaybillCode();
        /** 是否按运单验货 */
        boolean isByWayBillCode = false;
        // 如果是包裹号获取取件单号，则存在包裹号属性中
        if (WaybillUtil.isPackageCode(code)|| WaybillUtil.isSurfaceCode(code)) {
            request.setPackageBarcode(code);
        }
        else if (WaybillUtil.isWaybillCode(code)) {// 否则为运单号
            isByWayBillCode = true;
            request.setWaybillCode(code);
        }
        else {
            String errorMsg = "验货条码不符合规则:" + code;
            LOGGER.warn(errorMsg);
            throw new WayBillCodeIllegalException(errorMsg);
        }

        String waybillCode = WaybillUtil.getWaybillCode(request.getPackageBarOrWaybillCode());

        BigWaybillDto bigWaybillDto = getWaybill(waybillCode, isByWayBillCode);
        if (bigWaybillDto == null) {    //没有查到运单信息，可能运单号不存在或者服务暂不可用等
            context.setPassCheck(false);
            return context;
        }
        context.setBigWaybillDto(bigWaybillDto);

        resetBusinessType(request, bigWaybillDto);/*验货businessType存在非50的数据吗，需要验证*/
        resetStoreId(request, bigWaybillDto);
        builderInspectionList(request, context);
        builderCenConfirmList(context);

        return context;
    }

    @Override
    protected boolean otherOperation(InspectionRequest request, InspectionTaskExecuteContext context) {

        this.sendInspectionMQ(request);

        // 发送物资消息
        this.sendRecycleMaterialOperateRecordMq(request);

        return true;
    }

    /**
     * 按运单维度发送验货消息
     * @param request
     */
    private void sendInspectionMQ(InspectionRequest request) {

        inspectionNotifyService.sendMQFromRequest(request);
    }

    private void sendRecycleMaterialOperateRecordMq(InspectionRequest inspectionRequest) {
        LOGGER.info("inspectionWaybillTaskExecutor sendRecycleMaterialOperateRecordMq {}", JsonHelper.toJson(inspectionRequest));
        try {
            final RecycleMaterialOperateRecordPublicDto recycleMaterialOperateRecordDto = new RecycleMaterialOperateRecordPublicDto();
            recycleMaterialOperateRecordDto.setPackageCode(inspectionRequest.getPackageBarcode());
            recycleMaterialOperateRecordDto.setOperateNodeCode(MaterialFlowActionDetailV2Enum.NODE_RECEIPT.getCode());
            recycleMaterialOperateRecordDto.setOperateNodeName(MaterialFlowActionDetailV2Enum.NODE_RECEIPT.getDesc());
            if (null != inspectionRequest.getUserCode() && inspectionRequest.getUserCode() > 0) {
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByStaffId(inspectionRequest.getUserCode());
                if (null != baseStaffSiteOrgDto) {
                    recycleMaterialOperateRecordDto.setOperateUserErp(baseStaffSiteOrgDto.getErp());
                    recycleMaterialOperateRecordDto.setOperateUserName(baseStaffSiteOrgDto.getStaffName());
                }
            }
            recycleMaterialOperateRecordDto.setOperateSiteId(String.valueOf(inspectionRequest.getSiteCode()));
            recycleMaterialOperateRecordDto.setOperateSiteName(inspectionRequest.getSiteName());
            final String operateTimeStr = inspectionRequest.getOperateTime();
            final long currentTimeMillis = System.currentTimeMillis();
            if (StringUtils.isNotBlank(operateTimeStr)) {
                recycleMaterialOperateRecordDto.setOperateTime(DateUtil.parse(operateTimeStr, DateUtil.FORMAT_DATE_TIME).getTime());
            } else {
                recycleMaterialOperateRecordDto.setOperateTime(currentTimeMillis);
            }
            if (inspectionRequest.getReceiveSiteCode() != null) {
                recycleMaterialOperateRecordDto.setReceiveSiteId(inspectionRequest.getReceiveSiteCode().toString());

                Integer receiveSiteId = inspectionRequest.getReceiveSiteCode();
                BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(receiveSiteId);
                // 查询目的地，判断如果是仓，则取仓的仓号_配送中心号
                if (baseStaffSiteOrgDto != null) {
                    recycleMaterialOperateRecordDto.setReceiveSiteName(baseStaffSiteOrgDto.getSiteName());
                    if (StringUtils.isNotBlank(baseStaffSiteOrgDto.getStoreCode())) {
                        String[] storeCodeArr = baseStaffSiteOrgDto.getStoreCode().split(Constants.SEPARATOR_HYPHEN);
                        if(storeCodeArr.length == 3){
                            // 仓号_配送中心号
                            recycleMaterialOperateRecordDto.setReceiveSiteId(storeCodeArr[2] + Constants.UNDER_LINE + storeCodeArr[1]);
                        }
                    }
                }
            }
            recycleMaterialOperateRecordDto.setSendTime(currentTimeMillis);
            recycleMaterialOperateRecordProducer.sendOnFailPersistent(recycleMaterialOperateRecordDto.getPackageCode(), JsonHelper.toJson(recycleMaterialOperateRecordDto));
        } catch (Exception e) {
            LOGGER.error("inspectionWaybillTaskExecutor sendRecycleMaterialOperateRecordMq exception {}", JsonHelper.toJson(inspectionRequest), e);
        }
    }

}
