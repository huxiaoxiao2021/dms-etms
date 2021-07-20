package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WayBillFinishedEnum;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @ClassName: C2cInterceptHandler
 * @Description: C2C运单打印面单校验揽收完成
 * @author: tangcq
 * @date: 2018年12月25日17:38:01
 */
@Service("c2cInterceptHandler")
public class C2cInterceptHandler extends NeedPrepareDataInterceptHandler<WaybillPrintContext,String> {
    private static final Logger log = LoggerFactory.getLogger(C2cInterceptHandler.class);

    @Autowired
    WaybillTraceManager waybillTraceManager;
    @Autowired
    private ReprintRecordService reprintRecordService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 需要校验运单是否已经妥投的类型
     */
    private static Set<Integer> needCheckWaybillFinished = new HashSet<Integer>();
    static {
        needCheckWaybillFinished.add(WaybillPrintOperateTypeEnum.PLATE_PRINT.getType());//平台打印
        needCheckWaybillFinished.add(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType());//站点平台打印
        needCheckWaybillFinished.add(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType());//换单打印
        needCheckWaybillFinished.add(WaybillPrintOperateTypeEnum.PACKAGE_WEIGH_PRINT.getType());//包裹称重
        needCheckWaybillFinished.add(WaybillPrintOperateTypeEnum.FIELD_PRINT.getType());//驻场打印
        needCheckWaybillFinished.add(WaybillPrintOperateTypeEnum.BATCH_SORT_WEIGH_PRINT.getType());//批量分拣称重
        needCheckWaybillFinished.add(WaybillPrintOperateTypeEnum.FAST_TRANSPORT_PRINT.getType());//快运称重打印
        needCheckWaybillFinished.add(WaybillPrintOperateTypeEnum.SITE_MASTER_REVERSE_CHANGE_PRINT.getType());//站长工作台换单打印
    }
    /**
     * 需要检查揽收完成的场景
     */
    private static Set<Integer> needCheckCollectFinished = new HashSet<>();
    static {
        needCheckCollectFinished.add(WaybillPrintOperateTypeEnum.PLATE_PRINT.getType());//平台打印
        needCheckCollectFinished.add(WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType());//包裹补打
        needCheckCollectFinished.add(WaybillPrintOperateTypeEnum.BATCH_PACKAGE_AGAIN_PRINT.getType());//批量包裹补打
        needCheckCollectFinished.add(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType());//站点平台打印
        needCheckCollectFinished.add(WaybillPrintOperateTypeEnum.SITE_MASTER_PACKAGE_REPRINT.getType());//站长工作台：包裹补打
    }

