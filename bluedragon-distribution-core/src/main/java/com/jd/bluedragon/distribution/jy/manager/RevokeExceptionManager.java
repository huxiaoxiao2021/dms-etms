package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.tms.dtp.dto.*;

/**
 * @author liwenji
 * @date 2023-01-04 16:39
 */
public interface RevokeExceptionManager {


    /**
     * 查询封签异常提报
     * @param accountDto
     * @param transAbnormalBillQueryDto
     * @param pageDto
     * @return
     */
    InvokeResult<PageDto<TransAbnormalBillDetailDto>> queryAbnormalPage(AccountDto accountDto, TransAbnormalBillQueryDto transAbnormalBillQueryDto, PageDto<TransAbnormalBillDetailDto> pageDto);

    /**
     * 撤销封签异常提报功能
     * @param accountDto
     * @param transAbnormalExtendDto
     * @return
     */
    InvokeResult<String> closeTransAbnormal(AccountDto accountDto, TransAbnormalExtendDto transAbnormalExtendDto);
}
