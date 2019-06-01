package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;

import java.util.List;
import java.util.Map;

public interface WaybillPackageManager {
    /**
     * 根据运单号获取包裹信息
     * @param waybillCode
     * @return
     */
    BaseEntity<List<DeliveryPackageD>> getPackListByWaybillCode(String waybillCode);

    /**
     * 根据运单号获取包裹信息 分页
     * @param waybillCode
     * @return
     */
    BaseEntity<List<DeliveryPackageD>> getPackListByWaybillCodeOfPage(String waybillCode,int pageNo,int pageSize);

    /**
     * 根据运单号列表获取包裹信息
     * @param waybillCodes
     * @return
     */
    BaseEntity<Map<String, List<DeliveryPackageD>>> batchGetPackListByCodeList(List<String> waybillCodes);

    /**
     * 根据包裹号列表获取包裹信息
     * @param packageCodes
     * @return
     */
    BaseEntity<List<DeliveryPackageD>> queryPackageListForParcodes(List<String> packageCodes);

    /**
     * 根据运单号获取包裹数据，通过调用运单的分页接口获得
     *
     * @param waybillCode
     * @return
     */
    BaseEntity<List<DeliveryPackageD>> getPackageByWaybillCode(String waybillCode);

    /**
     * 包裹称重和体积测量数据上传
     * 来源 PackOpeController
     *
     * @param packOpeJson 称重和体积测量信息
     * @return map data:true or false,code:-1:参数非法 -3:服务端内部处理异常 1:处理成功,message:code对应描述
     */
    Map<String, Object> uploadOpe(String packOpeJson);

    /**
     * 从sysconfig表里获取是否启用分页查询运单包裹的接口
     *
     * @return
     */
    boolean isGetPackageByPageOpen();

    /**
     * 修改运单包裹数量
     * @param waybillCode
     * @param packNum
     * @return
     */
    InvokeResult batchUpdatePackageByWaybillCode(String waybillCode, Integer packNum);
}
