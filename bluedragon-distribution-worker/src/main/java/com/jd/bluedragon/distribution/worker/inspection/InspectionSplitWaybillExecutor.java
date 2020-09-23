package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName InspectionSplitWaybillExecutor
 * @Description 验货多包裹拆分任务
 * @Author wyh
 * @Date 2020/9/18 11:27
 **/
@Component("inspectionSplitWaybillExecutor")
public class InspectionSplitWaybillExecutor extends InspectionTaskExecute {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspectionSplitWaybillExecutor.class);

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    WaybillPackageManager waybillPackageManager;

    @Override
    protected InspectionTaskExecuteContext prepare(Task domain) {
        InspectionTaskExecuteContext context = new InspectionTaskExecuteContext();
        context.setPassCheck(true);

        InspectionRequest request = JsonHelper.fromJsonUseGson(domain.getBody(), InspectionRequest.class);
        if (null == request) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("验货拆分任务JSON解析后对象为空, task:{}", domain.getBody());
            }
            context.setPassCheck(false);
            return context;
        }

        if (0 == request.getPageNo() || 0 == request.getPageSize()) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("拆分任务缺少分页参数, task:{}", domain.getBody());
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
            return WaybillUtil.getWaybillCode(request.getPackageBarOrWaybillCode());
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
                    waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
            if (pageLists != null && pageLists.getData() != null ) {
                bigWaybillDto.setPackageList(pageLists.getData());
            }
        }

        return bigWaybillDto;
    }
}
