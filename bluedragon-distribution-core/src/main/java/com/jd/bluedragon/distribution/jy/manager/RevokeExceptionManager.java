package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.tms.dtp.dto.AccountDto;
import com.jd.tms.dtp.dto.PageDto;
import com.jd.tms.dtp.dto.TransAbnormalBillDetailDto;
import com.jd.tms.dtp.dto.TransAbnormalBillQueryDto;

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
}
