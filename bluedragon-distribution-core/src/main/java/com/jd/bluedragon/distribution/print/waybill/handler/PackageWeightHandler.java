package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
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

        /* 是否开启用户称重 1启用称重 3启用称重量方*/
        boolean flag = context.getRequest().getWeightVolumeOperEnable() == null
                || context.getRequest().getWeightVolumeOperEnable() != Constants.WEIGHT_ENABLE
                || context.getRequest().getWeightVolumeOperEnable() != (Constants.WEIGHT_ENABLE|Constants.VOLUME_ENABLE);
        if (flag) {
            /* 未启用用户称重则直接退出 */
            return result;
        }

        /*
            如果是运单号的话，则计算未打印的包裹中index最小的那个
            如果是包裹号的话，则找到当前的包裹
            直接根据之前的willPrintPackageIndex值找到那个将要打印的包裹
         */
        PrintPackage printPackage = context.getResponse().getPackList().get(context.getResponse().getWillPrintPackageIndex());

        /* 设置用户称重 */
        printPackage.setWeight(context.getRequest().getWeightOperFlow().getWeight());
        printPackage.setPackageWeight(String.valueOf(context.getRequest().getWeightOperFlow().getWeight())
                + Constants.MEASURE_UNIT_NAME_KG);

        return result;
    }
}
