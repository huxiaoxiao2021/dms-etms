package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.workbench.api.PdaSorterApi;
import com.jd.tms.workbench.dto.*;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("pdaSorterApiManager")
public class PdaSorterApiManagerImpl implements PdaSorterApiManager{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PdaSorterApi pdaSorterApi;
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.PdaSorterApiManagerImpl.queryTransJobPage",mState = {JProEnum.TP, JProEnum.FunctionError})
    public CommonDto<PageDto<TmsTransJobBillDto>> queryTransJobPage(AccountDto accountDto, TransJobPdaQueryDto transJobBillDto, PageDto<TmsTransJobBillDto> pageDto) {
        try {
            return pdaSorterApi.queryTransJobPage(accountDto, transJobBillDto, pageDto);
        } catch (Exception e) {
            logger.warn("分页查询待派车任务异常！ 入参accountDto{} transJobBillDto{} pageDto{}", JsonHelper.toJson(accountDto), JsonHelper.toJson(transJobBillDto), JsonHelper.toJson(pageDto));
            return null;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.PdaSorterApiManagerImpl.remindTransJob",mState = {JProEnum.TP, JProEnum.FunctionError})
    public CommonDto<RemindTransJobReponseDTO> remindTransJob(AccountDto accountDto, RemindTransJobRequestDTO remindTransJobRequestDTO) {
        try {
            return pdaSorterApi.remindTransJob(accountDto, remindTransJobRequestDTO);
        } catch (Exception e) {
            logger.warn("催派待派车任务异常！ 入参accountDto{} remindTransJobRequestDTO{}", JsonHelper.toJson(accountDto), JsonHelper.toJson(remindTransJobRequestDTO));
            return null;
        }
    }
}
