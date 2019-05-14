package com.jd.bluedragon.core.base;

import com.jd.etms.finance.api.jsf.BusinessDetailQueryJsf;
import com.jd.etms.finance.dto.BizDutyDTO;
import com.jd.etms.finance.util.ResponseDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BusinessDetailQueryJsf businessDetailQueryJsf;

    /**
     * 根据运单号查询运费信息
     * @param waybillCode
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.businessDetailQueryJsf.queryDutyInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public ResponseDTO<BizDutyDTO> queryDutyInfo(String waybillCode){

        return businessDetailQueryJsf.queryDutyInfo(waybillCode);
    }
}
