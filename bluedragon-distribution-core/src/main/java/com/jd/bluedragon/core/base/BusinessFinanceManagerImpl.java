package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.spotcheck.exceptions.SpotCheckSysException;
import com.jd.etms.finance.api.jsf.BusinessDetailQueryJsf;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: BusinessFinanceManagerImpl
 * @Description: 计费接口包装类
 * @author: hujiping
 * @date: 2019/5/9 12:56
 */
@Service("businessFinanceManager")
public class BusinessFinanceManagerImpl implements BusinessFinanceManager {

    private static Logger logger = LoggerFactory.getLogger(BusinessFinanceManagerImpl.class);

    @Autowired
    private BusinessDetailQueryJsf businessDetailQueryJsf;

    /**
     * 根据运单号查询运费信息
     * @param waybillCode
     * @return
     */
    @Override
    public ResponseDTO<BizDutyDTO> queryDutyInfo(String waybillCode){
        ResponseDTO<BizDutyDTO> responseDTO = null;
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.BusinessFinanceManagerImpl.queryDutyInfo",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            responseDTO = businessDetailQueryJsf.queryDutyInfo(waybillCode);
        }catch (Exception e){
            logger.error("运单号:{}查询计费称重量方数据异常!", waybillCode, e);
            Profiler.functionError(callerInfo);
            throw new SpotCheckSysException(e.getMessage());
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return responseDTO;
    }
}
