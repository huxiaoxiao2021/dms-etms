package com.jd.bluedragon.core.jsf.tms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.tpc.dto.AccountDto;
import com.jd.tms.tpc.dto.LineCargoVolumeDetailDto;
import com.jd.tms.tpc.dto.LineCargoVolumeQueryDto;
import com.jd.tms.tpc.dto.LineCargoVolumeUpdateDto;

import java.util.List;

public interface TmsCarTaskManager {

    /**
     * 根据当前站点获取目的站点列表
     * @param page
     * @param transportResourceDto
     * @return
     */
    JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(PageDto<TransportResourceDto> page, TransportResourceDto transportResourceDto);

    /**
     * 获取运输任务列表
     * @param dto
     * @param pageDto
     * @return
     */
    JdCResponse<List<CarTaskResponse>> queryCarTaskList(LineCargoVolumeQueryDto dto, com.jd.tms.tpc.dto.PageDto<LineCargoVolumeDetailDto> pageDto);

    /**
     * 运输任务更新接口
     * @param updateDto
     * @return
     */
    JdCResponse updateCarTaskInfo(LineCargoVolumeUpdateDto updateDto);


}
