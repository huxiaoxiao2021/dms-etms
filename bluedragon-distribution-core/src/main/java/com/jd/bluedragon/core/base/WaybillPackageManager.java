package com.jd.bluedragon.core.base;

import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackFlowDetail;
import com.jd.etms.waybill.domain.PackageOpeFlowDetail;

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
     * 根据运单号获取包裹号列表
     * @param waybillCode 运单号
     * @return 包裹号列表
     */
    List<String> getWaybillPackageCodes(String waybillCode);

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
     * 获取所有称重量方记录
     * @param waybillCode
     * @param page
     * @return
     */
    Page<PackFlowDetail> getOpeDetailByCode(String waybillCode,Page<PackFlowDetail> page);

    /**
     * 根据包裹号获取包裹信息
     * @Param packageCode 包裹号
     */
    DeliveryPackageD getPackageInfoByPackageCode(String packageCode);
    /**
     * 根据运单号查询称重流水数据
     */
    List<PackageOpeFlowDetail> getWaybillWeightVolumeDetail(String waybillCode);
}
