package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.dto.BigWaybillDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 *函速达拒收件不允许换单
 *函速达用户拒收运单，waybill_sign第31位等于B且waybill_sign第5位等于5
 * @author biyubo1
 * @since 2020/09/28
 **/
@Service("letterExpressRejectHandler")
public class LetterExpressRejectHandler implements InterceptHandler<WaybillPrintContext, String> {

    private static final Logger log = LoggerFactory.getLogger(LetterExpressRejectHandler.class);

    @Override
    public InterceptResult<String> handle(WaybillPrintContext context) {
        InterceptResult<String> result = context.getResult();

        log.info("letterExpressRejectHandler-->函速达拒收件换单校验，新单号为:{}",context.getRequest().getBarCode());
        BigWaybillDto waybillDto = context.getOldBigWaybillDto();

        log.info("letterExpressRejectHandler-->函速达拒收件换单校验，原单信息:{}", JsonHelper.toJson(waybillDto));
        //2.2函速达拒收件不允许换单
        if(waybillDto != null && null!=waybillDto.getWaybill() && BusinessUtil.isLetterExpressReject(waybillDto.getWaybill().getWaybillSign())){
            result.toFail(SortingResponse.CODE_29319,SortingResponse.MESSAGE_29319);
        }

        return result;
    }
}
