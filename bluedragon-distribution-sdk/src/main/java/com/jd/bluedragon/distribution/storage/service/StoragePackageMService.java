package com.jd.bluedragon.distribution.storage.service;

import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: StoragePackageMService
 * @Description: 储位包裹主表--Service接口
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
public interface StoragePackageMService extends Service<StoragePackageM> {

    /**
     * 强制发货
     * @param performanceCodes
     */
    boolean forceSend(List<String> performanceCodes,PutawayDTO putawayDTO);

    /**
     * 上架
     * @param putawayDTO
     * @return
     */
    boolean putaway(PutawayDTO putawayDTO);

    /**
     * 获取最近一次上架的储位号
     * @param waybillCode
     * @return
     */
    String getExistStorageCode(String waybillCode);

    /**
     * 剔除运单
     * 并更新同履约单下其他运单状态
     * @param waybillCode 运单编号
     * @param performanceCode 履约单号
     */
    void removeWaybill(String waybillCode,String performanceCode);

    /**
     *
     * 运单是否可以发货（加履中心订单）
     *
     * @param waybillCode 运单号
     * @param waybillSign 运单标识
     * @return
     */
    boolean checkWaybillCanSend(String waybillCode,String waybillSign);

    StoragePackageD checkExistStorage(String barCode);

    /**
     * 变更运单的暂存状态为已发货
     * @param waybillCode
     */
    void makeWaybillSend(String waybillCode);

    /**
     * 更新暂存状态为已发货
     * @param waybillCode
     * @param packageCode
     */
    void updateStatusOnSend(String waybillCode,String packageCode);

    Boolean cancelPutaway( List<Long> ids);

    /**
     * 通过履约单号更新发货状态
     * @param parentOrderId
     */
    void updateStoragePackageMStatusForSendOfParentOrderId(String parentOrderId);

    /**
     * 通过运单更新发货状态
     * @param waybillCode
     */
    void updateStoragePackageMStatusForSend(String waybillCode);
}
