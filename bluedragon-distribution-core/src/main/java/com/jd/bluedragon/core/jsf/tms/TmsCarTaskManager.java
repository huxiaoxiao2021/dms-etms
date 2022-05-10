package com.jd.bluedragon.core.jsf.tms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.tpc.dto.AccountDto;

import java.util.List;

public interface TmsCarTaskManager {

    JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(PageDto<TransportResourceDto> page, TransportResourceDto transportResourceDto);

    //JdCResponse<List<CarTaskResponse>> queryCarTaskList(SortingReferQueryDto dto,Page<SortingReferQueryDto > pageDto);

   // JdCResponse updateCarTaskInfo(AccountDto accountDto, SortingReferQueryDto  sortingReferQueryDto);


}
