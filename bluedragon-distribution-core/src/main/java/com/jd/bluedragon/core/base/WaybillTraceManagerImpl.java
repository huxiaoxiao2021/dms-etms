package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.dto.BigPackageStateDto;
import com.jd.etms.waybill.dto.DChoice;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jd.bluedragon.Constants.RESULT_SUCCESS;
import static com.jd.bluedragon.Constants.WAYBILLTRACE_FINISHED;

/**
 * @author tangchunqing
 * @Description: 全程跟踪工具
 * @date 2018年12月25日 16时:43分
 */
@Service("waybillTraceManager")
public class WaybillTraceManagerImpl implements WaybillTraceManager {
    private static final Log logger = LogFactory.getLog(WaybillTraceManagerImpl.class);
    @Autowired
    private WaybillTraceApi waybillTraceApi;

    /**
     * 查运单的 某个状态的全程跟踪
     *
     * @param waybillCode
     * @param state
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillTraceManagerImpl.getPkStateByWCodeAndState", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<PackageState> getPkStateByWCodeAndState(String waybillCode, String state) {
        BaseEntity<List<PackageState>> baseEntity = waybillTraceApi.getPkStateByWCodeAndState(waybillCode, state);
        if (baseEntity != null && baseEntity.getResultCode() == RESULT_SUCCESS && baseEntity.getData() != null ) {
            return baseEntity.getData();
        } else {
            logger.warn("WaybillTraceManagerImpl.getPkStateByWCodeAndState无揽收全程跟踪，baseEntity："+JsonHelper.toJson(baseEntity)+",waybillCode:"+waybillCode);
            return Lists.newArrayList();
        }
    }

    /**
     * 判断运单是否已经妥投
     * @param waybillCode 运单号
     * @return true表示已经妥投，false表示还没有妥投
     */
    public boolean isWaybillFinished(String waybillCode){
        List<PackageState> list = getPkStateByWCodeAndState(waybillCode, WAYBILLTRACE_FINISHED);
        if(list != null && list.size() > 0 ){
            return true;
        }
        return false;
    }

    /**
     * 判断运单是否被拒收
     * @param waybillCode 运单号
     * @return true表示被拒收，false表示还没有拒收
     */
    @Override
    public boolean isWaybillRejected(String waybillCode) {
        List<PackageState> list = getPkStateByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_REJECTED);
        if(list != null && list.size() > 0 ){
            return true;
        }
        return false;
    }

    /**
     * 获取包裹的全程跟踪状态
     * @param packageCode
     * @return
     */
    @Override
    public BaseEntity<List<PackageState>> getPkStateByPCode(String packageCode){

        return waybillTraceApi.getPkStateByPCode(packageCode);
    }

    /**
     * 获取包裹的全程跟踪操作明细
     * @param packageCode
     * @return
     */
    @Override
    public BaseEntity<List<PackageState>> getAllOperations(String packageCode) {
        try {
            return waybillTraceApi.getAllOperations(packageCode);
        } catch (Exception e) {
            logger.error("获取包裹号" + packageCode + "全程跟踪列表失败", e);
        }
        return null;
    }

    /**
     * 获取运单的全程跟踪的部门信息
     * @param waybillCode 运单号
     * @param queryPickInfo 是否查询揽收信息
     * @param queryDeliveryInfo 是否查询配送信息
     * @param queryStoreInfo 是否查询计划仓信息
     * @param querySortingInfo 是否查询分拣中心信息
     * @return
     */
    @Override
    public BaseEntity<BigPackageStateDto> getPkStateByCodeAndChoice(String waybillCode, Boolean queryPickInfo, Boolean queryDeliveryInfo, Boolean queryStoreInfo, Boolean querySortingInfo) {
        try {
            DChoice dChoice = new DChoice();
            dChoice.setQueryDeliveryInfo(queryPickInfo);
            dChoice.setQueryPickInfo(queryDeliveryInfo);
            dChoice.setQueryStoreInfo(queryStoreInfo);
            dChoice.setQuerySortingInfo(querySortingInfo);
            return waybillTraceApi.getPkStateByCodeAndChoice(waybillCode, dChoice);
        } catch (Exception e) {
            logger.error("获取运单号" + waybillCode + "状态列表失败", e);
        }
        return null;
    }
}
