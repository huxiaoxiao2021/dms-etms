package com.jd.bluedragon.distribution.tms.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.bluedragon.core.jsf.tms.TmsCarTaskManager;
import com.jd.bluedragon.distribution.tms.TmsCarTaskService;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
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
        return tmsCarTaskManager.getEndNodeList(page,transportResourceDto);
    }

    @Override
    public JdCResponse<List<CarTaskResponse>> queryCarTaskList(String startNodeCode, String endNodeCode) {
        return null;
    }
}
