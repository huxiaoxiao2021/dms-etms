package com.jd.bluedragon.distribution.jy.service.common;

import com.jd.bluedragon.common.dto.operation.workbench.enums.BarCodeLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.UnloadBarCodeScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.UnloadScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionScanRequest;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
import com.jd.bluedragon.distribution.jy.enums.UnloadBarCodeQueryEntranceEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.NumberHelper;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 卸车相关工具
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-25 16:08:47 周二
 */
public class UnloadRenderUtils {

    public static List<LabelOption> resolveBarCodeLabel(UnloadBarCodeQueryEntranceEnum queryEntranceEnum, JyVehicleTaskUnloadDetail unloadDetail) {
        List<LabelOption> labelList = new ArrayList<>();

        // 标签展示的顺序
        int displayOrder = 0;

        // 待扫包裹不显示产品类型
        if (!Objects.equals(UnloadBarCodeQueryEntranceEnum.TO_SCAN, queryEntranceEnum)) {
            if (StringUtils.isNotBlank(unloadDetail.getProductType())) {
                displayOrder ++;
                labelList.add(new LabelOption(BarCodeLabelOptionEnum.PRODUCT_TYPE.getCode(), UnloadProductTypeEnum.getNameByCode(unloadDetail.getProductType()), displayOrder));
            }
        }

        if (NumberHelper.gt0(unloadDetail.getInterceptFlag())) {
            displayOrder ++;
            labelList.add(new LabelOption(BarCodeLabelOptionEnum.INTERCEPT.getCode(), BarCodeLabelOptionEnum.INTERCEPT.getName(), displayOrder));
        }

        return labelList;
    }

    /**
     * 解析扫描类型。待扫、多扫 etc.
     * @param unloadDetail
     * @return
     */
    public static UnloadBarCodeScanTypeEnum resolveScanTypeDesc(JyVehicleTaskUnloadDetail unloadDetail) {
        if (NumberHelper.gt0(unloadDetail.getMoreScanFlag())) {
            if (NumberHelper.gt0(unloadDetail.getLocalSiteFlag())) {
                return UnloadBarCodeScanTypeEnum.LOCAL_MORE_SCAN;
            }
            else {
                return UnloadBarCodeScanTypeEnum.OUT_MORE_SCAN;
            }
        }
        else {
            if (!NumberHelper.gt0(unloadDetail.getScannedFlag())) {
                return UnloadBarCodeScanTypeEnum.TO_SCAN;
            }
            else {
                return UnloadBarCodeScanTypeEnum.SCANNED;
            }
        }
    }

    public static UnloadScanDto createUnloadDto(InspectionScanRequest request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        Date operateTime = new Date();
        UnloadScanDto unloadScanDto = new UnloadScanDto();
        unloadScanDto.setBizId(request.getBizId());
        // 无任务场景下没有sealCarCode
        unloadScanDto.setSealCarCode(StringUtils.EMPTY);
        unloadScanDto.setVehicleNumber(taskUnloadVehicle.getVehicleNumber());
        unloadScanDto.setStartSiteId(taskUnloadVehicle.getStartSiteId());
        unloadScanDto.setEndSiteId(taskUnloadVehicle.getEndSiteId());
        unloadScanDto.setManualCreatedFlag(taskUnloadVehicle.getManualCreatedFlag());
        unloadScanDto.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
        unloadScanDto.setBarCode(request.getBarCode());
        if(Objects.equals(UnloadScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())){
            unloadScanDto.setBarCode(WaybillUtil.getWaybillCode(request.getBarCode()));
        }
        unloadScanDto.setOperateTime(operateTime);
        unloadScanDto.setCreateUserErp(request.getUser().getUserErp());
        unloadScanDto.setCreateUserName(request.getUser().getUserName());
        unloadScanDto.setUpdateUserErp(request.getUser().getUserErp());
        unloadScanDto.setUpdateUserName(request.getUser().getUserName());
        unloadScanDto.setCreateTime(operateTime);
        unloadScanDto.setUpdateTime(operateTime);

        unloadScanDto.setGroupCode(request.getGroupCode());
        unloadScanDto.setTaskId(request.getTaskId());

        return unloadScanDto;
    }
}
