package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 限制终端人员针对未揽收包裹进行包裹补打
 * 提供给终端使用
 */
@Service("terminalUncollectedPackageInterceptHandler")
public class TerminalUncollectedPackageInterceptHandler implements InterceptHandler<WaybillPrintContext, String> {

    private static final Logger log = LoggerFactory.getLogger(TerminalUncollectedPackageInterceptHandler.class);

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    // 揽收完成/三方揽收交接成功状态集合
    private static Set<Integer> collectedStateSet = new HashSet<>();
    static {
        //三方揽收交接成功：运单状态码：-1300
        collectedStateSet.add(Integer.valueOf(Constants.WAYBILL_TRACE_STATE_BMZT_COLLECT_HANDOVER_COMPLETE));
        //配送员完成揽收：运单状态码： -640
        collectedStateSet.add(Integer.valueOf(Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.TerminalUncollectedPackageInterceptHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
    public InterceptResult<String> handle(WaybillPrintContext context) {
        log.debug("执行终端未揽收包裹补打拦截器");
        InterceptResult<String> result = context.getResult();
        try{
            Integer operateType = context.getRequest().getOperateType();
            if (notNeedIntercept(operateType)) {
                return result;
            }

            String waybillSign = context.getBigWaybillDto().getWaybill().getWaybillSign();
            // 纯配、非拒收逆向单才执行拦截
            if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_53, WaybillSignConstants.CHAR_53_2)
                    && !BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_15, WaybillSignConstants.CHAR_15_6)) {

                String barCode = context.getRequest().getBarCode();
                if (!isCollected(barCode)) {
                    result.toFail(InterceptResult.STATUS_NO_PASSED, WaybillPrintMessages.MESSAGE_PACKAGE_UNCOLLECTED);
                    return result;
                }
            }
        }catch (Exception e) {
            log.error("标签打印接口异常，barCode:{}", context.getRequest().getBarCode(), e);
            result.toError();
        }
        return result;
    }

    protected boolean notNeedIntercept(Integer operateType) {
        // 100310（小哥、3PL工作台）|| 100311（站长工作台）|| operateType=100313（达达）才进行拦截校验
        return !WaybillPrintOperateTypeEnum.SMS_PDA_REPRINT.getType().equals(operateType)
                && !WaybillPrintOperateTypeEnum.SITE_HSD_PACKAGE_PRINT.getType().equals(operateType)
                && !WaybillPrintOperateTypeEnum.DADA_PACKAGE_PRINT.getType().equals(operateType);
    }

    /**
     * 是否有【揽收完成】/【三方揽收交接成功】节点
     * @param barCode 包裹号or运单号
     * @return
     */
    protected boolean isCollected(String barCode) {
        List<PackageState> packageStates = waybillTraceManager.getAllOperationsByOpeCodeAndState(barCode, collectedStateSet);
        if (CollectionUtils.isEmpty(packageStates)) {
            return false;
        }
        return true;
    }
}
