package com.jd.bluedragon.core.base;

import com.jd.tms.workbench.dto.*;

import java.util.List;

public interface PdaSorterApiManager {

    /**
     * 分页查询待派车
     * @param accountDto
     * @param transJobBillDto
     * @param pageDto
     * @return
     */
    CommonDto<PageDto<TmsTransJobBillDto>> queryTransJobPage(AccountDto accountDto, TransJobPdaQueryDto transJobBillDto, PageDto<TmsTransJobBillDto> pageDto);


    /**
     * 待派车催派
     * @param accountDto
     * @param remindTransJobRequestDTO
     * @return
     */
    CommonDto<RemindTransJobReponseDTO> remindTransJob(AccountDto accountDto, RemindTransJobRequestDTO remindTransJobRequestDTO) ;


}
