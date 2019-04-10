package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <P>
 *     换单打印：如果用户启用的包裹称重，则将称重重量赋值到当前需要打印的包裹
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/10
 */
@Service("packageWeightHandler")
public class PackageWeightHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PackageWeightHandler.class);

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        JdResult<String> result = context.getResult();
        WaybillPrintResponse waybillPrint = context.getResponse();
        if (null == waybillPrint) {
            return result;
        }
        /* 获取包裹列表 */
        List<PrintPackage> packages = waybillPrint.getPackList();
        if (null == packages || packages.isEmpty()) {
            return result;
        }

        /* 是否开启用户称重 */
        boolean flag = context.getRequest().getWeightVolumeOperEnable() != null
                && (context.getRequest().getWeightVolumeOperEnable() & Constants.WEIGHT_ENABLE) == Constants.WEIGHT_ENABLE;
        if (!flag) {
            /* 未启用用户称重则直接退出 */
            return result;
        }

        /*
            如果是运单号的话，则计算未打印的包裹中index最小的那个
            如果是包裹号的话，则找到当前的包裹
            逻辑好像可以精简
         */
        int min = packages.size();//最小的包裹index
        PrintPackage minPackage = null;//未打印的index最小的包裹对象
        if (WaybillUtil.isWaybillCode(context.getRequest().getBarCode())) {
            /* 遍历所有的包裹 */
            for (PrintPackage printPackage : packages) {
                if (printPackage.getIsPrintPack()) {
                    /* 如果已经打印的话，则 */
                    continue;
                }
                boolean bool = WaybillUtil.getPackIndexByPackCode(printPackage.getPackageCode()) <= min;
                if (bool) {
                    minPackage = printPackage;//将当前引用设置给minPackage
                }
            }
            if (null != minPackage) {
                Double weight = context.getRequest().getWeightOperFlow().getWeight();
                minPackage.setPackageWeight(String.valueOf(weight));
                minPackage.setWeight(weight);
            }
        } else if (WaybillUtil.isPackageCode(context.getRequest().getBarCode())) {
            /* 遍历所有的包裹 */
            for (PrintPackage printPackage : packages) {
                if (printPackage.getPackageCode().equals(context.getRequest().getBarCode())
                        || WaybillUtil.getPackIndexByPackCode(printPackage.getPackageCode()) == context.getRequest().getPackageIndex()) {
                    /* 如果是当前的包裹 则设置包裹称重信息 */
                    Double weight = context.getRequest().getWeightOperFlow().getWeight();
                    printPackage.setPackageWeight(String.valueOf(weight));
                    printPackage.setWeight(weight);
                }
            }
        }
        return result;
    }
}
