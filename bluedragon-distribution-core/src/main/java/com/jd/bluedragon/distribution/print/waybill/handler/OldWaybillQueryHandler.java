package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <P>
 *     如果从新单获取到旧单的信息并设置到context.oldWaybillEntity属性中
 *     截止到2019-4-18 目前只有换单打印和包裹补打中有使用到
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/18
 */
@Service("oldWaybillQueryHandler")
public class OldWaybillQueryHandler implements Handler<WaybillPrintContext, JdResult<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OldWaybillQueryHandler.class);

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public JdResult<String> handle(WaybillPrintContext context) {
        JdResult<String> result = context.getResult();

        if (context.getOldBigWaybillDto() != null && context.getOldBigWaybillDto().getWaybill() != null) {
            /* 如果context中已经有了oldWaybillEntity的属性，就不用重新设置了，直接返回 */
            if (StringHelper.isEmpty(context.getResponse().getOldWaybillCode())) {
                context.getResponse().setOldWaybillCode(context.getOldBigWaybillDto().getWaybill().getWaybillCode());
            }
            return result;
        }

        String newBarCode = context.getRequest().getBarCode();
        String newWaybillCode = WaybillUtil.getWaybillCode(newBarCode);

        //获取原运单
        BaseEntity<Waybill> oldWaybill= waybillQueryManager.getWaybillByReturnWaybillCode(newWaybillCode);
        if (null == oldWaybill || Constants.RESULT_SUCCESS != oldWaybill.getResultCode() || null == oldWaybill.getData()) {
            LOGGER.warn("OldWaybillQueryHandler.handler-->根据单号{}未获取到旧单号",newWaybillCode);
            return result;
        }

        String oldWaybillCode = oldWaybill.getData().getWaybillCode();

        //查询原单号的状态
        if (StringHelper.isEmpty(oldWaybillCode)) {
            LOGGER.warn("OldWaybillQueryHandler.handler-->获取到{}旧单信息，但是旧单号为空",newWaybillCode);
            return result;
        }

        context.getResponse().setOldWaybillCode(oldWaybillCode);
        /* 设置oldWaybillEntity对象到context上下文中 */
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getWaybillDataForPrint(oldWaybillCode);
        if(baseEntity != null && baseEntity.getData()!=null){
            /* 设置oldWaybillEntity对象到context上下文中 */
            context.setOldBigWaybillDto(baseEntity.getData());
        }

        return result;
    }
}
