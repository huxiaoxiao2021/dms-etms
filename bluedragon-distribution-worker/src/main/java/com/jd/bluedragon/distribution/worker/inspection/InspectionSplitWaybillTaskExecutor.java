package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName InspectionSplitWaybillTaskExecutor
 * @Description
 * @Author wyh
 * @Date 2020/9/25 9:43
 **/
@Service("splitWaybillTaskExecutor")
public class InspectionSplitWaybillTaskExecutor extends InspectionTaskCommonExecutor {

    @Autowired
    WaybillPackageManager waybillPackageManager;

    @Override
    protected InspectionTaskExecuteContext prepare(InspectionRequest request) {
        InspectionTaskExecuteContext context = new InspectionTaskExecuteContext();
        context.setPassCheck(true);

        if (0 == request.getPageNo() || 0 == request.getPageSize()) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("拆分任务缺少分页参数, task body:{}", JsonHelper.toJson(request));
            }
            context.setPassCheck(false);
            return context;
        }

        builderSite(request, context);

        String waybillCode = getWaybillCode(request);

        // 分页查询包裹数据
        BigWaybillDto bigWaybillDto = getWaybill(waybillCode, request.getPageNo(), request.getPageSize());

        context.setBigWaybillDto(bigWaybillDto);

        resetBusinessType(request, bigWaybillDto);/*验货businessType存在非50的数据吗，需要验证*/
        resetStoreId(request, bigWaybillDto);
        builderInspectionList(request, context);
        builderCenConfirmList(context);

        return context;
    }

    private String getWaybillCode(InspectionRequest request) {
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            return request.getWaybillCode();
        }
        else {
            String waybillCode = WaybillUtil.getWaybillCode(request.getPackageBarOrWaybillCode());
            request.setWaybillCode(waybillCode);
            return waybillCode;
        }
    }

    /**
     * 分页获取包裹数据
     * @param waybillCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    private BigWaybillDto getWaybill(String waybillCode, int pageNo, int pageSize) {

        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(false);
        wChoice.setQueryWaybillM(false);

        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
        BigWaybillDto bigWaybillDto = null;
        if (baseEntity != null && baseEntity.getData()!= null) {
            bigWaybillDto = baseEntity.getData();
            BaseEntity<List<DeliveryPackageD>> pageLists =
                    this.waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
            if (pageLists != null && pageLists.getData() != null ) {
                bigWaybillDto.setPackageList(pageLists.getData());
            }
        }

        return bigWaybillDto;
    }
}
