package com.jd.bluedragon.core.base;

import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.dto.BigPackageStateDto;
import com.jd.etms.waybill.dto.PackageStateDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tangchunqing
 * @Description: 全程跟踪工具
 * @date 2018年12月25日 16时:42分
 */
public interface WaybillTraceManager {
    /**
     *查运单的 某个状态的全程跟踪
     * @param waybillCode
     * @param state
     * @return
     */
    List<PackageStateDto> getPkStateDtoByWCodeAndState(String waybillCode, String state);

    /**
     * 判断运单是否已经妥投
     * @param waybillCode 运单号
     * @return true表示已经妥投，false表示还没有妥投
     */
    boolean isWaybillFinished(String waybillCode);

    /**
     * 判断运单是否被拒收
     * @param waybillCode 运单号
     * @return true表示被拒收，false表示还没有拒收
     */
    boolean isWaybillRejected(String waybillCode);

    /**
     * 判断运单是否为弃件
     * @param waybillCode 运单号
     * @return true表示是弃件，false表示不是弃件
     */
    boolean isWaybillWaste(String waybillCode);

    /**
     * 获取包裹的全程跟踪状态
     * @param packageCode
     * @return
     */
    BaseEntity<List<PackageState>> getPkStateByPCode(String packageCode);

    /**
     * 根据操作号、状态 查全程跟踪
     * @param opeCode
     * @param states
     * @return
     */
    List<PackageState> getAllOperationsByOpeCodeAndState(String opeCode, Set<Integer> states);

    /**
     * 获取包裹的全程跟踪操作明细
     * @param packageCode
     * @return
     */
    BaseEntity<List<PackageState>> getAllOperations(String packageCode);

    /**
     * 获取运单的全程跟踪的部门信息
     * @param waybillCode 运单号
     * @param queryPickInfo 是否查询揽收信息
     * @param queryDeliveryInfo 是否查询配送信息
     * @param queryStoreInfo 是否查询计划仓信息
     * @param querySortingInfo 是否查询分拣中心信息
     * @return
     */
    BaseEntity<BigPackageStateDto> getPkStateByCodeAndChoice(String waybillCode, Boolean queryPickInfo, Boolean queryDeliveryInfo, Boolean queryStoreInfo, Boolean querySortingInfo);

    /**
     * 根据操作单号 批量查最新一条全程跟踪
     * @param opCodes 操作号（包括取件单号，面单号，包裹号，运单号） 最多一次传500个
     * @see <a>https://cf.jd.com/pages/viewpage.action?pageId=162204941</a>
     * @return 全程跟踪记录
     */
    BaseEntity<Map<String, PackageState>> getNewestPKStateByOpCodes(List<String> opCodes);
}
