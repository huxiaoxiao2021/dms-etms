package com.jd.bluedragon.core.jsf.tms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.tms.basic.dto.PageDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.tpc.dto.RouteLineCargoDto;
import com.jd.tms.tpc.dto.RouteLineCargoQueryDto;
import com.jd.tms.tpc.dto.RouteLineCargoUpdateDto;

import java.util.List;

public interface TmsCarTaskManager {

    /**
     * 根据当前站点获取可查询运输车辆任务的目的站点列表
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
    JdCResponse<List<CarTaskResponse>> queryCarTaskList(RouteLineCargoQueryDto dto, com.jd.tms.tpc.dto.PageDto<RouteLineCargoDto> pageDto);

    /**
     * 运输任务更新接口
     * @param updateDto
     * @return
     */
    JdCResponse updateCarTaskInfo(RouteLineCargoUpdateDto updateDto);


}
