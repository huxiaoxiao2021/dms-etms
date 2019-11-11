package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.dto.PackageStateDto;
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
    @JProfiler(jKey = "DMS.BASE.WaybillTraceManagerImpl.getPkStateDtoByWCodeAndState",jAppName=Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<PackageStateDto> getPkStateDtoByWCodeAndState(String waybillCode, String state) {
        BaseEntity<List<PackageStateDto>> baseEntity = waybillTraceApi.getPkStateDtoByWCodeAndState(waybillCode, state);
        if (baseEntity != null && baseEntity.getResultCode() == RESULT_SUCCESS && baseEntity.getData() != null ) {
            return baseEntity.getData();
        } else {
            logger.warn("WaybillTraceManagerImpl.getPkStateDtoByWCodeAndState无揽收全程跟踪，baseEntity："+JsonHelper.toJson(baseEntity)+",waybillCode:"+waybillCode);
            return Lists.newArrayList();
        }
    }

    /**
     * 判断运单是否已经妥投
     * @param waybillCode 运单号
     * @return true表示已经妥投，false表示还没有妥投
     */
    public boolean isWaybillFinished(String waybillCode){
        List<PackageStateDto> list = getPkStateDtoByWCodeAndState(waybillCode, WAYBILLTRACE_FINISHED);
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
        List<PackageStateDto> list = getPkStateDtoByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_REJECTED);
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

}
