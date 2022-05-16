package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.ministore.*;

import java.util.List;

public interface MiniStoreGatewayService {
    /**
     * 校验设备可用状态
     * @param request
     * @return 成功 代表可用，反之 不可用
     */
    JdCResponse validateDeviceStatus(DeviceStatusValidateReq request);

    /**
     * 微仓相关设备（保温箱、冰板、箱子）进行绑定
     * @param request
     * @return
     */
    JdCResponse bindMiniStoreDevice(BindMiniStoreDeviceReq request);

    /**
     * 封箱
     * @param sealBoxReq
     * @return
     */
    JdCResponse sealBox(SealBoxReq sealBoxReq);

    /**
     *查询当前集包数量（已集包裹数量）暂时不用这个接口
     */
    JdCResponse<Integer> querySortCount(String boxCode);

    /**
     * 校验三码(微仓码 冰板码 集包码)的原绑定关系是否正确/是否存在绑定关系（解封箱或者集包时校验）
     * @param unBoxValidateReq
     * @return
     */
    JdCResponse<UnBoxValidateResp> unBoxValidateBindRelation(UnBoxValidateReq unBoxValidateReq);

    /**
     * 校验集包关系（箱子和包裹的绑定关系）--解封箱的时候校验使用
     * @param validateSortRelationReq
     * @return
     */
    JdCResponse validateSortRelation(ValidateSortRelationReq validateSortRelationReq);

    /**
     * 解封箱
     * @param unBoxReq
     * @return
     */
    JdCResponse<List<String>> unBox(UnBoxReq unBoxReq);

    /**
     * 查询绑定未集包的任务列表
     * @param bindAndNoSortTaskReq
     * @return
     */
    JdCResponse<PageObject<BindAndNoSortTaskResp>> queryBindAndNoSortTaskList(BindAndNoSortTaskReq bindAndNoSortTaskReq);

    /**
     * 解绑定微仓设备：解除三码（微仓码 冰板码 集包码）的绑定关系
     * @param unBindReq
     * @return
     */
    JdCResponse unBind(UnBindReq unBindReq);

    /**
     * 更新微仓集包数量
     */
    JdCResponse incrSortCount(IncrSortCountReq incrSortCountReq);



}
