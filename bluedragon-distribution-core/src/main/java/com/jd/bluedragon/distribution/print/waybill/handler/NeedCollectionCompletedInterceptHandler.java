package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.ldop.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName: NeedCollectionCompletedInterceptHandler
 * @Description: 对 揽收时采集商品信息运单 未揽收完成禁止打印限制
 * @author: zhangzhongkai
 * @date: 2018年12月25日17:38:01
 */
@Service("needCollectionCompletedInterceptHandler")
public class NeedCollectionCompletedInterceptHandler extends NeedPrepareDataInterceptHandler<WaybillPrintContext,String> {
    private static final Logger log = LoggerFactory.getLogger(NeedCollectionCompletedInterceptHandler.class);

    @Autowired
    WaybillTraceManager waybillTraceManager;

    /**
     * 需要校验运单是否已经妥投的类型
     */
    private static Set<Integer> needCheckCollectWaybillFinished = new HashSet<Integer>();
    static {
        needCheckCollectWaybillFinished.add(WaybillPrintOperateTypeEnum.PLATE_PRINT.getType());//平台打印
        needCheckCollectWaybillFinished.add(WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType());//站点平台打印
        needCheckCollectWaybillFinished.add(WaybillPrintOperateTypeEnum.BATCH_PACKAGE_AGAIN_PRINT.getType());//批量包裹补打
        needCheckCollectWaybillFinished.add(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType());//站点平台打印
        needCheckCollectWaybillFinished.add(WaybillPrintOperateTypeEnum.SITE_MASTER_PACKAGE_REPRINT.getType());//站长工作台：包裹补打

        needCheckCollectWaybillFinished.add(WaybillPrintOperateTypeEnum.SMS_REPRINT.getType());//终端：补打
        needCheckCollectWaybillFinished.add(WaybillPrintOperateTypeEnum.SMS_PDA_REPRINT.getType());//终端：一体机
    }

    @Override
    public void prepareData(WaybillPrintContext param) {
        //校验需要的数据是否存在，不存在加载需要的数据
        if (null == param.getCollectComplete()){
            //查询揽收完成（-640）全程跟踪结果
            List<PackageStateDto> collectCompleteResult = waybillTraceManager.getPkStateDtoByWCodeAndState(
                    param.getWaybill().getWaybillCode(), Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
            if (CollectionUtils.isEmpty(collectCompleteResult)){
                param.setCollectComplete(Boolean.FALSE);
            }else{
                param.setCollectComplete(Boolean.TRUE);
            }
        }
    }

    @Override
    InterceptResult<String> doHandler(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();

        String waybillSign = context.getWaybill().getWaybillSign();
        if (needCheckCollectWaybillFinished.contains(context.getRequest().getOperateType())
                && BusinessHelper.isNeedCollectingWaybill(waybillSign)) {
            // 没有揽收完成 禁止打印
            if (!context.getCollectComplete()) {
                interceptResult.toFail(InterceptResult.STATUS_NO_PASSED, WaybillPrintMessages.MESSAGE_NEED_COLLECT_FINISHED);
                return interceptResult;
            }
        }
        return interceptResult;
    }

}
