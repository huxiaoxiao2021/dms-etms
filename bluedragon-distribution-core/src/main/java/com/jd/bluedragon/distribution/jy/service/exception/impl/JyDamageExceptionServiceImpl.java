package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpTypeCheckReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskExceptionTypeEnum;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionStrategy;
import com.jd.bluedragon.distribution.qualityControl.dto.QcReportOutCallJmqDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/25 20:18
 * @Description: 异常-破损 service 实现
 */
@Service
public class JyDamageExceptionServiceImpl extends JyExceptionStrategy implements JyDamageExceptionService {

    private final Logger logger = LoggerFactory.getLogger(JyExceptionServiceImpl.class);

    public Integer getExceptionType() {
        return JyBizTaskExceptionTypeEnum.DAMAGE.getCode();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyDamageExceptionServiceImpl.exceptionTaskCheckByExceptionType", mState = {JProEnum.TP})
    public JdCResponse<Boolean> exceptionTaskCheckByExceptionType(ExpTypeCheckReq req) {

        JdCResponse<Boolean> response = new JdCResponse<>();
        String waybillCode = req.getBarCode();
        //todo  添加校验港澳单

        return response;
    }

    @Override
    public void dealExpDamageInfoByAbnormalReportOutCall(QcReportOutCallJmqDto qcReportJmqDto) {

        try {
            if(StringUtils.isBlank(qcReportJmqDto.getAbnormalDocumentNum())){
                logger.error("abnormalDocumentNum 为空！");
                return ;
            }

        }catch (Exception e){
            logger.error("根据质控异常提报mq处理破损数据异常!");

        }

    }
}
