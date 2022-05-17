package com.jd.bluedragon.distribution.tms.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskQueryRequest;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskUpdateDto;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.bluedragon.core.jsf.tms.TmsCarTaskManager;
import com.jd.bluedragon.distribution.tms.TmsCarTaskService;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.tpc.dto.LineCargoVolumeQueryDto;
import com.jd.tms.tpc.dto.LineCargoVolumeUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TmsCarTaskServiceImpl implements TmsCarTaskService {

    @Autowired
    private TmsCarTaskManager tmsCarTaskManager;

    @Override
    public JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(String startNodeCode) {
        PageDto<com.jd.tms.basic.dto.TransportResourceDto> page = new PageDto<>();
        page.setCurrentPage(1);
        page.setPageSize(100);
        TransportResourceDto transportResourceDto = new TransportResourceDto();
        transportResourceDto.setStartNodeCode(startNodeCode);
        //只查询目的分拣中心数据
        transportResourceDto.setEndNodeType(2);
        return tmsCarTaskManager.getEndNodeList(page, transportResourceDto);
    }

    @Override
    public JdCResponse<List<CarTaskResponse>> queryCarTaskList(CarTaskQueryRequest queryRequest) {

        LineCargoVolumeQueryDto queryDto = new LineCargoVolumeQueryDto();
        queryDto.setBeginNodeCode(queryRequest.getBeginNodeCode());
        queryDto.setEndNodeCode(queryRequest.getEndNodeCode());
        com.jd.tms.tpc.dto.PageDto pageDto = new com.jd.tms.tpc.dto.PageDto();
        pageDto.setCurrentPage(1);
        pageDto.setPageSize(200);
        return tmsCarTaskManager.queryCarTaskList(queryDto, pageDto);
    }

    @Override
    public JdCResponse updateCarTaskInfo(CarTaskUpdateDto updateDto) {
        LineCargoVolumeUpdateDto volumeUpdateDto = new LineCargoVolumeUpdateDto();
        volumeUpdateDto.setBeginNodeCode(updateDto.getBeginNodeCode());
        volumeUpdateDto.setEndNodeCode(updateDto.getEndNodeCode());
        volumeUpdateDto.setRouteLineCode(updateDto.getRouteLineCode());
        volumeUpdateDto.setPlanDepartTime(updateDto.getPlanDepartTime());
        volumeUpdateDto.setVolume(updateDto.getVolume());
        volumeUpdateDto.setAccountCode(updateDto.getAccountCode());
        volumeUpdateDto.setAccountName(updateDto.getAccountName());
        return tmsCarTaskManager.updateCarTaskInfo(volumeUpdateDto);
    }
}
