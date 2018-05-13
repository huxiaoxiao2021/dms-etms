package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.waybill.domain.WaybillPackageDTO;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.dto.BigWaybillDto;

import java.util.List;

public interface WaybillService {

    BigWaybillDto getWaybill(String waybillCode);

    BigWaybillDto getWaybillProduct(String waybillCode);

    /**
     * 获取运单状态接口
     */
    BigWaybillDto getWaybillState(String waybillCode);

    /***
     * 处理task_waybill的任务
     * @param task
     * @return
     */
    Boolean doWaybillStatusTask(Task task);

    /***
     * 处理task_pop的任务
     * @param task
     * @return
     */
    Boolean doWaybillTraceTask(Task task);

    /**
     * 根据包裹号获取包裹体积重量信息
     *
     * @param packageCode
     * @return
     */
    public WaybillPackageDTO getWaybillPackage(String packageCode);

    /**
     * 查询运单是否存在
     */
    public Boolean queryWaybillIsExist(String waybillCode);
}
