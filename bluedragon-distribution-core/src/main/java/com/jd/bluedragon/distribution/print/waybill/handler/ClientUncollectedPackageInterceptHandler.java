package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("clientUncollectedPackageInterceptHandler")
public class ClientUncollectedPackageInterceptHandler extends TerminalUncollectedPackageInterceptHandler {

    private static final Logger log = LoggerFactory.getLogger(ClientUncollectedPackageInterceptHandler.class);

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ClientUncollectedPackageInterceptHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
    public InterceptResult<String> handle(WaybillPrintContext context) {
        log.debug("青龙打印客户端未揽收包裹补打拦截器");
        InterceptResult<String> result = context.getResult();
        try{
            Integer operateType = context.getRequest().getOperateType();
            if (this.notNeedIntercept(operateType)) {
                return result;
            }

            String waybillSign = context.getBigWaybillDto().getWaybill().getWaybillSign();
            // 是否是 纯配、非拒收逆向单
            boolean isOrderMatched = BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_53, WaybillSignConstants.CHAR_53_2)
                    && !BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_15, WaybillSignConstants.CHAR_15_6);

            if(null == context.getRequest().getUserCode()){
                result.toFail(InterceptResult.STATUS_NO_PASSED, WaybillPrintMessages.MESSAGE_USER_CODE_NOT_FOUND);
                return result;
            }
            BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(context.getRequest().getUserCode());
            if(null == dto) {
                result.toFail(InterceptResult.STATUS_NO_PASSED, WaybillPrintMessages.MESSAGE_USER_INFO_NOT_FOUND);
                return result;
            }
            //是否是 营业部 或 第三方
            boolean isUserMatched = Constants.BASE_SITE_TYPE_THIRD.equals(dto.getSiteType()) || Constants.TERMINAL_SITE_TYPE_4.equals(dto.getSiteType());
            if (isOrderMatched && isUserMatched) {

                String barCode = context.getRequest().getBarCode();
                // 是否有【揽收完成】/【三方揽收交接成功】节点
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
}
