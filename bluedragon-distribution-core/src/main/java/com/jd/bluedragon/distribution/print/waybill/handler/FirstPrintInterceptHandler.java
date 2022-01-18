package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wyh
 * @className FirstPrintInterceptHandler
 * @description 首次打印拦截
 * @date 2021/12/8 11:04
 **/
@Service("firstPrintInterceptHandler")
public class FirstPrintInterceptHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private PopPrintService popPrintService;

    @Autowired
    private UccPropertyConfiguration uccConfig;

    private static final List<Integer> FIRST_PRINT_OPERATE_TYPE = new ArrayList<>();

    static {

        FIRST_PRINT_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.PLATE_PRINT.getType());
        FIRST_PRINT_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType());
        FIRST_PRINT_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.SWITCH_BILL_PRINT.getType());
        FIRST_PRINT_OPERATE_TYPE.add(WaybillPrintOperateTypeEnum.FIELD_PRINT.getType());
    }

    /**
     * 执行处理，返回处理结果
     *
     * @param context
     * @return
     */
    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> interceptResult = context.getResult();

        // 未首次打印的单子，限制营业部人员使用包裹补打
//        if (Constants.SWITCH_OFF.equals(uccConfig.getJudgePackagePrintedIncludeSiteTerminal())) {
//            return interceptResult;
//        }

        // 包裹标签已打印拦截。输入运单号查询打印信息，大包裹情况存在性能问题
//        if (FIRST_PRINT_OPERATE_TYPE.contains(context.getRequest().getOperateType())) {
//            if (WaybillUtil.isPackageCode(context.getRequest().getBarCode())) {
//                if (!popPrintService.judgePackageFirstPrint(context.getRequest().getBarCode())) {
//                    interceptResult.toFail(WaybillPrintMessages.MESSAGE_PACKAGE_PRINTED);
//                    return interceptResult;
//                }
//            }
//        }

        if (siteTerminalLimitUseReprint(context, interceptResult)) {
            return interceptResult;
        }

        return interceptResult;
    }

    /**
     * 判断营业部未首打不允许使用包裹补打功能
     * @param context
     * @param interceptResult
     * @return
     */
    private boolean siteTerminalLimitUseReprint(WaybillPrintContext context, InterceptResult<String> interceptResult) {
        Integer operateType = context.getRequest().getOperateType();
        if (!WaybillPrintOperateTypeEnum.PACKAGE_AGAIN_PRINT.getType().equals(operateType)) {
            return true;
        }

        // 登录人场地为分拣中心或营业部
        Integer siteCode = context.getRequest().getSiteCode();
        if (null == siteCode) {
            return true;
        }

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(siteCode);
        if (null == baseSite) {
            return true;
        }

        if (!needLimitSite(baseSite)) {
            return true;
        }

        if (popPrintService.judgePackageFirstPrint(context.getRequest().getPackageBarCode())) {
            interceptResult.toFail(WaybillPrintMessages.MESSAGE_WAYBILL_FIRST_PRINT_INTERCEPT);
            return true;
        }

        return false;
    }

    /**
     * 查包裹/运单的打印记录
     * @param context
     * @return
     */
    private boolean findPopPrintRecord(WaybillPrintContext context) {
        // 包裹补打输入的是包裹号
        if (WaybillUtil.isPackageCode(context.getRequest().getPackageBarCode())) {
            PopPrint popPrint = new PopPrint();
            popPrint.setWaybillCode(WaybillUtil.getWaybillCode(context.getRequest().getPackageBarCode()));
            popPrint.setPackageBarcode(context.getRequest().getPackageBarCode());
            return popPrintService.findByPackage(popPrint) != null;
        }
        else {
            List<PopPrint> popPrints = popPrintService.findAllByWaybillCode(context.getRequest().getBarCode());
            return CollectionUtils.isNotEmpty(popPrints);
        }
    }

    /**
     * 限制未首次打印的站点类型
     * @param baseSite
     * @return
     */
    private boolean needLimitSite(BaseStaffSiteOrgDto baseSite) {
        return Arrays.asList(Constants.BASE_SITE_SITE, Constants.BASE_SITE_TYPE_ZT).contains(baseSite.getSiteType())
                || BusinessUtil.isSchoolyard(baseSite.getSiteType(), baseSite.getSubType())
                || BusinessUtil.isRecovery(baseSite.getSiteType(), baseSite.getSubType())
                || BusinessUtil.isRuralSite(baseSite.getSiteType(), baseSite.getSubType());
    }
}
