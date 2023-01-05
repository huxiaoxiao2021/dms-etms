package com.jd.bluedragon.distribution.jy.service.revokeException;

import com.jd.bluedragon.common.dto.revokeException.request.QueryExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.request.RevokeExceptionReq;
import com.jd.bluedragon.common.dto.revokeException.response.ExceptionReportResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.TmsLineTypeEnum;
import com.jd.bluedragon.distribution.jy.manager.RevokeExceptionManager;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.tms.dtp.dto.AccountDto;
import com.jd.tms.dtp.dto.PageDto;
import com.jd.tms.dtp.dto.TransAbnormalBillDetailDto;
import com.jd.tms.dtp.dto.TransAbnormalBillQueryDto;
import com.sleepycat.je.tree.IN;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;

/**
 * @author liwenji
 * @date 2023-01-04 16:16
 */
@Service
@Slf4j
public class IRevokeExceptionServiceImpl implements IRevokeExceptionService {
    
    @Autowired
    private RevokeExceptionManager revokeExceptionManager;
    
    //封签破损
    public static final String SEAL_CODES_BREAK = "10311";
    //封签缺失
    public static final String SEAL_CODES_MISS = "10312";
    //封签捆绑不规范
    public static final String SEAL_CODES_IRREGULAR = "10313";
    //封签不一致     
    public static final String SEAL_CODES_INCONSISTENT = "10314";
    
    public static final Integer SOURCE = 1;
    
    public static final Integer REVOKE_EXCEPTION_HOUR = 100;
    
    @Override
    public InvokeResult<List<ExceptionReportResp>> queryAbnormalPage(QueryExceptionReq request) {
        if (!checkQueryExceptionReq(request)) {
            return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
        }
         
        AccountDto accountDto = getAccountDto(request);
        
        TransAbnormalBillQueryDto transAbnormalBillQueryDto = getTransAbnormalBillQueryDto(request);
        
        PageDto<TransAbnormalBillDetailDto> pageDto = getPageDto(request);
        
        if (log.isInfoEnabled()) {
            log.info("开始查询封签异常提报：{} {} {}", JsonHelper.toJson(accountDto),JsonHelper.toJson(transAbnormalBillQueryDto), JsonHelper.toJson(pageDto));
        }
        InvokeResult<PageDto<TransAbnormalBillDetailDto>> invokeResult = revokeExceptionManager.queryAbnormalPage(accountDto, transAbnormalBillQueryDto, pageDto);
        
        if (invokeResult != null && invokeResult.getCode() != RESULT_SUCCESS_CODE) {
            return new InvokeResult<>(invokeResult.getCode(),invokeResult.getMessage());
        }
        if (invokeResult != null && invokeResult.getCode() == RESULT_SUCCESS_CODE) {
            return new InvokeResult<>(invokeResult.getCode(),invokeResult.getMessage(), getExceptionReportRespList(invokeResult.getData()));
        }
        
        return new InvokeResult<>(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    private List<ExceptionReportResp> getExceptionReportRespList(PageDto<TransAbnormalBillDetailDto> data) {
        
        List<ExceptionReportResp> exceptionReportRespList = new ArrayList<>();

        if (data == null) {
            return exceptionReportRespList;
        }
        List<TransAbnormalBillDetailDto> result = data.getResult();
        
        if (!CollectionUtils.isEmpty(result)) {
            for (TransAbnormalBillDetailDto dto : result) {
                if (dto != null) {
                    ExceptionReportResp reportResp = new ExceptionReportResp();
                    reportResp.setTransAbnormalCode(dto.getTransAbnormalCode());
                    reportResp.setVehicleNumber(dto.getVehicleNumber());
                    reportResp.setReportTime(dto.getCreateTime());
                    reportResp.setLineTypeName(TmsLineTypeEnum.getLineType(dto.getTransType().intValue()).getName());
                    reportResp.setAbnormalTypeName(dto.getAbnormalTypeName());
                    reportResp.setRevokeSurplusTime(new Date().getTime() - dto.getCreateTime().getTime());
                    exceptionReportRespList.add(reportResp);
                }
            }
        }
        return exceptionReportRespList;
    }

    private PageDto<TransAbnormalBillDetailDto> getPageDto(QueryExceptionReq request) {
        PageDto<TransAbnormalBillDetailDto> pageDto = new PageDto<>();
        pageDto.setPageSize(request.getPageSize());
        pageDto.setCurrentPage(request.getCurrentPage());
        return pageDto;
    }
    
    private TransAbnormalBillQueryDto getTransAbnormalBillQueryDto(QueryExceptionReq request) {
        TransAbnormalBillQueryDto queryDto = new TransAbnormalBillQueryDto();
        queryDto.setCreateSiteId(request.getCurrentOperate().getSiteCode());
        List<String> abnormalTypeCodeList = new ArrayList<>();
        abnormalTypeCodeList.add(SEAL_CODES_BREAK);
        abnormalTypeCodeList.add(SEAL_CODES_MISS);
        abnormalTypeCodeList.add(SEAL_CODES_IRREGULAR);
        abnormalTypeCodeList.add(SEAL_CODES_INCONSISTENT);
        queryDto.setAbnormalTypeCodeList(abnormalTypeCodeList);
        Date time = DateHelper.add(new Date(), Calendar.HOUR, -REVOKE_EXCEPTION_HOUR);
        queryDto.setAbnormalTimeBegin(time);
        queryDto.setAbnormalTimeEnd(new Date());
        if (!StringUtils.isEmpty(request.getVehicleNumber())) {
            queryDto.setVehicleNumber(request.getVehicleNumber());
        }
        return queryDto;
    }
    
    private AccountDto getAccountDto(QueryExceptionReq request) {
        AccountDto accountDto = new AccountDto();
        // 1:内网erp,2:外网PIN,3:APP-PIN请求,4:大屏erp请求 8:3pl-PIN，9:TFC
        accountDto.setSource(SOURCE);
        accountDto.setUserCode(request.getUser().getUserErp());
        accountDto.setUserName(request.getUser().getUserName());
        return accountDto;
    }

    private boolean checkQueryExceptionReq(QueryExceptionReq request) {
        
        if (request.getCurrentOperate() == null) {
            return false;
        }
            
        if (request.getUser() == null) {
            return false;
        }
        
        if (request.getPageSize() < 0) {
            return false;
        }
        
        if (request.getCurrentPage() < 0) {
            return false;
        }
        
        return true;
    }

    @Override
    public InvokeResult<String> closeTransAbnormal(RevokeExceptionReq revokeExceptionReq) {
        return null;
    }
}
