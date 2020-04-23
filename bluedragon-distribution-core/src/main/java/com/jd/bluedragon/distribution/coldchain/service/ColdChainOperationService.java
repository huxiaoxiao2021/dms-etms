package com.jd.bluedragon.distribution.coldchain.service;

import com.jd.bluedragon.distribution.api.response.ColdChainOperationResponse;
import com.jd.bluedragon.distribution.coldchain.dto.*;
import com.jd.jmq.common.exception.JMQException;

import java.util.List;

public interface ColdChainOperationService {

    /**
     * 上传卸货数据
     *
     * @param unloadDto
     * @return
     */
    boolean addUploadTask(ColdChainUnloadDto unloadDto);

    /**
     * 查询卸货任务
     *
     * @param request
     * @return
     */
    List<ColdChainUnloadQueryResultDto> queryUnloadTask(ColdChainQueryUnloadTaskRequest request);

    /**
     * 卸货完成
     *
     * @param taskNo
     * @param operateErp
     * @return
     */
    boolean unloadTaskComplete(String taskNo, String operateErp);

    /**
     * 出入库操作
     *
     * @param request
     * @return
     * @throws JMQException
     */
    ColdChainOperationResponse inAndOutBound(ColdChainInAndOutBoundRequest request) throws JMQException;

    /**
     * 获取冷链车型信息
     *
     * @return
     */
    List<VehicleTypeDict> getVehicleModelList();

    /**
     * 包裹号绑定温度计
     *
     * @return
     */
    ColdChainOperationResponse boundThermometer(ThermometerRequest request);
}