    @Override
    void prepareData(WaybillPrintContext param) {
        //校验需要的数据是否存在，不存在加载需要的数据
        if (null == param.getCollectComplete()){
            //查询揽收完成（-640）全程跟踪结果
            List<PackageStateDto> collectCompleteResult = waybillTraceManager.getPkStateDtoByWCodeAndState(
                    param.getWaybill().getWaybillCode(), Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
            if (com.jd.ldop.utils.CollectionUtils.isEmpty(collectCompleteResult)){
                param.setCollectComplete(Boolean.FALSE);
            }else{
                param.setCollectComplete(Boolean.TRUE);
            }
        }

        //校验操作人所属场地是否为分拣中心
        if(null!=param.getRequest().getUserCode()){
            BaseStaffSiteOrgDto baseStaffByErpNoCache = baseMajorManager.getBaseStaffByStaffId(param.getRequest().getUserCode());
            if(null!=baseStaffByErpNoCache && Integer.valueOf(64).equals(baseStaffByErpNoCache.getSiteType())){
                param.setDmsCenter(Boolean.TRUE);
            }
        }
    }

    @Override
    InterceptResult<String> doHandler(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        String waybillSign = context.getWaybill().getWaybillSign();
        if (needCheckCollectFinished.contains(context.getRequest().getOperateType())
                && !context.getDmsCenter()
                && BusinessHelper.isC2cForward(waybillSign)
                && !(BusinessHelper.isC2cChangeAddress(waybillSign))) {
            //揽收交接完成（-1300）全程跟踪结果
            List<PackageStateDto> collectHandoverCompleteResult = waybillTraceManager.getPkStateDtoByWCodeAndState(context.getWaybill().getWaybillCode(), Constants.WAYBILL_TRACE_STATE_BMZT_COLLECT_HANDOVER_COMPLETE);
            //存在揽收完成或交接完成的全程跟踪，都可以进行打印，反之，进行拦截提示，禁止打印
            if (! (context.getCollectComplete() || collectHandoverCompleteResult.size() != 0)) {
                interceptResult.toFail(InterceptResult.STATUS_NO_PASSED, WaybillPrintMessages.MESSAGE_NEED_RECEIVE);
                return interceptResult;
            }
        }

        log.debug("C2cInterceptHandler-校验运单是否已经妥投");
        if(needCheckWaybillFinished.contains(context.getRequest().getOperateType()) && waybillTraceManager.isWaybillFinished(context.getWaybill().getWaybillCode())){
            interceptResult.toFail(InterceptResult.STATUS_NO_PASSED, WaybillPrintMessages.MESSAGE_WAYBILL_STATE_FINISHED);
            return interceptResult;
        }

        if (WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(context.getRequest().getOperateType())
                || WaybillPrintOperateTypeEnum.SITE_MASTER_PACKAGE_REPRINT.getType().equals(context.getRequest().getOperateType()))
        {
            String barCode = context.getRequest().getBarCode();
            boolean  isRepeatPrint = false;
            if (StringHelper.isNotEmpty(barCode) && reprintRecordService.isBarCodeRePrinted(barCode)) {
                log.warn("C2cInterceptHandler.handler-->{}该单号重复打印", barCode);
                isRepeatPrint = true;
            }
            // 当前面单正常状态，且已操作过补打，请确认是否打印
            List<PackageState> collectCompleteResult = waybillTraceManager.getAllOperationsByOpeCodeAndState(barCode, WayBillFinishedEnum.waybillStatusFinishedSet);
            if (CollectionUtils.isEmpty(collectCompleteResult)) {
                if(isRepeatPrint){
                    interceptResult.toWeakSuccess(JdResponse.CODE_RE_PRINT_REPEAT, JdResponse.MESSAGE_RE_PRINT_REPEAT);
                }
                return interceptResult;
            }
            // key：包裹状态 value：包裹详情
            Map<Integer, List<PackageState>> stateMap = getStateMap(collectCompleteResult);
            Set<Integer> needHitStatusSet = new HashSet<>();
            Set<Integer> needInterceptStatusSet = new HashSet<>();
            getPackReprintStatus(needHitStatusSet, needInterceptStatusSet);
            // 补打强拦截
            boolean isInterceptFlag = interceptDeal(interceptResult, stateMap, needInterceptStatusSet);
            if(isInterceptFlag){
                return interceptResult;
            }
            // 补打弱拦截
            hitDeal(interceptResult, stateMap, needHitStatusSet, isRepeatPrint);
            return interceptResult;
        }
        return interceptResult;
    }

    /**
     * 获取包裹状态
     * @param collectCompleteResult
     * @return
     */
    private Map<Integer, List<PackageState>> getStateMap(List<PackageState> collectCompleteResult) {
        Map<Integer, List<PackageState>> stateMap = new HashMap<>(5);
        for (PackageState packageState : collectCompleteResult) {
            Integer stateInt = NumberHelper.convertToInteger(packageState.getState());
            if(stateInt == null){
                continue;
            }
            if(stateMap.containsKey(stateInt)){
                stateMap.get(stateInt).add(packageState);
            }else {
                List<PackageState> list = new ArrayList<>();
                list.add(packageState);
                stateMap.put(stateInt, list);
            }
        }
        return stateMap;
    }

    /**
     * 补打提示拦截处理
     * @param interceptResult
     * @param stateMap
     * @param needInterceptStatusSet
     * @param isRepeatPrint
     */
    private void hitDeal(InterceptResult<String> interceptResult, Map<Integer, List<PackageState>> stateMap,
                         Set<Integer> needInterceptStatusSet, boolean isRepeatPrint) {
        if(CollectionUtils.isEmpty(needInterceptStatusSet)){
            return;
        }
        for (Integer state : stateMap.keySet()) {
            if(needInterceptStatusSet.contains(state)){
                String stateName = stateMap.get(state).get(0).getStateName();
                String  message =String.format(WaybillPrintMessages.MESSAGE_WAYBILL_FINISHED, stateName);
                if(isRepeatPrint){
                    message =String.format(WaybillPrintMessages.MESSAGE_WAYBILL_FINISHED_REPRINT, stateName);
                    interceptResult.toWeakSuccess(WaybillPrintMessages.CODE_WAYBILL_FINISHED_REPRINT,message);
                }else {
                    interceptResult.toWeakSuccess(WaybillPrintMessages.CODE_WAYBILL_FINISHED, message);
                }
                break;
            }
        }
    }

    /**
     * 补打强制拦截处理
     * @param interceptResult
     * @param stateMap
     * @param needInterceptStatusSet
     * @return
     */
    private boolean interceptDeal(InterceptResult<String> interceptResult, Map<Integer, List<PackageState>> stateMap, Set<Integer> needInterceptStatusSet) {
        if(CollectionUtils.isEmpty(needInterceptStatusSet)){
            return false;
        }
        boolean isInterceptFlag = false;
        for (Integer state : stateMap.keySet()) {
            if(needInterceptStatusSet.contains(state)){
                isInterceptFlag = true;
                String message = String.format(WaybillPrintMessages.MESSAGE_WAYBILL_FINISHED_INTERCEPT, stateMap.get(state).get(0).getStateName());
                interceptResult.toFail(WaybillPrintMessages.CODE_WAYBILL_FINISHED, message);
                break;
            }
        }
        return isInterceptFlag;
    }

    /**
     * 获取需提示状态码、需拦截状态码
     * @param needHitStatusSet
     * @param needInterceptStatusSet
     */
    private void getPackReprintStatus(Set<Integer> needHitStatusSet, Set<Integer> needInterceptStatusSet) {
        try {
            String packRePrintInterceptStatus = uccPropertyConfiguration.getPackRePrintInterceptStatus();
            if(StringUtils.isNotEmpty(packRePrintInterceptStatus)){
                String[] interceptStatusArray = packRePrintInterceptStatus.split(Constants.SEPARATOR_COMMA);
                for (String interceptStatus : interceptStatusArray) {
                    needInterceptStatusSet.add(Integer.parseInt(interceptStatus));
                }
            }
        }catch (Exception e){
            log.error("获取包裹补打需强制拦截的状态码异常!", e);
        }
        needHitStatusSet.addAll(WayBillFinishedEnum.waybillStatusFinishedSet);
        needHitStatusSet.removeAll(needInterceptStatusSet);
    }

}
