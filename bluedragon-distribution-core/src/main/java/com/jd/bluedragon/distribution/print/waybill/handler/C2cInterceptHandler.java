package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.IHintApiService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WayBillFinishedEnum;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.dms.utils.ParamsMapUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
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
    private IHintApiService hintApiService;

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

        if (WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(context.getRequest().getOperateType()))
        {
            boolean  isRepeatPrint=false;
            //当前面单已XX状态，且已操作过补打，请确认是否打印
            if (StringHelper.isNotEmpty(context.getRequest().getBarCode()) && reprintRecordService.isBarCodeRePrinted(context.getRequest().getBarCode())) {
                log.warn("C2cInterceptHandler.handler-->{}该单号重复打印",context.getWaybill().getWaybillCode());
                isRepeatPrint=true;
            }
            List<PackageState> collectCompleteResult = waybillTraceManager.getAllOperationsByOpeCodeAndState(context.getWaybill().getWaybillCode(),WayBillFinishedEnum.waybillStatusFinishedSet);
            if (CollectionUtils.isEmpty(collectCompleteResult)&&isRepeatPrint) {
                interceptResult.toWeakSuccess(JdResponse.CODE_RE_PRINT_REPEAT, hintApiService.getPrintClientHintReverseDefault(HintCodeConstants.REPRINT_REPEAT, new ParamsMapUtil().put("barCode", context.getRequest().getBarCode())));
                return interceptResult;
            }
            if(CollectionUtils.isNotEmpty(collectCompleteResult)){
                Collections.sort(collectCompleteResult, new Comparator<PackageState>() {
                    @Override
                    public int compare(PackageState packageState, PackageState packageState2) {
                        return packageState.getCreateTime().compareTo(packageState2.getCreateTime());
                    }
                });
                String  message =String.format(WaybillPrintMessages.MESSAGE_WAYBILL_FINISHED,collectCompleteResult.get(0).getStateName());
                if(isRepeatPrint){
                    message =String.format(WaybillPrintMessages.MESSAGE_WAYBILL_FINISHED_REPRINT,collectCompleteResult.get(0).getStateName());
                    interceptResult.toWeakSuccess(WaybillPrintMessages.CODE_WAYBILL_FINISHED_REPRINT,message);
                }else {
                    interceptResult.toWeakSuccess(WaybillPrintMessages.CODE_WAYBILL_FINISHED, message);
                }
            }
        }
        return interceptResult;
    }

}
