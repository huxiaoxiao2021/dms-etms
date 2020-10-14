package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.inspection.exception.WayBillCodeIllegalException;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    protected InspectionTaskExecuteContext prepare(InspectionRequest request) {
        InspectionTaskExecuteContext context = new InspectionTaskExecuteContext();
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

        return true;
    }

    /**
     * 按运单维度发送验货消息
     * @param request
     */
    private void sendInspectionMQ(InspectionRequest request) {

        inspectionNotifyService.sendMQFromRequest(request);
    }

}
