package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 包裹超长提示 处理器
 *
 * @author: hujiping
 * @date: 2019/12/6 18:20
 */
@Service("overLengthRemindHandler")
public class OverLengthRemindHandler implements InterceptHandler<WaybillPrintContext,String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = new InterceptResult<String>();
        interceptResult.toSuccess();
        Boolean longPackFlag = context.getRequest().getLongPackCheckFlag();
        if(longPackFlag){
            Waybill waybill = context.getWaybill();
            if(checkIsOverLengthPackage(null,waybill)){
                context.getResponse().setLongPack(Boolean.TRUE);
                interceptResult.toWeakSuccess(JdResult.CODE_SUC, WaybillPrintMessages.MESSAGE_PACKAGE_OVER_LENGTH_REMIND);
            }
        }
        return interceptResult;
    }

    /**
     * 判断是否是超长包裹
     * @param waybillCode
     * @param waybill
     * @return
     */
    public Boolean checkIsOverLengthPackage(String waybillCode,Waybill waybill) {
        Boolean sign = Boolean.FALSE;
        try {
            if(StringUtils.isBlank(waybillCode) && waybill == null){
                return sign;
            }
            String waybillSign = null;
            Integer busiId = null;
            if(waybill == null){
                com.jd.etms.waybill.domain.Waybill waybillByWayCode = waybillService.getWaybillByWayCode(waybillCode);
                waybillSign = waybillByWayCode.getWaybillSign();
                busiId = waybillByWayCode.getBusiId();
            }else {
                waybillSign = waybill.getWaybillSign();
                busiId = waybill.getBusiId();
            }
            BasicTraderInfoDTO dto = baseMinorManager.getBaseTraderById(busiId);
            if(dto != null && StringUtils.isNotBlank(dto.getTraderSign())){
                if(BusinessUtil.isOverLength(dto.getTraderSign())
                        && BusinessUtil.isB2CPureMatch(waybillSign)
                        && BusinessUtil.isMonthFinish(waybillSign) &&
                        (BusinessUtil.isPreferentialSend(waybillSign)
                                || BusinessUtil.isNextMorningArrived(waybillSign)
                                || BusinessUtil.isSameCityArrived(waybillSign))){
                    sign = Boolean.TRUE;
                }
            }
        }catch (Exception e){
            logger.error("参数:{},异常信息:{}", waybillCode,e.getMessage(),e);
        }
        return sign;
    }

}
