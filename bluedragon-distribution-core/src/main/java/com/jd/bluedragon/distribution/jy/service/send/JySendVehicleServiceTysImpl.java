package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendAbnormalPackRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.send.*;
import com.jd.bluedragon.distribution.jy.manager.IJySendVehicleJsfManager;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("jySendVehicleServiceTys")
public class JySendVehicleServiceTysImpl extends JySendVehicleServiceImpl implements ScanStatisticsService {
    @Autowired
    private IJySendVehicleJsfManager sendVehicleJsfManager;

    @Override
    public ExcepWaybillDto queryExcepScanWaybill(QueryExcepWaybillDto queryExcepWaybillDto) {
        Pager<SendVehicleTaskQuery> queryPager =assembleQueryWaybillDto(queryExcepWaybillDto);
        Pager<SendWaybillDto> sendWaybillDtoPager =sendVehicleJsfManager.querySendTaskWaybill(queryPager);
        if (ObjectHelper.isNotNull(sendWaybillDtoPager)){
            ExcepWaybillDto excepWaybillDto =new ExcepWaybillDto();
            excepWaybillDto.setSendWaybillDtoList(sendWaybillDtoPager.getData());
            excepWaybillDto.setTotal(sendWaybillDtoPager.getTotal());
            return excepWaybillDto;
        }
        return null;
    }

    private Pager<SendVehicleTaskQuery> assembleQueryWaybillDto(QueryExcepWaybillDto queryExcepWaybillDto) {
        SendVehicleTaskQuery sendVehicleTaskQuery =new SendVehicleTaskQuery();
        sendVehicleTaskQuery.setSendVehicleBizId(queryExcepWaybillDto.getSendVehicleBizId());
        sendVehicleTaskQuery.setQueryBarCodeFlag(queryExcepWaybillDto.getExcepScanTypeEnum().getCode());
        Pager<SendVehicleTaskQuery> queryPager =new Pager();
        queryPager.setSearchVo(sendVehicleTaskQuery);
        queryPager.setPageNo(queryExcepWaybillDto.getPageNo());
        queryPager.setPageSize(queryExcepWaybillDto.getPageSize());
        return queryPager;
    }

    @Override
    public ExcepPackageDto queryExcepPackageUnderWaybill(QueryExcepPackageDto queryExcepPackageDto) {
        Pager<SendVehicleTaskQuery> queryPager =assembleQueryPackageDto(queryExcepPackageDto);
        Pager<SendPackageDto> sendPackageDtoPager =sendVehicleJsfManager.querySendPackageDetail(queryPager);
        if (ObjectHelper.isNotNull(sendPackageDtoPager)){
            ExcepPackageDto excepPackageDto =new ExcepPackageDto();
            excepPackageDto.setSendPackageDtoList(sendPackageDtoPager.getData());
            excepPackageDto.setTotal(sendPackageDtoPager.getTotal());
        }
        return null;
    }

    private Pager<SendVehicleTaskQuery> assembleQueryPackageDto(QueryExcepPackageDto queryExcepPackageDto) {
        SendVehicleTaskQuery sendVehicleTaskQuery =new SendVehicleTaskQuery();
        sendVehicleTaskQuery.setSendVehicleBizId(queryExcepPackageDto.getSendVehicleBizId());
        sendVehicleTaskQuery.setWaybillCode(queryExcepPackageDto.getWaybillCode());
        sendVehicleTaskQuery.setQueryBarCodeFlag(queryExcepPackageDto.getExcepScanTypeEnum().getCode());
        Pager<SendVehicleTaskQuery> queryPager =new Pager();
        queryPager.setSearchVo(sendVehicleTaskQuery);
        queryPager.setPageNo(queryExcepPackageDto.getPageNo());
        queryPager.setPageSize(queryExcepPackageDto.getPageSize());
        return queryPager;
    }
}
