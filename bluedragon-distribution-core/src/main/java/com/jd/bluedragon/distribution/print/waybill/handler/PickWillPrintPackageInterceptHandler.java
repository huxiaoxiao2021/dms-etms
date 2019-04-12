package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.SITE_MASTER_REVERSE_CHANGE_PRINT;

/**
 * <P>
 *     如果用户用的是包裹号进行打印则将运单的包裹列表过滤为当前的包裹信息
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/5
 */
@Service("pickWillPrintPackageInterceptHandler")
public class PickWillPrintPackageInterceptHandler implements  InterceptHandler<WaybillPrintContext,String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PickWillPrintPackageInterceptHandler.class);

    /* 最大打印包裹数 */
    private static final int MAX_PRINT_SIZE = 100;

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();

        String barCode = context.getRequest().getBarCode();

        /* 获取打印结果 */
        WaybillPrintResponse waybillPrintResponse = context.getResponse();
        if (null == waybillPrintResponse) {
            result.toError(JdResponse.CODE_OK_NULL,JdResponse.MESSAGE_OK_NULL);
            return result;
        }

        /* 获取包裹列表 */
        List<PrintPackage> packages =  waybillPrintResponse.getPackList();
        if (null == packages || packages.isEmpty()) {
            LOGGER.warn("handler.pickWillPrintPackageInterceptHandler-->运单{}没有包裹信息，无法打印包裹标签",
                    waybillPrintResponse.getWaybillCode());
            result.toError(JdResponse.CODE_RE_PRINT_NO_PACK_LIST,MessageFormat
                    .format(JdResponse.MESSAGE_RE_PRINT_NO_PACK_LIST,waybillPrintResponse.getWaybillCode()));
            return result;
        }

        /* 挑选未打印的包裹 */
        result = pickPrintPackage(context);

        if (waybillPrintResponse.getPackList().size() > MAX_PRINT_SIZE) {
            LOGGER.warn("handler.pickWillPrintPackageInterceptHandler-->该单{}包裹数为{}，确定打印所有包裹",
                    waybillPrintResponse.getWaybillCode(),waybillPrintResponse.getPackList().size());
            result.toWeakSuccess(JdResponse.CODE_RE_PRINT_PACK_SIZE_TOO_LARGE, MessageFormat
                    .format(JdResponse.MESSAGE_RE_PRINT_PACK_SIZE_TOO_LARGE,waybillPrintResponse.getPackList().size()));
            return result;
        }
        return result;
    }

    /**
     * 找出当前要打印的包裹
     * 换单打印和包裹补打
     *      如果是包裹号操作的话，则挑选出仅一条的包裹列表
     *      如果是运单号的话，则将willPrintPackageIndex置为待打印的index
     * 换单打印
     *      如果包裹列表都打印的完成的话，则给出提示
     * @param context
     * @return
     */
    private InterceptResult<String> pickPrintPackage(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();
        List<PrintPackage> newPackList = new ArrayList<>(context.getResponse().getPackList().size());
        int minIndex = context.getResponse().getPackList().size();
        boolean alreadyPrint = Boolean.TRUE;
        for (int i = 0; i < context.getResponse().getPackList().size(); i++) {
            PrintPackage printPackage = context.getResponse().getPackList().get(i);
            if (context.getRequest().getBarCode().equals(printPackage.getPackageCode())
                    || context.getRequest().getPackageIndex().equals(printPackage.getPackageIndexNum())) {
                /* 请求是包裹号 */
                newPackList.add(printPackage);
                context.getResponse().setWillPrintPackageIndex(0);
                if (!printPackage.isPrintPack) {
                    /* 一旦出现未打印的则置alreadyPrint = false */
                    alreadyPrint = Boolean.FALSE;
                }
            } else if (WaybillUtil.isWaybillCode(context.getRequest().getBarCode())) {
                /* 请求是运单号 */
                newPackList.add(printPackage);
                if (!printPackage.isPrintPack) {
                    /* 如果该包裹未打印 */
                    if (printPackage.getPackageIndexNum() <= minIndex) {
                        minIndex = printPackage.getPackageIndexNum();
                        context.getResponse().setWillPrintPackageIndex(i);
                    }
                    /* 一旦出现未打印的则置alreadyPrint = false */
                    alreadyPrint = Boolean.FALSE;
                }
            }
        }
        /* 只对换单打印进行判断 */
        if (alreadyPrint && SITE_MASTER_REVERSE_CHANGE_PRINT.getType().equals(context.getRequest().getOperateType())) {
            LOGGER.warn("handler.pickWillPrintPackageInterceptHandler-->该包裹{}已经打印",context.getRequest().getOldBarCode());
            result.toError(JdResponse.CODE_REVERSE_CHANGE_PRINT_ALREADY,
                    MessageFormat.format(JdResponse.MESSAGE_REVERSE_CHANGE_PRINT_ALREADY,context.getRequest().getOldBarCode()));
        }
        context.getResponse().setPackList(newPackList);
        return result;
    }
}
