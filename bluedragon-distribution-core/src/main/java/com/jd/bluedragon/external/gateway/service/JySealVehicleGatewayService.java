package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealCodeResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.SealVehicleTaskResponse;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

import java.util.List;

/**
 * @ClassName JySealVehicleGatewayService
 * @Description 拣运到车岗网关服务
 * @Author wyh
 * @Date 2022/3/9 17:19
 **/
public interface JySealVehicleGatewayService {

    /**
     * 封车任务列表
     * @param request
     * @return
     */
    JdCResponse<SealVehicleTaskResponse> fetchSealTask(SealVehicleTaskRequest request);

    /**
     * 拉取待解封车任务
     * @param request
     * @return
     */
    JdCResponse<SealVehicleTaskResponse> fetchUnSealTask(SealVehicleTaskRequest request);

    /**
     * 封车任务明细
     * @param request
     * @return
     */
    JdCResponse<SealTaskInfo> taskInfo(SealTaskInfoRequest request);

    /**
     * 车辆状态枚举
     * @return
     */
    JdCResponse<List<SelectOption>> vehicleStatusOptions();

    /**
     * 待解封车状态枚举
     * @return
     */
    JdCResponse<List<SelectOption>> unSealVehicleStatusOptions();

    /**
     * 封签号列表
     * @param request
     * @return
     */
    JdCResponse<SealCodeResponse> sealCodeList(SealCodeRequest request);



    //--------------------jy封车相关接口----------------------
    /**
     * 根据运输任务bizId查询车的封签号列表
     * @param sealCodeReq
     * @return
     */
    JdCResponse<SealCodeResponse> listSealCodeBy(SealCodeReq sealCodeReq);

    /**
     * 查询封车数据详情
     * @param sealVehicleInfoReq
     * @return
     */
    JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq);

    /**
     * 根据运力编码查询运输信息
     * @param transportReq
     * @return
     *
     *  NewSealVehicleGatewayServiceImpl#getTransportInfoByCode
     */
    JdCResponse<TransportResp>  getTransportResourceByTransCode(TransportReq transportReq);

    /**
     * 校验运力编码和任务简码是否匹配
     * NewSealVehicleGatewayServiceImpl#checkTransportCode
     *
     */
    JdCResponse checkTransportCode(CheckTransportCodeReq checkTransportCodeReq);
    /**
     * 根据任务简码查询车牌号信息
     *
     * NewSealVehicleGatewayServiceImpl#getTaskInfo
     */
    JdCResponse<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq);

    /**
     * 提交封车
     *
     * NewSealVehicleGatewayServiceImpl#sealCar
     */
    JdCResponse sealVehicle(SealVehicleReq sealVehicleReq);


    /**
     * 获取待解封车数据详情
     * @param request
     * @return
     */
    JdCResponse<SealTaskInfo> getSealTaskInfo(SealTaskInfoRequest request);


}
