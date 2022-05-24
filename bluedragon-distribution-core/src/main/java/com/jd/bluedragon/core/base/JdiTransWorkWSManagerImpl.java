package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.ws.JdiTransWorkWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/5/24
 * @Description:
 */
@Service("jdiTransWorkWSManager")
public class JdiTransWorkWSManagerImpl implements JdiTransWorkWSManager{


    private JdiTransWorkWS jdiTransWorkWS;


    /**
     * 查询派车单明细信息
     *
     * @param itemCode
     * @param option
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JdiTransWorkWSManager.queryTransWorkItemByOptionWithRead",mState = {JProEnum.TP, JProEnum.FunctionError})
    public BigTransWorkItemDto queryTransWorkItemByOptionWithRead(String itemCode, BigQueryOption option) {
        CommonDto<BigTransWorkItemDto> commonDto = jdiTransWorkWS.queryTransWorkItemByOptionWithRead(itemCode,option);
        if(commonDto.isSuccess() && commonDto.getData() != null){
            return commonDto.getData();
        }
        return null;
    }
}
