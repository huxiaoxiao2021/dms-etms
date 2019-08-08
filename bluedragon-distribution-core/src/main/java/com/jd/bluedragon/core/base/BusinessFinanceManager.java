package com.jd.bluedragon.core.base;

import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;

/**
 * @ClassName: BusinessFinanceManager
 * @Description: 计费接口包装接口
 * @author: hujiping
 * @date: 2019/5/9 12:55
 */
public interface BusinessFinanceManager {

    /**
     * 根据运单号查询运费信息
     * @param waybillCode
     * @return
     */
    ResponseDTO<BizDutyDTO> queryDutyInfo(String waybillCode);

}
